(ns simpleip.main
  (:require [eucalypt :as Ra]
            [squint.string :as squint.string]
            ["ip-subnet-calculator" :as Ip-calc]))


(def placeholder-content "192.168.1.0/24")

(defonce DB-App
  (Ra/atom {:div-content placeholder-content :color "grey"}))

(defn Ip-do-string->int
  [ip-string]
  (let [[o1 o2 o3 o4] (map #(js/parseInt % 10)
                        (squint.string/split ip-string #"\."))]
    (bit-or (bit-shift-left o1 24)
            (bit-shift-left o2 16)
            (bit-shift-left o3 8)
            o4)))

(def ip-example (Ip-do-string->int "192.168.1.14"))

(defn Ip-do-int->string
  [ip-int]
  (squint.string/join "."
                      [(unsigned-bit-shift-right ip-int 24)
                       (bit-and (unsigned-bit-shift-right ip-int 16)
                                0xFF)
                       (bit-and (unsigned-bit-shift-right ip-int 8)
                                0xFF) (bit-and ip-int 0xFF)]))

(println (Ip-do-int->string ip-example))

(defn Do-prefix->mask-32bit
  [prefix-n]
  (cond (<= prefix-n 0) 0
        (>= prefix-n 32) -1
        :else (bit-shift-left -1 (- 32 prefix-n))))

(println (Do-prefix->mask-32bit 17))

(defn Subnet-addr
  [ip-int prefix-n]
  (bit-and ip-int (Do-prefix->mask-32bit prefix-n)))

(println (Subnet-addr (Ip-do-string->int "192.168.1.0" 24)))

(defn Block-size [prefix-n] (bit-shift-left 1 (- 32 prefix-n)))

(println (Block-size 32))

(defn Broadcast-addr
  [ip-int prefix-n]
  (let [sa (Subnet-addr ip-int prefix-n)]
    (dec (+ sa (Block-size prefix-n)))))

(defn Parse-cidr
  [cidr-string]
  (let [[ip-string prefix-string] (squint.string/split cidr-string #"/")
        ip-int (Ip-do-string->int ip-string)
        prefix-n (js/parseInt prefix-string 10)]
    [ip-int prefix-n]))


(println (Parse-cidr "192.168.2.0/24"))

(defn Enumerate-subnets
  [cidr-string prefix-new]
  (let [
        [ip-int-original prefix-int-original] (Parse-cidr cidr-string)
        ]
    (when (< prefix-new prefix-int-original)
      (throw (js/Error. (squint.string/join " "
                                            ["New prefix" prefix-new
                                             "must be >="
                                             prefix-int-original]))))
    (when (or (< prefix-new 0) (> prefix-new 32))
      (throw (js/Error. (squint.string/join " " ["Prefix between 32 an 0"])))
      )
    (let [
          net-original (Subnet-addr ip-int-original prefix-int-original)
          end-original (+ net-original (Block-size prefix-int-original))
          step (Block-size prefix-new)
          ]
      (map
        (fn [net]
          (let [b-cast (dec (+ net step))
                ips-avail step
                n-of-usable (max 0 (- ips-avail 2))
                fh (when (> ips-avail 2) (Ip-do-int->string (inc net)))
                lh (when (> ips-avail 2) (Ip-do-int->string (dec b-cast)))
                ]
            {
             :cidr (str (Ip-do-int->string net) "/" prefix-new)
             :network (Ip-do-int->string net)
             :broadcast (Ip-do-int->string b-cast)
             :first-host fh
             :last-host lh
             :ips-per-subnet ips-avail
             :usable-hosts n-of-usable
             }
            )
          )
        (range net-original end-original step)
        )
      )
    ))

; (println (Enumerate-subnets "192.168.1.0/24" 25))
; (println (take 3 (Enumerate-subnets "192.168.1.0/24" 25)))
(println (vec (take 3 (Enumerate-subnets "192.168.1.0/24" 25))))
;; Utility function to set the caret position at the end of a
;; contenteditable element)
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
    ; [:h1 {:class "title"} "Enter IP Address"]
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
