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
  (with-open [rdr (io/reader file)]
    (reduce (fn [counts line]
              (update-in counts [(kind-of line)] inc))
            {:source (str file)
             :LOC 0 :comment 0 :blank 0}
            (line-seq rdr))))

(defn files-in-path [path]
  (->> (file-seq (io/file path))
       (filter #(.isFile %))
       (map #(.getPath %))
       (filter #(or (.endsWith % ".clj")
                    (.endsWith % ".cljs")))))

(defn path-stats [path]
  (let [relative-cwd (partial relative-file
                              (-> "" io/file .getAbsoluteFile str))
        files (map relative-cwd (files-in-path path))]
    (map #(-> (io/file %1) line-stats) files)))

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
  (let [source (distinct (mapcat path-stats (:source-paths project)))
        cljs (distinct  (mapcat path-stats (cljs-files project)))
        test (distinct (mapcat path-stats (:test-paths project)))
        all [source
             (subtotal "- Subtotal Clojure" source)
             cljs
             (subtotal "- Subtotal ClojureScript" cljs)
             test
             (subtotal "- Subtotal Test" test)
             (subtotal "- Total" (concat source test cljs))]]
    (print-table (flatten all))))
