(ns localweather.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame]))


(re-frame/reg-sub
 :get-current-weather
 (fn [db]
   (:current-weather db)))


(re-frame/reg-sub
 :get-temp-type
 (fn [db]
   (:temp-type db)))
