(ns global-places-recommendation.server
  "Requests and responses on server"
  (:use compojure.core
	(sandbar stateful-session)
	[ring.middleware.params]
	[ring.middleware.multipart-params])
  (:require [compojure.handler :as handler]
	    [compojure.route :as route]
	    [global-places-recommendation.login.login-view :as lv]
	    [global-places-recommendation.login.login-controller :as lc]
	    [global-places-recommendation.restaurants.restaurants-view :as rv]
		[global-places-recommendation.hotels.hotels-view :as hv]
		[global-places-recommendation.neo4j :as n4j]
	    [ring.adapter.jetty :as jetty]))

;; defroutes macro defines a function that chains individual route
;; functions together. The request map is passed to each function in
;; turn, until a non-nil response is returned.
(defroutes app-routes
  ; to serve document root address
  (GET "/restaurants"
    []
    (lc/is-logged-in (rv/restaurants-form)))
  (POST "/search-restaurants"
    request
    (lc/is-logged-in (rv/restaurants-result (:params request))))
   (GET "/hotels"
    []
    (lc/is-logged-in (hv/hotels-form)))
  (POST "/search-hotels"
    request
    (lc/is-logged-in (hv/hotels-result (:params request))))
  (POST "/change-country"
    request
    (lc/is-logged-in (lv/localities (:country (:params request)))))
  (POST "/change-city"
    request
    (lc/is-logged-in (lv/locality-recommend (:city (:params request)))))
  (GET "/home"
    []
    (lc/is-logged-in (lv/home)))
  (GET "/login"
    []
    (lc/is-logged-in (lv/home)))
  (GET "/logout"
    []
    (do (destroy-session!)
	(lc/is-logged-in (lv/home))))
  (GET "/save-user"
    request
 (do (session-pop! :login-try 1)
 (lc/is-not-logged-in (lc/save-user (:params request)))))
  (GET "/delete-user"
    []
    (do (lc/is-logged-in (lc/delete-user (read-string (str (session-get :id)))))
	(destroy-session!)
	(lc/is-logged-in (lv/home))))
  (GET "/edit-user"
    []
    (lc/is-logged-in (lv/edit-user (n4j/read-node (session-get :id)))))
  (POST "/update-user"
    request
    (lc/is-logged-in (do (lc/update-user (:params request))
			 (lc/is-logged-in (lv/home)))))
  (POST "/login"
    request
    (do (lc/authenticate-user (:params request))
	(lc/is-logged-in (lv/home))))
  ; to serve static pages saved in resources/public directory
  (route/resources "/")
  ; if page is not found
  (route/not-found (lv/page-not-found "Page not found"))
;  (GET "/:url/:id"
;    request
;    (println request))
;  (POST "/:url/:id"
;    request
;    (println request))
)

;; site function creates a handler suitable for a standard website,
;; adding a bunch of standard ring middleware to app-route:
(def handler (-> (handler/site app-routes)
		 wrap-stateful-session
		 wrap-params
		 wrap-multipart-params))

(defn run-server
  "Run jetty server"
  []
  (jetty/run-jetty handler {:port 3000 :join? false})
  )