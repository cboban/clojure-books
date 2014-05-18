(ns books.shelves.shelves-view
  (:use (sandbar stateful-session))
  (:require [books.neo4j :as n4j]
	    [books.html-generator :as hg]
	    [net.cgrand.enlive-html :as en]))

  (en/deftemplate index-template
    (hg/build-html-page [{:temp-sel [:div.content],
			 :comp "public/shelves/index.html",
			 :comp-sel [:div.shelves]}])
    [param]
   [:title] (en/content "Shelves")
   [:div.script] (en/content {:tag :script,
			     :attrs {:src "/js/shelves.js"},
			     :content nil})
   [:div.script] (en/append {:tag :script,
			    :attrs nil,
			    :content "books.shelves.jsshelves.init();"})
  )
  
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
	  (hg/build-html-page [{:temp-sel [:div.content],
			:comp "public/shelves/edit.html",
			:comp-sel [:div.shelves-edit]}])
	  [param]
	  [:title] (en/content "Shelves")
	  [:div.script] (en/content {:tag :script,
				    :attrs {:src "js/shelves.js"},
				    :content nil})
	  [:div.script] (en/append {:tag :script,
				   :attrs nil,
				   :content "books.shelves.jsshelves.init();"})
	 )

  
  (en/deftemplate view-template
	  (hg/build-html-page "ajax" [{:temp-sel [:div.ajax-loaded-content],
			:comp "public/shelves/view.html",
			:comp-sel [:div.shelves-view]}])
	  [param]
	 )
  
	
	(defn index 
	  "Renders shelves index"
	  ([]
	  (hg/render (apply str (index-template "")) {:user (session-get :user)}))
	  ([var-map]
	  (hg/render (apply str (index-template "")) (merge {:user (session-get :user)} var-map ))))
 
 	
	(defn listing 
	  "Renders list table over ajax"
	  ([]
	  (hg/render (apply str (list-template "")) {:user (session-get :user)}))
	  ([var-map]
	  (hg/render (apply str (list-template "")) (merge {:user (session-get :user)} var-map ))))
	
	
 (defn form
	  "Render form for adding new shelve"
	  ([]
	  (hg/render (apply str (form-template "")) {:user (session-get :user)}))
	  ([var-map]
	  (hg/render (apply str (form-template "")) (merge {:user (session-get :user)} var-map ))))
	
	
 (defn edit
	  "Render form for editing existing shelve"
	  ([]
	  (hg/render (apply str (edit-template "")) {:user (session-get :user)}))
	  ([var-map]
	  (hg/render (apply str (edit-template "")) (merge {:user (session-get :user)} var-map ))))

	
 (defn view
	  "Render list of books in shelve"
	  ([]
	  (hg/render (apply str (view-template "")) {:user (session-get :user)}))
	  ([var-map]
	  (hg/render (apply str (view-template "")) (merge {:user (session-get :user)} var-map ))))



 
  