# DMath.py
# This class implements math functions with Dome Numbers

from mit.cadlab.dome3.objectmodel.dataobject import DomeMath
from mit.cadlab.dome3.objectmodel.dataobject import DomeMatrixData
from mit.cadlab.dome3.objectmodel.dataobject import DomeVectorData
from mit.cadlab.dome3.objectmodel.dataobject import EnumerationData
from types import IntType, StringType

def sin(x):
    return DomeMath.sin(x)

def asin(x):
    return DomeMath.asin(x)

def cos(x):
    return DomeMath.cos(x)

def acos(x):
    return DomeMath.acos(x)

def tan(x):
    return DomeMath.tan(x)

def atan(x):
    return DomeMath.atan(x)

def sinh(x):
    return DomeMath.sinh(x)

def asinh(x):
    return DomeMath.asinh(x)

def cosh(x):
    return DomeMath.cosh(x)

def acosh(x):
    return DomeMath.acosh(x)

def tanh(x):
    return DomeMath.tanh(x)

def atanh(x):
    return DomeMath.atanh(x)

def exp(x):
    return DomeMath.exp(x)

def log(x):
    return DomeMath.log(x)

def log10(x):
    return DomeMath.log10(x)

def abs(x):
    return DomeMath.abs(x)

def sqrt(x):
    return DomeMath.sqrt(x)

def floor(x):
    return DomeMath.floor(x)

def ceil(x):
    return DomeMath.ceil(x)

def round(x):
    return DomeMath.round(x)

def Integer(x):
    return DomeMath.convertToInteger(x)

def Real(x):
    return DomeMath.convertToReal(x)

def Matrix(x):
    return DomeMath.convertToMatrix(x)

def Vector(x):
    return DomeMath.convertToVector(x)

def Matrix(x):
    return DomeMath.convertToMatrix(x)

def inverse(x):
    return DomeMath.inverse(x)

def transpose(x):
    return DomeMath.transpose(x)

def rowEchelon(x):
    return DomeMath.rowEchelon(x)

def rowEchelon(x):
    return DomeMath.rowEchelon(x)

def subMatrix(x,r1,c1,r2,c2):
    return DomeMath.subMatrix(x,r1,c1,r2,c2)

def det(x):
    return DomeMath.det(x)

def trace(x):
    return DomeMath.trace(x)

def euclidNorm(x):
    return DomeMath.normF(x)

def sumOfSquares(x):
    return DomeMath.sumOfSquares(x)

def absSum(x):
    return DomeMath.absSum(x)

def fill(x,y):
    DomeMath.fill(x,y)

def getRow(mat, x):
    if isinstance(mat,DomeMatrixData):
        return mat.getRow(x)

def getCol(mat, x):
    if isinstance(mat,DomeMatrixData):
        return mat.getCol(x)

def numRows(mat):
    if isinstance(mat,DomeMatrixData):
        return mat.getRowCount()

def numCols(mat):
    if isinstance(mat,DomeMatrixData):
        return mat.getColumnCount()

def size(mat):
    if isinstance(mat,DomeMatrixData) or isinstance(mat,DomeVectorData):
        return mat.getSize()
    elif isinstance(mat,EnumerationData):
        return mat.getSize()

def setSize(mat,x,y=0,z=0):
    if isinstance(mat,DomeMatrixData):
        mat.setSize(x,y,z)
    elif isinstance(mat,DomeVectorData):
        mat.setSize(x,y)

def setNumRows(mat,x,y=0):
    if isinstance(mat,DomeMatrixData):
        mat.setNumRows(x,y)

def setNumCols(mat,x,y=0):
    if isinstance(mat,DomeMatrixData):
        mat.setNumCols(x,y)

def isEmpty(mat):
    if isinstance(mat,DomeMatrixData) or isinstance(mat,DomeVectorData):
        return mat.isEmpty()

def isSquare(mat):
    if isinstance(mat,DomeMatrixData):
        return mat.isSquare()

def setRow(mat,x,vec):
    if isinstance(mat,DomeMatrixData):
        mat.setRow(x,vec)

def setCol(mat,x,vec):
    if isinstance(mat,DomeMatrixData):
        mat.setCol(x,vec)

def subVector(vec,x,y):
    if isinstance(vec,DomeVectorData):
        return DomeMath.subVector(vec,x,y)

def isRow(vec):
    if isinstance(vec,DomeVectorData):
        return vec.isRow()

def isCol(vec):
    if isinstance(vec,DomeVectorData):
        return vec.isCol()

def set(x,i):
    if isinstance(x,EnumerationData):
        if isinstance(i,IntType):
            x.setLastSelection(i)
            print i
        elif isinstance(i,StringType):
            x.setLastSelectionToName(i)
            print i

def getIndex(x):
    if isinstance(x,EnumerationData):
        return x.getLastSelection()

def getName(x):
    if isinstance(x,EnumerationData):
        return x.getElementName(x.getLastSelection())

def getValue(x):
    if isinstance(x,EnumerationData):
        return x.getElementValue(x.getLastSelection())

def names(x):
    if isinstance(x,EnumerationData):
        return x.getNamesArray()

pi = 3.1415926535897932384626433832795028841971693993751058209749445923