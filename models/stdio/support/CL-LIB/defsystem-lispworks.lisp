(in-package cl-user);; Time-stamp: <2011-05-16 09:13:37 millerb>     ;; CVS: $Id: defsystem-lispworks.lisp,v 1.4 2011/05/17 00:16:35 gorbag Exp $;; This portion of CL-LIB Copyright (C) 2000-2008 Bradford W. Miller;; ;; This library is free software; you can redistribute it and/or modify it under the terms of the GNU ;; Lesser General Public License as published by the Free Software Foundation; either version 3.0 of ;; the License, or (at your option) any later version.;; This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; ;; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. ;; See the GNU Lesser General Public License for more details.;; You should have received a copy of the GNU Lesser General Public License along with this library; ;; if not, see <http://www.gnu.org/licenses/>.#-lispworks(eval-when (compile load eval)  (error "improper specialized defsystem file loaded."));; miller - Brad Miller (miller@cs.rochester.edu) (now bradfordmiller@mac.com);; new fractured cl-lib, with multiple defsystems for each package. So the "right ones" can be added as needed.(eval-when (:load-toplevel :execute)  (unless (boundp '*CL-LIB-BASE*)    (defvar *CL-LIB-BASE* (or (getenv "CL_LIB_BASE") "~/Lisp/freedev/CL-LIB/")))  (setf (logical-pathname-translations "CL-LIB")        `(("NULL;*.*.*"           ,(make-pathname :host (pathname-host *cl-lib-base*)                           :device (pathname-device *cl-lib-base*)                           :directory (pathname-directory *cl-lib-base*)))          ("**;*.*.*"           ,(make-pathname :host (pathname-host *cl-lib-base*)                           :device (pathname-device *cl-lib-base*)                           :directory (append (pathname-directory *cl-lib-base*)                                              (list :wild-inferiors))))          ("NULL;*.*"           ,(make-pathname :host (pathname-host *cl-lib-base*)                           :device (pathname-device *cl-lib-base*)                           :directory (pathname-directory *cl-lib-base*)))          ("**;*.*"           ,(make-pathname :host (pathname-host *cl-lib-base*)                           :device (pathname-device *cl-lib-base*)                           :directory (append (pathname-directory *cl-lib-base*)                                              (list :wild-inferiors)))))))(defun reload-cl-lib-defsystem ()  #+lispworks  (load "cl-lib:defsystem-lispworks")  #-lispworks  :dont-know-filename);;; For a History of recent changes, see the file cl-lib-news in this directory.(defsystem :cl-lib-essentials    (:default-pathname "CL-LIB:functions;"        :documentation "Administrative Features of CL-LIB")  :members ("cl-lib-defpackage"            "cl-lib:packages;initializations"            "cl-lib-essentials")  :rules ((:in-order-to :compile :all (:requires (:load :previous)))))(defsystem :cl-lib-console    (:default-pathname "CL-LIB:packages;"        :documentation "Console package for CL-LIB")  :members ((:cl-lib-essentials :type :system)            "popup-console")  :rules ((:in-order-to :compile :all (:requires (:load :previous)))))(defsystem :cl-lib    (:default-pathname "CL-LIB:functions;"        :documentation "Collected CL-LIB Functions")  :members ((:cl-lib-essentials :type :system)            "cl-extensions"            "keywords"            "cl-list-fns"            "cl-sets"            "cl-array-fns"            "cl-map-fns"            "cl-boolean"            "clos-extensions"            "strings"            "number-handling"            "files"            #+clim            "cl-lib:compatibility;clim-extensions"            "cl-lib:null;cl-lib-version")  :rules ((:in-order-to :compile :all (:requires (:load :previous)))))(defsystem :cl-lib-scheme-streams    (:default-pathname "CL-LIB:packages;")  :members ((:cl-lib-essentials :type :system)            "scheme-streams")  :rules ((:in-order-to :compile :all (:requires (:load :previous)))))(defsystem :cl-lib-queues    (:default-pathname "CL-LIB:packages;")  :members ((:cl-lib-essentials :type :system)            "queues")  :rules ((:in-order-to :compile :all (:requires (:load :previous)))))(defsystem :cl-lib-locatives    (:default-pathname "CL-LIB:packages;")  :members ((:cl-lib-essentials :type :system)            "locatives")  :rules ((:in-order-to :compile :all (:requires (:load :previous)))))(defsystem :cl-lib-resources    (:default-pathname "CL-LIB:packages;")  :members ((:cl-lib-essentials :type :system)            "resources")  :rules ((:in-order-to :compile :all (:requires (:load :previous)))))(defsystem :cl-lib-chart    (:default-pathname "CL-LIB:packages;")  :members ((:cl-lib-essentials :type :system)            "chart")  :rules ((:in-order-to :compile :all (:requires (:load :previous)))))(defsystem :cl-lib-transcripts    (:default-pathname "CL-LIB:packages;")  :members ((:cl-lib-essentials :type :system)            "transcripts")  :rules ((:in-order-to :compile :all (:requires (:load :previous)))))(defsystem :cl-lib-prompt-and-read    (:default-pathname "CL-LIB:packages;")  :members ((:cl-lib-essentials :type :system)            "prompt-and-read")  :rules ((:in-order-to :compile :all (:requires (:load :previous)))))(defsystem :cl-lib-better-errors    (:default-pathname "CL-LIB:packages;")  :members ((:cl-lib-essentials :type :system)            "better-errors")  :rules ((:in-order-to :compile :all (:requires (:load :previous)))))(defsystem :cl-lib-syntax    (:default-pathname "CL-LIB:packages;")  :members ((:cl-lib-essentials :type :system)            "syntax")  :rules ((:in-order-to :compile :all (:requires (:load :previous)))))(defsystem :cl-lib-nregex    (:default-pathname "CL-LIB:packages;")  :members ((:cl-lib-essentials :type :system)            "nregex")  :rules ((:in-order-to :compile :all (:requires (:load :previous)))))(defsystem :cl-lib-reader  (:default-pathname "CL-LIB:packages;")  :members ((:cl-lib-essentials :type :system)            "reader")  :rules ((:in-order-to :compile :all (:requires (:load :previous)))))(defsystem :cl-lib-clos-facets    (:default-pathname "CL-LIB:packages;")  :members ((:cl-lib-essentials :type :system)            "clos-facets"            "clos-facet-defs"            ;; while debugging            "clos-facets-tests")  :rules ((:in-order-to :compile :all (:requires (:load :previous)))          (:in-order-to :compile ("clos-facet-defs" "clos-facets-tests")                        (:caused-by (:compile "clos-facets"))                        (:requires (:load "clos-facets")))))(defsystem :cl-lib-logic-parser    (:default-pathname "CL-LIB:packages;")  :members ((:cl-lib-essentials :type :system)            "logic-parser")  :rules ((:in-order-to :compile :all (:requires (:load :previous)))))(defsystem :lispdoc    (:default-pathname "CL-LIB:packages;")  :members ((:cl-lib-essentials :type :system)            "lispdoc")  :rules ((:in-order-to :compile :all (:requires (:load :previous)))))(defsystem :cl-lib-all    (:default-pathname "CL-LIB:packages;"     :documentation "Common Lisp Library (was minimal CL-LIB)")  :members ((:cl-lib-essentials :type :system)            (:cl-lib :type :system)            (:cl-lib-console :type :system)            (:cl-lib-scheme-streams :type :system)            (:cl-lib-queues :type :system)            (:cl-lib-better-errors :type :system)            (:cl-lib-prompt-and-read :type :system)            (:cl-lib-locatives :type :system)            (:cl-lib-resources :type :system)            (:cl-lib-syntax :type :system)            (:cl-lib-nregex :type :system)            (:cl-lib-chart :type :system)            (:cl-lib-transcripts :type :system)            (:cl-lib-reader :type :system)            (:cl-lib-clos-facets :type :system)            (:cl-lib-logic-parser :type :system)            (:lispdoc :type :system)))