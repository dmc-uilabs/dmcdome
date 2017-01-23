# VectorTest.py
# Test Vector data type
import sys
print sys.path
from TestHelper import *
from Vector import Vector
from Matrix import Matrix
from Real import Real
from Integer import Integer
print sys.path

def initVector1():
    vectorA = Vector()
    vectorA.setSize(4)
    vectorA[0] = 1
    vectorA[1] = 2
    vectorA[2] = 3
    vectorA[3] = 4
    vectorB = Vector()
    vectorB.setSize(4)
    vectorB.setRowVector(1)
    vectorB[0] = 5
    vectorB[1] = 6
    vectorB[2] = 7
    vectorB[3] = 8
    vectorC = Vector()
    vectorC.setSize(6)
    vectorC[0] = 9
    vectorC[1] = 10
    vectorC[2] = 11
    vectorC[3] = 12
    vectorC[4] = 13
    vectorC[5] = 14
    vectorD = Vector()
    vectorE = Vector()
    vectorE.setSize(4)
    vectorE.setValueType('real')
    vectorE[0] = 5.5
    vectorE[1] = 6.6
    vectorE[2] = 7.7
    vectorE[3] = 8.8
    return [vectorA, vectorB, vectorC, vectorD, vectorE]

def initMatrix1():
    matrixA = Matrix(3,4)
    matrixA[0][0] = 1
    matrixA[0][1] = 2
    matrixA[0][2] = 3
    matrixA[0][3] = 4
    matrixA[1][0] = 5
    matrixA[1][1] = 6
    matrixA[1][2] = 7
    matrixA[1][3] = 8
    matrixA[2][0] = 9
    matrixA[2][1] = 10
    matrixA[2][2] = 11
    matrixA[2][3] = 12
    matrixB = Matrix(3,4)
    matrixC = Matrix(2,2)
    matrixD = Matrix(6,8)
    matrixE = Matrix(1,4)
    return [matrixA, matrixB, matrixC, matrixD, matrixE]

def testCopy():
    print '***** test Copying of Vector'
    a = initVector1()
    vectorA = a[0]
    vectorB = a[1]
    vectorC = a[2]
    vectorD = a[3]
    testExprs = ["vectorA", "vectorB", "vectorC", "vectorD",
                 "vectorB.copyFrom(vectorA)", "vectorC.copyFrom(vectorA)",
                 "vectorD.copyFrom(vectorA)", "vectorA==vectorB"]
    for expr in testExprs:
        evalExpr(expr,globals(),locals())
    testStmts = ["vectorE = vectorA.dup()", "print vectorE",
                 "vectorF = vectorD.dup()", "print vectorF",]
    for stmt in testStmts:
        execStmt(stmt,globals(),locals())

def testOperators():
    print '***** test Vector Math Operators'

    a = initMatrix1()
    matrixA = a[0]
    matrixB = a[1]
    matrixC = a[2]
    matrixD = a[3]
    b = initVector1()
    vectorA = b[0]
    vectorB = b[1]
    vectorC = b[2]
    vectorD = b[3]
    vectorE = b[4]
    testExprs = [
                 "vectorA", "vectorB", "vectorC", "vectorD", "vectorE",
                 "vectorB+vectorA", "vectorA+vectorB",
                 "vectorA+vectorE",
                 "vectorC+matrixD", "matrixB+vectorC",
                 "vectorA + vectorA", "vectorC + vectorC",
                 "vectorA + 3.5", "2 + vectorA",
                 "1.5 + vectorA", "5 + vectorC",
                 "vectorA + 3.5 + vectorE", "vectorE +vectorE",
                 "vectorB-vectorA", "vectorA-vectorB",
                 "vectorA-vectorE",
                 "vectorC-vectorD", "vectorB-vectorC",
                 "vectorA-vectorA", "vectorC-vectorC",
                 "vectorA - 3.5", "2 - vectorA",
                 "vectorA - 3.5 - vectorE", "vectorE - vectorE",
                 "vectorB*vectorA", "vectorA*vectorB",
                 "vectorA*vectorE",
                 "vectorC*vectorD", "vectorB*vectorC",
                 "vectorA*vectorA", "vectorC*vectorC",
                 "vectorA * 3.5", "2 * vectorA",
                 "vectorA * 3.5 * vectorE", "vectorE  *vectorE",
                 "vectorB/vectorA", "vectorA/vectorB",
                 "vectorA/vectorE",
                 "vectorC/vectorD", "vectorB/vectorC",
                 "vectorA/vectorA", "vectorC/vectorC",
                 "vectorA/3.5", "2/vectorA",
                 "vectorA /3.5 /vectorE", "vectorE/vectorE",
                 ]
    for expr in testExprs:
        evalExpr(expr,globals(),locals())


def testFunctions1():
    print '***** test Vector Math Functions 1'
    #a = initMatrix1()
    #matrixA = a[0]
    #matrixB = a[1]
    #matrixC = a[2]
    #matrixD = a[3]
    b = initVector1()
    vectorA = b[0]
    vectorB = b[1]
    vectorC = b[2]
    vectorD = b[3]
    vectorE = b[4]
    real3third = Real(3.3)
    integer2 = Integer(2)
    testExprs = [
        "vectorA", "vectorB", "vectorC", "vectorD", "vectorE",
        "vectorA.transpose()", "vectorD.transpose()",
        "vectorE.transpose()", 
        "vectorA.subVector(1,2)", "vectorA.subVector(0,0)",
        "vectorD.subVector(1,2)", "vectorD.subVector(0,0)",
        "vectorC.subVector(4,6)", "vectorC.subVector(0,5)",
        "vectorE.subVector(2,4)", "vectorE.subVector(0,0)",
        "vectorA.euclidNorm()", "vectorB.euclidNorm()",
        "vectorC.euclidNorm()", "vectorD.euclidNorm()",
        "vectorE.euclidNorm()",
        "vectorA", "vectorB", "vectorC", "vectorD", "vectorE",
        "vectorA.sumOfSquares()", "vectorB.sumOfSquares()",
        "vectorC.sumOfSquares()", "vectorD.sumOfSquares()",
        "vectorE.sumOfSquares()",
        "vectorA.absSum()", "vectorB.absSum()",
        "vectorC.absSum()", "vectorD.absSum()",
        "vectorE.absSum()",
        "vectorD.fill(4)", "vectorD",
        "vectorC.fill(4)", "vectorC","vectorC.fill(2.5)", "vectorC",
        "vectorC.fill(real3third)", "vectorC",
        "vectorC.fill(integer2)", "vectorC",
        "vectorE.fill(4)", "vectorE", "vectorE.fill(2.5)", "vectorE",
        "vectorE.fill(real3third)", "vectorE",
        "vectorE.fill(integer2)", "vectorE",
        "vectorA.size()", "vectorB.size()",
        "vectorC.size()", "vectorD.size()",
        "vectorE.size()",
        "vectorA", "vectorB", "vectorC", "vectorD", "vectorE",
        "vectorD.setSize(2)","vectorD",
        "vectorD.setSize(0)","vectorD",
        "vectorD.setSize(10)", "vectorD",
        "vectorD.setSize(3,1.4)", "vectorD",
        "vectorD.setSize(4, real3third)", "vectorD",
        "vectorD.setSize(11, 3.5)", "vectorD",
        "vectorD.setSize(1)", "vectorD",
        "vectorD.setSize(3, 4)", "vectorD",
        "vectorD.setSize(5, real3third)", "vectorD",
        "vectorD.setSize(2, integer2)", "vectorD",
        "vectorD.setSize(0)",
        "vectorE.setSize(4, real3third)", "vectorE",
        "vectorE.setSize(11, 3.5)", "vectorE",
        "vectorE.setSize(1)", "vectorE",
        "vectorE.setSize(3, 4)", "vectorE",
        "vectorE.setSize(5, real3third)", "vectorE",
        "vectorE.setSize(2, integer2)", "vectorE",
        "vectorA", "vectorB", "vectorC", "vectorD", "vectorE",
        "vectorA.isEmpty()", "vectorD.isEmpty()", "vectorE.isEmpty()",
        "vectorA.isRow()", "vectorB.isRow()", "vectorC.isRow()",
        "vectorD.isRow()", "vectorE.isRow()",
        "vectorA.isCol()", "vectorB.isCol()", "vectorC.isCol()",
        "vectorD.isCol()", "vectorE.isCol()"
                 ]
    for expr in testExprs:
        evalExpr(expr,globals(),locals())



if __name__ == '__main__':
    print 'Start testing VECTOR...'
    #testCopy()
    #testOperators()
    testFunctions1()
