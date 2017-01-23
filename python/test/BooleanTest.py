# BooleanTest.py
# Test Boolean implementation

from TestHelper import *

def testCopy():
    print '***** test Copying of Boolean'
    from Boolean import Boolean
    from Real import Real
    from Integer import Integer
    true = Boolean(1)
    false = Boolean(0)
    boolA = Boolean()
    real10 = Real(10.0)
    real0 = Real(0)
    integer10 = Integer(10)
    integer0 = Integer(0)
    testExprs = ["true", "false", "boolA",
                 "boolA.copyFrom(true)", "boolA",
                 "boolA.copyFrom(false)", "boolA",
                 "true.copyTo(boolA)", "boolA",
                 "false.copyTo(boolA)", "boolA",
                 "real10", "real0", "integer10", "integer0",
                 "boolA.copyFrom(real10)", "boolA",
                 "boolA.copyFrom(real0)", "boolA",
                 "boolA.copyFrom(integer10)", "boolA",
                 "boolA.copyFrom(integer0)", "boolA",
                 "boolA.copyFrom(2.4)", "boolA",
                 "boolA.copyFrom(0.0)", "boolA",
                 "boolA.copyFrom(6)", "boolA",
                 "boolA.copyFrom(0)", "boolA"]
    for expr in testExprs:
        evalExpr(expr,globals(),locals())
    testStmts = ["boolB = true.dup()", "print boolB",
                 "boolC = false.dup()", "print boolC",]
    for stmt in testStmts:
        execStmt(stmt,globals(),locals())

def testOperators():
    print '***** test Logical Operators of Boolean'
    from Boolean import Boolean
    from Real import Real
    from Integer import Integer
    real10 = Real(10.5)
    real0 = Real(0)
    integer10 = Integer(10)
    integer0 = Integer(0)
    true = Boolean(1)
    false = Boolean(0)
    boolA = Boolean()
    testExprs = ["true", "false", "boolA",
                 "not(true)", "not(false)",
                 "true or true", "false or false",
                 "true or false", "false or true",
                 "true and true", "false and false",
                 "true and false", "false and true",
                 "real10", "real0", "integer10", "integer0",
                 "not(1)", "not(0)", "not(real10)", "not(integer0)",
                 "true or real10", "real10 or true",
                 "true and integer10", "true and integer0",
                 "integer10 and true", "integer0 and true",
                 "false and integer10", "false and integer0",
                 "integer10 and false", "integer0 and false",
                 ]
    for expr in testExprs:
        evalExpr(expr,globals(),locals())



def testTypeConversions():
    print '***** test TypeConversions of Boolean'
    from Boolean import Boolean
    from Real import Real
    from Integer import Integer
    from String import String
    true = Boolean(1)
    false = Boolean(0)
    testExprs = ["true", "false",
                 "Integer(true)", "Integer(false)",
                 "String(true)", "String(false)",
                 "Real(true)", "Real(false)",
                 "float(true)", "float(false)",
                 "int(true)", "int(false)",
                 "str(true)", "str(false)"]
    for expr in testExprs:
        evalExpr(expr,globals(),locals())
        
    

        
if __name__ == '__main__':
    print 'Start testing BOOLEAN...'
    testCopy()
    testOperators()
    testTypeConversions()
