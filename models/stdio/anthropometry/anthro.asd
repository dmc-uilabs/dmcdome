;;Time-stamp: <2016-05-23 17:34:43 Miller(on Nsk1200019714M)>
(defpackage :anthro-asd
  (:use :common-lisp :asdf))

(in-package :anthro-asd)

;; SVN: $Header: $

;; what is needed to load a stdio service
;;
;; clone and own to wrap your own stdio service
;;
;; No copyright here - nothing particularly exciting, feel free to reuse for your own work.
;; Consider in the PUBLIC DOMAIN

;; NB: in my system, asdf is part of the world dump (provided by LispWorks) with minor enhancements.
;; You may need to download and install asdf to use this file.
;;
;; More information on installing ASDF is provided here as of 11/24/15:
;; https://en.wikibooks.org/wiki/Common_Lisp/External_libraries/ASDF/Installing_ASDF

(asdf:oos 'asdf:load-op :xmls)
(asdf:oos 'asdf:load-op :stdiosvc)

(defsystem :anthro
    :serial t
    :depends-on (:stdiosvc)
    :components ((:file "anthro-defs")
                 (:file "anthro-data")
                 (:file "anthropometry")))
