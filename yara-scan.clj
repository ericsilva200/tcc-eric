(ns yara-scanner-in-clojure.yara-scan
  (:require [clojure.java.shell :as shell]
            [clojure.string :as str]))

(defn yara-scan
  "Yara Scan in Clojure"
  [file]
  (let [start-ns (System/nanoTime)]
    (try
      (let [file-path (.getAbsolutePath file)
            file-name (.getName file)
            extension (second (re-find #"\.([^.]+)$" file-name))
            yara-result (shell/sh "yara"
                                  "-d" (str "filepath=" file-path)
                                  "-d" (str "filename=" file-name)
                                  "-d" (str "extension=" extension)
                                  "yara_rules/index.yar"
                                  file-path)
            elapsed-ms (long (/ (- (System/nanoTime) start-ns) 1000000.0))]
        {:status (if (= 0 (:exit yara-result)) :scanned :error)
         :matches (when (= 0 (:exit yara-result))
                    (->> (str/split-lines (:out yara-result))
                         (remove str/blank?)
                         vec))
         :error (when-not (= 0 (:exit yara-result))
                  (or (:err yara-result) "Unknown error"))
         :elapsed-ms elapsed-ms})
      (catch Exception e
        (let [elapsed-ms (long (/ (- (System/nanoTime) start-ns) 1000000.0))]
          (println (str "Error executing yara scan: " (ex-message e)))
          (throw (ex-info (str "Error executing yara scan: " (ex-message e))
                          {:elapsed-ms elapsed-ms} e)))))))

(defn -main [& args]
  (if-let [target (first args)]
    (prn (yara-scan (java.io.File. target)))
    (do
      (binding [*out* *err*]
        (println "Uso: clojure -M -m yara-scanner-in-clojure.yara-scan <arquivo>"))
      (System/exit 1))))

(apply -main *command-line-args*)