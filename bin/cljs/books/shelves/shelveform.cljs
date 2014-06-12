(ns books.shelves.shelveform
  (:require [domina :as dom]
	    [domina.events :as evts]
	    [domina.css :as domcss]
      [books.helpers.ui-helper :as uihelper]
      [clojure.browser.net :as net]
      [clojure.browser.event :as gevent]
      [books.signin.validators :as validator]
      [enfocus.core :as ef]
      [enfocus.events :as ev]))



(defn on-add-response 
  "After ajax response"
  [response]
  (.log js/console response)
  )

(defn save-shelve
  "Save shelve through ajax"
  []
  (let [formData (uihelper/read-form "#shelveAddForm")]
    (let [ajaxUrl "/shelves" postData (str "name="(:name formData)
					    "&description="(:description formData)) xhr (net/xhr-connection)]
      (gevent/listen xhr :error #(.log js/console "Error %1"))
      (gevent/listen xhr :success on-add-response)
      (net/transmit xhr ajaxUrl "POST" postData)
      )
 ))
  

(defn validate-add-form
  "Validate add form"
  []
	 (do (uihelper/remove-form-errors)
	 (if-let [errors (validator/create-shelve-form-errors {                                    
	         :name (uihelper/get-input-value "#shelveName")
	         :description (uihelper/get-input-value "#shelveDescription")})]
	 (do 
	   (uihelper/append-error "#shelveName" (:name errors))
	   (uihelper/append-error "#shelveDescription" (:description errors))
	     false)
  true)))


(defn set-form-listeners
  "Set listeners for form view"
  []
  (ef/at "#shelveAddForm" (ev/listen :submit 
	  (fn [evt]
	    (if (validate-add-form) (save-shelve) (.preventDefault evt))
	   (.preventDefault evt)))))


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
          (dom/set-style! (dom/by-id "warningInfoMessage") "display" "block")))
))


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
  (get-add-form))