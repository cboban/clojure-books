(ns books.server
  "Requests and responses on server"
  (:use compojure.core
	(sandbar stateful-session)
	[ring.middleware.params]
	[ring.middleware.multipart-params]
  [ring.util.response :only [redirect]])
  (:require [compojure.handler :as handler]
	    [compojure.route :as route]
	    [books.signin.signin-view :as sninv]
	    [books.signin.signin-controller :as sninc]
	    [books.home.home-controller :as homec]
	    [books.shelves.shelves-controller :as shelvec]
	    [books.users.users-controller :as userc]
	    [books.books.books-controller :as booksc]
      [books.neo4j :as n4j]
	    [ring.adapter.jetty :as jetty]))

;; defroutes macro defines a function that chains individual route
;; functions together. The request map is passed to each function in
;; turn, until a non-nil response is returned.
(defroutes app-routes
  ; to serve document root address
  (GET ""
    []
    (redirect "/signin"))
  (GET "/"
    []
    (redirect "/signin"))
  (GET "/home"
    []
    (sninc/is-logged-in (homec/home)))
  (GET "/signin"
    []
    (sninc/is-logged-in (redirect "/home")))
  (GET "/logout"
    []
    (do (destroy-session!)
    (sninc/is-logged-in (homec/home))))
 
  
  (POST "/shelves"
       request
       (shelvec/save (:params request)))
  (GET "/shelves/:action"
       [action]
       (when-let [fun (ns-resolve 'books.shelves.shelves-controller (symbol action))]
        (apply fun [])))
  (GET "/shelves/:action/:params"
       [action params]
       (when-let [fun (ns-resolve 'books.shelves.shelves-controller (symbol action))]
        (apply fun [params])))
  (DELETE "/shelves/:action/:params"
       [action params]
       (when-let [fun (ns-resolve 'books.shelves.shelves-controller (symbol action))]
        (apply fun [params])))
   
  
  (POST "/users"
       request
       (userc/save (:params request)))
  (GET "/users/:action"
       [action]
       (when-let [fun (ns-resolve 'books.users.users-controller (symbol action))]
        (apply fun [])))
  (GET "/users/:action/:params"
       [action params]
       (when-let [fun (ns-resolve 'books.users.users-controller (symbol action))]
        (apply fun [params])))
  (DELETE "/users/:action/:params"
       [action params]
       (when-let [fun (ns-resolve 'books.users.users-controller (symbol action))]
        (apply fun [params])))
  
  
  (GET "/search/:term"
       [term]
       (booksc/search term))
  
  (GET "/home/search"
       []
       (homec/search-form))
  
  (POST "/books/add-book"
     request
     (booksc/add-book (:params request)))
 
  (POST "/books/remove-book"
     request
     (booksc/remove-book (:params request)))
    
  (GET "/books/:action"
       [action]
       (when-let [fun (ns-resolve 'books.books.books-controller (symbol action))]
        (apply fun [])))
  (GET "/books/:action/:params"
       [action params]
       (when-let [fun (ns-resolve 'books.books.books-controller (symbol action))]
        (apply fun [params])))

  
  
  (PUT "/register-user"
    request
 (do (session-pop! :login-try 1)
 (sninc/is-not-logged-in (sninc/register-new-user (:params request)))))
  (POST "/signin"
    request
    (do (sninc/authenticate-user (:params request))
	(sninc/is-logged-in (redirect "/home"))))
  ; to serve static pages saved in resources/public directory
  (route/resources "/")
  ; if page is not found
  (route/not-found (redirect "/home"))
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
  (jetty/run-jetty handler {:port 3000 :join? false}))