(ns books.shelves.shelves-controller
  (:use (sandbar stateful-session))
  (:use [clojure.tools.logging :only (info error)])
  (:require [books.neo4j :as n4j]
	    [books.signin.signin-controller :as sninc]
	    [books.shelves.shelves-view :as shelvev]
	    [books.json-helper :as jsonh]))

(defn index
  "Show shelves index"
  []
  (shelvev/index))


(defn listing
  "Show shelves list"
  []
  (let [ajaxData (shelvev/listing)]
    (jsonh/output-message "OK" "List data returned" ajaxData)
  ))


(defn add
  "Add new shelve"
  []
   (let [ajaxData (shelvev/form)]
    (jsonh/output-message "OK" "Form data returned" ajaxData)
  ))


(defn edit
  "Edit existing shelve"
  [id]
  (shelvev/edit))


(defn view
  "Show books in shelve"
  [id]
   (let [ajaxData (shelvev/view {:id id})]
    (jsonh/output-message "OK" "Form data returned" ajaxData)
  ))


(defn delete
  "Remove existing shelve"
  [id]
 )
