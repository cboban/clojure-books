(ns books.books.books-model
  (:use (sandbar stateful-session))
  (:use [clojure.tools.logging :only (info error)])
  (:require [books.neo4j :as n4j]
            [books.goodread.goodread-parser :as gparser]
            [books.shelves.shelves-model :as shelvem]))



(defn search
  "Search books based on term"
  [term]
  (gparser/search term))


(defn get-book-data 
  "Get book node based on id"
  [id]
  (first (for [[book-id book-data] (:data (n4j/cypher-query (str "MATCH(book:Book {id : \"" id "\"}) RETURN id(book), book")))]
                      (assoc (:data book-data) :node-id book-id))))


(defn get-book
  "Return book details"
  [id]
  (let [book (get-book-data id)
        authors (for [[author-id author-data] (:data (n4j/cypher-query (str "MATCH (author:Author)-[:WROTE]->(book:Book {id : \"" id "\"}) RETURN id(author), author")))]
               (assoc (:data author-data) :node-id author-id))
        links (for [[link-id link-data] (:data (n4j/cypher-query (str "MATCH (book:Book {id : \"" id "\"})-[:HASLINK]->(links) RETURN id(links), links")))]
                   (assoc (:data link-data) :node-id link-id))
        available-shelves (shelvem/get-available-shelves id)
        in-shelves (shelvem/in-shelves id)]
    {:book book :authors authors :links links :available-shelves available-shelves :in-shelves in-shelves}))


(defn get-similar
  "Get similar books"
  [id]
  (gparser/get-similar id))

(defn add-book
  "Add book to shelve"
  [book-id shelve-id]
  (let [book-node (n4j/read-node (:node-id (get-book-data (Integer/valueOf book-id))))
        shelve-node (n4j/read-node (Integer/parseInt shelve-id))]
    (n4j/create-relationship shelve-node book-node :STORES {})))

(defn remove-book
  "Add book to shelve"
  [book-id shelve-id]
  (let [relationship-id 
        (first 
          (for [[shelve-id] 
                (:data 
                  (n4j/cypher-query 
                    (str "MATCH (user:User)-[:OWNS]->(shelve:Shelve)-[store:STORES]->(book:Book {id : \"" book-id "\"}) WHERE ID(user) = " (:id (session-get :user)) " AND ID(shelve) = " shelve-id " RETURN ID(store) LIMIT 1")))]
			            shelve-id
			            ))]
    (n4j/delete-relationship (n4j/read-relationship relationship-id))))
