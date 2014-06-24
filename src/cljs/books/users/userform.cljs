(ns books.users.userform
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
  "Handle save user response"
  [content]
  (ajaxhelper/parse-json-response content 
     (fn [data]
       ((js/alert (:message data)
        (if (= (:profile (:data data)) true)
          (set! (.-location js/window) "/#/")
          (set! (.-location js/window) "#/users")))))
     
     (fn [data]
       ((js/alert (:message data))))))

(defn save-user
  "Save user through ajax"
  []
  (let [formData (uihelper/read-form "#userAddForm")]
    (.log js/console formData)
    (let [ajaxUrl "/users" postData (str 
              "name="(:name formData)
					    "&surname="(:surname formData)
					    "&city="(:city formData)
					    "&country="(:country formData)
					    "&email="(:email formData)
					    "&username="(:username formData)
					    "&password="(:password formData) 
              (if (contains? formData :id) (str "&id=" (:id formData)) (str ""))
              
              ) xhr (net/xhr-connection)]
      (gevent/listen xhr :error #(.log js/console "Error %1"))
      (gevent/listen xhr :success on-add-response)
      (net/transmit xhr ajaxUrl "POST" postData)
      )
 ))
  

(defn validate-add-form
  "Validate add form"
  []
	 (do (uihelper/remove-form-errors)
	 (if-let [errors (validator/create-user-form-errors {                                    
	         :name (uihelper/get-input-value "#userName")
	         :surname (uihelper/get-input-value "#userSurname")
	         :city (uihelper/get-input-value "#userCity")
	         :country (uihelper/get-input-value "#userCountry")
	         :email (uihelper/get-input-value "#userEmail")
	         :username (uihelper/get-input-value "#userUsername")
	         :password (uihelper/get-input-value "#userPassword")})]
	 (do 
	   (uihelper/append-error "#userName" (:name errors))
	   (uihelper/append-error "#userSurname" (:surname errors))
	   (uihelper/append-error "#userCity" (:city errors))
	   (uihelper/append-error "#userCountry" (:country errors))
	   (uihelper/append-error "#userEmail" (:email errors))
	   (uihelper/append-error "#userUsername" (:username errors))
	   (uihelper/append-error "#userPassword" (:password errors))
	     false)
  true)))


(defn set-form-listeners
  "Set listeners for form view"
  []
  (ef/at "#userAddForm" (ev/listen :submit 
	  (fn [evt]
	    (if (validate-add-form) (save-user) (.preventDefault evt))
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
  "Get users list html over AJAX"  
  ([user-id]
  (let [ajaxUrl (if(= user-id nil) (str "/users/add") (str "/users/edit/" user-id)) xhr (net/xhr-connection)]
      (gevent/listen xhr :error #(.log js/console "Error %1"))
      (gevent/listen xhr :success on-form-loaded)
      (net/transmit xhr ajaxUrl "GET" {:q "json"})
          (uihelper/show-loading-bar)))
 )


(defn load-add-form
  "Load add or edit form from server"
  ([] (get-add-form nil))
  ([user-id]
  (get-add-form user-id)))