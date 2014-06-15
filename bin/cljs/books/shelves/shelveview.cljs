(ns books.shelves.shelveview
  (:require [domina :as dom]
	    [domina.events :as evts]
	    [domina.css :as domcss]
      [books.helpers.ui-helper :as uihelper]
      [books.helpers.ajax-helper :as ajaxhelper]
      [clojure.browser.net :as net]
      [clojure.browser.event :as gevent]))

(defn set-view-listeners
  []
  )

(defn on-view-loaded
  "Render shelve details"
  [content]
  (ajaxhelper/parse-json-reponse content 
   (fn [data]   
     (do
       (uihelper/swap-app-content (str (:data data)))
       (uihelper/hide-loading-bar)
       (set-view-listeners)
     )
    )
   
   (fn [data]
      (do
        (dom/prepend! (dom/by-id "warningInfoMessage")  (str "<div class=\"one-validation-message\">" (:message data) "</div>"))
        (dom/set-style! (dom/by-id "warningInfoMessage") "display" "block"))
    )
))

(defn get-view 
  "Get shelve details over AJAX"
  [id]
  (let [ajaxUrl (str "/shelves/view/"id) xhr (net/xhr-connection)]
      (gevent/listen xhr :error #(.log js/console "Error %1"))
      (gevent/listen xhr :success on-view-loaded)
      (net/transmit xhr ajaxUrl "GET" {:q "json"})
          (uihelper/show-loading-bar)  
))
