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
