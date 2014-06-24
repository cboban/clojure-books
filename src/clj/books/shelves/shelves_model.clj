(ns books.shelves.shelves-model
  (:use (sandbar stateful-session))
  (:use [clojure.tools.logging :only (info error)])
  (:require [books.neo4j :as n4j]))



(defn save-shelve
  "Save new shelve"
  [data]
  (if (contains? data :id)
    (let [shelve (n4j/read-node (Integer/parseInt (:id data)))]
      (n4j/update-node shelve data))
    
    (let [shelve (n4j/create-node "Shelve" {
                        :name (:name data)
		                    :description (:description data)})
          user (n4j/read-node (:id (session-get :user)))]
      (n4j/create-relationship user shelve :OWNS {})) 
  ))


(defn get-user-shelves
  "Get all user shelves"
  []
  (for [[id shelve-data book-count] (:data (n4j/cypher-query (str "MATCH(user:User)-[:OWNS]->(shelves) WHERE id(user) = " (:id (session-get :user)) " OPTIONAL MATCH (shelves)-[:STORES]->(books) RETURN id(shelves), shelves, count(books)")))]
    (assoc (:data shelve-data) :id id :count book-count)
  ))


(defn get-shelve
  "Get shelve by id"
  [id]
  (first (for [[id shelve-data] (:data (n4j/cypher-query (str "MATCH (shelve:Shelve)<-[:OWNS]-(user:User) WHERE id(shelve) = " id " AND id(user) = " (:id (session-get :user)) " RETURN ID(shelve), shelve")))]
           (assoc (:data shelve-data) :id id))))

(defn delete-shelve
  "Delete shelve by id"
  [id]
  (let [shelve (get-shelve (Integer/parseInt id))]
    (if (not= shelve nil)
      (n4j/delete-node (Integer/parseInt id))
      (false))))


(defn get-available-shelves
  "Get free shelves for book"
  [book-id]
  (for [[id shelve-data] (:data (n4j/cypher-query (str "MATCH (user:User)-[:OWNS]->(shelves), (books:Book {id : \"" book-id "\"}) WHERE id(user) = " (:id (session-get :user)) " AND not((shelves)-[:STORES]->(books)) RETURN id(shelves), shelves")))]
    (assoc (:data shelve-data) :id id)))

(defn in-shelves
  "Get shelves for book"
  [book-id]
  (for [[id shelve-data] (:data (n4j/cypher-query (str "MATCH (user:User)-[:OWNS]->(shelves)-[:STORES]->(books:Book {id : \"" book-id "\"}) WHERE id(user) = " (:id (session-get :user)) " RETURN id(shelves), shelves")))]
    (assoc (:data shelve-data) :id id)))

(defn get-shelve-books
  "Get shelve books"
  [shelve-id]
  (for [[id book-data] (:data (n4j/cypher-query (str "MATCH (shelve:Shelve)-[:STORES]->(books:Book) WHERE id(shelve) = " shelve-id " RETURN id(books), books")))]
    (assoc (:data book-data) :node-id id)))

(defn get-shelve-with-books
  "Get shelve info with books"
  [shelve-id]
  {:shelve (get-shelve shelve-id)
       :books (get-shelve-books shelve-id)})
