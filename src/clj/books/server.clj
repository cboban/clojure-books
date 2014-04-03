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
	    [books.home.home-view :as homev]
	    [books.shelves.shelves-controller :as shelvec]
      [books.neo4j :as n4j]
	    [ring.adapter.jetty :as jetty]))

;; defroutes macro defines a function that chains individual route
;; functions together. The request map is passed to each function in
;; turn, until a non-nil response is returned.
(defroutes app-routes
  ; to serve document root address
  (GET "/home"
    []
    (sninc/is-logged-in (homev/home)))
  (GET "/signin"
    []
    (sninc/is-logged-in (homev/home)))
  (GET "/logout"
    []
    (do (destroy-session!)
	(sninc/is-logged-in (homev/home))))
  (GET "/shelves"
       []
       (shelvec/list))
  (GET "/shelves/:action"
       [action]
       (when-let [fun (ns-resolve 'books.shelves.shelves-controller (symbol action))]
        (apply fun [])))
  (PUT "/register-user"
    request
 (do (session-pop! :login-try 1)
 (sninc/is-not-logged-in (sninc/register-new-user (:params request)))))
  (POST "/signin"
    request
    (do (sninc/authenticate-user (:params request))
	(sninc/is-logged-in (homev/home))))
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