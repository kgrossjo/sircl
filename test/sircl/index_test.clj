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
    (is (= (vocabulary-for-document {"one" 1, "two", 2})
           #{"one", "two"}))))

(deftest vocabulary-test
  (testing "simple"
    (is (= (vocabulary
            (list #{"one", "two"}
                  #{"two", "three"}))
           {"one" 1,
            "two" 2,
            "three" 1}))))
