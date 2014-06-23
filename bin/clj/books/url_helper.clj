(ns books.url-helper
  "Namespace for URL handling"
  (:require [clojure.java.io :as io]))


(defn fetch-url
  "Fetch data from url"
  [address]
  (with-open [stream (.openStream (java.net.URL. address))]
    (let  [buf (java.io.BufferedReader. 
                (java.io.InputStreamReader. stream))]
      (apply str (line-seq buf)))))


(defn string-to-stream 
  "String to stream"
  [string]
	(java.io.ByteArrayInputStream.
	(.getBytes (.trim string))))
                         

