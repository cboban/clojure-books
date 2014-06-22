(ns books.books.books-controller
  (:use (sandbar stateful-session))
  (:use [clojure.tools.logging :only (info error)])
  (:require [books.neo4j :as n4j]
	    [books.signin.signin-controller :as sninc]
	    [books.books.books-view :as bookv]
	    [books.books.books-model :as bookm]
	    [books.json-helper :as jsonh]))

(defn search
  "Show books list"
  [term]
  (let [ajaxData (bookm/search term)]
    (jsonh/output-message "OK" "List data returned" ajaxData)
  ))

(defn details
  "Show book details"
  [id]
  (let [ajaxData (bookm/get-book id)
        ajaxHtml (bookv/get-details-view)]
    (jsonh/output-message "OK" "Form data returned" ajaxData ajaxHtml)))

(defn similar
  "Get similar books"
  [id]
  (let [ajaxData (bookm/get-similar id)]
    (jsonh/output-message "OK" "Form data returned" ajaxData)))


(defn delete
  "Remove existing shelve"
  [id])

(defn add-book
  "Add book to shelve"
  [request]
  (if (bookm/add-book (str (:book-id request)) (:shelve-id request))
  (jsonh/output-message "OK" "Book added to shelve") (jsonh/output-message "ERROR" "Book not added to shelve")))


(defn remove-book
  "Remove book from shelve"
  [request]
  (if (bookm/remove-book (str (:book-id request)) (:shelve-id request))
  (jsonh/output-message "OK" "Book removed from shelve") (jsonh/output-message "ERROR" "Book not removed from shelve")))
