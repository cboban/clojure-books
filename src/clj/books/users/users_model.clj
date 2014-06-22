(ns books.users.users-model
  (:use (sandbar stateful-session))
  (:use [clojure.tools.logging :only (info error)])
  (:require [books.neo4j :as n4j]))



(defn save-user
  "Save new user"
  [data]
  (if (contains? data :id)
    (let [user (n4j/read-node (Integer/parseInt (:id data)))]
      (n4j/update-node user data))
    
    (n4j/create-node "User" {
           :name (:name data)
		       :surname (:surname data)
		       :city (:city data)
		       :country (:country data)
		       :email (:email data)
		       :username (:username data)
		       :password (:password data)})
  ))


(defn get-users
  "Get all users"
  []
  (for [[id user-data] (:data (n4j/cypher-query (str "MATCH(user:User) WHERE NOT id(user) = " (:id (session-get :user)) " RETURN id(user), user")))]
    (assoc (:data user-data) :id id)
  ))


(defn get-user
  "Get user by id"
  [id]
  (first (for [[id user-data] (:data (n4j/cypher-query (str "MATCH (user:User) WHERE id(user) = " id " RETURN ID(user), user")))]
           (assoc (:data user-data) :id id))))

(defn delete-user
  "Delete user by id"
  [id]
  (let [user (get-user (Integer/parseInt id))]
    (if (not= user nil)
      (n4j/delete-node (Integer/parseInt id))
      (false)
    )))
 
  