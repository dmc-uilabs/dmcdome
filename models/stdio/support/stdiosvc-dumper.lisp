(in-package cl-user)
(defvar *stdiosvc-version-reporter-initializations* nil)
(defparameter *stdiosvc-version-reporter-string* "~&;; ~A~50t~A~80t~D.~D~%;;~2t(~A)~%" "Default control string to announce the version")
(cl-lib:version-reporter "Stdiosvc" 0 5
                         ";; Time-stamp: <2016-05-24 13:37:02 Miller(on Nsk1200019714M)>" ; this line within time-stamp-line-limit lines of top so emacs sees it and updates.
                         ";; don't shake stdiosvc package on delivery"
                         *stdiosvc-version-reporter-string*
                         *stdiosvc-version-reporter-initializations*)

;; Version
;; 0.0        3-24-2016  clone and own
;; 0.1        4-13-2016  set up exports
;; 0.2        4-14-2016  export parse-and-dispatch-service-request, report-service-result
;; 0.3        4-15-2016  add code for dumper, advise cl-lib:do-report-version to use xml
;; 0.4        4-28-2016  add ability for dumper to take additional arguments
;; 0.5        5-24-2016  never shake the stdiosvc package (because some functions may be called through construction)

;; what is needed to dump a stdiosvc image.

;; (C) 2016 General Electric, permission granted to copy under terms negotiated with DMC

;; Author: Bradford Miller

(defun stdiosvc-announce ()
  "Just a little something to annonce the version number"

  (cl-lib:report-version nil '*stdiosvc-version-reporter-initializations*)) ; us

(eval-when (load eval)
  (cl-lib:add-initialization "Announce stdiosvc version on startup"
                             '(stdiosvc-announce)
                             '(:warm)))

(defpackage :stdiosvc (:use common-lisp :cl-lib :xmls)
            (:export #:service-declaration #:defservice
                     #:write-xml-comment #:write-xml-blank #:write-xml-banner
                     #:announce-service #:announce-captive-services
                     #:report-service-result #:parse-and-dispatch-service-request
                     #:stdiosvc-toplevel))

;; change cl-lib:do-report-version to use xml comments
(defadvice
    (cl-lib-essentials::do-report-version xml-report-1 :around)
    (report-string-symbol date part-name major-version minor-version comments)
  (when (cl-lib-essentials::report-at-depth-p part-name)
    (stdiosvc:write-xml-comment (format nil (symbol-value report-string-symbol) date part-name major-version minor-version comments))))

(defvar *stdiosvc-initializations* nil)

;; set up for dump, inspired by TRAINS code

(defparameter *dump-image-name* "generic-stdiosvc"
  "Bind this to whatever you want to call your image")

(defparameter *install-directory* "~/DOME/models/stdio/support/"
  "Directory to copy the executable image to - need to update dump.sh as well")

(defparameter *toplevel-function* 'stdiosvc:stdiosvc-toplevel
  "Bind this to a toplevel funciton - the provided one should work for services defined with defservice")

(defparameter *original-terminal-io* *terminal-io*)

(defun zoom-and-exit (e)
  "Called after error E has been trapped to cause a stack trace. It uses
LOGGING:*log-stream* if it is non-nil, otherwise writes to the file dm.zoom.
It then calls excl:exit to terminate execution (and so never returns)."
  (format *error-output* "Error: ~A~%" e)
  (finish-output *error-output*)
  (setq system::*top-level-loop-function*
    #'(lambda (&key &allow-other-keys)
        (setq system::*top-level-loop-function* 
              nil)
        (unwind-protect
            (ignore-errors
              (let ((*print-readably* nil)
                    (*print-miser-width* 40)
                    (*print-circle* t)
                    (*print-pretty* t)
                    (stream (or transcripts:*crash-log-stream* ;; was logging 6/29/05 BWM
                                (open (format nil "~A.zoom" *dump-image-name*) :direction :output
                                      :if-does-not-exist :create
                                      :if-exists :append)
                                *error-output*)))
                (let ((*terminal-io* stream)
                      (*standard-output* stream)
                      (*debug-io* stream))
                  (format stream "Error: ~A~%" e)
                  (dbg::simple-output-backtrace)
                  (finish-output stream))))

          (lw:quit :status 0 :confirm nil :ignore-errors-p t)))))

(defun my-restart-function ()
  "This function is passed to `dumplisp' as the :restart-function parameter.
It simply calls the value of *toplevel-function* inside a handler-bind
causes a stack trace on error."
  ;; Rebind most streams to use stderr for output
  (setq *original-terminal-io* *terminal-io*)

  (setq *terminal-io* (make-two-way-stream *standard-input* (system:make-stderr-stream)))

  (progn (setq *standard-output* (make-synonym-stream '*original-terminal-io*))
         (setq *standard-input* (make-synonym-stream '*original-terminal-io*))
         (setq *error-output* (system:make-stderr-stream)))

  ;; print cl-lib version (to stderr)
  ;;  (cl-lib-essentials:report-version)
  (finish-output *error-output*)
  ;; Here we go...
  (handler-bind
   ((error #'(lambda (e)
	       (zoom-and-exit e))))
   (apply *toplevel-function* nil)))

;; call this to dump a delivered image
(defun dump-stdiosvc (&rest other-keys &key (image-name *dump-image-name*) (level 0) &allow-other-keys)
  "Dump a new Lisp image named IMAGE-NAME containing the dm. "

  (setq other-keys (cl-lib:remove-keyword-arg :level (cl-lib:remove-keyword-arg :image-name other-keys))) ; already captured
  ;; Always a good idea to gc so we restart nice and fresh
  (format t "~%; Garbage collecting prior to dumping...~%")
  (hcl:gc-generation :blocking-gen-num)
  ;; Dump the image, which will restart by calling *parser-restart-function*
  (format t "; Dumping image file ~A...~%" image-name)
  (apply #'lw:deliver 'my-restart-function image-name level ; 0 is least compression, 5 is most
         :never-shake-packages (list :stdiosvc)
         :keep-debug-mode :all
         :keep-pretty-printer t
         :multiprocessing t
         other-keys))
