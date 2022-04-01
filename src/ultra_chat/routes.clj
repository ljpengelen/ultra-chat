(ns ultra-chat.routes
  (:require [reitit.ring :as ring]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [ring.middleware.params :refer [wrap-params]]
            [ultra-chat.handlers :as h]))

(defn no-caching [handler]
  (fn [request]
    (let [response (handler request)]
      (assoc-in response [:headers "Cache-Control"] "no-cache, no-store"))))

(defn routes []
  (ring/ring-handler
   (ring/router
    [["/" h/render-landing-page]
     ["/message" {:post h/accept-message}]
     ["/file" {:post h/accept-file
               :middleware [wrap-multipart-params]}]
     ["/message-stream" h/message-stream]]
    {:data {:middleware [wrap-params
                         wrap-keyword-params
                         no-caching]}})
   (ring/routes
    (ring/create-resource-handler
     {:path "/"})
    (ring/create-default-handler
     {:not-found (constantly {:status 404 :body "Not found"})}))))
