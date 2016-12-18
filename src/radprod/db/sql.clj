(ns radprod.db.sql
  (require [clojure.java.jdbc :as sql])
  (:require [clj-time.core :as t])
  (:require [clj-time.coerce :as coerce])
  )

(def radprod-db "postgresql://localhost:5432/radprod")

(def entry-keys [:date :accession :exam :modality :site_code :rad :status])

(def entry-types [:date :text :text :text :text :text :text :text])

(def db-fields (into [] (map vector entry-keys entry-types)))

(def sample-date (t/local-date-time 2016 1 1 12))

(def sample-data {:date (coerce/to-sql-date sample-date) 
                  :accession ""
                  :exam ""
                  :modality ""
                  :site_code ""
                  :rad ""
                  :status ""} )

(def multiple [sample-data sample-data])
(defn create-table
  "Create a table in the provided db"
  [db tablename fields]
  (sql/db-do-commands db (sql/create-table-ddl tablename fields)))

(defn add-record
  [db table coll]
  (sql/insert! db table coll))
