 ;; Time-stamp: <2016-05-24 13:35:06 Miller(on Nsk1200019714M)>
;; This file should be cloned and owned - customized to load your specific service definition along with stdiosvc.
;; don't forget to override variables in stdiosvc-dumper like *dump-image-name* to make it your own...
(in-package cl-user)
(load-all-patches)
(require "delivery")

;; asdf customizations
(load "~/DOME/models/stdio/support/asdf-customizations")
(setq *asdf-pathname* "~/Lisp/FreeDev/asdf")
(setq *init-file-name* nil) ; don't run user initializations
(setq asdf:*central-registry* '(*default-pathname-defaults*))

(setq *asdf-dirs* '("~/DOME/models/stdio/"
                    "~/DOME/models/stdio/support/"
                    "~/DOME/models/stdio/anthropometry/"
                    ))

(update-asdf-central-registry)

;; cl-lib
#-cl-lib
(progn
  (load "~/Lisp/FreeDev/CL-LIB/defsystem-lispworks.lisp") ;; CL-LIB defsystem
  (lw:load-system :cl-lib))
#-cl-lib-console(lw:load-system :cl-lib-console)
#-cl-lib-better-errors(lw:load-system :cl-lib-better-errors)
#-cl-lib-transcripts(lw:load-system :cl-lib-transcripts)

;; load captive services

(asdf :anthro)
;; at this point variables should be overridden for the specific services you are defining
                                        ;(setq *dump-image-name* "generic-stdiosvc")
                                        ;(setq *install-directory* "~/DOME/models/stdio/support/")
                                        ;etc..

(setq *dump-image-name* "anthropometry")
(setq *install-directory* "~/DOME/models/stdio/anthropometry/")

;; should now be defined...
(dump-stdiosvc :level 3
               :keep-complex-numbers nil
               :kill-dspec-table nil) ; we use advice in cl-lib

;; invoke as (eg)
;; cd "/Applications/Lispworks 7.0 (64-bit)/Lispworks (64-bit).app/Contents/MacOS"
;; ./lispworks-7-0-0-x86-darwin -build deliver.lisp
