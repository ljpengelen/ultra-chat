(ns ultra-chat.core
  (:require [clojure.java.browse :refer [browse-url]]
            [org.httpkit.server :as http-kit]
            [ring.middleware.reload :refer [wrap-reload]]
            [ultra-chat.routes :refer [routes]])
  (:gen-class))

(defn wrap [request] ((routes) request))

(defonce server (atom nil))

(defn start! []
  (when-not @server
    (reset! server (http-kit/run-server (wrap-reload #'wrap) {:port 3000 :join? false}))))

(defn stop! []
  (when-let [running-server @server]
    (running-server)
    (reset! server nil)))

(defn restart! []
  (stop!)
  (start!))

(defn -main [_]
  (start!))

(comment
  (start!)
  (browse-url "http://localhost:3000")
  (stop!)
  (restart!))
