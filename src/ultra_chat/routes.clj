(ns ultra-chat.routes
  (:require [muuntaja.core :as m]
            [reitit.coercion.spec]
            [reitit.ring :as ring]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
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
    [["/" {:get h/render-landing-page
           :no-doc true}]
     ["/message" {:post h/accept-message
                  :no-doc true}]
     ["/file" {:post h/accept-file
               :middleware [wrap-multipart-params]
               :no-doc true}]
     ["/message-stream" {:get h/message-stream
                         :no-doc true}]
     ["/api/message" {:post {:handler h/accept-message-api
                             :parameters {:body {:message string?}}
                             :summary "Post a new chat message."
                             :responses {204 nil}}}]
     ["/swagger.json"
      {:get {:no-doc true
             :swagger {:info {:title "Ultra chat API"}}
             :handler (swagger/create-swagger-handler)}}]]
    {:data {:coercion reitit.coercion.spec/coercion
            :middleware [wrap-params
                         wrap-keyword-params
                         muuntaja/format-response-middleware
                         exception/exception-middleware
                         muuntaja/format-negotiate-middleware
                         muuntaja/format-request-middleware
                         coercion/coerce-response-middleware
                         coercion/coerce-request-middleware
                         no-caching]
            :muuntaja m/instance}})
   (ring/routes
    (ring/create-resource-handler
     {:path "/"})
    (swagger-ui/create-swagger-ui-handler
     {:path "/api-docs"})
    (ring/create-default-handler
     {:not-found (constantly {:status 404 :body "Not found"})}))))
