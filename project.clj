(defproject Books "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  ;; CLJ source code path
  :source-paths ["src/clj"]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-1889"]
                 [compojure "1.1.5"]
                 [domina "1.0.2-SNAPSHOT"]
                 [hiccups "0.2.0"]]

  ;; lein-cljsbuild plugin to build a CLJS project
  :plugins [
            [lein-cljsbuild "0.3.3"]
            [lein-ring "0.8.7"]
  ]
  
  :ring {:handler books.core/handler}

  ;; cljsbuild options configuration
  :cljsbuild {:builds
              [{;; CLJS source code path
                :source-paths ["src/cljs" "src/brepl"]

                ;; Google Closure (CLS) options configuration
                :compiler {;; CLS generated JS script filename
                           :output-to "resources/public/js/main.js"

                           ;; minimal JS optimization directive
                           :optimizations :whitespace

                           ;; generated JS code prettyfication
                           :pretty-print true}}]})
