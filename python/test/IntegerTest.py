#! /usr/bin/env jython

# IntegerTest.py
# Test Integer implementation

from TestHelper import *


def testCopy():
    print '***** test Copying of Integer'
    from Boolean import Boolean
    from Real import Real
    from Integer import Integer
    true = Boolean(1)
    real6half = Real(6.5)
    integer10 = Integer(10)
    integer0 = Integer(0)
    testExprs = ["integer0", "integer10", "real6half", "true"
                 "integer0.copyFrom(integer10)", "integer0",
                 "integer0.copyFrom(real6half)", "integer0",
                 "integer0.copyFrom(true)", "integer0",
                 "integer0.copyFrom(80.543)", "integer0",
                 "integer0.copyFrom(5)", "integer0"]
    for expr in testExprs:
        evalExpr(expr,globals(),locals())
    testStmts = ["integerB = integer10.dup()", "print integerB",
                 "integerC = integer0.dup()", "print integerC",]
    for stmt in testStmts:
        execStmt(stmt,globals(),locals())



def testOperators():
    from Integer import Integer
    from Real import Real
    print '***** test Integer Math Operators'
    # test of "true" division
    integ5 = Integer(5)
    integ0 = Integer(0)
    integ9 = Integer(9)
    integ2 = Integer(2)
    integ2neg = Integer(-2)
    testExprs = ["integ2neg", "integ0","integ2", "integ5","integ9",
                 "integ2+integ0","integ5+integ2neg","integ2neg+integ5+integ9",
                 "2.5+integ9","integ9+2.5","integ5+3", "3+integ5",
                 "integ2-integ0","integ5-integ2neg","integ2neg-integ5-integ9",
                 "2.5-integ9","integ9-2.5","integ5-3", "3-integ5",
                 "integ2*integ0","integ5*integ2neg","integ2neg*integ5*integ9",
                 "2.5*integ9","integ9*2.5","integ5*3", "3*integ5",
                 "integ2/integ0","integ5/integ2neg","integ2neg/integ5/integ9",
                 "2.5/integ9","integ9/2.5","integ5/3", "3/integ5",
                 "integ2**integ0","integ5**integ2neg",
                 "integ2neg**integ5**integ0",
                 "2.5**integ9","integ9**2.5","integ5**3", "3**integ5"]
    for expr in testExprs:
        evalExpr(expr,globals(),locals())


def testTypeConversions():
    print '***** test TypeConversions of Integer'
    from Boolean import Boolean
    from Real import Real
    from Integer import Integer
    from String import String
    integer5 = Integer(5)
    integer0 = Integer(0)
    testExprs = ["integer5", "integer0",
                 "Real(integer5)", "Real(integer0)",
                 "Boolean(integer5)", "Boolean(integer0)",
                 "String(integer5)", "String(integer0)",
                 "float(integer5)", "float(integer0)",
                 "int(integer5)", "int(integer0)",
                 "str(integer5)", "str(integer0)"]
    for expr in testExprs:
        evalExpr(expr,globals(),locals())
        






if __name__ == '__main__':
    print 'Start testing INTEGER...'
    testCopy()
    testOperators()
    testTypeConversions()
