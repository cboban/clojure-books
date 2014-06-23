(ns books.users.users-controller
  (:use (sandbar stateful-session))
  (:use [clojure.tools.logging :only (info error)])
  (:require [books.neo4j :as n4j]
	    [books.signin.signin-controller :as sninc]
	    [books.users.users-view :as userv]
	    [books.users.users-model :as userm]
	    [books.json-helper :as jsonh]))


(defn listing
  "Show users list"
  []
  (let [ajaxData (userv/listing)]
    (jsonh/output-message "OK" "List data returned" ajaxData)
  ))


(defn json-list
  "Get users in JSON format"
  []
  (let [ajaxData (userm/get-users)]
    (jsonh/output-message "OK" "JSON returned" ajaxData)))


(defn add
  "Add new user"
  []
   (let [ajaxData (userv/form)]
    (jsonh/output-message "OK" "Form data returned" ajaxData)
  ))


(defn edit
  "Edit existing user"
  [id]
  (let [ajaxData (userv/edit (userm/get-user id))]
    (jsonh/output-message "OK" "Form data returned" ajaxData)))


(defn view
  "Show user info"
  [id]
   (let [ajaxData (userv/view {:id id})]
    (jsonh/output-message "OK" "Form data returned" ajaxData)
  ))


(defn save
  "Save user data"
  [data]
  (if (userm/save-user data)
  (jsonh/output-message "OK" "User saved") (jsonh/output-message "ERROR" "Save failed")))


(defn delete
  "Remove existing user"
  [id]
  (if (userm/delete-user id)
  (jsonh/output-message "OK" "User deleted") (jsonh/output-message "ERROR" "User not deleted")))


(defn profile
  "Edit profile"
  []
  (let [ajxHtml (userv/edit (session-get :user))]
    (jsonh/output-message "OK" "Form data returned" {} ajxHtml)))
