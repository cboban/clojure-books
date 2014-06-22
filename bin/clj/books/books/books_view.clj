(ns books.books.books-view
  (:use (sandbar stateful-session))
  (:require [books.neo4j :as n4j]
	    [books.html-generator :as hg]
	    [net.cgrand.enlive-html :as en]))

  (en/deftemplate details-template
    (hg/build-html-page "ajax" [{:temp-sel [:div.ajax-loaded-content],
			 :comp "public/books/view.html",
			 :comp-sel [:div.books-view]}])
    [param])
 

 (defn get-details-view
	  "Show book details"
	  []
	  (hg/render (apply str (details-template "")) {:user (session-get :user)}))



 
  