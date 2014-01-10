(ns books.login.jssignin-edit
  (:require [domina :as dom]
	    [domina.events :as evts]
	    [domina.css :as domcss]
            [books.signin.signin-validators :refer [login-credential-errors
									 create-user-errors]]))

(defn prepend-errors
  "Prepend error"
  [errors]
  (doseq [error errors]
	 (dom/prepend! (dom/by-id "error-msgs")
		       (str "<div class=\"help\">"error"</div>"))))

(defn check-registration-form
  "Validate form"
  []
  (do (dom/destroy! (dom/by-class "help"))
  (if-let [errors (create-user-errors {:name (dom/value (dom/by-id "name"))
					:surname (dom/value (dom/by-id "surname"))
					:email (dom/value (dom/by-id "email"))
					:username (dom/value (dom/by-id "username"))
					:password (dom/value (dom/by-id "password"))
					:city (dom/value (dom/by-id "city"))
					:country (dom/value (dom/by-id "country"))})]
	(do (prepend-errors (:name errors))
	    (prepend-errors (:surname errors))
	    (prepend-errors (:email errors))
	    (prepend-errors (:username errors))
	    (prepend-errors (:password errors))
	    (prepend-errors (:city errors))
	    (prepend-errors (:country errors))
	    false)
	true)))

(defn ^:export init []
  (if (and js/document
	   (.-getElementById js/document))
    (let [register-form (dom/by-id "register-form")]
	(set! (.-onsubmit register-form) check-registration-form)
)))