(ns books.signin.signin-controller
  (:use (sandbar stateful-session))
  (:use [clojure.tools.logging :only (info error)])
  (:require [books.neo4j :as n4j]
	    [books.signin.signin-view :as sninv]
	    [books.home.home-view :as homev]
	    [books.json-helper :as jsonh]
	    [books.signin.signin-validators :refer [create-user-errors]]))

(defn register-new-user
  "Save newly registered user"
  [req-params]
  (if-let [user-errors (create-user-errors {:name (:name req-params)
					    :surname (:surname req-params)
					    :email (:email req-params)
					    :username (:username req-params)
					    :password (:password req-params)
					    :city (:city req-params)
					    :country (:country req-params)})]
    (println (str "user errors: " user-errors))
    (do 
      (n4j/create-node "User" {:name (:name req-params)
			       :surname (:surname req-params)
			       :email (:email req-params)
			       :username (:username req-params)
			       :password (:password req-params)
			       :city (:city req-params)
			       :country (:country req-params)})
      (jsonh/output-message "OK" "User registered")
      )))

(defn update-user
  "Update user in neo4j database"
  [req-params]
  (if-let [user-errors (create-user-errors {
              :name (:name req-params)
					    :surname (:surname req-params)
					    :email (:email req-params)
					    :username (:username req-params)
					    :password (:password req-params)
					    :city (:city req-params)
					    :country (:country req-params)})]
    (println (str "user errors: " user-errors))
    (let [node (n4j/read-node (session-get :id))]
      (n4j/update-node node
		       {:name (:name req-params)
			:surname (:surname req-params)
			:email (:email req-params)
			:username (:username req-params)
			:password (:password req-params)
			:age (:age req-params)
			:city (:city req-params)
			:country (:country req-params)
			:gender (:gender req-params)}))))

(defn delete-user
  "Delete user from neo4j database"
  [id]
  (n4j/delete-node "user" id))


(defn authenticate-user
  "Authenticate user if exists in database"
  [req-params]

  (let [username (:username req-params)
        password (:password req-params)]
    (doseq [[id
             name
             surname
             username
             password
             email
             city
             country]
  (:data (n4j/cypher-query (str "MATCH (u:User {username: \""(str username)"\", password: \""(str password)"\"}) 
						RETURN ID(u), 
						u.name, 
						u.surname, 
						u.username, 
						u.password, 
						u.city, 
						u.country")))]
  (session-put! :user {:id id :name name :surname surname :city city :username username :country country}))
    (if (= (session-get :user) nil)
      (flash-put! :session-message-warning "Wrong username or password"))
	(session-put! :login-try 1)))


(defn is-logged-in
  "Checks if user is logged in"
  [response-fn]
  (if (= (session-get :user) nil)
      (sninv/signin "Please login")
      (do (session-pop! :login-try 1)
	  response-fn)))

(defn is-not-logged-in
  "Checks if user is logged in"
  [response-fn]
  (if (= (session-get :user) nil)
      response-fn
      (homev/home)))