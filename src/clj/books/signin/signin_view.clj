(ns books.signin.signin-view
  (:use (sandbar stateful-session))
  (:require [books.neo4j :as n4j]
	    [books.html-generator :as hg]
	    [net.cgrand.enlive-html :as en]))

  (en/deftemplate signin
    (hg/build-html-page "signin" [{:temp-sel [:div.container],
			 :comp "public/signin/forms.html",
			 :comp-sel [:div.user-forms]}])
  [message]
  [:title] (en/content "Sign in")
  [:div.script] (en/content {:tag :script,
			     :attrs {:src "js/signin.js"},
			     :content nil})
  [:div.script] (en/append {:tag :script,
			    :attrs nil,
			    :content "books.signin.jssignin.init();"})
  [:div#alertInfoMessage] (en/content (cond 
                            (clojure.string/blank? message) "" 
                            :else (str message))))


(en/deftemplate page-not-found
  (hg/build-html-page "signin" [{:temp-sel [:div.container],
			:comp "public/basic/404.html",
			:comp-sel [:div.content]}])
  [param]
  [:title] (en/content param)
  [:div.page-not-found] (en/content {:tag :div,
				     :attrs nil,
				     :content param}
				    {:tag :div,
				     :attrs nil,
				     :content [{:tag :a,
						:attrs {:href "/signin"},
						:content "back"}]}))
 
  