(ns sleep.components.common
  (:require ["@radix-ui/react-label" :as Label]
            ["@radix-ui/react-separator" :as Separator]
            [sleep.utils.frontend :refer [cn]]))

(defn button [{:keys [class disabled?]
               :as props}
              children]
  (let [default-class "inline-flex items-center justify-center gap-2 whitespace-nowrap rounded-md text-sm font-medium transition-colors
                       bg-indigo-500 text-white shadow hover:bg-primary/90
                       h-10 px-4 py-2
                       hover:bg-indigo-700 cursor-pointer
                       focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-indigo-700
                       disabled:pointer-events-none disabled:opacity-50
                       [&_svg]:pointer-events-none [&_svg]:size-4 [&_svg]:shrink-0"]
    [:button (merge
              props
              {:class (cn default-class class)
               :disabled disabled?})
     children]))

(defn input [{:keys [class required? disabled?]
              :as props}
             children]
  (let [default-class "flex h-10 w-full rounded-md border border-slate-200 bg-transparent px-4 py-2 text-base shadow-sm transition-colors
                       placeholder:text-slate-400
                       focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-indigo-700
                       disabled:cursor-not-allowed disabled:opacity-50
                       md:text-sm"]
    [:input (merge
             props
             {:class (cn default-class class)
              :required required?
              :disabled disabled?})
     children]))

(defn form-input [{:keys [id label type placeholder value on-change required? disabled? error]}]
  [:div {:class "grid gap-2"}
   [:> Label/Root {:class "text-sm font-medium leading-none"
                   :for   id}
    label]
   [input {:id          id
           :placeholder placeholder
           :type        type
           :value       value
           :on-change   on-change
           :required    required?
           :disabled    disabled?}]
   (when error
     [:div {:class "capitalize text-red-500 text-sm font-semibold leading-none"}
      (first error)])])

(defn separator [{:keys [class orientation decorative] :as props}]
  [:> Separator/Root (merge
                      {:class (cn "shrink-0 bg-slate-200"
                                  (if (= orientation "horizontal") "h-[1px] w-full" "h-full w-[1px]")
                                  class)
                       :orientation orientation
                       :decorative decorative}
                      props)])