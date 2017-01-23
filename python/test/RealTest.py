# RealTest.py
# Test Real data type

from TestHelper import *

def testCopy():
    print '***** test Copying of Real'
    from Boolean import Boolean
    from Real import Real
    from Integer import Integer
    true = Boolean(1)
    integer6 = Integer(6)
    real10 = Real(10.0)
    real0 = Real(0.0)
    testExprs = ["real0", "real10", "integer6", "true",
                 "real0.copyFrom(real10)", "real0",
                 "real0.copyFrom(integer6)", "real0",
                 "real0.copyFrom(true)", "real0",
                 "real0.copyFrom(80.543)", "real0",
                 "real0.copyFrom(5)", "real0"]
    for expr in testExprs:
        evalExpr(expr,globals(),locals())
    testStmts = ["realB = real10.dup()", "print realB",
                 "realC = real0.dup()", "print realC",]
    for stmt in testStmts:
        execStmt(stmt,globals(),locals())


def testOperators():
    from Real import Real
    from Integer import Integer
    print '***** test Real Math Operators'
    real3half = Real(3.5)
    real2quarter = Real(2.25)
    real1neg = Real(-1)
    real0 = Real(0.0)
    testExprs = ["real3half","real2quarter", "real1neg", "real0",
                 "real3half+real0", "real0+real3half", "real2quarter+real1neg",
                 "real3half+real2quarter+real1neg",
                 "real1neg+2.4", "2.4+real1neg", "real3half+4", "4+real3half", 
                 "real3half-real0", "real0-real3half", "real2quarter-real1neg",
                 "real3half-real2quarter-real1neg",
                 "real1neg-2.4", "2.4-real1neg", "real3half-4", "4-real3half", 
                 "real3half*real0", "real0*real3half", "real2quarter*real1neg",
                 "real3half*real2quarter*real1neg",
                 "real1neg*2.4", "2.4*real1neg", "real3half*4", "4*real3half", 
                 "real3half/real0", "real0/real3half", "real2quarter/real1neg",
                 "real3half/real2quarter/real1neg",
                 "real1neg/2.4", "2.4/real1neg", "real3half/4", "4/real3half", 
                 "real3half**real0", "real0**real3half",
                 "real2quarter**real1neg","real3half**real2quarter**real1neg",
                 "real1neg**2.4", "2.4**real1neg", "real3half**4",
                 "4**real3half", "-real3half"]
    for expr in testExprs:
        evalExpr(expr,globals(),locals())


def testTypeConversions():
    print '***** test TypeConversions of Real'
    from Boolean import Boolean
    from Real import Real
    from Integer import Integer
    from String import String
    real5half = Real(5.5)
    real0 = Real(0.0)
    testExprs = ["real5half", "real0",
                 "Integer(real5half)", "Integer(real0)",
                 "Boolean(real5half)", "Boolean(real0)",
                 "String(real5half)", "String(real0)",
                 "float(real5half)", "float(real0)",
                 "int(real5half)", "int(real0)",
                 "str(real5half)", "str(real0)"]
    for expr in testExprs:
        evalExpr(expr,globals(),locals())




if __name__ == '__main__':
    print 'Start testing REAL...'
    testCopy()
    testOperators()
    testTypeConversions()
