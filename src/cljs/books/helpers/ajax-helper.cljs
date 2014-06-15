(ns books.helpers.ajax-helper)

(defn parse-json-response
  "Parse JSON response from goog AJAX request"
  [response success failure]
  (let [data (js->clj (.getResponseJson (.-target response)) :keywordize-keys true)]
    (if (= (:status data) "OK")
      (success data)
      (failure data))))