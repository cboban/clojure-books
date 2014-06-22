(ns books.home.home-controller
  (:use (sandbar stateful-session))
  (:use [clojure.tools.logging :only (info error)])
  (:require [books.neo4j :as n4j]
	    [books.home.home-view :as homev]
	    [books.json-helper :as jsonh]))


(defn home
  "Show home"
  []
  (homev/home)
  )


(defn search-form
  "Show search form"
  []
  (let [ajaxData (homev/search-form)]
    (jsonh/output-message "OK" "Form data returned" ajaxData)
  ))
