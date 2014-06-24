# Clojure Books

This app is written using clojure as backend provider and clojurescript for frontend renderer.
It uses Goodreads API methods to provide search functionality to users, scraping books data and enabling users to store books in their shelves.

Dependencies:
- [ring/ring-codec "1.0.0"]
- [org.clojure/clojure "1.6.0"]
- [compojure "1.1.6"]
- [com.cemerick/valip "0.3.2"]
- [clojurewerkz/neocons "3.0.0"]
- [ring "1.2.2"]
- [enlive "1.1.5"]
- [de.ubercode.clostache/clostache "1.4.0"]
- [domina "1.0.2"]
- [sandbar "0.4.0-SNAPSHOT"]
- [org.clojure/data.json "0.2.4"]
- [xml-apis/xml-apis "2.0.2"]
- [clj-webdriver "0.6.0"]
- [org.clojure/clojurescript "0.0-2227"]
- [lib-noir "0.8.1"]
- [enfocus "2.0.2"]
- [secretary "1.1.1"]
- [org.clojure/tools.logging "0.3.0"]
- [org.clojure/data.zip "0.1.1"]

### How to start it

This app requres Neo4j server instance (2.0+, labels support necesary), running on port 7474. Also, port 3000 needs to be available on machine, cause that is port which will web server use.

After all this is ready, clojurescript code can be compiled using command:

#### lein cljsbuild once

After this compilation, server can be started using command

#### lein run

At first run, app will automaticaly add one admin user (username: admin, pass: admin), with three shelves connected.


##Features:
- registration of new users and login for existing ones
- adding, editting and removing shelves, which will be used to group books
- searching books using simple form, with pagination
- viewing book details, including authors, links, similar books
- adding and removing books from/to shelves
- user profile editing
- two types of users - non admin and admin
- admin users can manage other users


##Additional features
- Client side routing (# routing) is implemented using Secretary
- All views for logged-in users are loaded using AJAX
- All books which shows up in search list or as similar book in some book details view are stored in database, with all available links and authors properly connected
- Saving large books data sets in database is implemented using agents


### License

Distributed under the Eclipse Public License, as Clojure
