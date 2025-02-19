(ns sleep.utils
  (:require [reagent.core :as r]
            ["clsx" :refer [clsx]]
            ["tailwind-merge" :refer [twMerge]]))

(defn cn [& args]
  (twMerge (clsx (clj->js args))))

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

(def button
  (create-component
   :button
   "inline-flex items-center justify-center gap-2 whitespace-nowrap rounded-md text-sm font-medium transition-colors
    bg-primary text-primary-foreground shadow hover:bg-primary/90
    h-9 px-4 py-2
    focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring
    disabled:pointer-events-none disabled:opacity-50
    [&_svg]:pointer-events-none [&_svg]:size-4 [&_svg]:shrink-0"
   {:variant {:default "bg-primary text-primary-foreground shadow hover:bg-primary/90"
              :destructive "bg-destructive text-destructive-foreground shadow-sm hover:bg-destructive/90"
              :outline "border border-input bg-background shadow-sm hover:bg-accent hover:text-accent-foreground"
              :secondary "bg-secondary text-secondary-foreground shadow-sm hover:bg-secondary/80"
              :ghost "hover:bg-accent hover:text-accent-foreground"
              :link "text-primary underline-offset-4 hover:underline"}
    :size {:default "h-9 px-4 py-2"
           :sm "h-8 rounded-md px-3 text-xs"
           :lg "h-10 rounded-md px-8"
           :icon "h-9 w-9"}}))