(ns ultra-chat.handlers
  (:require [clojure.data.json :as json]
            [hiccup.core :as hc]
            [hiccup.form :as hf]
            [hiccup.page :as hp]
            [org.httpkit.server :as http-kit]
            [ring.util.response :as rr])
  (:import [java.util.concurrent Executors]
           [java.util.concurrent TimeUnit]))

(defn page [& content]
  {:status 200
   :headers {"Content-type" "text/html"}
   :body (hp/html5 [:head
                    [:meta {:charset "utf-8"}]
                    [:meta {:name "viewport"
                            :content "width=device-width,initial-scale=1"}]
                    [:title "Ultra chat"]
                    (hp/include-css "css/screen.css")]
                   [:body content])})

(defonce messages (atom (list)))

(comment
  @messages
  (reset! messages (list)))

(defonce channels (atom #{}))

(defn list-item [message]
  [:li [:pre (hc/h message)]])

(defn send-message! [message]
  (doseq [channel @channels]
    (http-kit/send! channel {:headers {"Content-Type" "text/event-stream"}
                             :status 200
                             :body (str "event: message\ndata: " (-> message
                                                                     list-item
                                                                     hc/html
                                                                     json/write-str) "\n\n")} false)))

(defn send-keep-alive! []
  (doseq [channel @channels]
    (http-kit/send! channel {:headers {"Content-Type" "text/event-stream"}
                             :status 200
                             :body ":keepalive \n\n"} false)))

(.scheduleAtFixedRate (Executors/newScheduledThreadPool 1) send-keep-alive! 0 1 TimeUnit/MINUTES)

(comment
  @channels
  (reset! channels #{})
  (send-message! "tester123"))

(comment
  (json/write-str (hc/html (list-item "test1\n\ntest2<script>"))))

(defn render-landing-page [_]
  (page
   [:h1 "Ultra chat"]
   [:p "What do you want to say?"]
   (hf/form-to
    [:post "/message"]
    (hf/text-area "message")
    (hf/submit-button "Submit"))
   [:ul {:id "message-list"}
    (for [message @messages]
      (list-item message))]
   (hp/include-js "js/events.js")))

(defn accept-message [request]
  (when-let [message (get-in request [:params :message])]
    (swap! messages conj message)
    (send-message! message)
    (rr/redirect "/" :see-other)))

(defn message-stream [request]
  (http-kit/as-channel
   request
   {:on-open (fn [channel] (swap! channels conj channel))
    :on-close (fn [channel _] (swap! channels disj channel))}))
