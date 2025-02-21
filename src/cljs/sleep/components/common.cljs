(ns sleep.components.common
  (:require ["@radix-ui/react-separator" :as Separator]
            [sleep.utils :refer [cn]]))

(defn button [{:keys [class]
               :as props}
              children]
  (let [default-class "inline-flex items-center justify-center gap-2 whitespace-nowrap rounded-md text-sm font-medium transition-colors
                       bg-primary text-primary-foreground shadow hover:bg-primary/90
                       h-9 px-4 py-2
                       focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring
                       disabled:pointer-events-none disabled:opacity-50
                       [&_svg]:pointer-events-none [&_svg]:size-4 [&_svg]:shrink-0"]
    [:button (merge
              props
              {:class (cn default-class class)})
     children]))

(defn input [{:keys [class required? disabled?]
              :as props}
             children]
  (let [default-class "flex h-9 w-full rounded-md border border-input bg-transparent px-3 py-1 text-base shadow-sm transition-colors
                     file:border-0 file:bg-transparent file:text-sm file:font-medium file:text-foreground
                     placeholder:text-muted-foreground
                     focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring
                     disabled:cursor-not-allowed disabled:opacity-50
                     md:text-sm"]
    [:input (merge
             props
             {:class (cn default-class class)
              :required required?
              :disabled disabled?})
     children]))

(defn separator [{:keys [class orientation decorative] :as props}]
  [:> Separator/Root (merge
                      {:class (cn "shrink-0 bg-slate-500"
                                  (if (= orientation "horizontal") "h-[1px] w-full" "h-full w-[1px]")
                                  class)
                       :orientation orientation
                       :decorative decorative}
                      props)])