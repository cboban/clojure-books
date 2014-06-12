(defproject books "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  ;; source code path
  :source-paths ["src/clj"]
  :test-paths ["src/clj"]
  :resource-paths ["resources"]

  :dependencies [
     [ring/ring-codec "1.0.0"]
     [org.clojure/clojure "1.6.0"]
		 [compojure "1.1.6"]
		 [com.cemerick/valip "0.3.2"]
		 [clojurewerkz/neocons "2.0.1"]
		 [ring "1.2.2"]
		 [enlive "1.1.5"]
     [de.ubercode.clostache/clostache "1.3.1"]
		 [domina "1.0.2"]
		 [sandbar "0.4.0-SNAPSHOT"]
		 [org.clojure/data.json "0.2.4"]
		 [xml-apis/xml-apis "2.0.2"]
		 [clj-webdriver "0.6.0"]
     [org.clojure/clojurescript "0.0-2227"]
     [lib-noir "0.8.1"]
     [enfocus "2.0.2"] 
     [secretary "1.1.1"]
   ]
  ;:plugins [[lein2-eclipse "2.0.0"]]

  :main books.repl

  :repl-init books.repl

  :plugins [;; cljsbuild plugin
	    [lein-cljsbuild "1.0.3"]

	    ;; ring plugin
	    [lein-ring "0.8.10"]

	    ;; codox plugin
	    [codox "0.6.6"]]

  ;; cljsbuild options configuration
  :cljsbuild {:crossovers [valip.core 
                           valip.predicates
			   books.signin.signin-validators]
	      :builds
		{
     
   :signin
		 {;; CLJS source code path
		  :source-paths [
				 "src/cljs/books/signin"]

		  ;; Google Closure (CLS) options configuration
		  :compiler {;; CLS generated JS script filename
			     :output-to "resources/public/js/signin.js"

			     ;; minimal JS optimization directive
			     :optimizations :whitespace

			     ;; generated JS code prettyfication
			     :pretty-print true}}
  
   :home
		 {;; CLJS source code path
		  :source-paths [
				 "src/cljs/books/home"]

		  ;; Google Closure (CLS) options configuration
		  :compiler {;; CLS generated JS script filename
			     :output-to "resources/public/js/home.js"

			     ;; minimal JS optimization directive
			     :optimizations :whitespace

			     ;; generated JS code prettyfication
			     :pretty-print true}}   
  
   :shelves
		 {;; CLJS source code path
		  :source-paths [
				 "src/cljs/books/shelves"
         "src/cljs/books/helpers"]

		  ;; Google Closure (CLS) options configuration
		  :compiler {;; CLS generated JS script filename
			     :output-to "resources/public/js/shelves.js"

			     ;; minimal JS optimization directive
			     :optimizations :whitespace

			     ;; generated JS code prettyfication
			     :pretty-print true}}
		 }})