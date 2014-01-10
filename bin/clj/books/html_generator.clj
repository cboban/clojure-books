(ns books.html-generator
  "Namespace for generating html pages"
  (:require [net.cgrand.enlive-html :as en]))

(defn generate-html-resource
  "Generates html resource from parameter template and map variable
   components with three properties temp-sel, comp and comp-sel"
  [template components]
  (en/html-resource
    (en/transform
      (en/html-resource template)
      (:temp-sel components)
      (fn [selected-tag]
	  (assoc selected-tag :content (en/select (en/html-resource (:comp components)) (:comp-sel components)))))))

(defn build-html-page
  "Build html page with vector of maps where every map contains 
   template selector where component, as second map property,
   is going to be appended. And component selector that represents
   part or all of component html file."
  ([files-and-selectors] (build-html-page "home" files-and-selectors))
  ([template files-and-selectors]
  (reduce generate-html-resource (en/html-resource (str "public/templates/"template".html")) files-and-selectors)))