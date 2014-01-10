(ns books.signin.signin-controller
  (:use (sandbar stateful-session))
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
      (n4j/create-node "user" {:name (:name req-params)
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
		   email
       username
	     age
	     city
	     country]
	    (:data (n4j/cypher-query (str "start n=node("(clojure.string/join ","(n4j/get-type-indexes "user"))")
					   where n.username? = \""username"\" and
						 n.password? = \""password"\"
					   return ID(n),
						  n.name,
						  n.surname,
						  n.email,
						  n.username,
						  n.city,
						  n.country")))]
	(session-put! :id id)
	(session-put! :name name)
	(session-put! :surname surname)
	(session-put! :city city)
	(session-put! :country country))
	(session-put! :login-try 1)))


(defn is-logged-in
  "Checks if user is logged in"
  [response-fn]
  (if (= (session-get :id) nil)
      (sninv/signin)
      (do (session-pop! :login-try 1)
	  response-fn)))

(defn is-not-logged-in
  "Checks if user is logged in"
  [response-fn]
  (if (= (session-get :id) nil)
      response-fn
      (homev/home)))