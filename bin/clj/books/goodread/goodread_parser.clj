(ns books.goodread.goodread-parser
  (:use (sandbar stateful-session))
  (:use [clojure.tools.logging :only (info error)])
  (:import (java.net URLEncoder))

  (:require [books.neo4j :as n4j]
            [clojure.xml :as xmlparser]
            [books.url-helper :as urlhelper]
            [clojure.zip :as zip]
            [clojure.data.zip.xml :as zip-xml]))

(defn get-api-key
  []
  (str "guTlRaNXH1OZebb7JfKXQ"))

(defn get-xml-response
  "Read xml from url"
  [url]
  (xmlparser/parse (urlhelper/string-to-stream (urlhelper/fetch-url url))))

(defn read-node-text
  [node key]
  (try
		     (zip-xml/text (zip-xml/xml1-> node key))
		     (catch Exception e "")))

(defn check-book-in-db
  "Check wether book exists in database"
  [book]
  (let [check (first (:data (n4j/cypher-query (str "MATCH (book:Book {isbn: \"" (:isbn book) "\"}) RETURN id(book)"))))]
    (if (= (count check) 0)
      true false
    )))

(defn save-all-info 
  "Save book with authors and links"
  [data]
  (if (check-book-in-db (:book data))
  (let [book (n4j/create-node "Book" (:book data))]
	    (doseq [author (:authors data)]
	     (let [saved-author (n4j/create-node "Author" author)]
	       (n4j/create-relationship saved-author book :WROTE {}))
	    )
	    (doseq [link (:links data)]
		    (let [saved-link (n4j/create-node "Link" link)]
		      (n4j/create-relationship book saved-link :HASLINK {}))
	    )
    )))


(defn save-detailed-info
  "Save book detailed info"
  [book]
  (let [book-details (get-xml-response (str "https://www.goodreads.com/book/show/" (:id book) "?key=" (get-api-key)))]
    (let [all-info (zip-xml/xml1-> (zip/xml-zip book-details) :book)]
       (let [book {
                   :id (read-node-text all-info :id)
                   :title (read-node-text all-info :title)
                   :isbn (read-node-text all-info :isbn)
                   :isbn13 (read-node-text all-info :isbn13)
                   :image_url (read-node-text all-info :image_url)
                   :small_image_url (read-node-text all-info :small_image_url)
                   :publication_year (read-node-text all-info :publication_year)
                   :description (read-node-text all-info :description)
                   :num_pages (read-node-text all-info :num_pages)
                   :average_rating (read-node-text all-info :average_rating)
                   :ratings_count (read-node-text all-info :ratings_count)
                   :url (read-node-text all-info :url)
                   :link (read-node-text all-info :link)
                  }
             authors (for [author (zip-xml/xml-> all-info :authors :author)]
                       { 
                        :id (zip-xml/text (zip-xml/xml1-> author :id))
                        :name (zip-xml/text (zip-xml/xml1-> author :name))
                        :image_url (zip-xml/text (zip-xml/xml1-> author :image_url))
                        :small_image_url (zip-xml/text (zip-xml/xml1-> author :small_image_url))
                        :average_rating (zip-xml/text (zip-xml/xml1-> author :average_rating))
                       })
             
             links (for [link (zip-xml/xml-> all-info :book_links :book_link)]
                     {
                       :name  (zip-xml/text (zip-xml/xml1-> link :name))
                       :link  (zip-xml/text (zip-xml/xml1-> link :link))
                      })
             ]
         (save-all-info {:book book :authors authors :links links})
                 
   ))))

(defn parse-response
  "Parse goodreads response"
  [response]
  (for [m (zip-xml/xml-> (zip/xml-zip response) :search :results :work)]
       (let [book {
                   :id (zip-xml/text (zip-xml/xml1-> m :best_book :id))
                   :rating (zip-xml/text (zip-xml/xml1-> m :average_rating))
                   :title (zip-xml/text (zip-xml/xml1-> m :best_book :title))
                   :author_name (zip-xml/text (zip-xml/xml1-> m :best_book :author :name))
                   :image (zip-xml/text (zip-xml/xml1-> m :best_book :image_url))
                   :small_image (zip-xml/text (zip-xml/xml1-> m :best_book :small_image_url))
                  }]
         (send (agent book) save-detailed-info)
         ;(save-detailed-info book)
         book
   )))


(defn parse-similar
  "Parse similar books"
  [response]
	  (for [m (zip-xml/xml-> (zip/xml-zip response) :book :similar_books :book)]
	       (let [book {
	                   :id (zip-xml/text (zip-xml/xml1-> m :id))
	                   :rating (zip-xml/text (zip-xml/xml1-> m :average_rating))
	                   :title (zip-xml/text (zip-xml/xml1-> m :title))
	                   :image (zip-xml/text (zip-xml/xml1-> m :image_url))
	                   :small_image (zip-xml/text (zip-xml/xml1-> m :small_image_url))
	                  }]
	         (send (agent book) save-detailed-info)
	         ;(save-detailed-info book)
	         book
	   ))
  )


(defn get-similar
  "Get similar books"
  [id]
  (parse-similar (get-xml-response (str "https://www.goodreads.com/book/show/" id "?key=" (get-api-key)))))

(defn search
  "Search books based on term"
  [term]
  (parse-response (get-xml-response (str "https://www.goodreads.com/search.xml?q=" (URLEncoder/encode term) "&key=" (get-api-key)))))