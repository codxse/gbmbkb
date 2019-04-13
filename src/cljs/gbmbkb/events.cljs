(ns gbmbkb.events
  (:require
   [re-frame.core :as re-frame]
   [gbmbkb.db :as db]
   ))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))
