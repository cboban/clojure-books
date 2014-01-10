(ns books.repl
  "The starting namespace for the project. This is the namespace that
  users will land in when they start a Clojure REPL. It exists to
  provide convenience functions like 'go' and 'dev-server'."
  (:use [clojure.repl]
	[clojure.java.shell :only [sh]])
  (:require [clojure.java.browse :as browse]
	    [books.server :as server]
	    [books.neo4j :as n4j]
      [books.signin.signin-controller :as sninc]))

(defonce server (ref nil))

(defn start-server
  "Start the development server and open the host application in the
  default browser."
  []

  (n4j/connect-neo4j)
  
  (dosync (ref-set server (server/run-server)))
  (future (Thread/sleep 3000)
          (browse/browse-url "http://localhost:3000/signin")))

(defn restart-server
  "Restart server"
  []
  (.stop @server)
  (start-server))

(defn stop-server
  "Stop server"
  []
  (.stop @server)
  (if (= (re-find #"Neo4j Server is running at pid " (:out (sh "neo4j-community/bin/neo4j" "status"))) "Neo4j Server is running at pid ")
      (println (:out (sh "neo4j-community/bin/neo4j" "stop")))
  )
)

(defn -main [& args]
  (start-server))

(println)
(println "Type (start-server) to launch server.")
(println "Type (restart-server) to restart server.")
(println "Type (stop-server) to stop server.")
(println)
