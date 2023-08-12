(ns ftlm.hearts.auth.ui
  (:require
   [ftlm.hearts.html :refer [page-resp]]
   [shadow.graft :as graft]
   [shadow.css :refer [css]]))

(defn login [_req]
  (page-resp
   [:div
    {:class (css :min-h-screen :bg-gray-100 :flex :flex-col :justify-center :sm:py-12)}
    [:div
     {:class (css :p-10 :xs:p-0 :mx-auto [:md :w-full] [:md :max-w-md])}
     [:h1
      {:class (css :font-bold :text-center :text-2xl :mb-5 :text-amber-500)}
      "Hearts Vibrate"]
     [:div
      {:class (css :bg-white :shadow :rounded-lg :divide-y :divide-gray-200)}
      [:div
       {:class (css :px-5 :py-7)}
       [:form
        {:action "/login" :method "POST"}
        [:label
         {:class (css :font-semibold :text-sm :text-gray-600 :pb-1 :block)
          :for :email}
         "E-mail"]
        [:input
         {:class (css :border :rounded-lg :px-3 :py-2 :mt-1 :mb-5 :text-sm :w-full)
          :type :email
          :id :email
          :placeholder "example@example.com"}]
        [:label
         {:class (css :font-semibold :text-sm :text-gray-600 :pb-1 :block)
          :for :password}
         "Password"]
        [:input
         {:class (css :border :rounded-lg :px-3 :py-2 :mt-1 :mb-3 :text-sm :w-full)
          :type :password
          :id :password
          :placeholder "password"}]
        [:button
         {:class (css
                  :transition
                  :duration-200
                  :bg-amber-500
                  :hover:bg-amber-300
                  :focus:bg-amber-500
                  :focus:shadow-sm
                  :focus:ring-4
                  :focus:ring-pink-500
                  :focus:ring-opacity-50
                  :hover:bg-amber-500
                  :text-white
                  :w-full
                  :py-2.5
                  :rounded-lg
                  :text-sm
                  :shadow-sm
                  :font-semibold
                  :text-center
                  :inline-block)
          :type :submit}
         "Sign in"]
        [:div]]]]]]))
