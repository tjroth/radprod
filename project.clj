(defproject radprod "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"] [clojure-csv/clojure-csv "2.0.2"] [clj-time "0.12.2"]]
  :main ^:skip-aot radprod.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
