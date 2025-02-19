(ns sleep.components.common
  (:require [reagent.core :as r]
            ["@radix-ui/react-popover" :as Popover]
            [sleep.utils :refer [cn]]))

(defn create-component
  ([hiccup default-class] (create-component hiccup default-class nil))
  ([hiccup default-class variants]
   (fn []
     (let [this (r/current-component)
           {class :class
            :as props} (r/props this)
           variant-classes (map (fn [[k v]] (get-in variants [k v]))
                                (select-keys props (keys variants)))
           children (r/children this)]
       (into [hiccup (merge
                      props
                      {:class (cn default-class variant-classes class)})
              nil]
             children)))))

(def button-temp
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

(defn button []
  (let [this (r/current-component)
        {class :class
         :as props} (r/props this)
        children (r/children this)
        default-class "inline-flex items-center justify-center gap-2 whitespace-nowrap rounded-md text-sm font-medium transition-colors
                      bg-primary text-primary-foreground shadow hover:bg-primary/90
                      h-9 px-4 py-2
                      focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring
                      disabled:pointer-events-none disabled:opacity-50
                      [&_svg]:pointer-events-none [&_svg]:size-4 [&_svg]:shrink-0"]
    (into [:button (merge
                    props
                    {:class (cn default-class class)})
           nil]
          children)))

(def popover (r/adapt-react-class Popover/Root))
(def popover-trigger (r/adapt-react-class Popover/Trigger))

(defn popover-content [{:keys [class align side-offset]
                        :or {align "center" side-offset 4}
                        :as props}
                       children]
  (let [default-class "z-50 w-72 rounded-md border bg-popover p-4 text-popover-foreground shadow-md outline-none
                       data-[state=open]:animate-in data-[state=closed]:animate-out data-[state=closed]:fade-out-0 data-[state=open]:fade-in-0 data-[state=closed]:zoom-out-95 data-[state=open]:zoom-in-95 data-[side=bottom]:slide-in-from-top-2 data-[side=left]:slide-in-from-right-2 data-[side=right]:slide-in-from-left-2 data-[side=top]:slide-in-from-bottom-2"]
    [:> Popover/Portal
     [:> Popover/Content (merge props {:class (cn default-class class)
                                       :align align
                                       :side-offset side-offset})
      children]]))