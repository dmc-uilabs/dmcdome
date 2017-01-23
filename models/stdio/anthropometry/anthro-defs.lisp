(in-package cl-user)

(cl-lib:version-reporter "anthropometry-defs" 0 1
                         ";; Time-stamp: <2016-05-23 17:38:07 Miller(on Nsk1200019714M)>"
                         ";; initial models"
                         *stdiosvc-version-reporter-string*
                         *stdiosvc-version-reporter-initializations*)

;; Version
;; 0.0        5-4-2016  clone and own
;; 0.1        5-23-2016 initial models

;; implementation of a service to report anthropometry data
;; from Antropometric Data 4/21/06  from  http://www.theergonomicscenter.com
;; downloaded 4/29/16 from cached google search

;; a copy of which appears in this directory.

;; (C) 2016 General Electric, permission granted to copy under terms negotiated with DMC

;; Author: Bradford Miller

;; the following does not contain ALL the data from the paper (see the .xls file), but selected example tables.
;; Others can be easily implemented using the following as a pattern.

(in-package :stdiosvc)

(defparameter +percentiles+ '(1 2 3 5 10 15 20 25 30 35 40 45 50 55 60 65 70 75 80 85 90 95 97 98 99))

(defclass anthro ()
  ((n :accessor n :initarg :n)
   (mean :accessor mean :initarg :mean)
   (std-d :accessor std-d :initarg :std-d)
   (maximum :accessor maximum :initarg :max)
   (minimum :accessor minimum :initarg :min)
   (percentiles :accessor percentiles :initarg :percentiles)))

(defmacro defanthro (name &rest rest &key n mean std-d max min percentiles)
  (declare (ignorable n mean std-d max min percentiles)
           (special *anthro-data-alist*)) ;; defined in anthro-data
  
  `(update-alist ',name
                 (apply #'make-instance 'anthro ',rest)
                 *anthro-data-alist*))

