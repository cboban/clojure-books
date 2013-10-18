(ns books.signin.jssignin
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

(defn validate-form
  "Validate form"
  []
  (do (dom/destroy! (dom/by-class "help"))
  (if-let [errors (login-credential-errors {:username (dom/value (dom/by-id "username"))
					    :password (dom/value (dom/by-id "password"))})]
	(do (prepend-errors (:username errors))
	    (prepend-errors (:password errors))
	    false)
	true)))

	(defn validate-regiser-form
  "Validate form"
  []
  (do (dom/destroy! (dom/by-class "help"))
  (if-let [errors (create-user-errors {:name (dom/value (dom/by-id "name"))
          :surname (dom/value (dom/by-id "surname"))
          :email (dom/value (dom/by-id "email"))
          :username (dom/value (dom/by-id "username-reg"))
          :password (dom/value (dom/by-id "password-reg"))
          :city (dom/value (dom/by-id "city"))
          :country (dom/value (dom/by-id "country"))})]
  (do (prepend-errors (:name errors))
      (prepend-errors (:surname errors))
      (prepend-errors (:email errors))
      (prepend-errors (:username-reg errors))
      (prepend-errors (:password-reg errors))
      (prepend-errors (:city errors))
      (prepend-errors (:country errors))
      false)
  true)))

(defn onready
  "Swap updated content from response with current"
  [content]
  (if (and (= (aget (aget content "currentTarget") "readyState") 4)
	   (= (aget (aget content "currentTarget") "status") 200))
      (do (dom/destroy! (dom/by-class "help"))
	  (dom/append! (dom/by-id "error-msgs")
		       (str "<div class=\"help\">"(aget (aget content "currentTarget") "responseText")"</div>")))
))

(defn save-user
  "Save user XMLHttpReqest"
  []
  (let [xmlhttp (js/XMLHttpRequest.)]
	(aset xmlhttp "onreadystatechange" onready)
	(.open xmlhttp "PUT" (str "/save-user?name="(dom/value (dom/by-id "name"))
					    "&surname="(dom/value (dom/by-id "surname"))
					    "&email="(dom/value (dom/by-id "email"))
					    "&username="(dom/value (dom/by-id "username"))
					    "&password="(dom/value (dom/by-id "password"))
					    "&city="(dom/value (dom/by-id "city"))
					    "&country="(dom/value (dom/by-id "country"))
) true)
	(.send xmlhttp)
))

(defn hide-register-pop-up
  ""
  []
  (let [selector (dom/by-class "register")]
	(if (= (re-find #"block" (dom/style selector "display")) "block")
	    (dom/set-style! selector "display" "none"))))

(defn ^:export init []
  (if (and js/document
	   (.-getElementById js/document))
    (let [login-form (dom/by-id "login-form")]
	(set! (.-onsubmit login-form) validate-form)
	(evts/listen! (dom/by-id "register")
		      :click
		      (fn []
			  (let [selector (dom/by-class "register")]
				(if (= (re-find #"none" (dom/style selector "display")) "none")
				    (dom/set-style! selector "display" "block")))))
	(evts/listen! (dom/by-id "exit")
		      :click
		      (fn [] (hide-register-pop-up)))
	(evts/listen! (dom/by-id "register-btn")
        :click
        (fn [] (do (hide-register-pop-up)
     (if (validate-regiser-form)
         (save-user))
         )))
)))