(defpackage :cl-lib-logic (:use :cl-lib :common-lisp)
  (:export ))

(in-package cl-lib-logic)
(version-reporter "CL-LIB-Logic Parser" 0 0 ";; Time-stamp: <2007-05-18 11:26:26 miller>" 
                  "CVS: $Id: logic-parser.lisp,v 1.1.1.1 2007/11/19 17:45:56 gorbag Exp $
new")

;;; 
;;; Copyright (C) 2006 by Bradford W. Miller, bradfordmiller@mac.com
;;; Right of use & redistribution is granted as per the terms of the 
;;; GNU LIBRARY GENERAL PUBLIC LICENCE version 2 which is incorporated here by
;;; reference. 

;;; This program is free software; you can redistribute it and/or modify
;;; it under the terms of the GNU Library General Public License as published by
;;; the Free Software Foundation; version 2.

;;; This program is distributed in the hope that it will be useful,
;;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;;; GNU Library General Public License for more details.

;;; You should have received a copy of the GNU Library General Public License
;;; along with this program; if not, write to the Free Software
;;; Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.


;;
;;; Logic Parser
;;
;; Key idea: Logical terms are usually written in a lisp-like form, e.g.
;;
;; (exists ?x (and (ball ?x) (red ?x)))
;; 
;; however, in many cases we want to store facts and statements of logic as CLOS objects (in order to take advantage of 
;; the mechanisms available under that programming paradigm).
;;
;; This package is intended to support the bidirectional transformation between a lisp-like logical representation and 
;; a CLOS representation. While typically a either representation will have some kind of ontological definitions
;; (e.g., saying what kinds of predicates can be stated or what the arguments to a predicate should be), this library 
;; will have several settings to allow working with or without some predefinted ontology. For instance, in the case where
;; no ontology is supported, we could use the parser to read in the definition for some ontology written as a series of
;; s-expressions, then use the converted version as the default ontology for reading other logical statements (thus getting
;; errors when usage does not match the ontologically defined version, and also transforming the s-expression logic into 
;; appropriate CLOS objects that meet the ontology).
;;
;; Of course, in order to perform this transformation, we have to make some assumptions about what the appropriate 
;; representation, in CLOS should correspond to a logical statement. To the extent possible, these functions are themselves
;; generic, thus allowing this library to be of some use even when a different representation of the logic is required for
;; a particular application. Similarly, when the package is expected to support an extant ontology, it makes similar 
;; assumptions as to how that ontology is represented (compatible with the productions of this library). Again, we use 
;; generic functions to the extent possible (frequently tied to those used for conversion) in order to support using the API
;; and functional structure of this library for a different object representation.
;;
;; Note that a priori, we say nothing about the order of the logic we can represent. If one considers predicates in the
;; domain of discourse, for instance, then second order statements are fine.
;;
;; Requirement 0: The library imposes some reserved constants (e.g., predicate names) used for the appropriate representation
;;                of statements and facts. These may vary if the library user decides to change the representation. The
;;                parameter *reserved-constants* contains a list of such constants that the user should take care are used
;;                only with the library's intended meaning. 

(defparameter *reserved-types* '(type-name operator-name predicate-name quantifier-name constant attribute-name 
                                 function-name expression variable constrained-variable function-term 
                                 constrained-function-term)
  "Reserved types are used by the parser to identify the tokens and expressions as scanned. They should be used to 
declare how particular tokens in the user's logic are to be treated. Untyped tokens are just considered to be constants (the default type).

type-name - the token names a type, e.g. 'quantifier-name' or 'integer'
Operator-name - the token names an operator, e.g. 'AND'
Predicate-name - the token names a predicate, e.g. 'MOTHER-OF-P'
Quantifier-name - the token names a quantifier, e.g. 'FORALL'
Constant - the token names a constant, e.g. 'FRED27' or 'RED'. Constants may be used as attributes or values.
Attribute-name - the token names an attribute. Typically a predicate-name may be a variation of an attribute-name, e.g.
                 'MOTHER-OF-P' might be the predicate form of the attribute 'MOTHER'
Function-name - the token names a function. This is used in a function term to denote something rather than being a 
                predicate-name
Expression - typically used to generally label a compound, e.g., '(mother-of-p jim laura)'
Variable - the token is to be taken as a variable, e.g. '?x'
Constrained-variable - some logics allow syntactic sugar on variables to express a constraint, e.g. the type of object that
                       can be bound to the variable. Example (forall ?m@man (mortal ?m)), ?m is constrained to type man.
Function-Term - a compound function term, (e.g. (mother-of (mother-of jim)) might stand in for jim's 
                grandmother). Generally function-terms denote something in the domain of discourse, where expressions have 
                truth value.
Constrained-Function-Term - Similar to a function term, but the denoted value is constrained e.g. 
                (set-of men)@married might be used to denote the set of married men
                (child-of jim)@female might be used to denote a daughter of jim
                Here the constraints are just syntactic sugar for what could have been expressed in a more complex manner,
                and need not be used in any particular logic.


Reserved and user defined types are returned from the functions token-tagger and expression-tagger

[A future version may end up reserving and typing expressions, e.g. predicate-expression]

In a simple first order logic, only constants (those that did not fit any other reserved type) would normally be in the 
domain of discourse. Extended first order logics might also include the predicate-names and attribute-names in the domain
of discourse. First order expressions are only in the domain of discourse (can be bound to variables) in second order 
logics, etc.
")

(defparameter *reserved-constants* `(has-attribute ,@*reserved-types*)
  "A list of constants (typically predicate names) reserved for the implementation. 
Generally speaking, input logical statements are first transformed into a cononical representation 
that will use these constants. 

If the input statement uses these constants, then it is an error to use them incorrectly!")

;;
;; Preference 1: For attribution, monodic predicates are discouraged, binary predicates are preferred
;;  => While we can sometimes handle terms like (red ?x), (color-of ?x red) or some varient are better because they 
;;     explicitly state the object, the attribute and the value. That is, all A/V "pairs" are really triples of object, 
;;     attribute, and value.
;;  => (red ?x) has to be internally transformed (for the assumed ontological reprentation) into the 
;;     [has-attribute object attribute value] form, which implies that "red" must uniquely describe the value that only one
;;     attribute may hold (generally speaking, unlikely, but it's your domain of discourse).
;;  
;;  Example: our original example (exists ?x (and (ball ?x) (red ?x))) would be better expressed either as
;;     (exists ?x (and (shape-of ?x ball) (color-of ?x red))) or 
;;     (exists ?x (and (attribute ?x shape ball) (attribute ?x color red))) 
;;  noting "attribute" is reserved, and used correctly
;;
;; Requirement 1: We distinguish the TYPE (i.e., natural kind, etc.) of a constant in the domain of discourse from 
;;                attributional statements about an object of a given type.
;;  => our s-expression logic has to distinguish type attributions from typical attributions
;;  => we can support typed variables, special implication forms that generally follow the literature.
;;  => we can perform more advanced error detection (attribute inappropriate for object of the given type).
;;
;;  Examples: (exists ?x (and (ball ?x) (red ?x))) would not be a valid expression under this requirement, assuming "ball" 
;;            expresses a type relationship and "red" a color attribute. (If ball is a shape attribute, as above, 
;;            it would be fine, though as ?x remains untyped, would still generate an error in strict mode).
;;            Instead, (exists ?x@ball (color-of ?x red)) would be the preferred formulation given ball uniquely defines 
;;            a type, or (exists ?x (and (type-of ?x ball) (color-of ?x red))), with the stipulation that "type-of" 
;;            is reserved, and used correctly here.
;;
;; Requirement 2: All constants and expressions should have a TYPE, that is, TYPE is a property of all things, not just
;;                those in the domain of discourse.
;;  => Even things not in the domain of discourse are typed. Operators, for instance, generally are not in the domain of 
;;     discourse, but they will have the Type "Operator". Predicates may or may not be in the domain of discourse, but 
;;     they still have the TYPE "predicate", etc.
;;  => This allows our parser to be very general, allowing the user to decide what is and what is not in the domain of 
;;     discourse.
;;
;; Requirement 3: Some types are reserved, in that they have special meaning to this library, and therefore have to be used
;;                in the defined way. These types will also appear on the *reserved-constants* list.  (see above)
;;  => the library doesn't necessarily need to know what the specific operators are in your logic, but it needs to know
;;     when you type something as an "operator", etc.
;;  => While not all such reserved types may make sense in your logic, they must be used to the extent that they do.
;;     e.g., if you have modal operators, you have to use the modal-operator type, to identify them etc.
;;  => Just because some types have been reserved, doesn't mean that they have to be used in your logic; it just means there
;;     already exists code to handle tokens or expressions of that sort. The library user may of course define their own
;;     types and handling functions.


;;;;
;;;; THE PARSER
;;;;
;; There's really two parts to the parser - the first part translates from the s-expressions to the CLOS representation, 
;; and the second does the reverse. Similarly, there are three parts of the implementation, that which converts from sexps,
;; that which converts from CLOS objects, and that which is in common. The first part of this file will implement the sexpr
;; to CLOS converter.

;;;; S-exp to CLOS

;; For generality, the steps we go through might be seens as a bit complex; obviously for a fixed logic a more simple 
;; approach could be used. Since we don't know a priori everything about the input logic, we have to rely on a multi-stage
;; parser.

;; Step 1: We assign tags to each token in the input. This lets us determine the actual pattern of what is being stated in 
;;         the logic. See the generic functions expression-tagger and token-tagger
;; Step 2: We perform any needed syntactic transformations. This lets us canonicalize the input into a form that is ready
;;         for the next step. See the generic functions [TBD], which can be extended through normal CLOS mechanisms by the 
;;         user.
;; Step 3: We generate CLOS objects (or class descriptions) based on the input.
;;         Here we use the value of the parameter *clos-generation-type* as follows:
;;         
(defvar *clos-generation-type :off
  ":off -               we generate a pure syntactic transformation, using the base required methods for attribute/value 
                        pairs, etc. This is the least 'useful' and most straightforward conversion, because A/V pairs 
                        simply turn into slots and slotvalues on a class (type) we generate automatically. This is a 
                        promiscuous parse, where any constant can be a value, an attribute, etc. depending on position.
                        No argument typechecking is performed (though user supplied extentions to the generic functions
                        employed can do anything they want).
   :ontology-defining - we generate class objects only. The set of class objects can then be used as an ontology for
                        the next usage. Because we are defining terms that will be used to describe the domain of 
                        discourse, one could argue that a higher order logic is necessary, however, we admit
                        essentially an augmented first order logic that treats the names of functions, for instance
                        as constants (so variables can be bound to the name of a predicate, but not to a predicate).
                        The predefined classes presuppose a lot about the logic that will be employed (e.g., temporal modal
                        logic, with synatctic extentions for typed or constrained variables, etc.), however a library
                        user is free to extend or override the existing predefined class representations.
   :ontology-obeying -  we use a defined set of classes, and create only instances of those classes. This would
                        typically follow an ontology-defining pass, e.g., the library user would first establish
                        an ontology defining functions, attributes and predicates, and then read in expressions 
                        written using those terms that have been defined. Specific functions that can be overridden or 
                        extended by the user are employed to do the testing.")
;;
;;        See the functions [TBD]

;;;; CLOS to S-exp

;; Our CLOS to S-exp converter is in some sense simpler than the reverse, because we treat generating the s-exp version of 
;; the logic as a particular print function on the objects. So the implementation is basically a simple object walker that
;; collects the list representation out of the specific peices encountered, with a final decanonicalization pass that allows
;; any user-defined syntactic sugar or other simplifying transformations to be invoked.
;; 
;; We also establish a particular variable, *print-as-logical-sexp* to interact with the printer.

(defvar *print-as-logical-sexp* nil
  "when it is bound non-nil, then the sexp generator will be used to create the formatted output for any objects that 
inherit from [TBD]. Note that if *print-readably* is also non-nil, it is the user's responsibility to make sure that 
the written version of the language either automatically invokes the parser, or they can write an :after method on 
the function [TBD] to wrap the final input with a parser invocation.")


;;;; Common Functions
