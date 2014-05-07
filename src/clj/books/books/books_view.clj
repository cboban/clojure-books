(ns books.books.books-view
  (:use (sandbar stateful-session))
  (:require [books.neo4j :as n4j]
	    [books.html-generator :as hg]
	    [net.cgrand.enlive-html :as en]))

  (en/deftemplate search-template
    (hg/build-html-page [{:temp-sel [:div.content],
			 :comp "public/books/search.html",
			 :comp-sel [:div.books-search]}])
    [param]
   [:title] (en/content "Shelves")
   [:div.script] (en/content {:tag :script,
			     :attrs {:src "js/books.js"},
			     :content nil})
   [:div.script] (en/append {:tag :script,
			    :attrs nil,
			    :content "books.books.jsbooks.init();"})
  )
  
  (en/deftemplate view-template
	  (hg/build-html-page [{:temp-sel [:div.content],
			:comp "public/books/view.html",
			:comp-sel [:div.books-view]}])
	  [param]
	  [:title] (en/content "Shelves")
	  [:div.script] (en/content {:tag :script,
				    :attrs {:src "js/books.js"},
				    :content nil})
	  [:div.script] (en/append {:tag :script,
				   :attrs nil,
				   :content "books.books.jsbooks.init();"})
	 )
  
	
	(defn search 
	  "Renders books search page"
	  ([]
	  (hg/render (apply str (search-template "")) {:user (session-get :user)}))
	  ([var-map]
	  (hg/render (apply str (search-template "")) (merge {:user (session-get :user)} var-map ))))
	

 (defn view
	  "Show book details"
	  ([]
	  (hg/render (apply str (view-template "")) {:user (session-get :user)}))
	  ([var-map]
	  (hg/render (apply str (view-template "")) (merge {:user (session-get :user)} var-map ))))



 
  