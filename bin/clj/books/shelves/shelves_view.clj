(ns books.shelves.shelves-view
  (:use (sandbar stateful-session))
  (:use [clojure.tools.logging :only (info error)])
  (:require [books.neo4j :as n4j]
	    [books.html-generator :as hg]
	    [net.cgrand.enlive-html :as en]))

  (en/deftemplate list-template
    (hg/build-html-page "ajax" [{:temp-sel [:div.ajax-loaded-content],
			 :comp "public/shelves/list.html",
			 :comp-sel [:div.shelves-list]}])
    [param]
  )
  
  (en/deftemplate form-template
	  (hg/build-html-page "ajax" [{:temp-sel [:div.ajax-loaded-content],
			:comp "public/shelves/add.html",
			:comp-sel [:div.shelves-add]}])
    [param]
	 )
  
  (en/deftemplate edit-template
	  (hg/build-html-page "ajax" [{:temp-sel [:div.ajax-loaded-content],
			:comp "public/shelves/edit.html",
			:comp-sel [:div.shelves-edit]}])
	  [param]
	)

  
  (en/deftemplate view-template
	  (hg/build-html-page "ajax" [{:temp-sel [:div.ajax-loaded-content],
			:comp "public/shelves/view.html",
			:comp-sel [:div.shelves-view]}])
	  [param]
	 )

	(defn listing 
	  "Renders list table over ajax"
	  ([]
	  (hg/render (apply str (list-template "")) {:user (session-get :user)})))
	
	
 (defn form
	  "Render form for adding new shelve"
	  ([]
	  (hg/render (apply str (form-template "")) {:user (session-get :user)}))
	  ([var-map]
	  (hg/render (apply str (form-template "")) (merge {:user (session-get :user)} var-map ))))
	
	
 (defn edit
	  "Render form for editing existing shelve"
	  ([shelve]
	  (hg/render (apply str (edit-template "")) {:user (session-get :user) :shelve shelve})))

	
 (defn view
	  "Render list of books in shelve"
	  ([]
	  (hg/render (apply str (view-template "")) {:user (session-get :user)}))
	  ([var-map]
	  (hg/render (apply str (view-template "")) (merge {:user (session-get :user)} var-map ))))



 
  