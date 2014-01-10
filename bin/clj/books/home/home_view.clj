(ns books.home.home-view  
  (:use (sandbar stateful-session))
  (:require [books.neo4j :as n4j]
	    [books.html-generator :as hg]
	    [net.cgrand.enlive-html :as en]))


(en/deftemplate home
  (hg/build-html-page [{:temp-sel [:div.container],
			:comp "public/home/dashboard.html",
			:comp-sel [:div.dashboard]}])
  []
  [:title] (en/content "Dashboard")
  [:div.script] (en/content {:tag :script,
			     :attrs {:src "js/home.js"},
			     :content nil})
  [:div.script] (en/append {:tag :script,
			    :attrs nil,
			    :content "books.home.home_js.init();"}))
