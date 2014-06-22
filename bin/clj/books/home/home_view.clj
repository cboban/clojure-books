(ns books.home.home-view  
  (:use (sandbar stateful-session))
  (:require [books.neo4j :as n4j]
	    [books.html-generator :as hg]
	    [net.cgrand.enlive-html :as en]))



(en/deftemplate home-template
  (hg/build-html-page [{:temp-sel [:div.content],
			:comp "public/general/ajax.html" ,
			:comp-sel [:div.ajaxplaceholder]}])
  [param]
  [:title] (en/content "Dashboard")
  [:div.script] (en/content {:tag :script,
			     :attrs {:src "js/app.js"},
			     :content nil})
  [:li.adminonly] (en/content (if(:admin (session-get :user)) {:tag :a, :attrs {:href "/#/users"} :content "Users"} nil))
  )


(en/deftemplate search-form-template
  (hg/build-html-page "ajax" [{:temp-sel [:div.ajax-loaded-content],
			:comp "public/books/search.html" ,
			:comp-sel [:div.books-search]}])
  [param])



(defn home 
  "Renders home page"
  ([]
  (hg/render (apply str (home-template "")) {:user (session-get :user)})))


	
 (defn search-form
	  "Render form for search"
	  []
	  (hg/render (apply str (search-form-template "")) {:user (session-get :user)}))
