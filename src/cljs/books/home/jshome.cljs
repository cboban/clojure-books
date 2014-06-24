(ns books.home.jshome
  (:require [domina :as dom]
	    [domina.events :as evts]
	    [domina.css :as domcss]
      [goog.events :as events]
      [books.helpers.ui-helper :as uihelper]
      [books.helpers.ajax-helper :as ajaxhelper]
      [clojure.browser.net :as net]
      [clojure.browser.event :as gevent]
      [enfocus.core :as ef]
      [enfocus.events :as ev]
      [secretary.core :as secretary :include-macros true :refer [defroute]])
  (:require-macros [enfocus.macros :as em]))


(defn handle-shelve-response
  "Handle add to shelve response"
  [content]
  (ajaxhelper/parse-json-response content 
        (fn [data]
          (do
            (js/alert (:message data))
            (.reload js/location)))
        (fn [data]
	        (js/alert (:message data)))))


(defn remove-book-from-shelve
  "Remove book tfrom shelve"
  [book-id shelve-id]
  (let [ajaxUrl "/books/remove-book" xhr (net/xhr-connection)
        postData (str "book-id=" (str book-id)
					    "&shelve-id=" (str shelve-id))]
      (gevent/listen xhr :error #(.log js/console "Error %1"))
      (gevent/listen xhr :success handle-shelve-response)
      (net/transmit xhr ajaxUrl "POST" postData)))

(defn add-book-to-shelve
  "Add book to shelve"
  [book-id shelve-id]
  (let [ajaxUrl "/books/add-book" xhr (net/xhr-connection)
        postData (str "book-id=" (str book-id)
					    "&shelve-id=" (str shelve-id))]
      (gevent/listen xhr :error #(.log js/console "Error %1"))
      (gevent/listen xhr :success handle-shelve-response)
      (net/transmit xhr ajaxUrl "POST" postData)))


(defn populate-shelves
  "Populate shelve select box"
  [book]
  (do 
    (ef/at "select.shelves-list option" 
         (em/clone-for [shelve (:available-shelves book)]
            (ef/do->
              (ef/set-attr :value (:id shelve))
              (ef/content (:name shelve)))))
    (ef/at ".add-to-shelve" (ev/listen :click 
           #(ef/at (.-currentTarget %)
                 (let [shelve-id (ef/from "select.shelves-list" (ef/get-prop :value))
                       shelve-name (ef/from "select.shelves-list" (ef/get-text))
                       id (:id (:book book))]
                   (do
                     (if (js/confirm (str "Add book \"" (:title (:book book)) "\" to shelve \"" (clojure.string/trim shelve-name) "\" ?"))
                       (add-book-to-shelve id shelve-id)
                       false)
                   )))))
    
    (if (= (count (:available-shelves book)) 0)
      (ef/at ".shelve-data" (ef/set-style :display "none")))
    
     (ef/at ".in-shelves .one-shelve" 
         (em/clone-for [shelve (:in-shelves book)]
            "em" (ef/content (:name shelve))
            "a" (ev/listen :click 
			           #(ef/at (.-currentTarget %)
			                 (let [shelve-id (:id shelve)
			                       shelve-name (:name shelve)
			                       id (:id (:book book))]
			                   (do
			                     (if (js/confirm (str "Remove book \"" (:title (:book book)) "\" from shelve \"" (clojure.string/trim shelve-name) "\" ?"))
			                       (remove-book-from-shelve id shelve-id)
			                       false)
			                   ))))))
     
    (if (= (count (:in-shelves book)) 0)
      (ef/at ".in-shelves-holder" (ef/set-style :display "none")))
     
     ))


(defn show-similar
  "Show similar books"
  [books]
  (do (ef/at ".similar-books .similar-book-template" 
           (em/clone-for [book books]
              "div.image img" (ef/set-attr :src (:image book))
              "div.rating .rating-value" (ef/content (:rating book))
              "div.title a" (ef/do->
                              (ef/content (:title book))
                              (ef/set-attr :href (str "/#/book/" (:id book))))))
    (if (not= (count books) 0)  (ef/at ".similar-books-holder" (ef/set-style :display "block")))
    (ef/at ".similar-books-loader" (ef/set-style :display "none"))))


(defn render-similar-books
  "Render similar books"
  [content]
  (ajaxhelper/parse-json-response content 
        (fn [data]
          (show-similar (:data data)))
        (fn [data]
	        (js/alert (:message data)))))


(defn get-similar 
  "Get similar books from goodreads"
  [book]
  (let [ajaxUrl (str "/books/similar/" (:id book)) xhr (net/xhr-connection)]
      (gevent/listen xhr :error #(.log js/console "Error %1"))
      (gevent/listen xhr :success render-similar-books)
      (net/transmit xhr ajaxUrl "GET")))


(defn show-links
  "Show book authors"
  [links book]
    (ef/at ".book-links ul li" 
      (em/clone-for [link links]
		       "div.link a" (ef/do->
                         (ef/set-attr :href (str (:link link) "?book_id=" (:id book)))
                         (ef/content (:name link))))))


(defn show-authors
  "Show book authors"
  [authors]
  (ef/at ".book-authors ul li" 
       (em/clone-for [author authors]
		        "div.image img" (ef/set-attr :src (:image_url author))
		        "div.name" (ef/content (:name author))
		        "div.rating" (ef/content (str "Rating: " (:average_rating author))))))


(defn show-book-details
  "Populate html with book details"
  [book]
  (do
  (ef/at ".books-view .book-name" (ef/content (:title (:book book))))
  (ef/at ".books-view .book-image img" (ef/set-attr :src (:image_url (:book book))))
  (ef/at ".books-view .book-rating" (ef/content (str "Rating: " (:average_rating (:book book)))))
  (ef/at ".books-view .book-isbn" (ef/content (str "ISBN: " (:isbn (:book book)))))
  (ef/at ".books-view .book-isbn13" (ef/content (str "ISBN13: " (:isbn13 (:book book)))))
  (ef/at ".books-view .book-publication-year " (ef/content (str "Publication year: " (:publication_year (:book book)))))
  (ef/at ".books-view .book-num-pages" (ef/content (str "Pages: " (:num_pages (:book book)))))
  (ef/at ".books-view .book-goodreads-link a" (ef/set-attr :href (:link (:book book))))
  (ef/at ".books-view .book-description" (ef/content (:description (:book book))))
  (ef/at ".books-view" (ef/set-style :display "block"))
  (populate-shelves book)
  (get-similar (:book book))
  (show-authors (:authors book))
  (show-links (:links book) (:book book))
  ))


(defn render-book-details
  "Render book details"
  [content]
  (ajaxhelper/parse-json-response content 
        (fn [data]
          (do
	          (uihelper/swap-app-content (str (:html data)))
	          (uihelper/hide-loading-bar)
	          (show-book-details (:data data))))
        (fn [data]
	        (js/alert (:message data)))))


(defn show-details
  "Show book details"
  [id]
  (let [ajaxUrl (str "/books/details/" id) xhr (net/xhr-connection)]
      (gevent/listen xhr :error #(.log js/console "Error %1"))
      (gevent/listen xhr :success render-book-details)
      (net/transmit xhr ajaxUrl "GET")))

(defn handle-paginator
  "Handle paginator"
  [pagination]
  (let [showNext (if(> (js/parseInt (:total pagination)) (js/parseInt (:end pagination))) true false)
        showPrev (if(> (js/parseInt (:start pagination)) 1) true false )]
    (if (or showNext showPrev) (ef/at "div.paginator" (ef/set-style :display "block")))
    (ef/at ".paginator .from" (ef/content (:start pagination)))
    (ef/at ".paginator .to" (ef/content (:end pagination)))
    (ef/at ".paginator .total" (ef/content (:total pagination)))
    (if showNext (ef/at ".paginator .next-page" (ef/do->
                                                  (ef/set-style :display "inline")
                                                  (ef/set-attr :href (str "#/books/" (:query pagination) "/" (+ (js/parseInt (:page pagination)) 1))))))
    (if showPrev (ef/at ".paginator .previous-page" (ef/do->
                                                      (ef/set-style :display "inline")
                                                      (ef/set-attr :href (str "#/books/" (:query pagination) "/" (- (js/parseInt (:page pagination)) 1))))))))


(defn render-table
  [data]
  (do
    (if (clojure.string/blank? (ef/from "#mainContentDiv" (ef/get-text)))
      (do
	          (uihelper/swap-app-content (str (:html data)))
	          (uihelper/hide-loading-bar)
	          (set-form-listeners))
      )
    
      (ef/at "#searchTerm" (ef/set-prop :value (:query (:pagination (:data data)))))
         (ef/at "ul.search-results-list .template-item" 
           (em/clone-for [book (:books (:data data))]
			          "div.title .book-title" (ef/content (:title book))
			          "div.image .book-image" (ef/set-attr :src (:image book))
			          "div.author .book-author" (ef/content (:author_name book))
			          "div.rating .book-rating" (ef/content (:rating book))
			          "div.actions .book-details" (ef/set-attr :href (str "/#/book/" (str (:id book))))))
       
         (ef/at "ul.search-results-list .template-item" (ef/set-style :display "block"))
         (ef/at "div.searching-loader" (ef/set-style :display "none"))
       
         (handle-paginator (:pagination (:data data)))
         ))


(defn render-search-response
  "Render search response"
  [content]
  (ajaxhelper/parse-json-response content 
     (fn [data]
       (render-table data))
     
     (fn [data]
       ((js/alert (:message data))))))


(defn clear-table
  "Clear results for new search"
  []
  (do
    (ef/at "div.paginator" (ef/set-style :display "none"))
    (ef/at "div.paginator a" (ef/set-style :display "none"))
    (ef/at "ul.search-results-list li" (ef/set-style :display "none"))
    (ef/at "ul.search-results-list li:not(:first-child)" (ef/remove-node))))


(defn search
  "Sarch term"
  ([term] (search term 1))
  ([term page]
  (do
    (clear-table)
	  (ef/at "div.searching-loader" (ef/set-style :display "block"))
    (let [ajaxUrl (str "/search/" term "/" page) xhr (net/xhr-connection)]
      (gevent/listen xhr :error #(.log js/console "Error %1"))
      (gevent/listen xhr :success render-search-response)
      (net/transmit xhr ajaxUrl "GET")
      ))))


(defn search-books
  "Search books"
  []
  (let [formData (uihelper/read-form "#searchForm")]
    (set! (.-location js/window) (str "#/books/" (:term formData))
 )))


(defn validate-search-form
  []
  (if (clojure.string/blank? (uihelper/get-input-value "#searchTerm"))
    (do
      (uihelper/append-error "#searchTerm" ["Please provide search term"])
      false)
    true))


(defn set-form-listeners
  "Set on submit listener"
  []
  (ef/at "#searchForm" (ev/listen :submit 
	  (fn [evt]
	    (if (validate-search-form) (search-books) (.preventDefault evt))
	   (.preventDefault evt)))))


(defn on-form-load
 "Render shelves list"
 [content]
  (ajaxhelper/parse-json-response content 
        (fn [data]
          (do
	          (uihelper/swap-app-content (str (:data data)))
	          (uihelper/hide-loading-bar)
	          (set-form-listeners)
            (search "a")))
        (fn [data]
	        (do
	          (dom/prepend! (dom/by-id "warningInfoMessage")  (str "<div class=\"one-validation-message\">" (:message data) "</div>"))
	          (dom/set-style! (dom/by-id "warningInfoMessage") "display" "block")))))



(defn show-search-form
  "Get search form through AJAX"
  []
  (let [ajaxUrl "/home/search" xhr (net/xhr-connection)]
      (gevent/listen xhr :error #(.log js/console "Error %1"))
      (gevent/listen xhr :success on-form-load)
      (net/transmit xhr ajaxUrl "GET" {:q "json"})
          (uihelper/show-loading-bar)  
))
