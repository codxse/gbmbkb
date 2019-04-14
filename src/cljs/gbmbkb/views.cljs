(ns gbmbkb.views
  (:require [re-frame.core :as rf]
            [clojure.string :as string]
            [goog.string :as gstring]
            [goog.string.format]))

(defn ensure-number
  [v]
  (let [new-v (js/parseFloat v)]
    (if (js/isNaN new-v) 0 new-v)))

(rf/reg-event-db
  :set-input-value
  (fn [db [_ doc value]]
    (when-let [new-value (ensure-number value)]
      (assoc-in db [:input-value doc] new-value))))

(rf/reg-sub
  :input-value
  (fn [db]
    (get db :input-value {})))

(defn format
  [v]
  (let [[v r] (string/split (gstring/format "%.2f" (double v)) #"\.")
        nice-v (->> (reverse v)
                    (partition-all 3)
                    (reverse))
        nice-v2 (flatten (interpose " " nice-v))]
    (if (every? (partial = "0") r)
      (apply str nice-v2)
      (str (apply str nice-v2) " , " r))))

(defn gb->mb
  [v]
  (* v 1024))

(defn gb->kb
  [v]
  (* (gb->mb v) 1024))

(defn mb->gb
  [v]
  (double (/ v 1024)))

(defn mb->kb
  [v]
  (* v 1024))

(defn kb->mb
  [v]
  (double (/ v 1024)))

(defn kb->gb
  [v]
  (double (/ (kb->mb v) 1024)))

(defn main-panel []
  (let [input-value (rf/subscribe [:input-value])]
    (fn []
      [:div.gbmbkb-wrapper
       ;; GB to MB / KB
       [:div.gbmbkb-gb-to-mbkb
        [:div.gbmbkb-gb-to-mbkb-title
         [:h3 "Konversi GB (Gigabyte) ke MB (Megabyte)"]
         [:h3 "Konversi GB (Gigabyte) ke KB (Kilobyte)"]]
        [:div.gbmbkb-gb-to-mbkb-body
         [:div.gb-input-result
          [:div.col1 [:span.mb-value (format (gb->mb (:gb @input-value 0))) " MB"]]
          [:div.col2 [:span.kb-value (format (gb->kb (:gb @input-value 0))) " KB"]]]
         [:input {:className "gb-input-body"
                  :type "number"
                  :onChange #(rf/dispatch [:set-input-value :gb (-> % .-target .-value)])}] "GB"]]

       ;; MB to GB / KB
       [:div.gbmbkb-mb-to-gbkb
        [:div.gbmbkb-mb-to-gbkb-title
         [:h3 "Konversi MB (Megabyte) ke GB (Gigabyte)"]
         [:h3 "Konversi MB (Megabyte) ke KB (Kilobyte)"]]
        [:div.gbmbkb-mb-to-gbkb-body
         [:div.mb-input-result
          [:div.col1 [:span.gb-value (format (mb->gb (:mb @input-value 0))) " GB"]]
          [:div.col2 [:span.kb-value (format (mb->kb (:mb @input-value 0))) " KB"]]]
         [:input {:className "mb-input-body"
                  :type "number"
                  :onChange #(rf/dispatch [:set-input-value :mb (-> % .-target .-value)])}] "MB"]]

       ;; MB to GB / KB
       [:div.gbmbkb-kb-to-gbmb
        [:div.gbmbkb-kb-to-gbmb-title
         [:h3 "Konversi KB (Kilobyte) ke GB (Gigabyte)"]
         [:h3 "Konversi KB (Kilobyte) ke MB (Megabyte)"]]
        [:div.gbmbkb-kb-to-gbmb-body
         [:div.mb-input-result
          [:div.col1 [:span.gb-value (format (kb->gb (:kb @input-value 0))) " GB"]]
          [:div.col2 [:span.mb-value (format (kb->mb (:kb @input-value 0))) " MB"]]]
         [:input {:className "kb-input-body"
                  :type "number"
                  :onChange #(rf/dispatch [:set-input-value :kb (-> % .-target .-value)])}] "KB"]]])))
