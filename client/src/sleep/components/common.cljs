(ns sleep.components.common)

(def button-class
  "w-full transition-all duration-300
   font-medium border border-gray-300 px-4 py-2 text-sm rounded-md
   focus:ring-2 focus:ring-offset-2 focus:ring-blue-500")

(defn form-input [{:keys [id class type label value on-change required?]}]
  (let [input-class "form-input relative transition-all duration-300 
                     w-full border-gray-300 
                     rounded-lg tracking-wide
                     placeholder-gray-400 bg-white 
                     focus:ring focus:border-indigo-500 focus:ring-indigo-500/20
                     disabled:opacity-40 disabled:cursor-not-allowed"]
    [:div {:class "mb-4"}
     [:label {:class "block font-medium mb-2"
              :for id}
      label]
     [:input {:class (str input-class " " class)
              :type (or type "text")
              :id id
              :value value
              :on-change on-change
              :required required?}]]))