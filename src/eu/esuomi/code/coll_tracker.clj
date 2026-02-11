(ns eu.esuomi.code.coll-tracker
  (:import (clojure.lang Associative ILookup ISeq Indexed Seqable)))

(declare wrap)

(deftype Tracked [value accessed path]

  Object
  (toString [_]
    (str "#tracked " value))

  ILookup
  (valAt [_ k]
    (let [p (conj path k)]
      (println "valAt" p _ k)
      (swap! accessed conj p)
      (wrap (get value k) accessed p)))

  (valAt [_ k not-found]
    (let [p (conj path k)]
      (swap! accessed conj p)
      (wrap (get value k not-found) accessed p)))

  Indexed
  (nth [_ i]
    (let [p (conj path i)]
      (swap! accessed conj p)
      (wrap (nth value i) accessed p)))

  (nth [_ i not-found]
    (let [p (conj path i)]
      (swap! accessed conj p)
      (wrap (nth value i not-found) accessed p)))

  Seqable
  (seq [_]
    (cond
      (map? value)
      (seq value)

      (or (vector? value) (list? value))
      (map-indexed
        (fn [i v]
          (wrap v accessed (conj path i)))
        value)

      (set? value)
      (do
        (swap! accessed conj (conj path :*))
        (seq value)))))

(defmethod print-method Tracked [^Tracked t ^java.io.Writer w]
  (.write w "#tracked ")
  (print-method (.value t) w))

(defn ^:private wrap [v accessed path]
  (if (or (map? v) (vector? v) (list? v) (set? v))
    (Tracked. v accessed path)
    v))

(defn tracked
  [value accessed]
  (wrap value accessed []))

(defn ^:private path->map [path]
  (reduce (fn [m k] {k m}) nil (reverse path)))

(defn ^:private deep-merge
  [& ms]
  (apply merge-with
         (fn [a b]
           (cond
             (and (map? a) (map? b)) (deep-merge a b)
             (map? a) a
             (map? b) b
             :else b))
         ms))

(defn paths->map [paths]
  (apply deep-merge (map path->map paths)))

