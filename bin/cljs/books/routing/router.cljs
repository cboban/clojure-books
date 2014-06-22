(ns books.routing.router
  (:require [secretary.core :as secretary :include-macros true :refer [defroute]]
            [goog.events :as events]
            [books.shelves.jsshelves :as jsshelves]
            [books.users.jsusers :as jsusers]
            [books.home.jshome :as jshome])
  (:require-macros [enfocus.macros :as em])
  (:import goog.History
           goog.History.EventType))
 
(def history (History.))

(defroute "/" []
 (jshome/show-search-form))

(defroute "/shelves" []
 (jsshelves/get-shelves-list))

(defroute "/shelves/add" []
  (jsshelves/add-shelve))

(defroute "/shelves/edit/:shelve" [shelve]
  (jsshelves/edit-shelve shelve))

(defroute "/shelves/view/:shelve" [shelve]
  (jsshelves/view-shelve shelve))

(defroute "/users" []
 (jsusers/get-users-list))

(defroute "/users/add" []
  (jsusers/add-user))

(defroute "/users/edit/:user" [user]
  (jsusers/edit-user user))


(defroute "/books"
  []
  (jshome/show-search-form))

(defroute "/books/:term"
  [term]
  (jshome/search term))

(defroute "/book/:id"
  [id]
  (jshome/show-details id))
 
(doto history
  (goog.events/listen
		    EventType/NAVIGATE 
		    #(em/wait-for-load (secretary/dispatch! (.-token %))))
  (.setEnabled true))
