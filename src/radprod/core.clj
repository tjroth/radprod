(ns radprod.core
;  (:require [clojure-csv.core :as csv])
  (:require [radprod.db.csv :as csv]) 
  (:require [clojure.java.io :as io])
  (:require [clojure.string :as str])
  (:require [clj-time.core :as t])
  (:require [clj-time.format :as f])
  (:gen-class))


(defn clean-string
  [str]
  ((comp str/trim str/upper-case) str))

(def date-formatter (f/formatter "MM/dd/yyy"))

(def csv-file "/Users/toddroth/Downloads/Productivity.csv")

;(def csv-file "/Users/developer/Desktop/Productivity.csv")

(def entry-keys [:date :accession :exam :modality :site-code :rad :status])

(def conversions {:date #(f/parse-local-date date-formatter %) 
                  :accession identity
                  :exam clean-string 
                  :modality  clean-string ;(comp str/trim str/upper-case)
                  :site-code clean-string ;clojure.string/trim
                  :rad clean-string 
                  :status clean-string })

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


(defn clean-name 
  [name] 
  (if (= (last name) "MD")
    (str (first name) " " (second name))
    (if (< (count name) 3) 
      (str (first name)) 
      (str (first name) " " (nth name 2)))))

(defn by-modality
  [data modality]
  (filter #(= modality  (:modality %)) data))

(defn modalities
  [coll]
  (into #{} (map :modality coll)))

(defn rads
  [coll]
    (into #{} (map :rad coll)))

(defn exams
  [coll]
  (into #{} (map :exam coll)))

(defn pretty
  [coll]
  (map println coll))

(defn daily-counts
  "Calculates the daily count for a vector of maps including by :date key"
  [coll]
  (map #(assoc {} :date (first %) :count (count (second %))) (sort (group-by :date coll))))


(defn monthly-counts
  "Calculates the monthly count for a vector of maps including by :date key"
  [coll]
  (sort-by #(first %) 
           (map #(assoc {} (first %) (count (second %))) (group-by #(t/month (:date %)) coll))))

(defn counts->csv
  [coll]
  (map (fn[entry] (vector (str (:date entry)) (str (:count entry)))) coll))

(def current-data
  (mapify (csv/process-csv csv-file true)))

(def mammograms
  (into [] (concat (by-modality current-data "MAM") (by-modality current-data "MAMC"))))

(def screening-mammograms
  (filter #(.contains (:exam %) "SCREENING") mammograms))

(defn -main
  "Main entry"
  [& args]
  (println "hello"))

