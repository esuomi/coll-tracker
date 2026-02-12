# coll-tracker

Track which keys and indices of a deep data structures are accessed.

[![main / Deploy](https://github.com/esuomi/coll-tracker/actions/workflows/deploy.yaml/badge.svg)](https://github.com/esuomi/coll-tracker/actions/workflows/deploy.yaml)
[![Clojars Project](https://img.shields.io/clojars/v/eu.esuomi.code/coll-tracker.svg)](https://clojars.org/eu.esuomi.code/coll-tracker)

## Usage

```clojure
; require the namespace
(require '[eu.esuomi.code.coll-tracker :as tracker])

; define some data
(def data {:simple 1 
           :nested {:value 2 
                    :deeply {:final-value 3}}
           :another 4
           :sequences [#{:awesome :cool}
                       #{:hot :lit}
                       [6 7]]})

; you need an atom for tracking
(def tracker (atom #{}))
(def tracked (tracker/tracked data tracker))

; use the tracked data structure
(println (get tracked :simple)
         (some-> tracked :nested :value)
         (get-in tracked [:nested :deeply :final-value])
         ((juxt first second) (-> tracked :sequences (nth 2)))) ; => 1
; => 1 2 3 [6 7]

; see which paths were accessed
(println (sort @tracker))
; => ([:nested] [:sequenced] [:simple] [:nested :deeply] [:nested :value] [:nested :deeply :final-value])

; Note! Accessing the original data does not impact the tracker
(println (get data :another)
         (get-in data [:nested :value]))
; => 4 2


; reprint to see that nothing changed
(println (sort @tracker))
; => ([:nested] [:sequenced] [:simple] [:nested :deeply] [:nested :value] [:nested :deeply :final-value])
```

The wrapper supports Clojure's `Associative`, `ILookup`, `ISeq`, `Indexed` and `Seqable` interfaces meaning maps, 
vectors, lists, sets are all supported.

In case of sets a special key is used to indicate access:

```clojure
(def colored (atom #{}))
(def paints (tracker/tracked {:colors #{{:name :red :value "#bb515d"}
                                        {:name :green :value "#00ff80"}
                                        {:name :blue :value "#0c5daa"}}}
                             colored))
(println (-> paints :colors first :value)) 
; => "#<hex value>"
@colored
; => #{[:colors] [:colors :*]}
```

The tracked data structure decorates printing with a `#tracked`:
```clojure
(println paints)
; => #tracked {:colors #{{:name :blue, :value #0c5daa} {:name :green, :value #00ff80} {:name :red, :value #bb515d}}}
```

The paths are useful, but a trie-like map is more readable to see what was used. Utility function is provided for 
this purpose:
```clojure
(clojure.pprint/pprint (tracker/paths->map @tracker))
; => {:sequences {2 nil},
;     :nested {:value nil, :deeply {:final-value nil}},
;     :simple nil}
```

## GenAI Usage Disclosure

OpenAI's ChatGPT with GPT-4.1 model was used to create parts of this library. As such the license is chosen as a very 
permissive one as I cannot claim ownership of a product of an amalgamation of all those who became before me. 

## License

All assets and code are under the [CC0 LICENSE](LICENSE) and in the public domain unless specified otherwise.