(ns books.shelves.shelveview
  (:require [domina :as dom]
	    [domina.events :as evts]
	    [domina.css :as domcss]
      [books.helpers.ui-helper :as uihelper]
      [books.helpers.ajax-helper :as ajaxhelper]
      [books.home.jshome :as jshome]
      [clojure.browser.net :as net]
      [enfocus.core :as ef]
      [enfocus.events :as ev]
      [clojure.browser.event :as gevent])
  (:require-macros [enfocus.macros :as em]))


(defn remove-book-from-shelve
  "Remove book from this shelve"
  [book-id shelve-id]
  (jshome/remove-book-from-shelve book-id shelve-id))

(defn render-view
  "Render shelve view"
  [data]
  (do
    (ef/at ".shelves-view .shelve-name" (ef/content (str "Shelve: " (:name (:shelve data)))))
    (ef/at ".shelves-view .shelve-description" (ef/content (:description (:shelve data))))
    (ef/at ".shelves-view .shelve-books .shelve-book-template" 
         (em/clone-for [book (:books data)]
            "div.image img" (ef/set-attr :src (:image_url book))
            "div.rating .rating-value" (ef/content (:average_rating book))
            "div.isbn .isbn-value" (ef/content (:isbn book))
            "a.remove-from-shelve" (ev/listen :click 
													           #(ef/at (.-currentTarget %)
													                 (let [shelve-id (:id (:shelve data))
													                       shelve-name (:name (:shelve data))
													                       id (:id book)]
													                   (do
													                     (if (js/confirm (str "Remove book \"" (:title book) "\" from shelve \"" (clojure.string/trim shelve-name) "\" ?"))
													                       (remove-book-from-shelve id shelve-id)
													                       false)
													                   ))))
            "div.title a" (ef/do->
                            (ef/content (:title book))
                            (ef/set-attr :href (str "/#/book/" (:id book))))))
    (if (= (count (:books data)) 0) (ef/at ".shelve-books-holder" (ef/set-attr :display :none)))))



(defn on-view-loaded
  "Render shelve details"
  [content]
  (ajaxhelper/parse-json-response content 
   (fn [data]   
     (do
       (uihelper/swap-app-content (str (:html data)))
       (uihelper/hide-loading-bar)
       (render-view (:data data))))
   
   (fn [data]
      (do
        (dom/prepend! (dom/by-id "warningInfoMessage")  (str "<div class=\"one-validation-message\">" (:message data) "</div>"))
        (dom/set-style! (dom/by-id "warningInfoMessage") "display" "block")))
))

(defn show-shelve 
  "Get shelve details over AJAX"
  [id]
  (let [ajaxUrl (str "/shelves/view/" id) xhr (net/xhr-connection)]
      (gevent/listen xhr :error #(.log js/console "Error %1"))
      (gevent/listen xhr :success on-view-loaded)
      (net/transmit xhr ajaxUrl "GET" {:q "json"})
          (uihelper/show-loading-bar)  
))
