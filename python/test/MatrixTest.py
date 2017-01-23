# MatrixTest.py
# Test Real data type

from TestHelper import *
from Matrix import Matrix
from Vector import Vector
from Real import Real
from Integer import Integer

def initVector1():
    vectorA = Vector()
    vectorA.setSize(2)
    vectorA[0] = 1
    vectorA[1] = 2
    vectorB = Vector()
    vectorB.setSize(4)
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
    return [vectorA, vectorB, vectorC]

def initVector2():
    vectorA = Vector()
    vectorA.setSize(3)
    vectorA[0] = 1
    vectorA[1] = 2
    vectorA[2] = 3
    vectorB = Vector()
    vectorB.setSize(4)
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
    return [vectorA, vectorB, vectorC]


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

def initMatrix2():
    matrixA = Matrix(3,3)
    matrixA[0][0] = 1
    matrixA[0][1] = 0
    matrixA[0][2] = 0
    matrixA[1][0] = -5
    matrixA[1][1] = 1
    matrixA[1][2] = 0
    matrixA[2][0] = 0
    matrixA[2][1] = 0
    matrixA[2][2] = 1
    # inverse of A
    matrixB = Matrix(3,3)
    matrixB[0][0] = 1
    matrixB[0][1] = 0
    matrixB[0][2] = 0
    matrixB[1][0] = 5
    matrixB[1][1] = 1
    matrixB[1][2] = 0
    matrixB[2][0] = 0
    matrixB[2][1] = 0
    matrixB[2][2] = 1
    # idenity matrix, rref from of a
    matrixC = Matrix(3,3)
    matrixC[0][0] = 1
    matrixC[0][1] = 0
    matrixC[0][2] = 0
    matrixC[1][0] = 0
    matrixC[1][1] = 1
    matrixC[1][2] = 0
    matrixC[2][0] = 0
    matrixC[2][1] = 0
    matrixC[2][2] = 1
    matrixD = Matrix(2,3)
    return [matrixA, matrixB, matrixC, matrixD]





def testCopy():
    print '***** test Copying of Matrix'
    from Matrix import Matrix
    a = initMatrix1()
    matrixA = a[0]
    matrixB = a[1]
    matrixC = a[2]
    matrixD = a[3]
    matrixE = a[4]
    testExprs = ["matrixA", "matrixB", "matrixC", "matrixD",
                 "matrixB.copyFrom(matrixA)", "matrixC.copyFrom(matrixA)",
                 "matrixD.copyFrom(matrixA)", "matrixA==matrixB"]
    for expr in testExprs:
        evalExpr(expr,globals(),locals())
    testStmts = ["matrixE = matrixA.dup()", "print matrixE",
                 "matrixF = matrixD.dup()", "print matrixF",]
    for stmt in testStmts:
        execStmt(stmt,globals(),locals())

def testOperators():
    print '***** test Matrix Math Operators'
    a = initMatrix1()
    matrixA = a[0]
    matrixB = a[1]
    matrixC = a[2]
    matrixD = a[3]
    b = initVector1()
    vectorA = b[0]
    vectorB = b[1]
    vectorC = b[2]
    testExprs = ["matrixA", "matrixB", "matrixC", "matrixD",
                 "vectorA", "vectorB", "vectorC",
                 "matrixB+matrixA", "matrixA+matrixB",
                 "matrixC+matrixD", "matrixB+matrixC",
                 "matrixA + vectorA", "matrixC + vectorC",
                 "matrixA + 3.5", "2 + matrixA",
                 "matrixB-matrixA", "matrixA-matrixB",
                 "matrixC-matrixD", "matrixB-matrixC",
                 "matrixA-vectorA", "matrixC-vectorC",
                 "matrixA - 3.5", "2 - matrixA",
                 "matrixB*matrixA", "matrixA*matrixB",
                 "matrixC*matrixD", "matrixB*matrixC",
                 "matrixA*vectorA", "matrixC*vectorC",
                 "matrixA * 3.5", "2 * matrixA",
                 "matrixB/matrixA", "matrixA/matrixB",
                 "matrixC/matrixD", "matrixB/matrixC",
                 "matrixA/vectorA", "matrixC/vectorC",
                 "matrixA/3.5", "2/matrixA"
                 ]
    for expr in testExprs:
        evalExpr(expr,globals(),locals())
    
def testFunctions1():
    print '***** test Matrix Math Functions 1'
    a = initMatrix2()
    matrixA = a[0]
    inverseA = a[1]
    idA = a[2]
    matrixD = a[3]
    matrixE = Matrix(4,4)
    b = initVector2()
    vectorA = b[0]
    vectorB = b[1]
    vectorC = b[2]
    real3third = Real(3.3)
    integer2 = Integer(2)
    testExprs = ["matrixA", "inverseA", "idA", "matrixD", "matrixE",
                 "vectorA", "vectorB", "vectorC",
                 "matrixA.inverse()", "matrixA.inverse()==inverseA",
                 "matrixA.transpose()", "matrixA.rowEchelon()",
                 "matrixA.subMatrix(0,1,0,1)","matrixA.det()==1",
                 "matrixA.trace()==3", "matrixA.euclidNorm()",
                 "matrixA.sumOfSquares()", "matrixA.absSum()",
                 "matrixD.fill(4)", "matrixD", "matrixD.fill(2.5)", "matrixD",
                 "matrixD.fill(real3third)", "matrixD",
                 "matrixD.fill(integer2)", "matrixD",
                 "matrixA.getRow(1)", "matrixA.getRow(1).getClass().getName()",
                 "matrixA.getCol(0)", "matrixA.getCol(0).getClass().getName()",
                 "matrixA.numCols()", "matrixA.numRows()",
                 "matrixA.size()", "matrixD.setSize(2,2)",
                 "matrixD",
                 "matrixD.setSize(3,3,1.4)", "matrixD",
                 "matrixD.setNumRows(4)", "matrixD",
                 "matrixD.setNumRows(6, 3.5)", "matrixD",
                 "matrixD.setNumCols(1)", "matrixD",
                 "matrixD.setNumCols(3, 4)", "matrixD",
                 "matrixD.setNumRows(5, real3third)", "matrixD",
                 "matrixD.setNumCols(2, integer2)", "matrixD",
                 "matrixD.setRow(1,vectorB)", "matrixD",
                 "matrixA.setRow(2,vectorA)", "matrixA",
                 "inverseA.setCol(0,vectorA)", "inverseA",
                 "matrixA.isEmpty()", "matrixE.isEmpty()",
                 "matrixE.isSquare()", "matrixE",
                 "matrixA.isSquare()","matrixA"
                 ]
    for expr in testExprs:
        evalExpr(expr,globals(),locals())



if __name__ == '__main__':
    print 'Start testing MATRIX...'
    #testCopy()
    testOperators()
    #testFunctions1()
