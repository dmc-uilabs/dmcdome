(in-package :clos-facets)

(cl-lib:version-reporter "CL-LIB-CLOS-Facets-Tests" 0 2 
                         ";; Time-stamp: <2011-10-21 10:34:55 millerb>" 
                         "CVS: $Id: clos-facets-tests.lisp,v 1.3 2011/11/04 14:10:52 gorbag Exp $
;; development - note warnings on compile are OK")

;; This portion of CL-LIB Copyright (C) 2003-2008 Bradford W. Miller
;; 
;; This library is free software; you can redistribute it and/or modify it under the terms of the GNU 
;; Lesser General Public License as published by the Free Software Foundation; either version 3.0 of 
;; the License, or (at your option) any later version.

;; This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
;; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
;; See the GNU Lesser General Public License for more details.

;; You should have received a copy of the GNU Lesser General Public License along with this library; 
;; if not, see <http://www.gnu.org/licenses/>.

;; test facets library

;; someplace to put instances we will create
(defvar *mumble* ())
(defvar *frotz* ())
(defvar *foo* ())
(defvar *bar* ())
(defvar *bletch* ())


(deftype hack-valid-symbol () (member 'a 'b 'c 'd 'e))

;; some hack classes that illustrate facet uasage

;; mumble lets us test the basics: value-type and multiple slot-values.

(defclass mumble ()
  ((mumble-slota :initarg :a :accessor slota :value-type hack-valid-symbol) ; check value-type
   (mumble-slotb :initarg :b :reader slotb-reader :writer slotb-writer ; check readers and writers
                 :slot-values :single :value-type hack-valid-symbol)
   ;; check many-values
   (mumble-slotc :initarg :c :accessor slotc :slot-values :multiple :value-type hack-valid-symbol)))

;; frotz adds cardinality checks

;; foo and bar check slot inverses

;; bletch makes use of denotational functions, testing everything together.

(eval-when (:compile-toplevel)
  (format *error-output* "Expect two compiler warnings when compiling facet-tester:
;;;*** Warning in (SUBFUNCTION 3 CLOS-FACETS::FACET-TESTER): CLOS-FACETS::SLOTB-READER is called with the wrong number of arguments: Got 2 wanted 1
;;;*** Warning in (SUBFUNCTION 3 CLOS-FACETS::FACET-TESTER): (SETF CLOS-FACETS::SLOTA) is called with the wrong number of arguments: Got 3 wanted 2
--- this is due to the test intentionally attempting to perform a multi-value operation on a single value slot."))

(defun facet-tester (&optional verbose-p)
  (flet ((tester (testname initfn testfn failurefn)
           (if verbose-p 
               (format t "~%;; Starting ~A" testname))
           (funcall initfn)
           (if verbose-p
               (format t "~%;; Testing ~A expecting success" testname))
           (unless (funcall testfn)
             (format t "~%;;; Test ~A FAILED (positive arm)" testname))
           (if verbose-p
               (format t "~%;; Testing ~A expecting failure (continue to continue with the tests)"))
           (unless (funcall failurefn)
             (format t "~%;;; Test ~A FAILED (negative arm)" testname))))

    (macrolet ((etest (expected-error error-generator)
                 `(handler-case ,error-generator
                   (,expected-error () t) ; successfully had an error/warning
                   (:no-error () nil)))) ; fail

      (tester "Value-Type and Multiple Values Value-Type"
              ;; init
              #'(lambda () 
                  (setq *mumble* (make-instance 'mumble :a 'a :b 'b)))
              ;; positive tests (all should work, return non-nil if OK
              #'(lambda () 
                  (and (equal (slota *mumble*) 'a)
                       (equal (slotb-reader *mumble*) 'b)
                       (progn
                         (slotb-writer 'e *mumble*)
                         (setf (slota *mumble*) 'd)
                         (and
                          (equal (slota *mumble*) 'd)
                          (equal (slotb-reader *mumble*) 'e)))
                       (progn
                         (setf (slotc *mumble*) 'a)
                         (setf (slotc *mumble*) 'b)
                         (setf (slotc *mumble*) 'c)
                         (and (slot-boundp *mumble* 'mumble-slotc 2)
                              (slot-boundp *mumble* 'mumble-slotc 0)
                              (equal (slotc *mumble* 0) 'a)
                              (equal (slotc *mumble* 1) 'b)
                              (equal (slotc *mumble* 2) 'c)
                              (equal (slot-value *mumble* 'mumble-slotc 2) 'c)))
                       (progn
                         (setf (slotc *mumble* 1) 'd)
                         (and (equal (slotc *mumble* 0) 'a)
                              (equal (slotc *mumble* 1) 'd)
                              (equal (slotc *mumble* 2) 'c)))
                       (progn
                         (slot-makunbound *mumble* 'mumble-slotc 0)
                         (and (equal (slotc *mumble* 0) 'd)
                              (equal (slotc *mumble* 1) 'c)
                              (handler-case (slotc *mumble* 2)
                                (unbound-slot () t))))
                       ))
              ;; negative tests (all should fail, get caught, and return non-nil if OK
              #'(lambda () 
                  (and (etest value-type-error (slotb-writer 'g *mumble*))
                       (etest value-type-error (setf (slota *mumble*) 7)) 
                       (etest value-type-error (setf (slotc *mumble*) 9))
                       (etest unbound-slot (slotc *mumble* 8))
                       (etest no-multiple-slotvalues (slotb-reader *mumble* 2)) ;; should give an error on compile!
                       (etest no-multiple-slotvalues (setf (slota *mumble* 3) 'c))))) ;; should give an error on compile! (wrong number of args - slota is not multi-valued)
      )))
