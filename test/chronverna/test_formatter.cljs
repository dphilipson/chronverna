(ns chronverna.test-formatter
  (:require
    [cljs.test :as test :include-macros true :refer [report]]))

(enable-console-print!)

(defn color-favicon-data-url [color]
  (let [cvs (.createElement js/document "canvas")]
    (set! (.-width cvs) 16)
    (set! (.-height cvs) 16)
    (let [ctx (.getContext cvs "2d")]
      (set! (.-fillStyle ctx) color)
      (.fillRect ctx 0 0 16 16))
    (.toDataURL cvs)))

(defn change-favicon-to-color [color]
  (let [icon (.getElementById js/document "favicon")]
    (set! (.-href icon) (color-favicon-data-url color))))

(defn set-title [title]
  (set! (.-title js/document) title))

(defn show-success []
  (change-favicon-to-color "#0d0")
  (set-title "Tests Passed"))

(defn show-failure []
  (change-favicon-to-color "#d00")
  (set-title "Tests Failed"))

(defmethod report [::test/default :summary] [m]
  (println "\nRan" (:test m) "tests containing"
           (+ (:pass m) (:fail m) (:error m)) "assertions.")
  (println (:fail m) "failures," (:error m) "errors.")
  (if (< 0 (+ (:fail m) (:error m)))
    (show-failure)
    (show-success)))
