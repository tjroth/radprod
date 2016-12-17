(ns radprod.schedule.qgenda
  (:require [clojure.java.io :as io])
  (:require [radprod.db.csv :as csv]) 
  (:require [clojure.string :as str])
  (:require [clj-time.core :as t])
  (:require [clj-time.format :as f]))

;Utility functions, TODO: separate out to separate library
(defn clean-string
  "Utility function to trim whitespace and all caps"
  [str]
  ((comp str/trim str/upper-case) str))


(def default-csv-file "/Users/developer/Downloads/schedule.csv")

(def date-formatter (f/formatter "MM/dd/yyy"))

(def entry-keys [:date :last-name :first-name :short-name :empty1 :rotation-long :rotation-short :empty2 :empty3 :length])

;(def keeper-keys [:date :last-name :first-name :short-name :rotation-long :rotation-short])


(def conversions {:date #(f/parse-local-date date-formatter %) 
                  :last-name clean-string 
                  :first-name clean-string 
                  :short-name clean-string
                  :empty1 identity
                  :rotation-long identity
                  :rotation-short identity
                  :empty2 identity
                  :empty3 identity
                  :length identity})

(defn convert
  "Convert a column string into proper value using conversion function"
  [entry-key value]
  ((get conversions entry-key) value))


(defn mapify
  "Return a seq of maps like {:name \"Edward Cullen\" :glitter-index 10}"
  [rows]
  (map (fn [unmapped-row]
         (reduce (fn [row-map [entry-key value]]
                   (assoc row-map entry-key (convert entry-key value)))
                 {}
                 (map vector entry-keys unmapped-row)))
       rows))


(defn remove-header
  "Removes the header from the parsed csv file"
  [coll]
  (drop 1 coll))


(defn remove-empty-keys
  "Remove unused keys from the map, see empty keys above  TODO: can probably shorten this"
  [coll]
  (map #(assoc {} :date (:date %) 
               :last-name (:last-name %) 
               :first-name (:first-name %) 
               :short-name (:short-name %)
               :rotation-long (:rotation-long %)
               :rotation-short (:rotation-short %)) coll))

(defn csv->schedule
  "Return a Schedule as a map from a csv-file"
  [csv-file]
  (remove-empty-keys (mapify (remove-header (csv/process-csv csv-file)))))


