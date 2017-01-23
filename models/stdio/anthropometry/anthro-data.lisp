(in-package cl-user)

(cl-lib:version-reporter "anthropometry-data" 0 1
                         ";; Time-stamp: <2016-05-23 17:36:42 Miller(on Nsk1200019714M)>"
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

(defvar *anthro-data-alist* nil
  "Simple tables of anthropometric data defined in following")

;; accessor functions
(defun gender-f-cm (x)
  (first x))

(defun gender-f-in (x)
  (second x))

(defun gender-m-cm (x)
  (third x))

(defun gender-m-in (x)
  (fourth x))


;;                Elbow Rest Height, Sitting
(defanthro :elbow-rest-height-sitting
    ;;   FEMALE             MALE
    :n (2208 1774)
    ;;   centimeters inches    centimeters inches
    :mean (22.05     8.68      23.06       9.08)
    :std-d(2.68      1.05      2.72        1.07)
    :max  (30.2      11.89     31.1        12.24)
    :min  (12.4      4.88      14          5.51)
    ;;  Percentiles                 Percentiles
    :percentiles
    ((15.8   6.22             16.75   6.6)  ;1st
     (16.49  6.49             17.35   6.83) ;2nd
     (16.94  6.67             17.78   7)    ;3rd
     (17.57  6.92             18.41   7.25) ;5th
     (18.56  7.31             19.44   7.65) ;10th
     (19.24  7.57             20.17   7.94) ;15th
     (19.77  7.87             20.74   8.17) ;20th
     (20.24  7.97             21.24   8.36) ;25th
     (20.65  8.13             21.69   8.54) ;30th
     (21.03  8.28             22.09   8.7)  ;35th
     (21.39  8.42             22.47  8.85)  ;40th
     (21.74  8.56             22.83  8.99)  ;45th
     (22.08  8.69             23.19  9.13)  ;50th
     (22.42  8.83             23.53  9.27)  ;55th
     (22.77  8.96             23.88   9.4)  ;60th
     (23.12   9.1             24.23  9.54)  ;65th
     (23.49  8.25             24.59  9.68)  ;70th
     (23.89  9.41             24.98  9.83)  ;75th
     (24.33  9.58             25.4    10)  ;80th
     (24.84  9.78             25.88 10.19)  ;85th
     (25.49  10.03            26.48 10.43) ;90th
     (26.44  10.41            27.37 10.78)  ;95th
     (27.06  10.65            27.96 11.01) ;97th
     (27.52  10.84            28.41 11.19) ;98th
     (28.24  11.12            29.16 11.48) ;99th
     ))


;;                                    Elbow-Wrist Length
(defanthro :elbow-wrist-length    
    ;;   FEMALE                        MALE
    :n (2208 1774)
    :mean (26.25 10.33 29.03 11.43)
    :std-d (1.54 0.61 1.54 0.61)
    :max (33.4 13.15 35 13.78)
    :min (17 6.69 22.6 8.9)
    :percentiles
    ((22.94   9.03               25.79        10.15)  ;1st
     (23.26   9.16               26.09        10.27)  ;2nd
     (23.47   9.24                26.33        10.35) ;rd
     (23.78   9.36                26.65        10.47) ;th
     (24.28  9.56               27.09       10.66)    ;10th
     (24.64   9.7               27.44        10.8)    ;15th
     (24.93  9.81               27.72       10.91)    ;20th
     (25.18  9.91               27.97       11.01)    ;25th
     (25.41    10               28.19        11.1)    ;30th
     (25.62 10.09                 28.4       11.18)  ;35th
     (25.83 10.17                 28.6       11.26)  ;40th
     (26.03 10.25                 28.8       11.34)  ;45th
     (26.22 10.32                28.99       11.41)   ;50th
     (26.42  10.4               29.18       11.49)    ;55th
     (26.62 10.48                29.38       11.57)   ;60th
     (26.83 10.56                29.58       11.65)   ;65th
     (27.05 10.65                 29.8       11.73)  ;70th
     (27.29 10.74                30.03       11.82)   ;75th
     (27.56 10.85                 30.3       11.93)  ;80th
     (27.86 10.97                30.61       12.05)   ;85th
     (28.26 11.12                31.01       12.21)   ;90th
     (28.83 11.35                31.61       12.45)   ;95th
     (29.21  11.5               32.02       12.61)    ;97th
     (29.49 11.61                32.33       12.73)   ;98th
     (29.93 11.78                32.84       12.93)   ;99th
     ))
