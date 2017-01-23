(in-package cl-user)

(cl-lib:version-reporter "reaction-time-data" 0 1
                         ";; Time-stamp: <2016-04-28 13:28:14 Miller(on Nsk1200019714M)>"
                         ";; fencepost"
                         *stdiosvc-version-reporter-string*
                         *stdiosvc-version-reporter-initializations*)

;; Version
;; 0.0        4-26-2016  new
;; 0.1        4-28-2016  fix fencepost error

;; implementation of a service to report finger reaction times
;; from Chan & Ng "Finger Response Times to Visual, Auditory and Tactile Modality Stimuli"
;; Proceedings of the IMECS 2012, Hong Kong

;; a copy of which appears in this directory.

;; (C) 2016 General Electric, permission granted to copy under terms negotiated with DMC

;; Author: Bradford Miller

(in-package :stdiosvc)

;; note that the paper reports mean response times for various factors, along with overall results for
;; visual and auditory stimuli. As the underlying data is not available, this service assumes these factors are
;; INDEPENDANT which may lead to factually incorrect inference when factors are combined. For example,
;; note that the mean response time for those with only a Primary eduction level on the simple task was .45 sec.
;; And note that the time spent on computer of <2 hours was also .45 sec. Further the sample size of both was 40
;; out of 690). It is possible these are the SAME 40 individuals, however, we will assume that as each population
;; was 6% of the whole, only 6% of the 40 are both (2.32 individuals). That way when asked the mean reaction time
;; for those with a primary eduction AND who spend <2 hours/day on a computer we note that .45 is .07 over the mean
;; for the 690, and so the mean for independent populations would be mean + .07 + .07 = .52
;;
;; That may not reflect the actual data, of course, but it is the best we can do without it. Queries of single factors
;; should just return the table data.

;; To use, supply the problem (simple, 2choice, 4choice, 8choice), the hand (L,R), the stimulus (auditory, visual)
;;  and 0 or more factors at a particular level:
;;
;; age {age11-20, age21-30, age31-40, age41-50, age51-60}
;; gender {gender-M, gender-F}
;; education level {Primary, Secondary, Tertiary}
;; Time spent on computer {time<2, time2-4, time4-6, time>6}
;;
;; If no factors are given, the overall mean for the 690 subjects will be reported.

(defconstant +visual-response-table+
;;  Factors Level               n        Mean response time (s)
;;                                                     Simple                         Two-choice                     Four-choice                     Eight-choice
;;                                              Left            Right            Left           Right            Left           Right            Left            Right
  '((age11-20                   50              0.39             0.37            0.47            0.44            0.59            0.57            0.66            0.64)
    (age21-30                   460             0.38             0.34            0.44            0.4             0.55            0.51            0.72            0.65)
    (age31-40                   120             0.35             0.36            0.45            0.44            0.57            0.55            0.69            0.65)
    (age41-50                   20              0.43             0.43            0.51            0.56            0.67            0.65            0.8             0.78)
    (age51-60                   40              0.47             0.45            0.65            0.62            0.81            0.79            0.94            0.99)
    (gender-m                   370             0.39             0.36            0.46            0.43            0.58            0.54            0.74            0.69)
    (gender-F                   320             0.37             0.35            0.46            0.42            0.57            0.53            0.71            0.66)
    (Primary                    40              0.45             0.42            0.61            0.58            0.74            0.74            0.87            0.9)
    (Secondary                  150             0.4              0.36            0.48            0.43            0.59            0.56            0.73            0.68)
    (Tertiary                   500             0.37             0.35            0.44            0.41            0.55            0.51            0.71            0.65)
    (time<2                     40              0.45             0.44            0.6             0.62            0.76            0.77            0.91            0.94)
    (time2-4                    150             0.41             0.37            0.48            0.44            0.59            0.56            0.73            0.68)
    (time4-6                    190             0.36             0.35            0.44            0.4             0.55            0.51            0.7             0.64)
    (time>6                     310             0.37             0.34            0.44            0.41            0.55            0.51            0.72            0.65)
    (t                          690             0.38             0.36            0.46            0.43            0.57            0.54            0.73            0.67)))

(defconstant +auditory-response-table+
;; Factors Level                n        Mean response time (s)
;;                                                     Simple                         Two-choice                     Four-choice                     Eight-choice
;;                                              Left            Right            Left           Right            Left           Right            Left            Right
  '((age11-20                   50              0.39             0.36            0.4S            0.47            0.55            0.51            0.69            0.64)
    (age21-30                   460             0.35             0.32            0.41            0.38            0.50            0.47            0.70            0.65)
    (age31-10                   120             0.33             0.33            0.49            0.45            0.55            0.51            0.67            0.59)
    (age41-50                   20              0.43             0.40            0.53            0.50            0.61            0.57            0.75            0.67)
    (age51-60                   40              0.51             0.49            0.58            0.63            0.77            0.75            0.93            0.88)
    (Gender-M                   370             0.36             0.33            0.45            0.43            0.54            0.51            0.71            0.66)
    (gender-F                   320             0.35             0.34            0.43            0.41            0.52            0.48            0.71            0.64)
    (Primary                    40              0.49             0.45            0.58            0.59            0.71            0.68            0.86            0.82)
    (Secondary                  150             0.38             0.35            0.45            0.44            0.56            0.52            0.72            0.65)
    (Tertiary                   500             0.34             0.32            0.42            0.39            0.51            0.48            0.69            0.64)
    (time<2                     40              0.5              0.47            0.58            0.61            0.73            0.71            0.89            0.81)
    (time2-4                    150             0.38             0.36            0.45            0.44            0.55            0.52            0.72            0.67)
    (time4-6                    190             0.34             0.32            0.42            0.39            0.51            0.47            0.68            0.63)
    (time>6                     310             0.34             0.32            0.43            0.4             0.51            0.48            0.69            0.64)
    (t                          690             0.36             0.34            0.44            0.42            0.53            0.5             0.71            0.65)))

;; accessor functions
(defun factor (f x)
  (cdr (assoc f x)))

(defun n (x)
  (first x))

(defun simple (x)
  (cdr x))

(defun 2choice (x)
  (cdddr x))

(defun 4choice (x)
  (nthcdr 5 x))

(defun 8choice (x)
  (nthcdr 7 x))

(defun l (x)
  (car x))

(defun r (x)
  (cadr x))
