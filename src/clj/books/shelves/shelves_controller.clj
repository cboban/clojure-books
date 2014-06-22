(ns books.shelves.shelves-controller
  (:use (sandbar stateful-session))
  (:use [clojure.tools.logging :only (info error)])
  (:require [books.neo4j :as n4j]
	    [books.signin.signin-controller :as sninc]
	    [books.shelves.shelves-view :as shelvev]
	    [books.shelves.shelves-model :as shelvem]
	    [books.json-helper :as jsonh]))


(defn listing
  "Show shelves list"
  []
  (let [ajaxData (shelvev/listing)]
    (jsonh/output-message "OK" "List data returned" ajaxData)
  ))


(defn json-list
  "Get shelves in JSON format"
  []
  (let [ajaxData (shelvem/get-user-shelves)]
    (jsonh/output-message "OK" "JSON returned" ajaxData)))


(defn add
  "Add new shelve"
  []
   (let [ajaxData (shelvev/form)]
    (jsonh/output-message "OK" "Form data returned" ajaxData)
  ))


(defn edit
  "Edit existing shelve"
  [id]
  (let [ajaxData (shelvev/edit (shelvem/get-shelve id))]
    (jsonh/output-message "OK" "Form data returned" ajaxData)))


(defn view
  "Show books in shelve"
  [id]
   (let [ajaxHtml (shelvev/view)
         ajaxData (shelvem/get-shelve-with-books id)]
    (jsonh/output-message "OK" "Form data returned" ajaxData ajaxHtml)
  ))


(defn save
  "Save shelve data"
  [data]
  (if (shelvem/save-shelve data)
  (jsonh/output-message "OK" "Shelve saved") (jsonh/output-message "ERROR" "Save failed")))


(defn delete
  "Remove existing shelve"
  [id]
  (if (shelvem/delete-shelve id)
  (jsonh/output-message "OK" "Shelve deleted") (jsonh/output-message "ERROR" "Shelve not deleted")))

