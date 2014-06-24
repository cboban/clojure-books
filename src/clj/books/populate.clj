(ns books.populate
  "The namespace used for initial data population"
 (:require [books.neo4j :as n4j])
 (:use [clojure.tools.logging :only (info error)]))


(defn populate
  "Populate with test data"
  []
  (let [admin (n4j/create-node "User" {
                                       :name "Admin"
                                       :surname "Admin"
                                       :email "admin@admin.com"
                                       :username "admin"
                                       :password "admin"
                                       :city "Belgrade"
                                       :country "Serbia"
                                       :is_admin true })
        shelve1 (n4j/create-node "Shelve" {
                                           :name "Shelve1"
                                           :description "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam feugiat elementum purus, ut rutrum arcu condimentum eu. In lectus ipsum, pulvinar et justo in, commodo laoreet augue. Curabitur ac turpis sed justo dapibus convallis. Lorem ipsum dolor sit amet, consectetur adipiscing elit."
                                           })
        shelve2 (n4j/create-node "Shelve" {
                                           :name "Shelve2"
                                           :description "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam feugiat elementum purus, ut rutrum arcu condimentum eu. In lectus ipsum, pulvinar et justo in, commodo laoreet augue. Curabitur ac turpis sed justo dapibus convallis. Lorem ipsum dolor sit amet, consectetur adipiscing elit."
                                           })
        shelve3 (n4j/create-node "Shelve" {
                                           :name "Shelve3"
                                           :description "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam feugiat elementum purus, ut rutrum arcu condimentum eu. In lectus ipsum, pulvinar et justo in, commodo laoreet augue. Curabitur ac turpis sed justo dapibus convallis. Lorem ipsum dolor sit amet, consectetur adipiscing elit."
                                           })]
    
    (do
      (n4j/create-relationship admin shelve1 :OWNS {})
      (n4j/create-relationship admin shelve2 :OWNS {})
      (n4j/create-relationship admin shelve3 :OWNS {}))))


(defn check-admin-exists
  "Check if admin exists in database"
  []
  (= (count (for [admin-id (:data (n4j/cypher-query "MATCH (u:User {is_admin: true}) RETURN ID(u)" ))]
   admin-id)) 0))
        

(defn populate-data
  "Start point for data population"
  []
  (if (check-admin-exists) (populate)))