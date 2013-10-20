(ns books.signin.jssignin
  (:require [domina :as dom]
	    [domina.events :as evts]
	    [domina.css :as domcss]
      [clojure.browser.net :as net]
      [clojure.browser.event :as gevent]
      [books.signin.signin-validators :refer [login-credential-errors create-user-errors]]))


(defn prepend-errors
  "Prepend error"
  [errors]
  (do 
    (doseq [error errors]
	   (dom/prepend! (dom/by-class "alert-warning")
		         (str "<div class=\"one-validation-message\">"error"</div>")))
    (dom/set-style! (dom/by-class "alert-warning") "display" "block")))

(defn remove-errors
  "Remove and hide errors data"
  []
  (do
    (dom/destroy! (dom/by-class "one-validation-message"))
    (dom/set-style! (dom/by-class "alert") "display" "none")))



(defn validate-signin-form
  "Validate form"
  []
  (do (remove-errors)
  (if-let [errors (login-credential-errors {:username (dom/value (dom/by-id "signinUsername"))
					    :password (dom/value (dom/by-id "signinPassword"))})]
	(do (prepend-errors (:username errors))
	    (prepend-errors (:password errors))
	    false)
	true)))


(defn show-registration-form
  "Hides login form and displays registration form"
  []
  (let [registration (dom/by-class "form-registration") login (dom/by-class "form-signin")]
    (remove-errors)
    (dom/set-style! login "display" "none")
    (dom/set-style! registration "display" "block")))



(defn show-login-form
  "Hides login form and displays registration form"
  ([] (show-login-form true))
  ([rmerrors]
  (let [registration (dom/by-class "form-registration") login (dom/by-class "form-signin")]
    (if (= rmerrors true) (remove-errors))
    (dom/set-style! registration "display" "none")
    (dom/set-style! login "display" "block")
  )))



(defn validate-registration-form
 "Validate registration form"
 []
 (do (remove-errors)
 (if-let [errors (create-user-errors {:name (dom/value (dom/by-id "registrationName"))
         :surname (dom/value (dom/by-id "registrationSurname"))
         :email (dom/value (dom/by-id "registrationEmail"))
         :username (dom/value (dom/by-id "registrationUsername"))
         :password (dom/value (dom/by-id "registrationPassword"))
         :city (dom/value (dom/by-id "registrationCity"))
         :country (dom/value (dom/by-id "registrationCountry"))})]
 (do (prepend-errors (:name errors))
     (prepend-errors (:surname errors))
     (prepend-errors (:email errors))
     (prepend-errors (:username errors))
     (prepend-errors (:password errors))
     (prepend-errors (:city errors))
     (prepend-errors (:country errors))
     false)
 true)))



(defn on-registration-response
  "Show registration status message"
  [content]
    (let [data (js->clj (.getResponseJson (.-target content)) :keywordize-keys true)]
      (remove-errors)
      (if (= (:status data) "OK")
        (do
          (dom/prepend! (dom/by-id "alertInfoMessage")  (str "<div class=\"one-validation-message\">"(:message data)"</div>"))
          (dom/set-style! (dom/by-id "alertInfoMessage") "display" "block")
          (show-login-form false))
        (do
          (dom/prepend! (dom/by-id "warningInfoMessage")  (str "<div class=\"one-validation-message\">" (:message data) "</div>"))
          (dom/set-style! (dom/by-id "warningInfoMessage") "display" "block"))
      )))



(defn register-user
  "Save user XMLHttpReqest"
  []
  (let [ajaxUrl (str "/register-user?name="(dom/value (dom/by-id "registrationName"))
					    "&surname="(dom/value (dom/by-id "registrationSurname"))
					    "&email="(dom/value (dom/by-id "registrationEmail"))
					    "&username="(dom/value (dom/by-id "registrationUsername"))
					    "&password="(dom/value (dom/by-id "registrationPassword"))
					    "&city="(dom/value (dom/by-id "registrationCity"))
					    "&country="(dom/value (dom/by-id "registrationCountry"))) xhr (net/xhr-connection)]
      (gevent/listen xhr :error #(.log js/console "Error %1"))
      (gevent/listen xhr :success on-registration-response)
      (net/transmit xhr ajaxUrl "PUT" {:q "json"})
))


(defn ^:export init []
  (if (and js/document
	   (.-getElementById js/document))
    (let [login-form (dom/by-id "signinForm") registration-form (dom/by-id "registrationForm")]
	(set! (.-onsubmit login-form) validate-signin-form)
  (set! (.-onsubmit registration-form) (fn [] (if (validate-registration-form) (register-user)) false))
	
  (evts/listen! (dom/by-class "registration-link")
		       :click
		       (fn [] (show-registration-form)))
  (evts/listen! (dom/by-class "login-link")
		       :click
		       (fn [] (show-login-form)))
)))