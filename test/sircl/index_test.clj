(ns sircl.index-test
  (:require [clojure.test :refer :all]
            [sircl.index :refer :all]))

(def belafonte "
I wonder why nobody don't like me
Or is it the fact that I'm ugly?
I wonder why nobody don't like me
Or is it the fact that I'm ugly?

I leave my whole house and home
My children don't want me no more
Bad talk inside de house dey bring
And when I talk they start to sing

\"Mama, look a boo-boo\" they shout
Their mother tell them \"shut up your mout' \"
\"That is your daddy\", \"oh, no\"
\"My daddy can't be ugly so\"
\"Shut your mout' \" \"Go away\"

\"Mama, look a boo-boo\" dey (ooh)
\"Shut your mout' \" \"Go away\"
\"Mama, look a boo-boo\" dey
")

(deftest normalize-word-test
  (testing "uppercase"
    (is (= (normalize-word "xYz") "xyz")))
  (testing "digits"
    (is (= (normalize-word "abc123") "abc123")))
  (testing "inner apostrophe is allowed"
    (is (= (normalize-word "don't") "don't")))
  (testing "outer apostrophe is stripped"
    (is (= (normalize-word "'abc") "abc"))
    (is (= (normalize-word "abc'") "abc"))))

(deftest string-words-test
  (testing "simple string"
    (is (= (string-words "a b c 123 one don't 'foo bar'")
           '("a" "b" "c" "123" "one" "don't" "foo" "bar")))))

(deftest document-vector-test
  (testing "Harry Belafonte"
    (is (= (document-vector (string-words belafonte))
           {
            "a" 3,
            "and" 2,
            "away" 2,
            "bad" 1,
            "be" 1,
            "boo" 6,
            "bring" 1,
            "can't" 1,
            "children" 1,
            "daddy" 2,
            "de" 1,
            "dey" 3,
            "don't" 3,
            "fact" 2,
            "go" 2,
            "home" 1,
            "house" 2,
            "i" 4,
            "i'm" 2,
            "inside" 1,
            "is" 3,
            "it" 2,
            "leave" 1,
            "like" 2,
            "look" 3,
            "mama" 3,
            "me" 3,
            "more" 1,
            "mother" 1,
            "mout" 3,
            "my" 3,
            "no" 2,
            "nobody" 2,
            "oh" 1,
            "ooh" 1,
            "or" 2,
            "shout" 1,
            "shut" 3,
            "sing" 1,
            "so" 1,
            "start" 1,
            "talk" 2,
            "tell" 1,
            "that" 3,
            "the" 2,
            "their" 1,
            "them" 1,
            "they" 2,
            "to" 1,
            "ugly" 3,
            "up" 1,
            "want" 1,
            "when" 1,
            "whole" 1,
            "why" 2,
            "wonder" 2,
            "your" 4,
            }))))

(deftest vocabulary-for-document-test
  (testing "simple"
    (let [d {:type :document,
             :filename "d",
             :content "one two",
             :vector {"one" 1, "two", 2}}]
      (is (= (vocabulary-for-document d)
             #{"one", "two"})))))

(deftest vocabulary-test
  (testing "simple"
    (let [d1 {:type :document,
              :filename "d1",
              :content "one two",
              :vector {"one" 1, "two" 1}},
          d2 {:type :document,
              :filename "d2",
              :content "two three",
              :vector {"two" 1, "three" 1}}]
      (is (= (vocabulary (list d1 d2))
             {"one" 1,
              "two" 2,
              "three" 1})))))

(deftest term-index-test
  (testing "simple"
    (let [d1-content "one two three"
          d1-vector (document-vector (string-words d1-content))
          d1 {:type :document,
              :filename "d1",
              :content d1-content,
              :vector d1-vector}
          d2-content "two three three"
          d2-vector (document-vector (string-words d2-content))
          d2 {:type :document,
              :filename "d2",
              :content d2-content,
              :vector d2-vector},
          docs [d1 d2]
          coll {:type :document-collection,
                :directory ".",
                :documents docs,
                :vocabulary (vocabulary docs)}
          iv (inverted-index coll)]
      (is (= (term-index "one" docs)
             {"d1" 1}))
      (is (= (term-index "two" docs)
             {"d1" 1, "d2" 1}))
      (is (= (term-index "three" docs)
             {"d1" 1, "d2" 2}))
      (is (= iv {"one" {"d1" 1},
                 "two" {"d1" 1, "d2" 1},
                 "three" {"d1" 1, "d2" 2}})))))

(deftest make-document-from-file-test
  (testing "Harry Belafonte"
    (let [filename "test/resources/harry.txt"
          d (make-document-from-file filename)]
      (is (= (:filename d) filename))
      (is (= (:vector d) {"a" 3,
                          "and" 2,
                          "away" 2,
                          "bad" 1,
                          "be" 1,
                          "boo" 6,
                          "bring" 1,
                          "can't" 1,
                          "children" 1,
                          "daddy" 2,
                          "de" 1,
                          "dey" 3,
                          "don't" 3,
                          "fact" 2,
                          "go" 2,
                          "home" 1,
                          "house" 2,
                          "i" 4,
                          "i'm" 2,
                          "inside" 1,
                          "is" 3,
                          "it" 2,
                          "leave" 1,
                          "like" 2,
                          "look" 3,
                          "mama" 3,
                          "me" 3,
                          "more" 1,
                          "mother" 1,
                          "mout" 3,
                          "my" 3,
                          "no" 2,
                          "nobody" 2,
                          "oh" 1,
                          "ooh" 1,
                          "or" 2,
                          "shout" 1,
                          "shut" 3,
                          "sing" 1,
                          "so" 1,
                          "start" 1,
                          "talk" 2,
                          "tell" 1,
                          "that" 3,
                          "the" 2,
                          "their" 1,
                          "them" 1,
                          "they" 2,
                          "to" 1,
                          "ugly" 3,
                          "up" 1,
                          "want" 1,
                          "when" 1,
                          "whole" 1,
                          "why" 2,
                          "wonder" 2,
                          "your" 4,
                          })))))

(defn lookup-term-in-document-test0 []
  (let [filename "test/resources/harry.txt"
        d (make-document-from-file filename)
        existing-term "daddy"
        nonexisting-term "doesnotexist"
        existing-lookup (lookup-term-in-document existing-term d)
        nonexisting-lookup (lookup-term-in-document nonexisting-term d)]
    (is (= existing-lookup 2))
    (is (= nonexisting-lookup nil))))

(deftest lookup-term-in-document-test
  (testing "Harry Belafonte"
    (lookup-term-in-document-test0)))

(deftest get-inverted-list-entry-test
  (testing "Harry Belafonte"
    (let [filename "test/resources/harry.txt"
          d (make-document-from-file filename)
          x (get-inverted-list-entry "daddy" d)
          doesnotexist (get-inverted-list-entry "doesnotexist" d)]
      (is (= x [filename 2]))
      (is (= doesnotexist nil)))))

