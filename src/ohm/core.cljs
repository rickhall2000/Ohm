(ns ohm.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [put! chan <!]]))

(enable-console-print!)

(def app-state (atom {:result ""}))

(defn do-calc [{:keys [ohms amps volts]}]
  (cond
   (and (> ohms 0) (> volts 0) (> amps 0))
   (str "you need to leave 1 field blank")
   (and (> ohms 0) (> volts 0))
   (str "Current = " (/ volts ohms))
   (and (> ohms 0) (> amps 0))
   (str "Voltage = " (* amps ohms))
   (and (> volts 0)  (> amps 0))
   (str "Resistance = " (/ volts amps))
   :else "You need to enter 2 values"))

(defn handle-change [e owner key state]
  (let [value (.. e -target -value)
        text (key state)
        allowed (set (seq (str (range 10))))]
    (if (every? allowed (seq value))
      (om/set-state! owner key value)
      (om/set-state! owner key text))))

(defn entry-pane [app owner]
  (reify
    om/IRenderState
    (render-state [this state]
      (dom/div #js {:className "entry-pane"}
       (dom/h3 nil "Ohm's Law Calculator")
       (dom/label nil "Resistance")
       (dom/input #js {:type "text" :ref "ohms"
                       :value (:ohms state)
                       :onChange #(handle-change % owner :ohms state)})
       (dom/label nil "Current")
       (dom/input #js {:type "text" :ref "amps"
                       :value (:amps state)
                       :onChange #(handle-change % owner :amps state)})
       (dom/label nil "Voltage")
       (dom/input #js {:type "text" :ref "volts"
                       :value (:volts state)
                       :onChange #(handle-change % owner :volts state)})
       (dom/button #js {:className "button"
                        :onClick
                        (fn [e] (put! (:calculate state) state))}
                   "Calculate")))))

(defn result-pane [app owner]
  (reify
    om/IRender
    (render [this]
      (dom/div #js {:className "result-pane"}
               (dom/label #js {:className "result"} (:result app))))))

(defn calculator-view [app owner]
  (reify
    om/IInitState
    (init-state [_]
      {:calculate (chan)
       :ohms ""
       :amps ""
       :volts ""})
    om/IWillMount
    (will-mount [_]
      (let [calculate (om/get-state owner :calculate)]
        (go (loop []
              (let [inputs (<! calculate)]
                (om/transact! app
                              (fn [xs]
                                (assoc xs :result (do-calc inputs))))
                (recur))))))
    om/IRenderState
    (render-state  [this state]
      (dom/div nil
               (om/build entry-pane app
                         {:init-state state})
               (om/build result-pane app)))))

(om/root
 calculator-view
  app-state
  {:target (. js/document (getElementById "app"))})
