(IN-PACKAGE CL-LIB)(version-reporter "CL-LIB-FNS-Set Fns" 5 0 ";; Time-stamp: <2008-05-03 13:33:57 gorbag>"                   "CVS: $Id: cl-sets.lisp,v 1.2 2008/05/03 17:42:00 gorbag Exp $restructured version");; This portion of CL-LIB Copyright (C) 1984-2008 Bradford W. Miller and the ;;                                                Trustees of the University of Rochester;; ;; This library is free software; you can redistribute it and/or modify it under the terms of the GNU ;; Lesser General Public License as published by the Free Software Foundation; either version 3.0 of ;; the License, or (at your option) any later version.;; This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; ;; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. ;; See the GNU Lesser General Public License for more details.;; You should have received a copy of the GNU Lesser General Public License along with this library; ;; if not, see <http://www.gnu.org/licenses/>.;;;; Note the local extensions remain in cl-extensions. <miller>;;;;; ****************************************************************;;;; Extensions to Common Lisp **************************************;;;; ****************************************************************;;;;;;;; This file is a collection of extensions to Common Lisp. ;;;;;;;; It is a combination of the CL-LIB package copyleft by Brad Miller;;;; <miller@cs.rochester.edu> and a similar collection by;;;; Mark Kantrowitz <mkant+@cs.cmu.edu>.;;;;;;;; The following functions were originally from CL-LIB:;;;;   let-if, factorial, update-alist, truncate-keywords, while,;;;;   defclass-x, copy-hash-table, defflag, round-to, extract-keyword;;;;   let*-non-null, mapc-dotted-list, ;;;;   mapcar-dotted-list, mapcan-dotted-list, some-dotted-list, ;;;;   every-dotted-list, msetq, mlet, dosequence, force-string, prefix?,;;;;   elapsed-time-in-seconds, bit-length, flatten, ;;;;   sum-of-powers-of-two-representation, ;;;;   difference-of-powers-of-two-representation,;;;;   ordinal-string, between, ;;;;   cond-binding-predicate-to <quiroz@cs.rochester.edu>;;;;   remove-keywords <baldwin@cs.rochester.edu>;;;;;;;; The following functions were contributed by Mark Kantrowitz:;;;;   circular-list, dofile, seq-butlast, seq-last, firstn, in-order-union;;;;   parse-with-delimiter, parse-with-delimiters, string-search-car,;;;   string-search-cdr, parallel-substitute, lisp::nth-value,;;;;   parse-with-string-delimiter, parse-with-string-delimiter*,;;;;   member-or-eq, number-to-string, null-string, time-string.;;;;   list-without-nulls, cartesian-product, cross-product, permutations;;;;   powerset, occurs, split-string, format-justified-string, ;;;;   eqmemb, neq, car-eq, dremove, displace, tailpush, explode,;;;;   implode, crush, listify-string, and-list, or-list, lookup,;;;;   make-variable, variablep, make-plist, make-keyword;;;;   ;;;; The GNU Emacs distribution agreement is included by reference.;;;; Share and Enjoy!;;;;;;;; ********************************;;; Sets ***************************;;; ********************************;;; list-without-nulls;;; cross-product;;; cartesian-product;;; permutations(defun list-without-nulls (list)  "Returns a copy of list with all null elements removed."  (let* ((head (list nil))         (tail head))    (loop     (if (null list)	 (return-from list-without-nulls (cdr head))	 (when (car list)	   (rplacd tail (list (car list)))	   (setf tail (cdr tail))))     (setf list (cdr list)))))(defun cartesian-product (set1 set2)  "Returns the cross product of two sets."  (let ((result ()))    (dolist (elt1 set1)      (dolist (elt2 set2)        (push (cons elt1 elt2) result)))    result))(defun cross-product (&rest lists)  "Returns the cross product of a set of lists."  (labels ((cross-product-internal (lists)	     (if (null (cdr lists))		 (mapcar #'list (car lists))		 (let ((cross-product (cross-product-internal (cdr lists)))		       (result '()))		   (dolist (elt-1 (car lists))		     (dolist (elt-2 cross-product)		       (push (cons elt-1 elt-2) result)))		   result))))    (cross-product-internal lists)))(defun permutations (items)  "Given a list of items, returns all possible permutations of the list."  (let ((result nil))    (if (null items)        '(nil)        (dolist (item items result)          (dolist (permutation (permutations (remove item items)))            (push (cons item permutation) result))))))(defun powerset (list)  "Given a set, returns the set of all subsets of the set."  (let ((result (list nil)))    (dolist (item list result)      (dolist (subset result)	(push (cons item subset) result)))))#-lispm(defun circular-list (&rest list)  "Creates a circular list of the arguments. Handy for use with    the list mapping functions. For example,      (mapcar #'+ '(1 2 3 4 5) (circular-list 3)) --> (4 5 6 7 8)     (mapcar #'+ '(1 2 3 4 5) (circular-list 0 1)) --> (1 3 3 5 5)"  (setf list (copy-list list))  (setf (cdr (last list)) list)  list)(defun occurs (elt lst)  "Returns T if ELT occurs somewhere in LST's tree structure."  (cond ((null lst)         nil)        ((consp lst)         ;; This walks down the tree structure of LST.         (or (occurs elt (car lst))             (occurs elt (cdr lst))))        ((atom lst)         ;; If we are at a leaf, test if ELT is the same as the leaf.         (eq lst elt))))(defun firstn (list &optional (n 1))  "Returns a new list the same as List with only the first N elements."  (cond ((> n (length list)) list)	((< n 0) nil)	(t (ldiff list (nthcdr n list)))))(defun in-order-union (list1 list2)  "Append and remove duplicates. Like union, but the objects are   guarranteed to stay in order."  (remove-duplicates (append list1 list2) :from-end t));;;;;; The following is contributed by miller@cs.rochester.edu;; Fast versions of the commonlisp union and intersection, that want;; and return sorted lists.  rewrite for more speed 6/1/93 by miller.(defun fast-union (list1 list2 predicate &key (test #'eql) (key #'identity))  "Like Union (but no support for test-not) should be faster becauselist1 and list2 must be sorted.  Fast-Union is a Merge that handlesduplicates. Predicate is the sort predicate."  (declare (type list list1 list2))  (let (result result1	(wlist1 list1)	(wlist2 list2))    (while (and wlist1 wlist2)      (cond       ((funcall test (funcall key (car wlist1)) (funcall key (car wlist2)))        (setq result1 (nconc result1 (list (pop wlist1))))        (pop wlist2))       ((funcall predicate (funcall key (car wlist1)) (funcall key (car wlist2)))        (setq result1 (nconc result1 (list (pop wlist1)))))       (t        (setq result1 (nconc result1 (list (pop wlist2))))))      (if (null result) (setq result result1)))    (cond      (wlist1       (nconc result wlist1))      (wlist2       (nconc result wlist2))      (t       result))))(defun fast-intersection (list1 list2 predicate &key (test #'eql) (key #'identity))  "Like Intersection (but no support for test-not) should be fasterbecause list1 and list2 must be sorted.  Fast-Intersection is avariation on Merge that handles duplicates. Predicate is the sortpredicate."  (declare (type list list1 list2))  (let (result result1	(wlist1 list1)	(wlist2 list2))    (while (and wlist1 wlist2)      (cond	((funcall test (funcall key (car wlist1)) (funcall key (car wlist2)))	 (setq result1 (nconc result1 (list (pop wlist1))))	 (pop wlist2))	((funcall predicate (funcall key (car wlist1)) (funcall key (car wlist2)))	 (pop wlist1))	(t	 (pop wlist2)))      (if (null result) (setq result result1)))    result))