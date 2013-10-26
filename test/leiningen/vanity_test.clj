(ns leiningen.vanity-test
  (:use clojure.test
        leiningen.vanity))

(deftest kind-of-line
  (is (= (kind-of ";") :comment))
  (is (= (kind-of " ;") :comment))
  (is (= (kind-of "") :blank))
  (is (= (kind-of " ") :blank)))

(deftest subtotals
  (is (= (subtotal "title"
                   '({:source "a" :a 1 :b 2}
                     {:source "b" :a 1 :b 1}))
         {:source "title" :a 2 :b 3}))
  (is (empty? (subtotal "test" '({}))))
  (is (empty? (subtotal "test" '()))))

(deftest finding-cljs-files
  (is (= '("src-cljs")
         (cljs-files {:cljsbuild {:builds {:main {:source-paths ["src-cljs"]}}}})))
  (is (= '("src-cljs")
         (cljs-files {:cljsbuild {:builds [{:source-paths ["src-cljs"]}]}})))
  (is (= '("A" "B")
         (cljs-files {:cljsbuild {:builds {:main {:source-paths ["A"]}
                                           :second {:source-paths ["B"]}}}}))))
