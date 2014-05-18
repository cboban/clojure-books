(ns books.helpers.ui-helper
  (:require [domina :as dom]
            [enfocus.core :as ef]
            [enfocus.events :as events]
            [enfocus.effects :as effects]
            )
  (:require-macros [enfocus.macros :as em]))

(defn fade-out-element 
  "Fade out element"
  ([element duration]
    (em/defaction fade-out-nc [element duration]
      [(str element)] (effects/fade-out duration)
      ) 
    (fade-out-nc element duration))  
  
  ([element duration callback]
    (em/defaction  fade-out-callback
      [element duration callback]
      [(str element)] (effects/fade-out duration callback)) 
    (fade-out-callback element duration callback)) 
  )


(defn swap-app-content
  "Swap content of main content div with provided"
  [content]
  (dom/set-inner-html! (dom/by-id "mainContentDiv")  (str content))
  )


(defn show-loading-bar
  "Show loading bar"
  []
  (dom/set-style! (dom/by-id "loadingDiv") "display" "block")
  )


(defn hide-loading-bar
  "Hide loading bar"
  []
  (js/setTimeout
    (fn [] (fade-out-element "#loadingDiv" 200 (fn [] (ef/at "#loadingDiv" (ef/set-style :display "none" :opacity 0.7)))))
    100)  
  )


(defn get-input-value
  "Get input value"
  [selector]
  (ef/from selector (ef/get-prop :value)))

(defn read-form
  "Read form data"
  [selector]
  (ef/from selector (ef/read-form)))


(em/defaction remove-element
  [selector]
  selector (ef/remove-node))

(defn remove-form-errors
  "Remove all form errors from page"
  []
  (remove-element ".form-error"))


(defn append-error
  "Appends error after element which match provided selector"
  [selector errors]
    (em/defaction insert-after [selector error]
      selector (ef/after (str "<div class=\"form-error\">"error"</div>")))
    (doseq [error errors]
	   (insert-after selector error))
    
  )


  
