(ns radprod.db.csv
  (:require [clojure-csv.core :as csv])  
  (:require [clojure.java.io :as io])
  (:require [clojure.string :as str])
  (:require [clj-time.core :as t])
  (:require [clj-time.format :as f])
  ;(:gen-class)
  )




(defn process-csv [file]
  (with-open [rdr (io/reader file)]
    (doall (csv/parse-csv rdr))))

(defn csv-write [filename data]
  (with-open [f (io/writer filename)]
    (.write f (csv/write-csv data))))
