(ns localweather.views
  (:require [re-frame.core :as re-frame]
            [ajax.core :refer [GET]]
            goog.string.format
            [re-com.core :as re-com]))

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
                                     lat (.toFixed (.-latitude c) 4)
                                     lon (.toFixed (.-longitude c) 4)]
                                 (GET "http://api.openweathermap.org/data/2.5/weather"
                                      {:params {:lat lat
                                                :lon lon
                                                :appid "6fbab9f2b2b55416c6f5e70d16613b3a"}
                                       :handler (fn [res]
                                                  ;(println "OK")
                                                  (re-frame/dispatch [:save-current-weather res]))
                                       :response-format :json
                                       :keywords? true})
                                 )))))
(defn get-weather-status [code]
  (cond
    (= code 800) {:day :wi-day-sunny :night :wi-night-clear}
    :else (condp = (quot code 100)
            2 {:day :wi-day-thunderstorm :night :wi-night-alt-thunderstorm}
            3 {:day :wi-day-showers :night :wi-night-alt-showers}
            5 {:day :wi-day-rain :night :wi-night-rain}
            6 {:day :wi-day-snow :night :wi-night-snow-wind}
            7 {:day :wi-day-fog :night :wi-night-fog}
            8 {:day :wi-day-cloudy :night :wi-night-cloudy})))

(get-weather-status 210)

(defn main-panel []
  (let [cw (re-frame/subscribe [:get-current-weather])]
    (fn []
      (let [city (:name @cw)
            temp_c (str (.toFixed (k->c (get-in @cw [:main :temp])) 1) "°C")
            temp_f (str (.toFixed (k->f (get-in @cw [:main :temp])) 1) "°F")
            {:keys [id description]} (first (get-in @cw [:weather]))]
        [re-com/h-box
         :children
         [
          [re-com/box :child " " :size "auto"]
          [re-com/box
           :size "auto"
           :child
           [:div (str "Dziś " current-day-formatted " w " city
                      " jest " temp_c " stopni")
            [:span {:class (str "wi " (name (:day (get-weather-status id)))) :title description}]]]
          [re-com/box :child " " :size "auto"]]]
        ))))
