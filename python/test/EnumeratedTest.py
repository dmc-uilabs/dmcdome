# EnumeratedTest.py
# Test the Enumerated data type
from TestHelper import *
from Enumerated import Enumerated
from Real import Real
from Integer import Integer
from Matrix import Matrix
from Vector import Vector
from Boolean import Boolean

def initEnumerated1():
    enumA = Enumerated([(1,2),(3,4),(5,6),(7,8)])
    enumB = Enumerated([(1,2),(4,4),(6,6),(8,9), (10,11)])
    enumC = Enumerated(['try', 2, 'testing'])
    enumD = Enumerated(('foo', 2, Integer(4), Boolean(0), Vector(),
                        Matrix(3,4), Real(3.3)))
    enumE = Enumerated(('foo', 2, Integer(4), Boolean(0), Vector(),
                        Matrix(3,4), Real(3.3)))
    enumF = Enumerated(None)
    print '...... done enumerated intializations'
    return [enumA, enumB, enumC, enumD, enumE, enumF]


def testMethods():
    print '***** test Methods of Enumerated'
    a = initEnumerated1()
    enumA = a[0]
    enumB = a[1]
    enumC = a[2]
    enumD = a[3]
    enumE = a[3]
    testExprs = ["enumA", "enumB", "enumC", "enumD",
                 "enumA.hasName('foo')", "enumA.hasName(1)",
                 "enumC.hasName('try')", "enumD.setElementName(1, 'foo')",
                 "enumD.hasName('FOO')", 
                 "enumD.hasName('foo')", "enumD.hasName(1)",
                 "enumA.hasNameCI('try')", "enumA.hasNameCI('item1')",
                 "enumC.hasNameCI('item1')", "enumD.hasNameCI('item1')",
                 "enumA.getElementName(1)",
                 "enumD.hasNameCI('FOO')", "enumD.hasNameCI('foo')"
                 ]
    for expr in testExprs:
        evalExpr(expr,globals(),locals())
    testStmts = ["enumE = enumA.dup()", "print enumE",
                 "enumE = enumD.dup()", "print enumE",]
    for stmt in testStmts:
        execStmt(stmt,globals(),locals())

def testFunctions():
    print '***** test Enumerated Functions 1'
    a = initEnumerated1()
    enumA = a[0]
    enumB = a[1]
    enumC = a[2]
    enumD = a[3]
    testExprs = ["enumA", "enumB", "enumC", "enumD",
                 "enumA.getIndex()",
                 "enumA.set(0)", "enumA.getIndex()",
                 "enumA.getName()", "enumA.getValue()",
                 "enumA.set(1)", "enumA.getIndex()",
                 "enumA.getName()", "enumA.getValue()",
                 "enumA.set(3)", "enumA.getIndex()",
                 "enumA.getName()", "enumA.getValue()",
                 "enumA.set(4)", "enumA.getIndex()",
                 "enumA.getName()", "enumA.getValue()",
                 "enumD",
                 "enumD.getIndex()",
                 "enumD.set(0)", "enumD.getIndex()",
                 "enumD.getName()", "enumD.getValue()",
                 "enumD.set(1)", "enumD.getIndex()",
                 "enumD.getName()", "enumD.getValue()",
                 "enumD.set(3)", "enumD.getIndex()",
                 "enumD.getName()", "enumD.getValue()",
                 "enumD.set(4)", "enumD.getIndex()",
                 "enumD.getName()", "enumD.getValue()",
                 "enumA.size()", "enumA.names()",
                 "enumB.size()", "enumB.names()",
                 "enumC.size()", "enumC.names()",
                 "enumD.size()", "enumD.names()"

                 ]
    for expr in testExprs:
        evalExpr(expr,globals(),locals())

def testComparisons():
    print '***** test Comparisons of Enumerated'
    a = initEnumerated1()
    enumA = a[0]
    enumB = a[1]
    enumC = a[2]
    enumD = a[3]
    enumE = a[4]
    enumF = a[5]
    intA = Integer(3);
    testExprs = ["enumA", "enumB", "enumC", "enumD", "enumE", "enumF",
                 "enumA==enumB", "enumA!=enumB", "enumC==enumD",
                 "enumD==enumE", "enumA!=enumB",  "enumC==intA",
                 "enumA and 0", "enumF and 0", " not enumA", "not enumF",
                 "enumA and 1", "enumF and 1"
                 ]
    for expr in testExprs:
        evalExpr(expr,globals(),locals())

if __name__ == '__main__':
    print 'Start testing ENUMERATED...'
    testMethods()
    testFunctions()
    testComparisons()

from Enumerated import Enumerated
a = Enumerated([(1,2),(3,4)])
c = Enumerated(['try', 2, 'testing'])
c.hasName('try')
c.size()
f = Enumerated(None)
g = Enumerated()
