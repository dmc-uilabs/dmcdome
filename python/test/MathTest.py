# IntegerTest.py
# Test math implementation
# Tests that are more complicated, involving different types.

from TestHelper import *


def testOperatorMixing():
    from Integer import Integer
    from Real import Real
    from Boolean import Boolean
    print '***** test interaction of Boolean, Integer with Math Operators'
    # test of "true" division
    integ3 = Integer(3)
    integ2neg = Integer(-2)
    realHalf = Real(0.5)
    real0 = Real(0.0)
    true = Boolean(1)
    false = Boolean(0)
    # test mixing of types
    # test order of operations
    testExprs = ["'Addition'",
                 "integ3","integ2neg","realHalf", "real0", "true", "false",
                 "integ3+true", "true+integ3", "integ3+false",
                 "integ3+realHalf", "realHalf+integ3", "integ3+real0",
                 "realHalf+true", "realHalf+true", "realHalf+false",
                 "integ3+realHalf+true", "realHalf+true+integ3",
                 "'Subtraction'",
                 "integ3","integ2neg","realHalf", "real0", "true", "false",
                 "integ3-true", "true-integ3", "integ3-false",
                 "integ3-realHalf", "realHalf-integ3", "integ3-real0",
                 "realHalf-true", "realHalf-true", "realHalf-false",
                 "integ3-realHalf-true", "realHalf-true-integ3",
                 "'Multiplication'",
                 "integ3","integ2neg","realHalf", "real0", "true", "false",
                 "integ3*true", "true*integ3", "integ3*false",
                 "integ3*realHalf", "realHalf*integ3", "integ3*real0",
                 "realHalf*true", "realHalf*true", "realHalf*false",
                 "integ3*realHalf*true", "realHalf*true*integ3",
                 "'Division'",
                 "integ3","integ2neg","realHalf", "real0", "true", "false",
                 "integ3/true", "true/integ3", "integ3/false",
                 "integ3/realHalf", "realHalf/integ3", "integ3/real0",
                 "realHalf/true", "realHalf/true", "realHalf/false",
                 "integ3/realHalf/true", "realHalf/true/integ3",
                 "'Exponent'",
                 "integ3","integ2neg","realHalf", "real0", "true", "false",
                 "integ3**true", "true**integ3", "integ3**false",
                 "integ3**realHalf", "realHalf**integ3", "integ3**real0",
                 "realHalf**true", "realHalf**true", "realHalf**false",
                 "integ3**realHalf**true", "realHalf**true**integ3"]
    for expr in testExprs:
        evalExpr(expr,globals(),locals())

def testOperatorOrder():
    from Integer import Integer
    print '***** test order precedence of Math Operators'
    integ3 = Integer(3)
    integ10 = Integer(10)
    integ2 = Integer(2)
    integ4neg = Integer(-4)
    testExprs = ["integ3", "integ10", "integ2", "integ4neg",
                 "integ3+integ10-integ2", "integ10-integ2+integ3"
                 "integ3/integ10-integ2", "(integ3/integ10)-integ2",
                 "integ3/(integ10-integ2)",
                 "integ3*integ10-integ4neg", "(integ3*integ10)-integ4neg",
                 "integ3*(integ10-integ4neg)",
                 "integ3*integ4neg/integ10", "(integ3*integ4neg)/integ10",
                 "integ3*(integ4neg/integ10)"]
    for expr in testExprs:
        evalExpr(expr,globals(),locals())


def testDMathFunctions():
    from DMath import *
    print '***** test DMath functions'
    a = Integer(9)
    b = Integer(-1)
    c = Real(20.7)
    d = Real(.5)
    # don't redefine e
    f = Real(10)
    testExprs = ["a", "b", "c", "d", "f",
                 "sin(d)","sin(b)","asin(d)", "asin(c)", "asin(b)",
                 "cos(d)","cos(b)","acos(d)", "acos(c)", "acos(b)",
                 "tan(d)","tan(b)","atan(d)", "atan(c)", "atan(b)",
                 "exp(Real(0))", "exp(Integer(1))", "exp(b)", "exp(a+d)",
                 "pow(f, b)", "pow(d, f)", "log(exp(f))", "log(pow(f,f))",
                 "log10(f**f)",
                 "abs(a-c)","abs(b)", "abs(d)", "abs(d-f+a)",
                 "sqrt(c*c)", "sqrt(d)", "sqrt(a)", "sqrt(a+d)",
                 "floor(c+d)", "floor(f)", "floor(a-30.1)", "floor(a)",
                 "ceil(c+d)", "ceil(f)", "ceil(a-30.1)", "ceil(a)",
                 "round(c+d)", "round(f)", "round(a-30.1)", "round(a)"]
    print "     globals"
    print globals()
    print "     locals"
    print locals()
    for expr in testExprs:
        evalExpr(expr,globals(),locals())


def testTypeReturn():
    from Integer import Integer
    from Real import Real
    from types import IntType, FloatType
    print '***** test DMath functions type returns'
    a = Integer(9)
    b = Integer(-1)
    c = Real(20.7)
    d = Real(.5)
    # don't replace e
    f = Real(10)
    testExprs = ["a", "b", "c", "d", "f",
                 "type(sin(d))==FloatType","type(sin(b))==FloatType",
                 "type(asin(d))==FloatType", "type(asin(a))==FloatType",
                 "type(cos(d))==FloatType","type(cos(b))==FloatType",
                 "type(acos(d))==FloatType", "type(acos(a))==FloatType",
                 "type(tan(d))==FloatType","type(tan(b))==FloatType",
                 "type(atan(d))==FloatType", "type(atan(a))==FloatType",
                 "type(exp(Real(0)))==FloatType",
                 "type(exp(Integer(1)))==FloatType",
                 "type(pow(f, b))", "type(pow(d, f))",
                 "type(log(exp(f))==FloatType", "type(log10(f**f))==FloatType",
                 "isinstance(abs(a-b), Integer)",
                 "isinstance(abs(d-f+a), Real)",
                 "isinstance(sqrt(a), Real)", "isinstance(sqrt(c*c), Real)",
                 "isinstance(sqrt(a+d), Real)",
                 "isinstance(floor(c+d), Real)","isinstance(floor(a),Integer)",
                 "isinstance(ceil(c+d), Real)","isinstance(ceil(a),Integer)",
                 "isinstance(round(c+d), Real)",
                 "isinstance(round(a),Integer)"]
    for expr in testExprs:
        evalExpr(expr,globals(),locals())


def testDataType():
    from DataType import DataType
    dt1 = DataType()
    dt2 = 8
    testStmts = ['dt1.copyTo(dt2)','dt1.copyFrom(dt2)']
    for stmt in testStmts:
        execStmt(stmt,globals(),locals())



if __name__ == '__main__':
    print 'Start Math Testing'
    #testDataType()
    #testOperatorMixing()
    #testOperatorOrder
    testDMathFunctions()
    #testTypeReturn()
