(ns simpleip.main
  (:require [eucalypt :as Ra]
            ["ip-subnet-calculator" :as Ip-calc]))

(defonce App-statuz (Ra/atom {:div-content "Enter IP here", :color "grey"}))


(defn Check-ip-addr-validity
  [ev]
  (let [content-curr (-> ev
                         .-target
                         .-textContent)]
    (println content-curr)
    (println (.isIp Ip-calc content-curr))
    (if (.isIp Ip-calc content-curr)
      (swap! App-statuz assoc :color "green")
      (swap! App-statuz assoc :color "sienna"))
    (swap! App-statuz assoc :div-content content-curr)))


(defn component:main
  [App-statuz]
  [:section {:class "section"}
   [:div {:class "container has-text-centered"}
    [:h1 {:class "title"} "Enter IP Address"]
    [:div
     {:class "box editable-box has-text-centered",
      :contenteditable "true",
      :on-input Check-ip-addr-validity,
      :style {:color (:color @App-statuz)},
      :id "colorBox"} (:div-content @App-statuz)]]])

(Ra/render [component:main App-statuz] (js/document.getElementById "app"))
