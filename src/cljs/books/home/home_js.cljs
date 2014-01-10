(ns books.home.home-js)

(defn smt 
  [param]
  (js/alert param))


(defn ^:export init []
  (smt "bobanddd"))

