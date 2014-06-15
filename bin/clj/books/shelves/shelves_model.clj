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
  (for [[id shelve-data] (:data (n4j/cypher-query (str "MATCH(user:User)-[:OWNS]->(shelves) WHERE id(user) = " (:id (session-get :user)) " RETURN id(shelves), shelves")))]
    (assoc (:data shelve-data) :id id)
  ))


(defn get-shelve
  "Get shelve by id"
  [id]
  (first (for [[id shelve-data] (:data (n4j/cypher-query (str "MATCH (shelve:Shelve) WHERE id(shelve) = " id " RETURN ID(shelve), shelve")))]
           (assoc (:data shelve-data) :id id))))
 
  