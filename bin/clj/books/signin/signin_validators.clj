(ns books.signin.signin-validators
  (:require [valip.core :refer [validate]]
	    [valip.predicates :refer [present? matches email-address?]]))

(defn login-credential-errors
  "Credentials for signin form"
  [params]
  (validate params
	    [:username present? "Username can't be empty."]
	    [:password present? "Password can't be empty."]))

(defn create-user-errors
  "Credentials for login form"
  [params]
  (validate params
	    [:name present? "Name can't be empty."]
	    [:surname present? "Surname can't be empty."]
	    [:email present? "Email can't be empty."]
	    [:email email-address? "Email not in valid format."]
	    [:password present? "Password can't be empty."]
      [:username present? "Username can't be empty."]))