(ns simpleip.main
  (:require [eucalypt :as eu]))

; (println (fs/existsSync (fileURLToPath js/import.meta.url)))

(defn foo [{:keys [a b c]}]
  (+ a b c))

(println (foo {:a 1 :b 2 :c 3}))

(defonce statuz (eu/atom {}))

(defn component:main [statuz]
  [:<>
    [:p "Hello world!"]
    [:p "Hello world2!"]
    [:pre (pr-str @statuz)]])

(eu/render
  [component:main statuz]
  (js/document.getElementById "app"))
