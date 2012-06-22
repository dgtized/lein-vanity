(ns leiningen.vanity
  (:require [clojure.java.io :as io]
            [bultitude.core :as b])
  (:use clojure.pprint))

(defn kind-of [line]
  (cond (re-find #"^\s*;" line) :comment
        (re-find #"^\s*$" line) :blank
        :else :LOC))

(defn line-stats [file]
  (with-open [rdr (clojure.java.io/reader file)]
    (reduce (fn [counts line]
              (update-in counts [(kind-of line)] inc))
            {:source (str file)
             :LOC 0 :comment 0 :blank 0}
            (line-seq rdr))))

(defn path-stats [path]
  (let [namespaces (b/namespaces-in-dir path)
        files (map b/path-for namespaces)]
    (map #(line-stats (io/file path %1)) files)))

(defn relative-file
  "Return a path relative to base"
  [base path]
  (-> path
      (clojure.string/replace-first (str base) "")
      (clojure.string/replace-first #"^/" "")))

(defn vanity
  "Lines of code statistics for vanity's sake"
  [project]
  (let [cwd (-> "" io/file .getAbsoluteFile str)
        relative-cwd (partial relative-file cwd)
        source (map path-stats (:source-paths project))
        test (map path-stats (:test-paths project))
        all (map #(update-in % [:source] relative-cwd)
                 (flatten [source test]))]
    (print-table all)))
