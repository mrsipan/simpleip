(ns simpleip.main
  (:require [eucalypt :as eu]
            ["ip-subnet-calculator" :as Ip-calc]
            ))

(defonce App-statuz (eu/atom
                      {:div-content "Nada"
                       :color "blue"}))


(defn Check-ip-addr-validity [ev]
  (let [content-curr (-> ev .-target .-textContent)]
    (println content-curr)
    (println (.isIp Ip-calc content-curr))

    (if (.isIp Ip-calc content-curr)
      (swap! App-statuz assoc :color "green")
      (swap! App-statuz assoc :color "sienna")
      )
    (swap! App-statuz assoc :div-content content-curr)

    )
  ; (.isIp Ip-calc ip-addr)
  )


(defn component:main [App-statuz]
  [:section
 {:class "section"}
 [:div
  {:class "container has-text-centered"}
  [:h1 {:class "title"} "Enter IP Address"]
  [:div
   {:class "box editable-box has-text-centered",
    :contenteditable "true",
    :on-input Check-ip-addr-validity
    :style {
            :color (:color @App-statuz)}
    :id "colorBox"}]]]
  )

(eu/render
  [component:main App-statuz]
  (js/document.getElementById "app"))
