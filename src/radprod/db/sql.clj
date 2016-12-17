(ns radprod.db.sql
  (require [clojure.java.jdbc :as sql]))

(def radprod-db "postgresql://localhost:5432/radprod")

(def entry-keys [:date :accession :exam :modality :site-code :rad :status])

(def entry-types [:text :text :text :text :text :text :text :text])

(def db-fields (into [] (map vector entry-keys entry-types)))

(defn createtable
  "Create a table in the provided db"
  [db tablename fields]
  (sql/db-do-commands db (sql/create-table-ddl tablename fields)))
