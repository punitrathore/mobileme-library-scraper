(ns mobileme-library-scraper.core
  (:require [net.cgrand.enlive-html :as html])
  (:require [clojure.contrib.io :as io])
  (:require [ clojure.contrib.duck-streams :as duck-streams])
  (:require [clojure.contrib.string :as string])
  )

(def *books-file* (str "books.csv"))

(def *book-selector* [:div.medium])

(def urls
     [(str "http://homepage.mac.com/amitrathore/deliciouslibrary/index.html")
     (str "http://homepage.mac.com/amitrathore/deliciouslibrary/books-2.html")
     (str "http://homepage.mac.com/amitrathore/deliciouslibrary/books-3.html")
     (str "http://homepage.mac.com/amitrathore/deliciouslibrary/books-4.html")
     (str "http://homepage.mac.com/amitrathore/deliciouslibrary/books-5.html")
     (str "http://homepage.mac.com/amitrathore/deliciouslibrary/books-6.html")
     (str "http://homepage.mac.com/amitrathore/deliciouslibrary/books-7.html")
     (str "http://homepage.mac.com/amitrathore/deliciouslibrary/books-8.html")
     (str "http://homepage.mac.com/amitrathore/deliciouslibrary/books-9.html")
     (str "http://homepage.mac.com/amitrathore/deliciouslibrary/books-10.html")
     (str "http://homepage.mac.com/amitrathore/deliciouslibrary/books-11.html")
     (str "http://homepage.mac.com/amitrathore/deliciouslibrary/books-12.html")
     (str "http://homepage.mac.com/amitrathore/deliciouslibrary/books-13.html")
     (str "http://homepage.mac.com/amitrathore/deliciouslibrary/books-14.html")
     (str "http://homepage.mac.com/amitrathore/deliciouslibrary/books-15.html")
     (str "http://homepage.mac.com/amitrathore/deliciouslibrary/books-16.html")
     (str "http://homepage.mac.com/amitrathore/deliciouslibrary/books-17.html")
     (str "http://homepage.mac.com/amitrathore/deliciouslibrary/books-18.html")
     (str "http://homepage.mac.com/amitrathore/deliciouslibrary/books-19.html")
     (str "http://homepage.mac.com/amitrathore/deliciouslibrary/books-20.html")    
     ])

(defn fetch-page-data [url]
  (html/html-resource (java.net.URL. url)))

(defn html-of-all-pages [urls]
  (map #(fetch-page-data %) urls))

(defn div-containing-books-from-html [html]
  (html/select html *book-selector*))

(defn vector-of-divs-from-all-pages [pages]
  (map #(div-containing-books-from-html %) pages))

(defn format-content-from-html [html]
  (:content html)
  )

(defn href-from-link [html]
  (:href (:attrs html))
  )

(defn titles-and-authors-from-page [page-html]
  (map #(href-from-link %) (html/select page-html #{[:a.title]})))

(defn fetch-all-books-from-all-pages [pages]
  (map #(titles-and-authors-from-page %) pages))

;;final function which fetches all books
(defn vector-of-all-titles-and-authors []
  (flatten (fetch-all-books-from-all-pages (vector-of-divs-from-all-pages (html-of-all-pages urls)))))

(defn remove-commas-from-string [str]
  (.replace str "," " "))

(defn write-single-line-to-file [amazon-link]
  (duck-streams/append-spit *books-file* (str amazon-link "\n")))

(defn write-all-books-to-file [links]
  (map write-single-line-to-file links)
  )

(defn fetch-books-and-write-to-file []
  (let [links (vector-of-all-titles-and-authors)]
    (duck-streams/spit *books-file* "Amazon Link\n")
    (println "Finished fetching books!")
    (write-all-books-to-file links)))

(fetch-books-and-write-to-file)
