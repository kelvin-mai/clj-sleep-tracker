(ns sleep.pages.dashboard.components)

(defn stat-card [{:keys [title amount description]}]
  [:div {:class "rounded-xl border text-card-foreground shadow border-slate-200"}
   [:div {:class "flex flex-col space-y-1.5 p-6"}
    [:h3 {:class "font-semibold leading-none tracking-tight"}
     title]]
   [:div {:class "p-6 pt-0"}
    [:p {:class "text-2xl font-bold"} amount]
    [:p {:class "text-xs text-muted-foreground"} description]]])

(defn history-card []
  [:div {:class "rounded-xl border text-card-foreground shadow border-slate-200"}
   [:div {:class "flex flex-col space-y-1.5 p-6"}
    [:h2 {:class "font-semibold leading-none tracking-tight"}
     "History"]
    [:p {:class "text-sm text-muted-foreground"} "Your sleep patterns over time"]]])