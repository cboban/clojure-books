(ns books.home.jshome
  (:require [domina :as dom]
	    [domina.events :as evts]
	    [domina.css :as domcss]
      [goog.events :as events]
      [books.helpers.ui-helper :as uihelper]
      [books.helpers.ajax-helper :as ajaxhelper]
      [clojure.browser.net :as net]
      [clojure.browser.event :as gevent]
      [enfocus.core :as ef]
      [enfocus.events :as ev]
      [secretary.core :as secretary :include-macros true :refer [defroute]])
  (:require-macros [enfocus.macros :as em]))


(defn render-table
  [data]
  (do
       (ef/at "ul.search-results-list .template-item" 
         (em/clone-for [book (:data data)]
			        "div.title .book-title" (ef/content (:title book))
			        "div.image .book-image" (ef/set-attr :src (:image book))
			        "div.author .book-author" (ef/content (:author_name book))
			        "div.rating .book-rating" (ef/content (:rating book))))
       
       (ef/at "ul.search-results-list .template-item" (ef/set-style :display "block"))
       (ef/at "div.searching-loader" (ef/set-style :display "none"))
       ))


(defn render-search-response
  "Render search response"
  [content]
  (ajaxhelper/parse-json-response content 
     (fn [data]
       (render-table data))
     
     (fn [data]
       ((js/alert (:message data))))))

(defn clear-table
  "Clear results for new search"
  []
  (do
    (ef/at "ul.search-results-list li" (ef/set-style :display "none"))
    (ef/at "ul.search-results-list li:not(:first-child)" (ef/remove-node))))


(defn search
  "Sarch term"
  [term]
  (do
    (clear-table)
	  (ef/at "div.searching-loader" (ef/set-style :display "block"))
    (let [ajaxUrl (str "/search/" term) xhr (net/xhr-connection)]
      (gevent/listen xhr :error #(.log js/console "Error %1"))
      (gevent/listen xhr :success render-search-response)
      (net/transmit xhr ajaxUrl "GET")
      )))


(defn search-books
  "Search books"
  []
  (let [formData (uihelper/read-form "#searchForm")]
    (set! (.-location js/window) (str "#/books/" (:term formData))
 )))


(defn validate-search-form
  []
  (if (clojure.string/blank? (uihelper/get-input-value "#searchTerm"))
    (do
      (uihelper/append-error "#searchTerm" ["Please provide search term"])
      false)
    true))


(defn set-form-listeners
  "Set on submit listener"
  []
  (ef/at "#searchForm" (ev/listen :submit 
	  (fn [evt]
	    (if (validate-search-form) (search-books) (.preventDefault evt))
	   (.preventDefault evt)))))

(defn on-form-load
 "Render shelves list"
 [content]
  (ajaxhelper/parse-json-response content 
        (fn [data]
          (do
	          (uihelper/swap-app-content (str (:data data)))
	          (uihelper/hide-loading-bar)
	          (set-form-listeners)))
        (fn [data]
	        (do
	          (dom/prepend! (dom/by-id "warningInfoMessage")  (str "<div class=\"one-validation-message\">" (:message data) "</div>"))
	          (dom/set-style! (dom/by-id "warningInfoMessage") "display" "block")))))



(defn show-search-form
  "Get search form through AJAX"
  []
  (let [ajaxUrl "/home/search" xhr (net/xhr-connection)]
      (gevent/listen xhr :error #(.log js/console "Error %1"))
      (gevent/listen xhr :success on-form-load)
      (net/transmit xhr ajaxUrl "GET" {:q "json"})
          (uihelper/show-loading-bar)  
))
