(ns leiningen.vanity
  (:require [clojure.java.io :as io]
            [bultitude.core :as b])
  (:use clojure.pprint))

(defn relative-file
  "Return a path relative to base"
  [base path]
  (-> path
      (clojure.string/replace-first (str base) "")
      (clojure.string/replace-first #"^/" "")))

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
  (let [relative-cwd (partial relative-file
                              (-> "" io/file .getAbsoluteFile str))
        namespaces (b/namespaces-in-dir path)
        files (map b/path-for namespaces)]
    (map #(-> (io/file path %1)
              line-stats
              (update-in [:source] relative-cwd)) files)))

(defn subtotal
  "Return a subtotal for all numeric stats and rename source"
  [source-name stats]
  (let [numeric-stats (map #(dissoc % :source) stats)
        subtotals (apply merge-with + (vec numeric-stats))]
    (if (< (count stats) 2)
      '()
      (assoc subtotals :source source-name))))

(defn cljs-files [project]
  (let [builds (get-in project [:cljsbuild :builds] [])
        source-maps (if (map? builds) (vals builds) builds)]
    (mapcat #(get % :source-paths) source-maps)))

(defn vanity
  "Lines of code statistics for vanity's sake"
  [project]
  (let [source (mapcat path-stats (:source-paths project))
        test (mapcat path-stats (:test-paths project))
        all [source
             (subtotal "- Subtotal Source" source)
             test
             (subtotal "- Subtotal Test" test)
             (subtotal "- Total" (concat source test))]]
    (print-table (flatten all))))
