(in-package cl-user)

(cl-lib:version-reporter "reaction-time" 0 1
                         ";; Time-stamp: <2016-04-28 14:38:08 Miller(on Nsk1200019714M)>"
                         ";; return-value"
                         *stdiosvc-version-reporter-string*
                         *stdiosvc-version-reporter-initializations*)

;; Version
;; 0.0        4-26-2016  new
;; 0.1        4-28-2016  fix return-value

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


;;TODO
;; note that currently only the reaction time is returned - we should also return the estimated population size given our assumptions when multiple factors are used.

(export '(reaction-time response-time l r simple 2choice 4choice 8choice auditory visual aget11-20 age21-30 age31-40 age41-50 age51-60 gender-m gender-f primary secondary tertiary time<2 time2-4 time4-6 time>6))

(defservice (reaction-time data-interpolator ((problem-type (member simple 2choice 4choice 8choice))
                                              (hand (member L R))
                                              (stimulus (member auditory visual))
                                              ;; optional
                                              (age t (member age11-20 age21-30 age31-40 age41-50 age51-60))
                                              (gender t (member gender-m gender-f))
                                              (education-level t (member primary secondary tertiary))
                                              (computer-time t (member time<2 time2-4 time4-6 time>6)))
                                              
                           response-time float)
    "estimate the reaction time from the tabular data given some set of factors"

  (let* ((table (if (eql stimulus 'visual)
                    +visual-response-table+
                   +auditory-response-table+))
         (mean-row (factor 't table))
         (accessor-fn #'(lambda (row)
                          (funcall hand (funcall problem-type row)))) ; yes inefficient but how often are we going to call it? (we could compile it if we are anal)
         ;; start with mean
         (current-time (funcall accessor-fn mean-row))
         (mean-time current-time))
    ;; for each factor, adjust
    (flet ((adjust (level)
             ;(format t "Adjusting for ~S: current-time: ~D, adjustment:~%" level)
             (incf current-time
                   (- (funcall accessor-fn (factor level table))
                      mean-time))))
      (when age
        (adjust age))
      (when gender
        (adjust gender))
      (when education-level
        (adjust education-level))
      (when computer-time
        (adjust computer-time))
      current-time)))

;; sample call:
;; <request service="reaction-time"> <param name="problem-type" value="2choice"/> <param name="hand" value="r"/> <param name="stimulus" value="auditory"/> <param name="gender" value="gender-f"/></request>
