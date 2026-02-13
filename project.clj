(defproject eu.esuomi.code/coll-tracker "_"
  :description "Track which keys and indices of a deep data structures are accessed."
  :url "https://github.com/esuomi/coll-tracker"
  :license {:name "CC0-1.0"
            :url "https://creativecommons.org/publicdomain/zero/1.0/"}

  :scm {:name "git" :url "https://github.com/esuomi/coll-tracker"}

  :dependencies [[org.clojure/clojure "1.12.4"]]


  :deploy-repositories [["clojars" {:sign-releases false
                                    :url           "https://clojars.org/repo"
                                    :username      :env/CLOJARS_USERNAME
                                    :password      :env/CLOJARS_TOKEN}]]

  :plugins [[fi.polycode/lein-git-revisions "1.1.2"]
            [lein-pprint "1.3.2"]]

  :global-vars {*warn-on-reflection* true}

  :repl-options {:init-ns eu.esuomi.code.coll-tracker}

  :git-revisions {:format        :semver
                  :adjust        [:env/revisions_adjustment :minor]
                  :revision-file "resources/metadata.edn"})
