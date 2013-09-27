(ns books.login
  (:use [domina :only [by-id value]]))

;; define the function to be attached to form submission event
(defn validate-form []
  ;; get email and password element from their ids in the HTML form
  (let [email (by-id "email")
        password (by-id "password")]
    (if (and (> (count (value email)) 0)
             (> (count (value password)) 0))
      true
      (do (js/alert "Please, complete the form fieldas!")
          false))))

;; define the function to attach validate-form to onsubmit event of
;; the form
(defn ^:export init []
  ;; verify that js/document exists and that it has a getElementById
  ;; property
  (if (and js/document
           (.-getElementById js/document))
    ;; get loginForm by element id and set its onsubmit property to
    ;; our validate-form function
    (let [login-form (.getElementById js/document "loginForm")]
      (set! (.-onsubmit login-form) validate-form))))
