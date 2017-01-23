(in-package cl-user)

(cl-lib:version-reporter "Stdiosvc-xml" 0 0
                         ";; Time-stamp: <2016-04-14 09:43:03 Miller(on Nsk1200019714M)>" ; this line within time-stamp-line-limit lines of top so emacs sees it and updates.
                         ";; moved from stdiosvc-toplevel.lisp"
                         *stdiosvc-version-reporter-string*
                         *stdiosvc-version-reporter-initializations*)

;; Version
;; 0.0        4-14-2016  moved

;; xml support functions (outside of xmls package) for an xml driven stdio service (for linking to DOME)

;; (C) 2016 General Electric, permission granted to copy under terms negotiated with DMC

;; Author: Bradford Miller

(in-package :stdiosvc)

(defun write-xml-comment (string)
  (format *standard-output* "<!-- ~A~70T -->~%" string))

(defun write-xml-blank ()
  (terpri *standard-output*))

(defun write-xml-banner ()
  (write-xml-comment "================================================================"))

#|| 
;; test xml package
(with-open-file (stream "test-input.txt" :direction :input)
                      (setq foo1 (parse stream))
                (setq foo2 (parse stream)) (setq foo3 (parse stream)))

(write-prolog '((standalone t)) "foobar" *standard-output*)
<?xml STANDALONE="(T)" ?>
<!DOCTYPE foobar>
NIL

XMLS 4 > (write-prolog '((standalone t)) nil *standard-output*)
<?xml STANDALONE="(T)" ?>
NIL

XMLS 5 > (setq foo (with-open-file (stream "test.txt" :direction :input)
                     (parse stream)))
("function" (("name" "fred") ("type" "math"))
            ("param" (("class" "length") ("name" "a")))
            ("param" (("class" "length") ("name" "b")))
            ("output" (("class" "area") ("name" "o")))
            (("author" . "http://authors") NIL "J. Random Luser"))

XMLS 6 > foo
("function" (("name" "fred") ("type" "math")) ("param" (("class" "length") ("name" "a"))) ("param" (("class" "length") ("name" "b"))) ("output" (("class" "area") ("name" "o"))) (("author" . "http://authors") NIL "J. Random Luser"))

XMLS 7 > (toxml foo :indent t)
"<function name=\"fred\" type=\"math\">
  <param class=\"length\" name=\"a\"/>
  <param class=\"length\" name=\"b\"/>
  <output class=\"area\" name=\"o\"/>
  <author xmlns=\"http://authors\">
    J. Random Luser
  </author>
</function>
" ||#
