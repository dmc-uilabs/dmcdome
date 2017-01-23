;;; -*- Mode: LISP; Syntax: ansi-common-lisp; Package: CL-LIB; Base: 10 -*-
;; Time-stamp: <2007-05-18 11:32:34 miller>
;; CVS: $Id: clim-extensions.lisp,v 1.1.1.1 2007/11/19 17:37:57 gorbag Exp $
;;; 
;;; Copyright (C) 2002 by Bradford W. Miller gorbag@mac.com
;;;
;;; Right of use & redistribution is granted as per the terms of the 
;;; GNU LIBRARY GENERAL PUBLIC LICENCE version 2 which is incorporated here by
;;; reference. 

;;; This program is free software; you can redistribute it and/or modify
;;; it under the terms of the GNU Library General Public License as published
;;; by the Free Software Foundation; version 2.

;;; This program is distributed in the hope that it will be useful,
;;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;;; GNU Library General Public License for more details.

;;; You should have received a copy of the GNU Library General Public License
;;; along with this program; if not, write to the Free Software
;;; Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
;;;

(in-package cl-lib)

#-lispworks
(defgeneric frame-pane (frame)
  (:documentation "Returns the pane tht is the top-level pane of the
current layout of the frame"))

#-lispworks
(defmethod frame-pane (frame)
  (let ((panes (clim:frame-current-panes frame)))
    (while (cdr panes)
      (if (member (clim:frame-parent (car panes))
                  (cdr panes)) ; not the top-level pane
        (pop panes)
        (return-from frame-pane (car panes))))
    (car panes)))

