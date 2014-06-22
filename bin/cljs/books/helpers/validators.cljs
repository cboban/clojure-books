(ns books.signin.validators
  (:require [valip.core :refer [validate]]
	    [valip.predicates :refer [present? matches email-address?]]))

(defn create-shelve-form-errors
  "Validation for shelve adding form"
  [params]
  (validate params
	    [:name present? "Name can't be empty."]
      [:description present? "Description can't be empty."]))


(defn create-user-form-errors
  "Validation for user adding form"
  [params]
  (validate params
	    [:name present? "Name can't be empty."]
      [:surname present? "Surname can't be empty."]))