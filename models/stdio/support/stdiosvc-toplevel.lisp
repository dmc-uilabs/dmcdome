(in-package cl-user)

(defparameter *version-string* ; this line within time-stamp-line-limit lines of top so emacs sees it and updates.
  ";; Time-stamp: <2016-05-24 11:20:10 Miller(on Nsk1200019714M)>")
(defparameter *version-description*
  ";; multiple results")

(cl-lib:version-reporter "Stdiosvc-toplevel" 0 8
                         *version-string*
                         *version-description*
                         *stdiosvc-version-reporter-string*
                         *stdiosvc-version-reporter-initializations*)

;; Version
;; 0.0        4- 1-2016  new
;; 0.1        4-11-2016  add service-declaration, defservice
;; 0.2        4-13-2016  announce-service
;; 0.3        4-14-2016  make defservice wrap the service (single result)
;; 0.4        4-14-2016  report-service-result, parse-and-dispatch-service-request
;; 0.5        4-15-2016  stdiosvc-toplevel, make sure intern using stdiosvc package
;; 0.6        4-28-2016  turn list classes for paramters (e.g. (member a b c)) into a string before xmlizing it.
;; 0.7        5-23-2016  xml-tester
;; 0.8        5-24-2016  report multiple results, modify result XML to have service name included

;; toplevel for an xml driven stdio service (for linking to DOME)

;; (C) 2016 General Electric, permission granted to copy under terms negotiated with DMC

;; Author: Bradford Miller

(in-package :stdiosvc)

;; toplevel read-eval-print loop 
;; - initially write service description on stdout, then read stdin until eof, parsing xml, 
;;   looking for invocations of the wrapped service.

;; the service definition should supply appropriate descriptions for us, and we can then invoke the function.
;;
;; example:
;;  (defservice (name (argument-descriptor-1... argument-descriptor-n) result-name result-type) description-string  &body)
;;
;;  (defservice (calc-f ((mass real) (accelleration real)) f real) 
;;     "F=ma" 
;;     (* m a)) ;; returns f, rely on dome to get consistent units

;; at this point the types are expected to be lisp types, and we will somehow map to DOME types.

(defvar *debug-stdiosvc* nil
  "default to nil when working")

(defvar *captive-services* nil
  "Updated by service-declaration, so on startup we can output XML indicating how we are called")

(defun service-declaration (name service-type result-name result-type description-string argument-descriptors)
  "where each argument-descriptor is of the form (name type) or (name optional-p type) which isn't quite a 
  lambda-list (which would of course use &optional) but should be simple to parse individually without context, 
  and thus simpler to turn into xml."
  (cl-lib:add-initialization (format nil "Declare service ~S" name)
                             `(update-alist ',name '(:service-type ,service-type
                                                     :args ,argument-descriptors
                                                     :description ,description-string
                                                     :result-name ,result-name
                                                     :result-type ,result-type)
                                            *captive-services*)
                             '(:now)
                             '*stdiosvc-initializations*))

(defun declared-arg-type (name declaration)
  (let* ((args (extract-keyword :args declaration))
         (entry (cdr (assoc name args))))
    (car (last entry))))

(defun report-service-result (service-name result-name result)
  "report results (one or more)"
  (write-prolog '((standalone t)) "DOME service result(s)" *standard-output*)
  (write-xml-blank)
  (cond
   ((listp result-name) ; should be list of results
    (assert (listp result) (result) "Result reported should have been a list")
    (princ (toxml
            `("result" (("service" ,service-name))
              ,@(mapcar #'(lambda (x y)
                            `("param" (("name" ,(string x)) ("value" ,y))))
                       result-name
                       result))
            :indent t)
           *standard-output*))
   ((listp result)
    (error "Cannot report multiple results unless declared in defservice form"))
   (t
    (princ (toxml `("result" (("service" ,service-name))
                    ("param" (("name" ,(string result-name)) ("value" ,result))))
                  :indent t)
           *standard-output*))))

(defun parse-and-dispatch-service-request (stream)
  "a service requestion should look like:
<request service=\"service name\">
<param name=\"param1\" value=\"value1\">
...
</request>"
  (let (xmlrep)
    (while-not (setq xmlrep (parse stream))
       (write-xml-comment "Ill formed XML; flushing to #\Newline")
       (while-not (eql (read-char *standard-input*) #\Newline)))

    (let ((service-name (xmlrep-attrib-value "service" xmlrep))
          (service-parameters (xmlrep-find-child-tags "param" xmlrep))
          lambda-list)

      (let ((function-symbol (intern (string-upcase service-name) (find-package :stdiosvc)))
            (argument-alist (mapcar #'(lambda (param)
                                        (let ((name (string-upcase (xmlrep-attrib-value "name" param)))
                                              (value (xmlrep-attrib-value "value" param)))
                                          (cons name value)))
                                    service-parameters)))

        (let* ((service-description (cdr (assoc function-symbol *captive-services*)))
               (result-name (extract-keyword :result-name service-description)))

          ;; parse each value depending on the type
          (dolist (arg argument-alist)
            (push (make-keyword (car arg)) lambda-list)
            (push (case (declared-arg-type (car arg) service-description)
                    (string
                     (cdr arg))
                    (t
                     (let ((*package* (find-package :stdiosvc)))
                       (read-from-string (cdr arg)))))
                  lambda-list))
          (setq lambda-list (nreverse lambda-list))
          (when *debug-stdiosvc*
            (format t "Ready to invoke ~S on ~S to produce ~S~%" function-symbol lambda-list result-name))
          (report-service-result service-name result-name (apply function-symbol lambda-list)))))))


(defmacro defservice ((name service-type arg-descriptor-list result-name result-type) description-string &body body)
  (let ((optional-arguments)
        (required-arguments))
    (mapc #'(lambda (arg)
              (if (or (endp (cddr arg))
                      (not (second arg)))
                (push arg required-arguments)
                (push arg optional-arguments)))
          arg-descriptor-list)

    `(eval-when (load eval)
       (service-declaration ',name ',service-type ',result-name ',result-type ,description-string ',arg-descriptor-list)
       (defun ,name (&key ,@(mapcar #'car arg-descriptor-list))
         ,description-string

         ;; note there is no need to use the result-type or the
         ;; result-name - the latter will be used when we create the
         ;; xml for returning the result, the former is informational
         ;; to the caller

         ;; check the types of the arguments passed
         ,@(mapcar #'(lambda (arg)
                       `(check-type ,(car arg) ,(car (last arg))))
                   required-arguments)
         ,@(mapcar #'(lambda (arg)
                       `(check-type ,(car arg) (or null ,(car (last arg)))))
                   optional-arguments)

         (let ()
           (declare ,@(mapcar #'(lambda (arg)
                                  `(type ,(car (last arg)) ,(car arg)))
                              required-arguments)
                    ,@(mapcar #'(lambda (arg)
                                  `(type (or null ,(car (last arg))) ,(car arg)))
                              optional-arguments))

           ;; also note we now take are arguments as keywords, it's up
           ;; to the funcitonal def to deal with any declared optional
           ;; arguments or not. We can add an assertion about required
           ;; arguments, however, if needed. (using supplied-p to
           ;; distinguish from NIL if necessary depending on the
           ;; declared TYPE)
           ,@body)))))
         

(defun announce-service (name &key service-type args description result-name result-type)
  (write-xml-blank)
  (write-xml-banner)
  (write-xml-comment description)
  (write-xml-banner)
  (write-xml-blank)

  (princ (toxml `("function" (("name" ,(string name))
                              ("type" ,service-type))
                             ,@(mapcar #'(lambda (param)
                                           `("param"
                                             (("name" ,(string (car param)))
                                              ("optional-p" ,(if (endp (cddr param))
                                                                 nil
                                                                 (second param)))
                                              ("class" ,(progfoo (if (endp (cddr param))
                                                                     (second param)
                                                                     (third param))
                                                                 (if (consp foo)
                                                                     ;; turn into a string for XML
                                                                     (setq foo (format nil "~A" foo))))))))
                                       args)
                             ,(cond 
                               ((listp result-name)
                                `("result" nil
                                  ,@(mapcar #'(lambda (x y)
                                                `("param" (("name" ,(string x))
                                                           ("class" ,(if (listp y) (format nil "~S" y)
                                                                       y)))))
                                            result-name
                                            result-type)))
                               (t
                                `("result" nil
                                  ("param" (("name" ,(string result-name))
                                            ("class" ,(if (listp result-type) (format nil "~S" result-type)
                                                        result-type))))))))
                :indent t)
        *standard-output*))

(defun announce-captive-services ()
  (write-prolog '((standalone t)) "DOME service description" *standard-output*)
  (write-xml-banner)
  (write-xml-comment "stdio service package version: ")
  (write-xml-comment cl-user::*version-string*)
  (write-xml-comment "version description: ")
  (write-xml-comment cl-user::*version-description*)
  (write-xml-banner)

  (dolist (service *captive-services*)
    (apply #'announce-service service)))

(eval-when (load eval)
  (cl-lib:add-initialization "Announce captive services on startup"
                             '(announce-captive-services)
                             '(:warm)))

;; define a default toplevel, inspired by TRAINS
(defun stdiosvc-toplevel ()
  (setq *print-circle* t)
  (let ((*print-length* 80)
        (*print-level* 15)) ; to keep debug output manageable
    (format *error-output* "~&~A: @(#)~A ~A~%"
            (car system:*line-arguments-list*)
            cl-user::*dump-image-name*
            cl-user::*version-string*)
    (unwind-protect
     (catch :die-die-die
       (loop
          (catch :toplevel-input
            (restart-case
                (loop
                   (hcl:gc-generation 2)
                   (parse-and-dispatch-service-request *standard-input*))
              (continue ()
                :report (lambda (x) (format x "Restart ~A at top level input loop" cl-user::*dump-image-name*)))))))
     (dbg::simple-output-backtrace :stream *error-output*)))) ; see why we quit


;;test
#||
;; don't make part of all dumps!

(defservice (hack-adder function ((x integer) (y integer) (z t integer)) result integer)
                 "calculate the sum of two or three numbers"
               (if z
                 (+ x y z)
                 (+ x y)))

||#

(defun xml-tester (string)
  (with-input-from-string (stream string)
    (parse-and-dispatch-service-request stream)))


#||
(announce-captive-services)

(hack-adder :x 1 :y 2)
(hack-adder :x 3 :y 4 :z 5)

;; request without all the quotes (for cut and paste):
;;
;; <request service="hack-adder"> <param name="x" value="1"/> <param name="y" value="2"/> </request>
;; <request service="hack-adder"> <param name="x" value="1"/> <param name="y" value="2"/> <param name="z" value="11"/> </request>
(setq foo (xml-tester "<request service=\"hack-adder\">
                          <param name=\"x\" value=\"1\"/>
                          <param name=\"y\" value=\"2\"/>
                       </request>"))

||#



