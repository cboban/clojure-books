(ns books.users.users-view
  (:use (sandbar stateful-session))
  (:use [clojure.tools.logging :only (info error)])
  (:require [books.neo4j :as n4j]
	    [books.html-generator :as hg]
	    [net.cgrand.enlive-html :as en]))

  (en/deftemplate list-template
    (hg/build-html-page "ajax" [{:temp-sel [:div.ajax-loaded-content],
			 :comp "public/users/list.html",
			 :comp-sel [:div.users-list]}])
    [param]
  )
  
  (en/deftemplate form-template
	  (hg/build-html-page "ajax" [{:temp-sel [:div.ajax-loaded-content],
			:comp "public/users/add.html",
			:comp-sel [:div.users-add]}])
    [param]
	 )
  
  (en/deftemplate edit-template
	  (hg/build-html-page "ajax" [{:temp-sel [:div.ajax-loaded-content],
			:comp "public/users/edit.html",
			:comp-sel [:div.users-edit]}])
	  [param]
	)

  
  (en/deftemplate view-template
	  (hg/build-html-page "ajax" [{:temp-sel [:div.ajax-loaded-content],
			:comp "public/users/view.html",
			:comp-sel [:div.users-view]}])
	  [param]
	 )

	(defn listing 
	  "Renders list table over ajax"
	  ([]
	  (hg/render (apply str (list-template "")) {:user (session-get :user)})))
	
	
 (defn form
	  "Render form for adding new shelve"
	  []
	  (hg/render (apply str (form-template "")) {:user (session-get :user)}))
	  
	
	
 (defn edit
	  "Render form for editing existing user"
	  ([user]
	  (hg/render (apply str (edit-template "")) {:user (session-get :user) :edit-user user})))

	
 (defn view
	  "Show user info"
	  ([]
	  (hg/render (apply str (view-template "")) {:user (session-get :user)})))



 
  