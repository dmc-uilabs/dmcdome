# Vector.py
# This class represents vectors.
#from __future__ import division
from DataType import DataType
from Number import Number
import Matrix
# Somehow this avoids import problems.
from types import IntType, FloatType
from mit.cadlab.dome3.objectmodel.dataobject import DomeVectorData
from math import fabs, sqrt
import sys, traceback

#Odd rounding occurs if the value type is Integer/int and
#operations involve a Real/float, it gets casted to the correct type



class Vector(DataType, DomeVectorData):
    def __init__(self,value0=0):
        self.dataType = "Vector"
        # Input is a DomeVectorData
        if isinstance(value0, DomeVectorData):
            DomeVectorData.__init__(self, value0)
        elif (type(value0)==IntType):
            # size specified
            DomeVectorData.__init__(self, value0)

    #def __del__(self):

    def __repr__(self):
        return str(self.getData())

    def __str__(self):
        return str(self.getData())

    def __cmp__(self,other):
        raise TypeError(str(self)+' cmp '+str(other))

    #def __hash__(self):

    def __nonzero__(self):
        # Return 0 if all elements are 0
        for i in range(self.getSize()):
            if (self[i]!=0):
                return 1
        return 0

    # Methods for Attribute Access
    #def __getattr__(self, name):
    #def __setattr__(self, name, value):
    #def __delattr__(self, name):

    # Methods for Sequence and Mapping
    def __len__(self):
        return self.getSize()

    def __getitem__(self, key):
        if key < self.getSize():
            return self.getItem(key)
        else:
            raise IndexError('Index '+str(key)+' out of range')

    def __setitem__(self, key, value):
        if (key < self.getSize()):
            self.setItem(key, value)
        else:
            raise IndexError('Index '+str(key)+' out of range')

    #def __delitem__(self, key):
    #def __getslice__(self,i,j,s):
    #def __setslice__(self,i,j,s):
    #def __delslice__(self,i,j):
    #def __contains__(self,obj):



    # Mathematical Operations methods
    def __add__(self,other):
        try:
            return self._add(other)
        except TypeError, e:
            try:
                return other.__radd__(self)
            except:
                raise e

    def __sub__(self,other):
        try:
            return self._sub(other)
        except TypeError, e:
            try:
                return other.__rsub__(self)
            except:
                raise e

    def __mul__(self,other):
        try:
            return self._mul(other)
        except TypeError, e:
            try:
                return other.__rmul__(self)
            except:
                raise e

    def __div__(self,other):
        try:
            return self._div(other)
        except TypeError, e:
            try:
                return other.__rdiv__(self)
            except:
                raise e

    #def __mod__(self,other):
    #def __divmod__(self,other):
    #def __pow__(self,other):
    #def __lshift__(self,other):
    #def __rshift__(self,other):
    #def __and__(self,other):
    #def __or__(self,other):
    #def __xor__(self,other):
    def __radd__(self,other):
        try:
            return self._add(other)
        except TypeError, e:
            raise e

    def __rsub__(self,other):
        try:
            return -1*self._sub(other)
        except TypeError, e:
            raise e

    def __rmul__(self,other):
        try:
            return self._mul(other)
        except TypeError, e:
            raise e

    def __rdiv__(self,other):
        raise TypeError("Cannot divide by a vector.")

    #def __rmod__(self,other):
    #def __rdivmod__(self,other):
    #def __rpow__(self,other):
    #def __rlshift__(self,other):
    #def __rrshift__(self,other):
    #def __rand__(self,other):
    #def __ror__(self,other):
    #def __rxor__(self,other):
    #def __iadd__(self,other):
    #def __isub__(self,other):
    #def __imul__(self,other):
    #def __idiv__(self,other):
    #def __imod__(self,other):
    #def __ipow__(self,other):
    #def __ilshift__(self,other):
    #def __irshift__(self,other):
    #def __iand__(self,other):
    #def __ior__(self,other):
    #def __ixor__(self,other):

    def __neg__(self):
        return self*-1

    #def __pos__(self):
    #def __abs__(self):
    #def __invert__(self):
    #def __int__(self):
    #def __long__(self):
    #def __float__(self):
    #def __complex__(self):
    #def __oct__(self):
    #def __hex__(self):
    #def __coerce__(self,other):


    # Comparison Operations
    #def __lt__(self,other):
    #def __le__(self,other):
    #def __gt__(self,other):
    #def __ge__(self,other):

    def __eq__(self,other):
        if (isinstance(other,Vector)) and (self.getSize()==other.getSize()):
            for i in range(self.getSize()):
                if self[i] != other[i]:
                        return 0
            return 1
        else:
            return 0

    def __ne__(self,other):
        if (isinstance(other,Vector)) and (self.getSize()==other.getSize()):
            for i in range(self.getSize()):
                if self[i] != other[i]:
                    return 1
            return 0
        else:
            return 1


    # Overidding methods of DataType
    def _copyTo(self,other):
        # not implemented
        #if isinstance(other,Vector):
        #   other.value=self.value
        #else:
        DataType._copyTo(self,other)

        # shallow copy
    def _copyFrom(self,other):
        if isinstance(other, Vector):
            if self.isFixedSize() and (self.getSize()!=other.getSize()):
                raise ValueError(str(self)+' copyFrom '+str(other)+': mis-matched sizes')
            else:
                if not self.isFixedSize():
                   self.setSize(other.getSize())
                self.setValueType(other.getValueType())
                # setData updates the units, initialValue, isFixedSize
                # isRowVector
                self.setData(other)
        elif isinstance(other, ListType):
            if self.isFixedSize() and (self.getSize()!= len(other)):
                raise ValueError(str(self)+' copyFrom '+str(other)+': mis-matched sizes')
            else:
                if not self.isFixedSize():
                   self.setSize(other.getSize())
                self.setUnit(other.getUnit())
                self.setInitialValue(other.getInitialValue())
                self.setFixedSize(other.isFixedSize())
                self.setRowVector(other.isRowVector())
                for i in range(len(other)):
                    self[i] = other[i]
        else:
            # raises TypeError
            DataType._copyFrom(self,other)


    def dup(self):
        # return a copy of one self
        #return Vector(self.duplicate())
        # !!!!!!!! deep copy in Java is broken !!!!!!
        returnVector=Vector()
        returnVector.setSize(self.getSize())
        returnVector.setValueType(self.getValueType())
        returnVector.setUnit(self.getUnit())
        for i in range(self.getSize()):
            if isNumberType(self[i]):
                returnVector[i]=self[i]
            else:
                returnVector[i]=self[i].dup()
        returnVector.setInitialValue(self.getInitialValue())
        returnVector.setFixedSize(self.isFixedSize())
        returnVector.setRowVector(self.isRowVector())
        return returnVector

    # Vector specific functions, probably will move to DMath.py
    def transpose(self):
        # rows vs. columns.
        returnVector = self.dup()
        j = self.getSize() - 1
        for i in range(self.getSize()):
            returnVector[i] = self[j]
            j = j - 1
        returnVector.setRowVector (not self.isRowVector())
        return returnVector

    def subVector(self, e1, e2):
        returnVector = Vector()
        returnVector.setValueType(self.getValueType())
        returnVector.setUnit(self.getUnit())
        returnVector.setInitialValue(self.getInitialValue())
        returnVector.setFixedSize(self.isFixedSize())
        returnVector.setRowVector(self.isRowVector())
        returnVector.setSize(abs(e2-e1)+1)
        if e1<= e2 :
            j = 0
            for i in range(e1, e2+1):
                returnVector[j] = self[i]
                j = j + 1
        else:
            j = 0
            for i in range(e2, e1+1):
                returnVector[j] = self[i]
                j = j + 1
        return returnVector

    def euclidNorm(self):
        return sqrt(self.sumOfSquares())


    def sumOfSquares(self):
        ss = 0.0
        for i in range(self.getSize()):
            ss = ss + (self[i]*self[i])
        return ss



    def absSum(self):
        result = 0.0
        for i in range(self.getSize()):
            result += abs(self[i])
        return result


    def fill(self, value):
        # integer, real, float, int
        if (isinstance(value, Number)):
            value = value.getValue()
        for i in range(self.getSize()):
            self[i] = value


    def size(self):
        return self.getSize()

    # use super method
    def setSize(self, size, value=0):
        if (size > self.getSize()) and (value != None):
            if (isinstance(value, Number)):
                value = value.getValue()
            DomeVectorData.setSize(self, size, value)
        else:
            DomeVectorData.setSize(self, size)


    def isEmpty(self):
        return (self.getSize()==0)

    def isRow(self):
        return (self.isRowVector())


    def isCol(self):
        return not (self.isRowVector())

       # Actually implementation of mathematical operators
    def _add(self,other):
        if isinstance(other, Number):
            #scalar add
            returnVector = self.dup()
            if (isinstance(other, Real)):
                # automatically change return matrix type to real
                returnMatrix.setValueType("real")
            for i in range(returnVector.getSize()):
                returnVector[i] = returnVector[i]+other.value
            return returnVector
        elif isNumberType(other):
            #scalar add
            returnVector = self.dup()
            if (type(other)==FloatType):
                # automatically change return matrix type to real
                returnVector.setValueType("real")
            #print '....other value: ' + str(other)
            #print '....value type: ' + str(self.getValueType())
            for i in range(returnVector.getSize()):
                #nv = returnVector[i]+other
                #print ' ....... new value: ' + str(nv)
                #returnVector[i] = nv
                returnVector[i] = returnVector[i]+other
            return returnVector
        elif isinstance(other, Matrix.Matrix):
            # check the size of other matrix
            if (other.getRowCount() != 1) or (other.getColumnCount()!=1):
                raise ValueError(str(self)+' + '+str(other)+
                                 ': mis-matched sizes')
            elif (self.getSize()== other.getRowCount()):
                returnVector = self.dup()
                for i in range(returnVector.getSize()):
                    returnVector[i] = returnVector[i]+other[0][i]
                return returnVector
            elif (self.getSize() == other.getColumnCount()):
                returnVector = self.dup()
                for i in range(returnVector.getSize()):
                    returnVector[i] = returnVector[i]+other[i][0]
                return returnVector
            else:
                raise ValueError(str(self)+' + '+str(other)+': mis-matched sizes')
        elif isinstance(other, Vector):
            if (self.getSize()==other.getSize()):
                returnVector = self.dup()
                for i in range(other.getSize()):
                    returnVector[i] = returnVector[i] + other[i]
                return returnVector
            else:
                raise ValueError(str(self)+' + '+str(other)+
                                 ': mis-matched sizes')
        elif isinstance(other, ListType):
            if (self.getSize()==len(other)):
                returnVector = self.dup()
                for i in range(len(other)):
                    returnVector[i] = returnVector[i]+other[i]
                return returnVector
            else:
                raise ValueError(str(self)+' + '+str(other)+
                                 ': mis-matched sizes')
        else:
            raise TypeError(str(self)+' + '+str(other))



    def _sub(self,other):
        if isinstance(other, Number):
            #scalar add
            returnVector = self.dup()
            for i in range(returnVector.getSize()):
                returnVector[i] = returnVector[i]-other.value
        elif isNumberType(other):
            #scalar add
            returnVector = self.dup()
            for i in range(returnVector.getSize()):
                returnVector[i] = returnVector[i]-other
                return returnVector
        elif isinstance(other, Matrix.Matrix):
            # check the size of other matrix
            if (other.getRowCount() != 1) or (other.getColumnCount()!=1):
                raise ValueError(str(self)+'-'+str(other)+
                                 ': mis-matched sizes')
            elif (self.getSize()== other.getRowCount()):
                returnVector = self.dup()
                for i in range(returnVector.getSize()):
                    returnVector[i] = returnVector[i]-other[0][i]
                return returnVector
            elif (self.getSize() == other.getColumnCount()):
                returnVector = self.dup()
                for i in range(returnVector.getSize()):
                    returnVector[i] = returnVector[i]-other[i][0]
                return returnVector
            else:
                raise ValueError(str(self)+'-'+str(other)+': mis-matched sizes')
        elif isinstance(other, Vector):
            if (self.getSize()==other.getSize()):
                returnVector = self.dup()
                for i in range(other.getSize()):
                    returnVector[i] = returnVector[i] + other[i]
                return returnVector
            else:
                raise ValueError(str(self)+'-'+str(other)+
                                 ': mis-matched sizes')
        elif isinstance(other, ListType):
            if (self.getSize()==len(other)):
                returnVector = self.dup()
                for i in range(len(other)):
                    returnVector[i] = returnVector[i]-other[i]
                return returnVector
            else:
                raise ValueError(str(self)+'-'+str(other)+
                                 ': mis-matched sizes')
        else:
            raise TypeError(str(self)+'-'+str(other))



    def _mul(self,other):
        if isinstance(other, Number):
            #scalar multiplication
            returnVector = self.dup()
            for i in range(returnVector.getSize()):
                returnVector[i] = returnVector[i]*other.value
        elif isNumberType(other):
            #scalar multiplication
            returnVector = self.dup()
            for i in range(returnVector.getSize()):
                returnVector[i] = returnVector[i]*other
            return returnVector
        elif isinstance(other, Matrix.Matrix):
            # check the size of other matrix, size of rows == size of columns
            # or number of columns == # of rows
            # should we check if we are a row vector?
            if (self.getSize()== other.getRowCount()):
                # return has self # rows and other # columns
                returnMatrix = Matrix.Matrix(1,
                                      other.getColumnCount())
                for i in range(returnMatrix.getRowCount()):
                    for j in range(returnMatrix.getColumnCount()):
                        entry = 0
                        index = 0
                        for oR in range(other.getRowCount()):
                                entry = self[index]*other[oR][j]+entry
                                index += 1
                        returnMatrix[i][j] = entry
                return returnMatrix
            else:
                raise ValueError(str(self)+'*'+str(other)+
                                 ': mis-matched sizes')
        elif isinstance(other, Vector):
            # !!!! Need to worry about reverse, Vector * Matrix
            if (self.getSize()==other.getSize()):
                returnMatrix = Matrix.Matrix(1,1)
                entry = 0
                for i in range(self.getSize()):
                    entry = self[i]*other[i] + entry
                returnMatrix[0][0] = entry
                return returnMatrix
            else:
                raise ValueError(str(self)+'* '+str(other)+': mis-matched sizes')
        elif isinstance(other, ListType):
            if (self.getSize()==len(other)):
                returnMatrix = Matrix.Matrix(1,1)
                entry = 0
                for i in range(self.getSize()):
                    entry = self[i]*other[i] + entry
                returnMatrix[0][0] = entry
                return returnMatrix
            else:
                raise ValueError(str(self)+'*'+str(other)+': mis-matched sizes')
        else:
            raise TypeError(str(self)+'*'+str(other))


    def _div(self,other):
        if isinstance(other, Number):
            #scalar multiplication
            returnVector = self.dup()
            for i in range(returnVector.getSize()):
                returnVector[i] = returnVector[i]/other.value
        elif isNumberType(other):
            #like scalar multiplication
            returnVector = self.dup()
            for i in range(returnVector.getSize()):
                returnVector[i] = returnVector[i]/other
            return returnVector
        else:
            raise TypeError(str(self)+'/ '+str(other))


    # Need to change
    #def _pow(self,other):
    #def __radd__(self,other):
    #def __rsub__(self,other):
    #def __rmul__(self,other):
    #def __rdiv__(self,other):
    #def __rmod__(self,other):
    #def __rpow__(self,other):
    #def __neg__(self):
    #def __pos__(self):
    #def __abs__(self):
    #def __int__(self):
    #def __float__(self):
    #def __long__(self):
    #def __complex__(self):


    # Undocumented helper functions
    def isEmpty(self):
        return (self.getSize()==0)


    # Conversion functions
    def array(self):
        returnList = []
        for i in range(self.getSize()):
            returnList.append(self[i])
        return returnList

    def matrix(self):
        returnMatrix = Matrix.Matrix(1, self.size())
        returnMatrix.setUnit(self.getUnit())
        returnMatrix.setInitialValue(self.getInitialValue())
        returnMatrix.setFixedSize(self.isFixedSize())
        returnMatrix.setValueType(self.getValueType())
        return returnMatrix


def isNumberType(x):
    return type(x)==IntType or type(x)==FloatType
