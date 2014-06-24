(ns books.shelves.jsshelves
  (:require [domina :as dom]
	    [domina.events :as evts]
	    [domina.css :as domcss]
      [goog.events :as events]
      [books.helpers.ui-helper :as uihelper]
      [books.helpers.ajax-helper :as ajaxhelper]
      [books.shelves.shelveform :as shelveform]
      [books.shelves.shelveview :as shelveview]
      [clojure.browser.net :as net]
      [clojure.browser.event :as gevent]
      [enfocus.core :as ef]
      [enfocus.events :as ev]
      [secretary.core :as secretary :include-macros true :refer [defroute]])
  (:require-macros [enfocus.macros :as em]))

(defn on-shelve-deleted
  "Action on shelve deleted"
  [content]
  (ajaxhelper/parse-json-response content
   (fn [data]
     (js/alert "Shelve deleted")
     (get-shelves-list)
     )
   (fn [data]
     (js/alert "Shelve not deleted")
     )
                                  )
  )

(defn delete-shelve
  "Delete shelve over ajax"
  [id]
  (let [ajaxUrl (str "/shelves/delete/" id) xhr (net/xhr-connection)]
      (gevent/listen xhr :error #(.log js/console "Error %1"))
      (gevent/listen xhr :success on-shelve-deleted)
      (net/transmit xhr ajaxUrl "DELETE" {:q "json"})
          (uihelper/show-loading-bar)))

(defn on-json-load 
  "Render shelve table from json"
  [content]
  (ajaxhelper/parse-json-response content 
     (fn [data]
       (do
	       (ef/at "table.table .template-item" 
	         (em/clone-for [shelve (:data data)]
				        "td.shelve-name a" (ef/do->
                                     (ef/content (:name shelve))
                                     (ef/set-attr :href (str "#/shelves/view/" (str (:id shelve)))))
				        "td.shelve-id" (ef/content (str (:id shelve)))
				        "td.shelve-count" (ef/content (str (:count shelve)))
				        "td.shelve-actions .view-shelve-info" (ef/set-attr :href (str "#/shelves/view/" (str (:id shelve))))
				        "td.shelve-actions .edit-shelve" (ef/set-attr :href (str "#/shelves/edit/" (str (:id shelve))))
				        "td.shelve-actions .delete-shelve" (ef/do-> (ef/set-attr :data-shelve-id (:id shelve)) (ef/set-attr :data-shelve-name (:name shelve)))))
       
	       (ef/at "table .delete-shelve" (ev/listen :click 
		              #(ef/at (.-currentTarget %)
                        (let [name (ef/from (.-currentTarget %) (ef/get-attr :data-shelve-name))
                              id (ef/from (.-currentTarget %) (ef/get-attr :data-shelve-id))]
		                      (do
		                        (.preventDefault %)
		                        (if (js/confirm (str "Delete shelve " name "?"))
		                          (delete-shelve id)
		                          (true))))))))
       ))
     (fn [data]
       ((js/alert (:message data))))
)


(defn get-shelves 
  "Get shelves list json"
  []
  (let [ajaxUrl "/shelves/json-list" xhr (net/xhr-connection)]
      (gevent/listen xhr :error #(.log js/console "Error %1"))
      (gevent/listen xhr :success on-json-load)
      (net/transmit xhr ajaxUrl "GET" {:q "json"})
          (uihelper/show-loading-bar)))

(defn set-list-listeners
  "Set listeners after list is loaded over ajax"
  []
  (evts/listen! (dom/by-id "addNewListButton") :click (fn [evt] (shelveform/load-add-form)))
  (ef/at ".view-shelve-info" (ev/listen :click 
      (fn [evt]
      (let [id (ef/from (.-currentTarget evt) (ef/get-attr :data-id))]
        (shelveview/get-view id)
        (.preventDefault evt)))))
  (get-shelves)
  )

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


(defn add-shelve
  "Show form for adding shelve"
  []
  (shelveform/load-add-form))

(defn edit-shelve
  "Show form for editting shelve"
  [shelve-id]
  (shelveform/load-add-form shelve-id))

(defn view-shelve
  "Show shelve info"
  [shelve-id]
  (shelveview/show-shelve shelve-id))


