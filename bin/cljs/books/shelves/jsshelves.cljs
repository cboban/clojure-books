(ns books.shelves.jsshelves
  (:require [domina :as dom]
	    [domina.events :as evts]
	    [domina.css :as domcss]
      [books.helpers.ui-helper :as uihelper]
      [books.shelves.shelveform :as shelveform]
      [books.shelves.shelveview :as shelveview]
      [clojure.browser.net :as net]
      [clojure.browser.event :as gevent]
      [enfocus.core :as ef]
      [enfocus.events :as ev]
      )
  (:require-macros [enfocus.macros :as em]))


(defn set-list-listeners
  "Set listeners after list is loaded over ajax"
  []
  (evts/listen! (dom/by-id "addNewListButton") :click (fn [evt] (shelveform/load-add-form)))
  (ef/at ".view-shelve-info" (ev/listen :click 
      (fn [evt]
      (let [id (ef/from (.-currentTarget evt) (ef/get-attr :data-id))]
        (shelveview/get-view id)
        (.preventDefault evt)
)))))

(defn on-list-load
 "Render shelves list"
 [content]
 (let [data (js->clj (.getResponseJson (.-target content)) :keywordize-keys true)]
   (if (= (:status data) "OK")
        (do
          (uihelper/swap-app-content (str (:data data)))
          (uihelper/hide-loading-bar)
          (set-list-listeners)
        )
        (do
          (dom/prepend! (dom/by-id "warningInfoMessage")  (str "<div class=\"one-validation-message\">" (:message data) "</div>"))
          (dom/set-style! (dom/by-id "warningInfoMessage") "display" "block"))
      )
))

(defn get-shelves-list 
  "Get shelves list html over AJAX"
  []
  (let [ajaxUrl "/shelves/listing" xhr (net/xhr-connection)]
      (gevent/listen xhr :error #(.log js/console "Error %1"))
      (gevent/listen xhr :success on-list-load)
      (net/transmit xhr ajaxUrl "GET" {:q "json"})
          (uihelper/show-loading-bar)  
))


(defn ^:export init []
  (if (and js/document
	   (.-getElementById js/document))
    (get-shelves-list)
 ))
