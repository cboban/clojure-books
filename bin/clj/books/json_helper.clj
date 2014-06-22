(ns books.json-helper
  "Namespace for JSON handling"
  (:require [clojure.data.json :as json]))


(defn output-message
  "Generate output json message"
  ([status message ] (output-message status message ""))
  ([status message data] (json/write-str {:status status :message message :data data}))
  ([status message data html] (json/write-str {:status status :message message :data data :html html})))
                         

