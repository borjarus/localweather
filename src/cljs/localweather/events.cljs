(ns localweather.events
    (:require [re-frame.core :as re-frame]
              [localweather.db :as db]))

(re-frame/reg-event-db
 :initialize-db
 (fn  [_ _]
   db/default-db))


(re-frame/reg-event-db
 :save-current-weather
 (fn  [db [_ res]]
   (assoc-in db [:current-weather] res)))

(re-frame/reg-event-db
 :change-temp-type
 (fn  [db _]
   (let [temp-type @(re-frame/subscribe [:get-temp-type])]
     (assoc-in db [:temp-type] (condp = temp-type
                                 :c :f
                                 :f :c)))))
