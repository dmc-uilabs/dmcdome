#!/bin/sh

cd "/Applications/LispWorks 7.0 (64-bit)/LispWorks (64-bit).app/Contents/MacOS"

./lispworks-7-0-0-amd64-darwin -siteinit - -init "~/DOME/models/stdio/support/deliver.lisp"

#note this depends on what the delivery name and directory is - eventually take this from environment
mv generic-stdiosvc ~/DOME/models/stdio/support

