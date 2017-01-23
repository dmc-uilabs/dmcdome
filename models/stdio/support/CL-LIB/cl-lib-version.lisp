(in-package cl-lib)

;; load this AFTER :cl-lib-essentials

;; when updating the functions put this line before the CL-LIB one (so the time stamp is updated).
(cl-lib-essentials:version-reporter "CL-LIB-FNS" 5 14
                                    ";; Time-stamp: <2011-10-19 10:08:10 millerb>"
                                    "CVS: $Id: cl-lib-version.lisp,v 1.11 2011/12/17 03:13:58 millerb Exp $
;; detailed-version-reporter")


;; when updating the library (new package) put this line before the CL-LIB-FNS one (so the time stamp is updated).
(cl-lib-essentials:version-reporter "CL-LIB" 5 13
                                    ";; Time-stamp: <2011-11-16 17:30:02 millerb>" 
                                    "CVS: $Id: cl-lib-version.lisp,v 1.11 2011/12/17 03:13:58 millerb Exp $
;; transcripts: *supporess-logs*")


(pushnew :cl-lib *features*)
(pushnew :cl-lib-5 *features*)

