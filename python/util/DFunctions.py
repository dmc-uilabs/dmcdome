# DFunctions.py
# This class implements DOME functions

from Matrix import Matrix
from Vector import Vector
from Enumerated import Enumerated
from operator import truth

#=====================================================================
# vector/matrix functions

# transpose a matrix or vector
def transpose (A):
    if isMatrixOrVector(A):
        return A.transpose()
    else:
        raise TypeError(str(A) + ' is not of type Matrix or Vector')

# calculate the Euclidean norm of a matrix or vector
def euclidNorm(A):
    if isMatrixOrVector(A):
        return A.euclidNorm()
    else:
        raise TypeError(str(A) + ' is not of type Matrix or Vector')

# calculate the sum of squares of the entries in a matrix or vector
def sumOfSquares(A):
    if isMatrixOrVector(A):
        return A.sumOfSquares()
    else:
        raise TypeError(str(A) + ' is not of type Matrix or Vector')

# calculate sum of absolute values of the entries in a matrix or vector
def absSum(A):
    if isMatrixOrVector(A):
        return A.absSum()
    else:
        raise TypeError(str(A) + ' is not of type Matrix or Vector')

# fill a matrix or vector with the value x
def fill(A, x):
    if isMatrixOrVector(A):
        return A.fill(x)
    else:
        raise TypeError(str(A) + ' is not of type Matrix or Vector')

# return the size of a matrix, vector, or enumeration
def size(A):
    if isMatrixOrVector(A) or isEnumerated(A):
        return A.size()
    else:
        raise TypeError(str(A) + ' is not of type Matrix or Vector')

# checks whether a matrix or vector is empty
def isEmpty(A):
    if isMatrixOrVector(A):
        return A.isEmpty()
    else:
        raise TypeError(str(A) + ' is not of type Matrix or Vector')

# set the size of a matrix or vector
def setSize(A, size1, arg2, *arg3):
    if isVector(A):
        return A.setSize(size1, arg2)
    elif isMatrix(A):
        return A.setSize(size1, arg2, arg3)
    else:
        raise TypeError(str(A) + ' is not of type Matrix or Vector')
#=====================================================================


#=====================================================================
# vector-only functions

# get a subvector
def subVector(A, element1, element2):
    if isVector(A):
        return A.subVector(element1, element2)
    else:
        raise TypeError(str(A) + ' is not of type Vector')

# determine whether a vector is a row vector
def isRow(A):
    if isVector(A):
        return A.isRow()
    else:
        raise TypeError(str(A) + ' is not of type Vector')

# determine whether a vector is a column vector
def isCol(A):
    if isVector(A):
        return A.isCol()
    else:
        raise TypeError(str(A) + ' is not of type Vector')     
#=====================================================================


#=====================================================================
# matrix-only functions

# compute the inverse of a matrix
def inverse(A):
    if isMatrix(A):
        return A.inverse()
    else:
        raise TypeError(str(A) + ' is not of type Matrix')

# get the row echelon form of a matrix
def rowEchelon(A):
    if isMatrix(A):
        return A.rowEchelon()
    else:
        raise TypeError(str(A) + ' is not of type Matrix')

# get a submatrix
def subMatrix(A, row1, col1, row2, col2):
    if isMatrix(A):
        return A.subMatrix(row1, col1, row2, col2)
    else:
        raise TypeError(str(A) + ' is not of type Matrix')

# get the determinant of a matrix
def det(A):
    if isMatrix(A):
        return A.det()
    else:
        raise TypeError(str(A) + ' is not of type Matrix')

# get the trace of a matrix
def trace(A):
    if isMatrix(A):
        return A.trace()
    else:
        raise TypeError(str(A) + ' is not of type Matrix')

# get a row vector from a matrix
def getRow(A, row):
    if isMatrix(A):
        return A.getRow(row)
    else:
        raise TypeError(str(A) + ' is not of type Matrix')

# get a column vector from a matrix
def getCol(A, col):
    if isMatrix(A):
        return A.getCol(col)
    else:
        raise TypeError(str(A) + ' is not of type Matrix')

# get the number of rows in a matrix
def numRows(A):
    if isMatrix(A):
        return A.numRows()
    else:
        raise TypeError(str(A) + ' is not of type Matrix')

# get the number of columns in a matrix
def numCols(A):
    if isMatrix(A):
        return A.numCols()
    else:
        raise TypeError(str(A) + ' is not of type Matrix')

# set the number of rows in a matrix
def setNumRows(A, rows):
    if isMatrix(A):
        return A.setNumRows(rows)
    else:
        raise TypeError(str(A) + ' is not of type Matrix')

# set the number of columns in a matrix
def setNumCols(A, cols):
    if isMatrix(A):
        return A.setNumCols(cols)
    else:
        raise TypeError(str(A) + ' is not of type Matrix')

# set the values in a matrix row to those in the vector
def setRow(matA, row, vecB):
    if isMatrix(matA):
        return matA.setRow(row, vecB)
    else:
        raise TypeError(str(A) + ' is not of type Matrix')

# set the values in a matrix column to those in the vector
def setCol(matA, row, vecB):
    if isMatrix(matA):
        return matA.setCol(row, vecB)
    else:
        raise TypeError(str(A) + ' is not of type Matrix')

# determine whether a matrix is square
def isSquare(A):
    if isMatrix(A):
        return A.isSquare()
    else:
        raise TypeError(str(A) + ' is not of type Matrix')
#=====================================================================


#=====================================================================
# enumerated-only functions

# set the value of an element
def set(A, x):
    if isEnumerated(A):
        return A.set(x)
    else:
        raise TypeError(str(A) + ' is not of type Enumerated')

# get the index of the currently selected element
def getIndex(A):
    if isEnumerated(A):
        return A.getIndex()
    else:
        raise TypeError(str(A) + ' is not of type Enumerated')

# get the name of the currently selected element
def getName(A):
    if isEnumerated(A):
        return A.getName()
    else:
        raise TypeError(str(A) + ' is not of type Enumerated')

# get the value of the currently selected element
def getValue(A):
    if isEnumerated(A):
        return A.getValue()
    else:
        raise TypeError(str(A) + ' is not of type Enumerated')

# get an array containing all the element names
def names(A):
    if isEnumerated(A):
        return A.names()
    else:
        raise TypeError(str(A) + ' is not of type Enumerated')
#=====================================================================


#=====================================================================
# utility functions

# check whether the object is a matrix or a vector
def isMatrixOrVector(A):
    if (not isinstance(A, Vector) and not isinstance(A, Matrix)):
        return truth(0)
    else:
        return truth(1)

# check whether the object is a vector
def isVector(A):
    if (not isinstance(A, Vector)):
        return truth(0)
    else:
        return truth(1)

# check whether the object is a matrix
def isMatrix(A):
    if (not isinstance(A, Matrix)):
        return truth(0)
    else:
        return truth(1)

# check whether the object is an enumeration type
def isEnumerated(A):
    if (not isinstance(A, Enumerated)):
        return truth(0)
    else:
        return truth(1)
#=====================================================================