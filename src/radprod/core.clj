(ns radprod.core
  (:require [clojure-csv.core :as csv])  
  (:require [clojure.java.io :as io])
  (:require [clojure.string :as str])
  (:require [clj-time.core :as t])
  (:require [clj-time.format :as f])
  (:gen-class))


(defn clean-string
  [str]
  ((comp str/trim str/upper-case) str))

(def date-formatter (f/formatter "MM/dd/yyy"))

;(def csv-file "/Users/toddroth/Downloads/Productivity.csv")

(def csv-file "/Users/developer/Desktop/Productivity.csv")

(def entry-keys [:date :accession :exam :modality :site-code :rad :status])

(def conversions {:date #(f/parse-local-date date-formatter %) 
                  :accession identity
                  :exam #(clean-string %);
                  :modality  (comp str/trim str/upper-case)
                  :site-code clojure.string/trim
                  :rad identity
                  :status identity})

(defn process-csv [file]
  (with-open [rdr (io/reader file)]
    (doall (csv/parse-csv rdr))))

(defn csv-write [filename data]
  (with-open [f (io/writer filename)]
    (.write f (csv/write-csv data))))

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

(defn all-data
  "Return the parsed data as a vector of maps, drops first line if a header"
  [filename has-header?]
  (if has-header?
    (mapify (drop 1 (process-csv filename)))
    (mapify (process-csv filename))))


(defn by-modality
  [data modality]
  (filter #(= modality  (:modality %)) data))

(defn modalities
  [data]
  (into #{} (map :modality data)))

(defn exams
  [data]
  (into #{} (map :exam data)))

(defn pretty
  [data]
  (map println data))

(defn daily-counts
  "Calculates the daily count for a vector of maps including by :date key"
  [data]
  (map #(assoc {} :date (first %) :count (count (second %))) (sort (group-by :date data))))


(defn monthly-counts
  "Calculates the monthly count for a vector of maps including by :date key"
  [data]
  (sort-by #(first %) 
           (map #(assoc {} (first %) (count (second %))) (group-by #(t/month (:date %)) data))))

(defn counts->csv
  [data]
  (map (fn[entry] (vector (str (:date entry)) (str (:count entry)))) data))

(def current-data
  (all-data csv-file true))

(def mammograms
  (into [] (concat (by-modality current-data "MAM") (by-modality current-data "MAMC"))))

(def screening-mammograms
  (filter #(.contains (:exam %) "SCREENING") mammograms))

(defn -main
  "Main entry"
  [& args]
  (count  (by-modality (all-data csv-file true) "MAMC")))

