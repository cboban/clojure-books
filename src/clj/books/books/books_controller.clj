(ns books.books.books-controller
  (:use (sandbar stateful-session))
  (:use [clojure.tools.logging :only (info error)])
  (:require [books.neo4j :as n4j]
	    [books.signin.signin-controller :as sninc]
	    [books.books.books-view :as bookv]
	    [books.json-helper :as jsonh]))

(defn search
  "Show books list"
  []
  (bookv/search))


(defn view
  "Show book details"
  [id]
  (bookv/view))


(defn delete
  "Remove existing shelve"
  [id]
 )
