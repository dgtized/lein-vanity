(ns leiningen.vanity
  (:require [clojure.java.io :as io]
            [bultitude.core :as b])
  (:use clojure.pprint))

(defn kind-of [line]
  (cond (re-find #"^\s*;" line) :comment
        (re-find #"^\s*$" line) :blank
        :else :code))

(defn line-stats [file]
  (with-open [rdr (clojure.java.io/reader file)]
    (reduce (fn [counts line]
              (let [kind (kind-of line)]
                (assoc counts kind (inc (counts kind)))))
            {:file (str file)
             :code 0 :comment 0 :blank 0}
            (line-seq rdr))))

(defn path-stats [path]
  (let [namespaces (b/namespaces-in-dir path)
        files (map b/path-for namespaces)]
    (map #(line-stats (io/file path %1)) files)))

(defn vanity
  "Lines of code statistics for vanity's sake"
  [project]
  (let [source (map path-stats (:source-paths project))
        test (map path-stats (:test-paths project))]
    (print-table (flatten [source test]))))
