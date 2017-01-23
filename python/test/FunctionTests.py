from Vector import Vector;
from Matrix import Matrix;
from Boolean import Boolean;
from Enumerated import Enumerated;
from Real import Real;
from Integer import Integer
from TestHelper import *

from DFunctions import *


print 'Vector tests'
print '------------'
vectorA = Vector(3)
fill(vectorA, 3)
print 'vecA =', vectorA
print 'subVector =', subVector(vectorA, 0, 1)
print 'size =', size(vectorA)
print 'isCol =', isCol(vectorA)
print 'isRow =', isRow(vectorA)
vectorB = transpose(vectorA)
print 'transpose =', vectorB
print 'isCol =', isCol(vectorB)
print 'isRow =', isRow(vectorB)
setSize(vectorA, 5, 0)
print 'vecA =', vectorA
setSize(vectorA, 3, 0)
print 'vecA =', vectorA
print 'euclidNorm =', euclidNorm(vectorA)
print 'absSum =', absSum(vectorA)
print 'sumOfSquares =', sumOfSquares(vectorA)
print 'isEmpty =', isEmpty(vectorA)
matrixA = Matrix(vectorA)


print
print 'Matrix tests'
print '------------'
matrixA = Matrix(3,3)
fill (matrixA, 1)
print 'A =', matrixA
print 'transpose =', transpose(matrixA)
print 'subMatrix =', subMatrix (matrixA, 0, 0, 1, 1)
print 'trace =', trace(matrixA)
print 'euclidNorm =', euclidNorm(matrixA)
print 'SoS =', sumOfSquares(matrixA)
print 'absSum =', absSum(matrixA)
print 'getRow =', getRow(matrixA,0)
print 'getCol =', getCol(matrixA,0)
print 'numRows =', numRows(matrixA)
print 'numCols =', numCols(matrixA)
print 'setSize = ', setSize(matrixA, 4, 4, 0)
print 'size =', size(matrixA)
print 'setNumRows =', setNumRows(matrixA, 3)
print 'setNumCols =', setNumCols(matrixA, 3)
print 'size =', size(matrixA)
print 'setRow =', setRow(matrixA, 0, vectorA);
print 'A =', matrixA
print 'setCol =', setCol(matrixA, 0, vectorA);
print 'A =', matrixA
print 'isEmpty =', isEmpty(matrixA)
print 'isSquare =', isSquare(matrixA)
fill(matrixA,0)
matrixA[0][0] = 1;
matrixA[1][1] = 1;
matrixA[2][2] = 1;
print 'A =', matrixA
print 'inverse =', inverse(matrixA)
print 'rowEchelon =', rowEchelon(matrixA)
print 'det =', det(matrixA)


print
print 'Enumerated tests'
print '----------------'
enumA = Enumerated(('foo', 2, Integer(4), Boolean(0), Vector(), Matrix(3,4), Real(3.3)))
print 'getIndex =', getIndex(enumA)
print 'getName =', getName(enumA)
set(enumA, 2)
print 'getIndex =', getIndex(enumA)
print 'getName =', getName(enumA)
print 'getValue =', getValue(enumA)
print 'size =', size(enumA)
print 'names = ', names(enumA)