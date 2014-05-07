(ns books.shelves.shelves-controller
  (:use (sandbar stateful-session))
  (:use [clojure.tools.logging :only (info error)])
  (:require [books.neo4j :as n4j]
	    [books.signin.signin-controller :as sninc]
	    [books.shelves.shelves-view :as shelvev]
	    [books.json-helper :as jsonh]))

(defn list
  "Show shelves list"
  []
  (shelvev/index))


(defn add
  "Add new shelve"
  []
  (shelvev/add))


(defn edit
  "Edit existing shelve"
  [id]
  (shelvev/edit))


(defn view
  "Show books in shelve"
  [id]
  (shelvev/view))


(defn delete
  "Remove existing shelve"
  [id]
 )
