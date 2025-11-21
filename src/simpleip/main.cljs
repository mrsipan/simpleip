(ns simpleip.main
  (:require [eucalypt :as Ra]
            [squint.string :as squint.string]
            ["ip-subnet-calculator" :as Ip-calc]))


(def placeholder-content "Enter IP here")

(defonce DB-App
  (Ra/atom {:div-content placeholder-content, :color "grey"}))


;; Utility function to set the caret position at the end of a
;; contenteditable element
(defn Set-caret-to-end!
  [el]
  (when el
    (let [-range (document.createRange)
          sel (window.getSelection)]
      ;; Select all content in the element
      (.selectNodeContents -range el)
      ;; Collapse the range to its end point (placing the cursor
      ;; there)
      (.collapse -range false)
      ;; Remove any existing selections
      (.removeAllRanges sel)
      ;; Add the new range
      (.addRange sel -range)
      ;; Focus the element
      (.focus el))))

(defn Handle-key-down
  [ev]
  (when (= (.-key ev) "Escape")
    (.preventDefault ev)
    (swap! DB-App assoc :div-content "")))

(defn Check-ip-addr-validity
  [ev]
  (let [content-curr (-> ev
                         .-target
                         .-textContent)
        el-target (.-target ev)]
    (println content-curr)
    (println (.isIp Ip-calc content-curr))
    (if (.isIp Ip-calc content-curr)
      (swap! DB-App assoc :color "has-text-primary")
      (swap! DB-App assoc :color "has-text-warning"))
    (swap! DB-App assoc
      :div-content
      (-> ev
          .-target
          .-textContent))
    (Set-caret-to-end! el-target)))

(defn Handle-focusing
  [ev]
  (if (= placeholder-content (:div-content @DB-App))
    (swap! DB-App assoc :div-content "")))

(defn Component:main
  [DB-App]
  [:section {:class "section"}
   [:div {:class "container has-text-centered"}
    [:h1 {:class "title"} "Enter IP Address"]
    [:div
     {:class (squint.string/join " "
                                 ["box" "editable-box"
                                  "has-text-centered"
                                  (:color @DB-App)]),
      :contenteditable "true",
      :on-input Check-ip-addr-validity,
      :on-focus Handle-focusing,
      :on-key-down Handle-key-down,
      :style {:color (:color @DB-App)},
      :id "colorBox"} (:div-content @DB-App)]]])

(Ra/render [Component:main DB-App] (js/document.getElementById "app"))
