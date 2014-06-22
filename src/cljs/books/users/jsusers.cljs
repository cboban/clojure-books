(ns books.users.jsusers
  (:require [domina :as dom]
	    [domina.events :as evts]
	    [domina.css :as domcss]
      [goog.events :as events]
      [books.helpers.ui-helper :as uihelper]
      [books.helpers.ajax-helper :as ajaxhelper]
      [books.users.userform :as userform]
      [books.users.userview :as userview]
      [clojure.browser.net :as net]
      [clojure.browser.event :as gevent]
      [enfocus.core :as ef]
      [enfocus.events :as ev]
      [secretary.core :as secretary :include-macros true :refer [defroute]])
  (:require-macros [enfocus.macros :as em]))

(defn on-user-deleted
  "Action on user deleted"
  [content]
  (ajaxhelper/parse-json-response content
   (fn [data]
     (js/alert "User deleted")
     (get-users-list)
     )
   (fn [data]
     (js/alert "User not deleted")
     )
                                  )
  )

(defn delete-user
  "Delete user over ajax"
  [id]
  (let [ajaxUrl (str "/users/delete/" id) xhr (net/xhr-connection)]
      (gevent/listen xhr :error #(.log js/console "Error %1"))
      (gevent/listen xhr :success on-user-deleted)
      (net/transmit xhr ajaxUrl "DELETE" {:q "json"})
          (uihelper/show-loading-bar)))

(defn on-json-load 
  "Render users table from json"
  [content]
  (ajaxhelper/parse-json-response content 
     (fn [data]
       (do
	       (ef/at "table.table .template-item" 
	         (em/clone-for [user (:data data)]
				        "td.user-id" (ef/content (str (:id user)))
				        "td.user-name" (ef/content (:name user))
				        "td.user-surname" (ef/content (:surname user))
				        "td.user-email" (ef/content (:email user))
				        "td.user-actions .view-user-info" (ef/set-attr :href (str "#/users/view/" (str (:id user))))
				        "td.user-actions .edit-user" (ef/set-attr :href (str "#/users/edit/" (str (:id user))))
				        "td.user-actions .delete-user" (ef/do-> (ef/set-attr :data-user-id (:id user)) (ef/set-attr :data-user-name (:name user)))))
       
	       (ef/at "table .delete-user" (ev/listen :click 
		              #(ef/at (.-currentTarget %)
                        (let [name (ef/from (.-currentTarget %) (ef/get-attr :data-user-name))
                              id (ef/from (.-currentTarget %) (ef/get-attr :data-user-id))]
		                      (do
		                        (.preventDefault %)
		                        (if (js/confirm (str "Delete user " name "?"))
		                          (delete-user id)
		                          (true))))))))
       ))
     (fn [data]
       ((js/alert (:message data))))
)


(defn get-users 
  "Get users list json"
  []
  (let [ajaxUrl "/users/json-list" xhr (net/xhr-connection)]
      (gevent/listen xhr :error #(.log js/console "Error %1"))
      (gevent/listen xhr :success on-json-load)
      (net/transmit xhr ajaxUrl "GET" {:q "json"})
          (uihelper/show-loading-bar)))

(defn set-list-listeners
  "Set listeners after list is loaded over ajax"
  []
  (evts/listen! (dom/by-id "addNewListButton") :click (fn [evt] (userform/load-add-form)))
  (ef/at ".view-user-info" (ev/listen :click 
      (fn [evt]
      (let [id (ef/from (.-currentTarget evt) (ef/get-attr :data-id))]
        (userview/get-view id)
        (.preventDefault evt)))))
  (get-users)
  )

(defn on-list-load
 "Render users list"
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

(defn get-users-list 
  "Get users list html over AJAX"
  []
  (let [ajaxUrl "/users/listing" xhr (net/xhr-connection)]
      (gevent/listen xhr :error #(.log js/console "Error %1"))
      (gevent/listen xhr :success on-list-load)
      (net/transmit xhr ajaxUrl "GET" {:q "json"})
          (uihelper/show-loading-bar)  
))


(defn add-user
  "Show form for adding user"
  []
  (userform/load-add-form))

(defn edit-user
  "Show form for editting user"
  [user-id]
  (userform/load-add-form user-id))


