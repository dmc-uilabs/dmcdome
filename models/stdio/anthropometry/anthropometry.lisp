(in-package cl-user)

(cl-lib:version-reporter "anthropometry" 0 3
                         ";; Time-stamp: <2016-05-27 14:09:10 Miller(on Nsk1200019714M)>"
                         ";; multiple return values"
                         *stdiosvc-version-reporter-string*
                         *stdiosvc-version-reporter-initializations*)

;; Version
;; 0.0        5-4-2016   clone and own
;; 0.1        5-23-2016  initial
;; 0.2        5-23-2016  add lookup percentile by value
;; 0.3        5-24-2016  adjust service description for multiple return values

;; implementation of a service to report anthropometry data
;; from Antropometric Data 4/21/06  from  http://www.theergonomicscenter.com
;; downloaded 4/29/16 from cached google search

;; a copy of which appears in this directory.

;; (C) 2016 General Electric, permission granted to copy under terms negotiated with DMC

;; Author: Bradford Miller

;; CAVEAT: Currently we interpolate when given a percentile not in the table, however, query by value selects the next
;; higher value in the table rather than interpolating (so a value that is in the 51st percentile will return 55 as
;; the table only has values for 50 and 55).

(in-package :stdiosvc)

(defservice (anthropometry data-interpolator ((measurement (member :elbow-rest-height-sitting
                                                                   :elbow-wrist-length))
                                              (gender (member gender-m gender-f))
                                              (measure (member cm in))
                                              ;; optional
                                              (percentile t (integer 1 99))
                                              (value t float))
                                              
                           (n      mean  std-d max   min   percentile      value)
                           (fixnum float float float float (integer 0 100) float))
    "either return the tabular data specified along with mean, etc. or given a value, estimate the percentile."

  (let* ((table (cdr (assoc measurement *anthro-data-alist*)))
         (accessor-fn (intern (format nil "~A-~A" gender measure) (find-package :stdiosvc))))
    (let ((max (funcall accessor-fn (maximum table)))
          (min (funcall accessor-fn (minimum table))))
      (list  ;; note we have to report the results in the same order as declared (for now anyway)
       ;n
       (if (eql gender 'gender-f)
         (car (n table))
         (cdr (n table)))
       ;mean 
       (funcall accessor-fn (mean table))
       ;std-d 
       (funcall accessor-fn (std-d table))
       ;max 
       max
       ;min 
       min
       ;percentile 
       (cond
        (percentile
         percentile)
        ;; supplied value, calculate percentile
        ((<= value min)
         0)
        ((>= value max)
         100)
        (t
         (nth (position value (percentiles table) :test-not #'> :key accessor-fn) +percentiles+)))
       ;value 
       (cond
        ((and percentile
              (position percentile +percentiles+))
         (funcall accessor-fn
                  (nth (position percentile +percentiles+)
                       (percentiles table))))
        (percentile
         ;; interpolate
         (let* ((high-element (position percentile +percentiles+ :test-not #'>))
                (low-element (1- high-element))
                                        ; (diff-high (- (nth high-element +percentiles+) percentile))
                (diff-low (- percentile (nth low-element +percentiles+)))
                (spread (- (nth high-element +percentiles+) (nth low-element +percentiles+)))
                (data-spread (- (funcall accessor-fn (nth high-element (percentiles table)))
                                (funcall accessor-fn (nth low-element (percentiles table))))))
           (+ (* (/ diff-low spread) data-spread)
              (funcall accessor-fn (nth low-element (percentiles table))))))
        (t
         value))))))


;; sample calls:

#||
(xml-tester "<request service=\"anthropometry\"> <param name=\"measurement\" value=\":elbow-rest-height-sitting\"/> <param name=\"gender\" value=\"gender-f\"/> <param name=\"measure\" value=\"cm\"/> <param name=\"percentile\" value=\"92\"/></request>") ; note it will have to interpolate

(xml-tester "<request service=\"anthropometry\"> <param name=\"measurement\" value=\":elbow-rest-height-sitting\"/> <param name=\"gender\" value=\"gender-f\"/> <param name=\"measure\" value=\"cm\"/> <param name=\"value\" value=\"25.1\"/></request>") ; between 85 and 90th percentile

||#

;; or for cut/paste to dumped version:

;; <request service="anthropometry"> <param name="measurement" value=":elbow-rest-height-sitting"/> <param name="gender" value="gender-f"/> <param name="measure" value="cm"/> <param name="percentile" value="92"/></request>

;;<request service="anthropometry"> <param name="measurement" value=":elbow-rest-height-sitting"/> <param name="gender" value="gender-f"/> <param name="measure" value="cm"/> <param name="value" value="25.1"/></request>
