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
  (nr/connect! "http://localhost:7474/db/data/"))

(defn get-indexes-node-type-of
  "Get node with indexes of particular type"
  [index-type]
  (if (= (nn/all-indexes) [])
      (nn/create-index "indexesoftypes"))
  (first (nn/query "indexesoftypes" (str "type:"index-type))))

(defn vector-of-indexes
  "Vector of indexes in type node"
  [node]
  (:idx (:data node)))

(defn get-type-indexes
  "Returns indexes of particular type nodes"
  [index-type]
  (let [type-idx (get-indexes-node-type-of index-type)]
	     (vector-of-indexes type-idx)))

(defn read-all-nodes-type-of
  "Return all nodes of particular type"
  [index-type]
  (nn/get-many (get-type-indexes index-type)))

(defn create-type-node [node-data]
  "Create type node that is indexed and contains indexes of all nodes of that type in :idx property"
  (let [node (nn/create node-data)]
  (nn/add-to-index node "indexesoftypes" "type" (:type node-data))))

(defn add-node-key-to-indexes
  "Add new node index to node that contains indexes of nodes with same type"
  [index-type new-node-id]
  (if-let [node-idx (get-indexes-node-type-of index-type)]
    (if (contains? (into #{} (vector-of-indexes node-idx)) new-node-id)
      false
      (nn/set-property node-idx :idx (conj (vector-of-indexes node-idx) new-node-id)))
    (create-type-node {:idx [new-node-id] :type index-type})))

(defn remove-node-key-from-indexes
  "Remove index of removed node from indexes type node"
  [index-type removed-node-id]
  (let [node-idx (get-indexes-node-type-of index-type)]
    (if (contains? (into #{} (vector-of-indexes node-idx)) removed-node-id)
      (nn/set-property node-idx :idx (disj (into #{} (vector-of-indexes node-idx)) removed-node-id))
      false)))

(defn create-node
  "Create node in neo4j db"
  [node-label node-data]
  (let [node (nn/create node-data)]
    (nl/add node node-label)))

(defn read-node
  "Read node by id from neo4j db"
  [id]
  (nn/get id))

(defn update-node
  "Update node from neo4j db"
  [node data]
  (nn/update node data))

(defn delete-node
  "Delete node from neo4j db"
  [index-type id]
  (nrel/delete-many (nrel/all-ids-for (read-node id)))
  (nn/delete id)
  (remove-node-key-from-indexes index-type id))

(defn create-relationship
  "Create relationship between nodes"
  [from to rel-type data]
  (nrel/create from to rel-type data))

(defn update-relationship
  "Update relationship by id"
  [rel-id data]
  (nrel/update rel-id data))

(defn delete-relationship
  "Delete relationship by id"
  [rel-id]
  (nrel/delete rel-id))

(defn delete-many-relationships
  "Delete many relationships by id"
  [rel-ids]
  (nrel/delete-many rel-ids))

(defn cypher-query [query-statement]
  "Cypher query"
  (cy/query query-statement))

(defn set-node-property
  "Set node property"
  [node prop value]
  (nn/set-property node prop value))