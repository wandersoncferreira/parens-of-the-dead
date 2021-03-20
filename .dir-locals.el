((nil
  (cider-default-cljs-repl . shadow-select)
  (cider-preferred-build-tool . clojure-cli)
  (cider-known-endpoints . (("localhost" "8777")))
  (cider-clojure-cli-global-options . "-A:dev:test")
  (cider-ns-refresh-after-fn . "user/go")
  (cider-ns-refresh-before-fn . "user/stop")
  (tgt-projects . (((:root-dir "/home/wanderson/arch/study/parens-of-the-dead")
                    (:src-dirs "src")
                    (:test-dirs "test")
                    (:test-suffixes "_test")))))
 (emacs-lisp-mode
  (flycheck-disabled-checkers . "emacs-lisp-checkdoc")))
