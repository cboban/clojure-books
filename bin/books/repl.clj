(ns global-places-recommendation.repl
  "The starting namespace for the project. This is the namespace that
  users will land in when they start a Clojure REPL. It exists to
  provide convenience functions like 'go' and 'dev-server'."
  (:use [clojure.repl]
	[clojure.java.shell :only [sh]])
  (:require [clojure.java.browse :as browse]
	    [global-places-recommendation.server :as server]
	    [global-places-recommendation.neo4j :as n4j]
            [global-places-recommendation.login.login-controller :as lc]
	    [factual.api :as fact]))

(fact/factual! "dLdrD1DFyqEErPNYCWL6fUDfjOYRX91QGwidQHan" "4xzqiH0N87cgT1iMxG9MDdc1txGqkTovYVK1jxq4")

(defonce server (ref nil))

(defn start-server
  "Start the development server and open the host application in the
  default browser."
  []

  (n4j/connect-neo4j)
  (lc/save-user {:name "boban"
					            :surname "cirkovic"
					            :email "boban@intellex.rs"
					            :username "boban"
					            :password "boban"
					            :age "21"
					            :city "beograd"
					            :country "Srbija"
					            :gender "male"})
  (dosync (ref-set server (server/run-server)))
  (future (Thread/sleep 3000)
          (browse/browse-url "http://localhost:3000/login")))

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
