(ns localweather.views
  (:require [re-frame.core :as re-frame]
            [ajax.core :refer [GET]]
            goog.string.format))

(def current-day-formatted
  (let [date (js/Date.)
        y (.getFullYear date)
        m (inc (.getMonth date))
        d (.getDate date)]
    (goog.string.format "%02d.%02d.%d" d m y)))

(defn k->c [temp]
  (- temp 273.15))

(defn k->f [temp]
  (- (* temp (/ 9 5)) 459.67))

(when (exists? js/navigator.geolocation)
  (let [geo js/navigator.geolocation]
    (.getCurrentPosition geo (fn [pos]
                               (let [c (.-coords pos)
                                     lat (.-latitude c)
                                     lon (.-longitude c)]
                                 (GET "http://api.openweathermap.org/data/2.5/weather"
                                      {:params {:lat lat
                                                :lon lon
                                                :appid "6fbab9f2b2b55416c6f5e70d16613b3a"}
                                       :handler (fn [res]
                                                  (println "OK")
                                                  (re-frame/dispatch [:save-current-weather res]))
                                       :response-format :json
                                       :keywords? true})
                                 )))))


(defn main-panel []
  (let [cw (re-frame/subscribe [:get-current-weather])]
    (fn []
      (let [city (:name @cw)
            temp_c (str (.toFixed (k->c (get-in @cw [:main :temp])) 1) "°C")
            temp_f (str (.toFixed (k->f (get-in @cw [:main :temp])) 1) "°F")]
        [:div (str "Dziś " current-day-formatted " w " city
                   " jest " temp_c " stopni")] ))))
