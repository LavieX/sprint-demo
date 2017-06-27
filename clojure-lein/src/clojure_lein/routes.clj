(ns clojure-lein.routes
  (:require [clojure-lein.handlers.metrics :as metrics]
            [clojure-lein.middleware.json :as json]
            [clojure.pprint :as pprint]
            [compojure.core :refer :all]
            [compojure.handler :refer [api]]
            [compojure.route :as route]
            [ring.logger :as logger]))


(defn debug [handler]
  (fn [request]
    (handler request)))

(defroutes metrics-routes
  (GET "/healthcheck" [] metrics/healthcheck))

(defroutes all-routes
  (GET "/" [] "<h1>Hello World!</h1>")
  (context "/metrics" []
           metrics-routes)
  (route/not-found "<h1>Page not found</h1>"))

(defn build-handler []
  (-> (api all-routes)
      json/wrap-json-response
      json/wrap-content-type
      (logger/wrap-with-logger {:printer :no-color})))
