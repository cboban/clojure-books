(ns books.server
  "Requests and responses on server"
  (:use compojure.core
	(sandbar stateful-session)
	[ring.middleware.params]
	[ring.middleware.multipart-params])
  (:require [compojure.handler :as handler]
	    [compojure.route :as route]
	    [books.signin.signin-view :as sninv]
	    [books.signin.signin-controller :as sninc]
      [books.neo4j :as n4j]
	    [ring.adapter.jetty :as jetty]))

;; defroutes macro defines a function that chains individual route
;; functions together. The request map is passed to each function in
;; turn, until a non-nil response is returned.
(defroutes app-routes
  ; to serve document root address
  (GET "/home"
    []
    (sninc/is-logged-in (sninv/home)))
  (GET "/signin"
    []
    (sninc/is-logged-in (sninv/home)))
  (GET "/signout"
    []
    (do (destroy-session!)
	(sninc/is-logged-in (sninv/home))))
  (PUT "/register-user"
    request
 (do (session-pop! :login-try 1)
 (sninc/is-not-logged-in (sninc/register-new-user (:params request)))))
  (POST "/signin"
    request
    (do (sninc/authenticate-user (:params request))
	(sninc/is-logged-in (sninv/home))))
  ; to serve static pages saved in resources/public directory
  (route/resources "/")
  ; if page is not found
  (route/not-found (sninv/page-not-found "Page not found"))
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