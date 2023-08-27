(ns ftlm.hearts.auth.ui
  (:require
   [ftlm.hearts.html :refer [page-resp anti-forgery-input-field]]
   [shadow.graft :as graft]
   [ftlm.hearts.css.css :as classes]
   [shadow.css :refer [css]]))

(defn login [req]
  (page-resp
   req
   [:div
    {:class (css :min-h-screen :bg-gray-100 :flex :flex-col :justify-center :sm:py-12)}
    [:div
     {:class (css :p-10 :xs:p-0 :mx-auto [:md :w-full] [:md :max-w-md])}
     [:h1
      {:class (css :font-bold :text-center :text-2xl :mb-5 :text-amber-500)}
      "Hearts Vibrate"]
     [:div
      {:class (css :bg-white :shadow :rounded-lg)}
      [:div
       {:class (css :px-5 :py-7)}
       [:form
        {:action "/login" :method "POST"}
        (anti-forgery-input-field)
        [:label
         {:class (css :font-semibold :text-sm :text-gray-600 :pb-1 :block)
          :for :email}
         "E-mail"]
        [:input
         {:class (css :border :rounded-lg :px-3 :py-2 :mt-1 :mb-5 :text-sm :w-full)
          ;; :type :email
          :id :email
          :name :email
          :placeholder "example@example.com"}]
        [:label
         {:class (css :font-semibold :text-sm :text-gray-600 :pb-1 :block)
          :for :password}
         "Password"]
        [:input
         {:class (css :border :rounded-lg :px-3 :py-2 :mt-1 :mb-3 :text-sm :w-full)
          :type :password
          :id :password
          :name :password
          :placeholder "password"}]
        [:button
         {:class classes/button-primary :type :submit}
         "Login"]]
       [:a {:class (css :text-sm) :href "/reset-pw"} "Forgot password?"]
       ;; [:div {:class (css :divide-y-4 :divide-black :w-full)}
       ;;  [:div]]
       [:div {:class (css :rounded-b :border-2 :border-gray-200 :my-2)}]
       [:h3 "Create an account"]
       [:form
        {:class (css :mt-4) :action "/signup" :method "POST"}
        (anti-forgery-input-field)
        [:label
         {:class (css :font-semibold :text-sm :text-gray-600 :pb-1 :block)
          :for :email}
         "E-mail"]
        [:input
         {:class (css :border :rounded-lg :px-3 :py-2 :mt-1 :mb-5 :text-sm :w-full)
          :type :email
          :id :email
          :name :email
          :placeholder "example@example.com"}]
        [:label
         {:class (css :font-semibold :text-sm :text-gray-600 :pb-1 :block)
          :for :password}
         "Password"]
        [:input
         {:class (css :border :rounded-lg :px-3 :py-2 :mt-1 :mb-3 :text-sm :w-full)
          :type :password
          :id :password
          :name :password
          :placeholder "password"}]
        [:input
         {:class (css :border :rounded-lg :px-3 :py-2 :mt-1 :mb-3 :text-sm :w-full)
          :type :password
          :id :password-2
          :name :password-2
          :placeholder "repeat"}]
        [:button {:class classes/button-primary :type :submit} "Sign in"]
        [:div]]]]]]))

;; forgot pw

;; http://localhost:8093/signup
