(ns books.books.books-model
  (:use (sandbar stateful-session))
  (:require [books.neo4j :as n4j]
            [books.goodread.goodread-parser :as gparser]))



(defn search
  "Search books based on term"
  [term]
  (gparser/search term))
