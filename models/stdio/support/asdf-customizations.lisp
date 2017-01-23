(defvar *asdf-pathname* #+:win32 "c:/home/lisp/asdf"
                        #+(or :linux :macosx) "~/Lisp/FreeDev/asdf/build/asdf"
  "Where ASDF can be found.  This pathname should not have a type.")

(defvar *asdf-dirs* #+:win32 '("c:/home/lisp/" "c:/home/lisp/aserve/" "c:/emacs/site-lisp/")
                    #+(or :linux :macosx) '("~/Lisp/" "~/Lisp/FreeDev/" "~/ida/tools/")
  "A list of directories \(note trailing slashes) which contain
directories that contain ASDF system definitions.")

(defvar *skip-if-no-asdf-file-found-p* t
  "If this variable has a true value, the process which searches for
ASDF system definitions won't recurse into directories which don't
contain system definitions themselves.")


#-:asdf
(handler-case
  (when *asdf-pathname*
    (load (or (compile-file-if-needed *asdf-pathname*)
              *asdf-pathname*)))
  (conditions:fasl-error ()
    (load (compile-file *asdf-pathname*))))

(defun walk-directory-for-asdf (dir)
  "Looks into the directory DIR and all subdirectories and adds all
directories which contain files of type \"asd\" to
ASDF:*CENTRAL-REGISTRY*."
  (dolist (dir-candidate (directory (lw:pathname-location dir)))
    (when (lw:file-directory-p dir-candidate)
      (let (found-some-p)
        (let ((asd-candidate (merge-pathnames "*.asd" dir-candidate)))
          (when (directory asd-candidate)
            (setq found-some-p t)
            (pushnew dir-candidate asdf:*central-registry* :test #'equal)))
        (when (or found-some-p
                  (not *skip-if-no-asdf-file-found-p*))
          (walk-directory-for-asdf dir-candidate))))))

(defun update-asdf-central-registry ()
  "Loops through *ASDF-DIRS* recursively and adds all
directories containing system definitions to ASDF's central
registry."
  (dolist (base-dir *asdf-dirs*)
    (walk-directory-for-asdf base-dir)))

(update-asdf-central-registry)

(defmethod asdf:perform :around ((o asdf:load-op) (c asdf:cl-source-file))
  "When trying to load a Lisp source file with ASDF that has a wrong
FASL version recompiles it."
  ;; from Bill Clementson's blog
  (handler-case
    (call-next-method o c)
    (conditions:fasl-error ()
      (asdf:perform (make-instance 'asdf:compile-op) c)
      (call-next-method))))

(defun asdf (lib)
  "Shortcut for ASDF."
  (asdf:oos 'asdf:load-op lib))
