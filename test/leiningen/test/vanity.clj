(ns leiningen.test.vanity
  (:use clojure.test
        leiningen.vanity))

(deftest kind-of-line
  (is (= (kind-of ";") :comment))
  (is (= (kind-of " ;") :comment))
  (is (= (kind-of "") :blank))
  (is (= (kind-of " ") :blank)))
