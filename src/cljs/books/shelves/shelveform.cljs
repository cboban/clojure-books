(ns books.shelves.shelveform
  (:require [domina :as dom]
	    [domina.events :as evts]
	    [domina.css :as domcss]
      [books.helpers.ui-helper :as uihelper]
      [clojure.browser.net :as net]
      [clojure.browser.event :as gevent]))

(defn set-form-listeners
  []
  )

(defn on-form-loaded
  "Render add form"
  [content]
 (let [data (js->clj (.getResponseJson (.-target content)) :keywordize-keys true)]
   (if (= (:status data) "OK")
        (do
          (uihelper/swap-app-content (str (:data data)))
          (uihelper/hide-loading-bar)
          (set-form-listeners)
        )
        (do
          (dom/prepend! (dom/by-id "warningInfoMessage")  (str "<div class=\"one-validation-message\">" (:message data) "</div>"))
          (dom/set-style! (dom/by-id "warningInfoMessage") "display" "block"))
      )
)
  )

(defn get-add-form 
  "Get shelves list html over AJAX"
  []
  (let [ajaxUrl "/shelves/add" xhr (net/xhr-connection)]
      (gevent/listen xhr :error #(.log js/console "Error %1"))
      (gevent/listen xhr :success on-form-loaded)
      (net/transmit xhr ajaxUrl "GET" {:q "json"})
          (uihelper/show-loading-bar)  
))

(defn load-add-form
  "Load add form from server"
  []
  (get-add-form)
  )