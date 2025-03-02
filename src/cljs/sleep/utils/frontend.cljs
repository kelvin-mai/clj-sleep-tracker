(ns sleep.utils.frontend
  (:require [reagent.core :as r]
            [reitit.frontend.easy :as rfe]
            ["tailwind-merge" :refer [twMerge]]))

(defn cn [& args]
  (twMerge (clj->js args)))

(defn create-component
  ([hiccup default-class] (create-component hiccup default-class nil))
  ([hiccup default-class variants]
   (fn []
     (let [this (r/current-component)
           {class :class
            :as props} (r/props this)
           variant-classes (map (fn [[k v]] (or (get-in variants [k v])
                                                (get-in variants [k :default])))
                                (select-keys props (keys variants)))
           children (r/children this)]
       (into [hiccup (merge
                      props
                      {:class (cn default-class variant-classes class)})
              nil]
             children)))))

(defn href
  ([k] (href k nil nil))
  ([k params] (href k params nil))
  ([k params query] (rfe/href k params query)))