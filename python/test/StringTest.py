# StringTest.py
# Test String implementation


from TestHelper import *


def testCopy():
    print '***** test Copying of String'
    from String import String
    from Boolean import Boolean
    from Real import Real
    from Integer import Integer
    stringABC = String('ABC')
    stringDEF = String('DEF')
    stringH = String()
    false = Boolean(0)
    real10 = Real(10.0)
    integer10 = Integer(10)
    testExprs = ["stringABC", "stringDEF", "stringH",
                 "stringH.copyFrom(stringABC)", "stringH",
                 "stringDEF.copyTo(stringH)", "stringH",
                 "real10", "integer10", "false",
                 "stringH.copyFrom(false)", "stringH",
                 "stringH.copyFrom(real10)", "stringH",
                 "stringH.copyFrom(integer10)", "stringH",
                 "stringH.copyFrom(2.4)", "stringH",
                 "stringH.copyFrom(0.0)", "stringH",
                 "stringH.copyFrom(6)", "stringH",
                 "stringH.copyFrom(0)", "stringH"]
    for expr in testExprs:
        evalExpr(expr,globals(),locals())
    testStmts = ["stringB = stringABC.dup()", "print stringB",
                 "stringC = stringDEF.dup()", "print stringC",]
    for stmt in testStmts:
        execStmt(stmt,globals(),locals())

def testOperators():
    print '***** test String Operators'
    from String import String
    from Real import Real
    from Integer import Integer
    from Boolean import Boolean
    stringABC = String('ABC')
    stringDEF = String('DEF')
    stringH = String()
    testExprs = ["stringABC", "stringDEF", "stringH",
                 "stringABC+stringDEF", "stringDEF+stringABC",
                 "stringABC+'string primitive'",
                 "'string primitve'+stringDEF",
                 "stringABC+Real(4.3)", "stringABC+Integer(8)",
                 "stringABC+Boolean(1)", "stringABC+14", "stringABC+3.23"
                 ]
    for expr in testExprs:
        evalExpr(expr,globals(),locals())


def testFunctions():
    print '***** test String Fuctions'
    from String import *
    from Boolean import Boolean
    stringABC = String('ABC')
    stringABC2 = String('ABC')
    stringABC3 = String('abc')
    stringDEF = String('DEF')
    stringH = String()
    testExprs = ["stringABC", "stringABC2", "stringABC3",
                 "stringDEF", "stringH",
                 "join(stringABC, stringDEF)", "join(stringDEF, stringABC)",
                 "join(stringABC, stringDEF, stringH)",
                 "join(stringH, stringABC)",
                 "join(1, stringABC, stringH)",
                 "equalsIgnoreCase(stringABC, stringABC2)",
                 "equalsIgnoreCase(stringABC, stringABC3)",
                 "equalsIgnoreCase(stringABC, stringDEF)",
                 "equalsIgnoreCase(stringABC, stringH)",
                 "equalsIgnoreCase(stringABC, 'abc')",
                 "equalsIgnoreCase('abc', stringABC)",
                 "equalsIgnoreCase(stringABC, 'ABC')",
                 "equalsIgnoreCase(stringABC, 'DEF')",
                 "equalsIgnoreCase(stringABC, 2)"]
    for expr in testExprs:
        evalExpr(expr,globals(),locals())
                 

def testTypeConversions():
    print '***** test TypeConversions of String'
    from Boolean import Boolean
    from String import String
    stringABC = String('ABC')
    testExprs = ["stringABC",
                 "Boolean(stringABC)",
                 "str(stringABC)"]
    for expr in testExprs:
        evalExpr(expr,globals(),locals())
        
def testComparisons():
    print '***** test Comparisons of String'
    from Boolean import Boolean
    from String import String
    stringABC = String('ABC')
    stringABC2 = String('ABC')
    stringABC3 = String('abc')
    stringDEF = String('DEF')
    stringH = String()
    testExprs = ["stringABC", "stringABC2", "stringABC3",
                 "stringDEF", "stringH",
                 "stringABC==stringABC2",
                 "stringABC==stringABC3",
                 "stringABC==stringDEF",
                 "stringABC==stringH",
                 "stringABC=='abc'",
                 "'abc'==stringABC",
                 "stringABC=='ABC'",
                 "stringABC=='DEF'",
                 "stringABC!=stringABC2",
                 "stringABC!=stringABC3",
                 "stringABC!=stringDEF",
                 "stringABC!=stringH",
                 "stringABC!='abc'",
                 "'abc'!=stringABC",
                 "stringABC!='ABC'",
                 "stringABC!='DEF'",
                 ]
    for expr in testExprs:
        evalExpr(expr,globals(),locals())
    


        
if __name__ == '__main__':
    print 'Start testing STRING...'
    testCopy()
    testOperators()
    testFunctions()
    testTypeConversions()
    testComparisons()
