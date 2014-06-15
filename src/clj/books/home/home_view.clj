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
  )


(defn home 
  "Renders home page"
  ([]
  (hg/render (apply str (home-template "")) {:user (session-get :user)}))
  ([var-map]
  (hg/render (apply str (home-template "")) (merge {:user (session-get :user)} var-map ))))