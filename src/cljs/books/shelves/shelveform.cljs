(ns books.shelves.shelveform
  (:require [domina :as dom]
	    [domina.events :as evts]
	    [domina.css :as domcss]
      [books.helpers.ui-helper :as uihelper]
      [books.helpers.ajax-helper :as ajaxhelper]
      [clojure.browser.net :as net]
      [clojure.browser.event :as gevent]
      [books.signin.validators :as validator]
      [enfocus.core :as ef]
      [enfocus.events :as ev]))



(defn on-add-response 
  "Handle save shelve response"
  [content]
  (ajaxhelper/parse-json-response content 
     (fn [data]
       ((js/alert (:message data)
        (set! (.-location js/window) "#/shelves"))))
     
     (fn [data]
       ((js/alert (:message data))))))

(defn save-shelve
  "Save shelve through ajax"
  []
  (let [formData (uihelper/read-form "#shelveAddForm")]
    (let [ajaxUrl "/shelves" postData (str "name="(:name formData)
					    "&description="(:description formData) (if (contains? formData :id) (str "&id=" (:id formData)) (str ""))) xhr (net/xhr-connection)]
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
  (ajaxhelper/parse-json-response content 
     (fn [data]
       (do
         (uihelper/swap-app-content (str (:data data)))
         (uihelper/hide-loading-bar)
         (set-form-listeners)
       ))
     
     (fn [data]
       (do
          (dom/prepend! (dom/by-id "warningInfoMessage")  (str "<div class=\"one-validation-message\">" (:message data) "</div>"))
          (dom/set-style! (dom/by-id "warningInfoMessage") "display" "block")))))


(defn get-add-form 
  "Get shelves list html over AJAX"  
  ([shelve-id]
  (let [ajaxUrl (if(= shelve-id nil) (str "/shelves/add") (str "/shelves/edit/" shelve-id)) xhr (net/xhr-connection)]
      (gevent/listen xhr :error #(.log js/console "Error %1"))
      (gevent/listen xhr :success on-form-loaded)
      (net/transmit xhr ajaxUrl "GET" {:q "json"})
          (uihelper/show-loading-bar)))
 )


(defn load-add-form
  "Load add or edit form from server"
  ([] (get-add-form nil))
  ([shelve-id]
  (get-add-form shelve-id)))