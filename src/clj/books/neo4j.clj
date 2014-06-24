(ns books.neo4j
  "Namespace for manipulating data from database"
  (:require [clojurewerkz.neocons.rest :as nr]
            [clojurewerkz.neocons.rest.nodes :as nn]
            [clojurewerkz.neocons.rest.labels :as nl]
            [clojurewerkz.neocons.rest.relationships :as nrel]
	    [clojurewerkz.neocons.rest.cypher :as cy]
	    [clojure.string :refer [join]]))

(defn connect-neo4j
  "Connect to neo4j db"
  []
  (nr/connect "http://localhost:7474/db/data/"))

(defn create-node
  "Create node in neo4j db"
  [node-label node-data]
  (let [node (nn/create (connect-neo4j) node-data)]
    (nl/add (connect-neo4j) node node-label) node))

(defn read-node
  "Read node by id from neo4j db"
  [id]
  (nn/get (connect-neo4j) id))

(defn update-node
  "Update node from neo4j db"
  [node data]
  (nn/update (connect-neo4j) node data))

(defn delete-node
  "Delete node from neo4j db"
  [id]
  (nrel/delete-many (connect-neo4j) (nrel/all-ids-for (connect-neo4j) (read-node id)))
  (nn/delete (connect-neo4j) (read-node id)))

(defn read-relationship
  "Get relationship by id"
  [rel-id]
  (nrel/get (connect-neo4j) rel-id))

(defn create-relationship
  "Create relationship between nodes"
  [from to rel-type data]
  (nrel/create (connect-neo4j) from to rel-type data))

(defn update-relationship
  "Update relationship by id"
  [rel-id data]
  (nrel/update rel-id data))

(defn delete-relationship
  "Delete relationship by id"
  [rel-id]
  (nrel/delete (connect-neo4j) rel-id))

(defn delete-many-relationships
  "Delete many relationships by id"
  [rel-ids]
  (nrel/delete-many rel-ids))

(defn cypher-query [query-statement]
  "Cypher query"
  (cy/query (connect-neo4j) query-statement))

(defn set-node-property
  "Set node property"
  [node prop value]
  (nn/set-property node prop value))