(ns books.routing.router
  (:require [secretary.core :as secretary :include-macros true :refer [defroute]]
            [goog.events :as events]
            [books.shelves.jsshelves :as jsshelves])
  (:require-macros [enfocus.macros :as em])
  (:import goog.History
           goog.History.EventType))
 
(def history (History.))

(defroute "/" []
 (jsshelves/get-shelves-list))

(defroute "/shelves" []
 (jsshelves/get-shelves-list))

(defroute "/shelves/add" []
  (jsshelves/add-shelve))

(defroute "/shelves/edit/:shelve" [shelve]
  (jsshelves/edit-shelve shelve))
 
(doto history
  (goog.events/listen
		    EventType/NAVIGATE 
		    #(em/wait-for-load (secretary/dispatch! (.-token %))))
  (.setEnabled true))
