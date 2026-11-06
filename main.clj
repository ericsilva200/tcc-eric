(ns tcc-eric.main
  (:require [clojure.java.io :as io]))

(load-file "yara-scan.clj")

(defn -main [& args]
  (if-let [target (first args)]
    (let [file (io/file target)
          scan-fn (resolve 'yara-scanner-in-clojure.yara-scan/yara-scan)]
      (if scan-fn
        (do
          (println "Executando análise...")
          (prn (scan-fn file)))
        (do
          (binding [*out* *err*]
            (println "Erro: função yara-scan não foi carregada."))
          (System/exit 1))))
    (do
      (binding [*out* *err*]
        (println "Uso: clojure main.clj <arquivo>"))
      (System/exit 1))))

(apply -main *command-line-args*)
