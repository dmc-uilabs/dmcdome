# Matrix.py
# This class represents matrix.
#from __future__ import division
from DataType import DataType
from Number import Number
import Vector
# Somehow this avoids import problems.
from types import IntType, FloatType, ListType
from mit.cadlab.dome3.objectmodel.dataobject import DomeMatrixData
from mit.cadlab.dome3.objectmodel.dataobject import DomeVectorData

#Known Bugs::
#Odd rounding occurs if the value type is Integer/int and
#operations involve a Real/float


from math import fabs, sqrt

class Matrix(DataType, DomeMatrixData):
    # Object Creation and Representation Methods
    def __init__(self,value0=0, value1=1):
        self.dataType = "Matrix"
        # Input is a DomeMatrixData
        if isinstance(value0, DomeMatrixData):
            DomeMatrixData.__init__(self, value0)
        elif (type(value0)==IntType and type(value1)==IntType):
            # row and column size specified
            DomeMatrixData.__init__(self, value0, value1)

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
        for i in range(self.getRowCount()):
            for j in range(self.getColumnCount()):
                if self[i][j] != 0:
                    return 1
        return 0

    # Methods for Attribute Access
    #def __getattr__(self, name):
    #def __setattr__(self, name, value):
    #def __delattr__(self, name):

    # Methods for Sequence and Mapping
    #def __len__(self):

    def __getitem__(self, key):
        #print ' key: ' + str(key)
        if key < self.getRowCount():
            #return self.getRow(key)
            #return Vector.Vector(v[key])
            #return self.getData()[key]
            # KJT: changed return type from that above to that below
            return self.getData()[key]
        else:
            raise IndexError('Index '+str(key)+' out of range')

    #def __setitem__(self, key, value):
     #   print '...... setitem key: ' + str(key)
      #  if key < self.getRowCount():
       #     print '.... 2! setitem key: ' + str(key)
       #     self.setItem(key, value)
        #else:
         #   raise IndexError('Index '+str(key)+' out of range')

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
    #def __radd__(self,other):
    #def __rsub__(self,other):

    def __radd__(self,other):
        try:
            return self._add(other)
        except TypeError, e:
            try:
                return other.__radd__(self)
            except:
                raise e

    def __rsub__(self,other):
        try:
            return self._sub(other)
        except TypeError, e:
            try:
                return other.__rsub__(self)
            except:
                raise e

    def __rmul__(self,other):
        # case of scalar multiplication
        try:
            return self._mul(other)
        except TypeError, e:
            try:
                # is this a loop?
                return other.__rmul__(self)
            except:
                raise e


    def __rdiv__(self,other):
        raise TypeError("Cannot divide by a matrix.")

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
        #print '__eq__'
        if (isinstance(other,Matrix)) and (self.getRowCount()==other.getRowCount()) and (self.getColumnCount()==other.getColumnCount()):
            for i in range(self.getRowCount()):
                for j in range(self.getColumnCount()):
                    if self[i][j] != other[i][j]:
                        #print '... mismatch: ' + ' i=' + str(i) + ' j=' + str(j) + ' ' +str(self[i][j]) + ' ' + str(other[i][j])
                        return 0
            return 1
        else:
            return 0

    def __ne__(self,other):
        if (isinstance(other,Matrix)) and (self.getRowCount()==other.getRowCount()) and (self.getColumnCount()==other.getColumnCount()):
            for i in range(self.getRowCount()):
                for j in range(self.getColumnCount()):
                    if self[i][j] != other[i][j]:
                        return 1
            return 0
        else:
            return 1

    # Overidding methods of DataType
    def _copyTo(self,other):
        # not implemented
        #if isinstance(other,Matrix):
        #   other.value=self.value
        #else:
        DataType._copyTo(self,other)

    # shallow copy
    def _copyFrom(self,other):
        if isinstance(other,Matrix):
            # check the size of other matrix if it's important.
            if self.isFixedSize() and ((self.getRowCount()!= other.getRowCount()) or (self.getColumnCount() != other.getColumnCount())):
                raise ValueError(str(self)+' copyFrom '+str(other)+': mis-matched sizes')
            else:
                self.setValueType(other.getValueType())
                # setData updates the units, initialValue, isFixedSize,
                self.setData(other)
        elif isinstance(other, Vector.Vector):
            if self.isFixedSize() and (self.getRowCount()!=other.getSize()) or (self.getColumnCount()!=1):
                raise ValueError(str(self)+' copyFrom '+str(other)+': mis-matched sizes')
            elif self.isFixedSize() and (self.getColumnCount()!=other.getSize()) or (self.getRowCount()!=1):
                raise ValueError(str(self)+' copyFrom '+str(other)+': mis-matched sizes')
            elif (self.isFixedSize() and self.getColumnCount()==1):
                self.setValueType(other.getValueType())
                self.setUnit(other.getUnit())
                self.setInitialValue(other.getInitialValue())
                self.setFixedSize(other.getFixedSize())
                for i in range(other.getSize()):
                    self[i][0] = other[i]
            elif (self.isFixedSize() and self.getRowCount()==1):
                self.setValueType(other.getValueType())
                self.setUnit(other.getUnit())
                self.setInitialValue(other.getInitialValue())
                self.setFixedSize(other.getFixedSize())
                for i in range(other.getSize()):
                    self[0][i] = other[i]
            else:
                self.setValueType(other.getValueType())
                self.setRowCount(1);
                self.setColumnCount(other.getSize());
                self.setUnit(other.getUnit())
                self.setInitialValue(other.getInitialValue())
                self.setFixedSize(other.getFixedSize())
                for i in range(other.getSize()):
                    self[0][i] = other[i]
        elif isinstance(other, ListType):
            if self.isFixedSize() and (self.getRowCount()!=len(other)) or (self.getColumnCount()!=1):
                raise ValueError(str(self)+' copyFrom '+str(other)+': mis-matched sizes')
            elif self.isFixedSize() and (self.getColumnCount()!=len(other)) or (self.getRowCount()!=1):
                raise ValueError(str(self)+' copyFrom '+str(other)+': mis-matched sizes')
            elif (self.isFixedSize() and self.getColumnCount()==1):
                self.setValueType(other.getValueType())
                self.setUnit(other.getUnit())
                self.setInitialValue(other.getInitialValue())
                self.setFixedSize(other.getFixedSize())
                for i in range(len(other)):
                    self[i][0] = other[i]
            elif (self.isFixedSize() and self.getRowCount()==1):
                self.setValueType(other.getValueType())
                self.setUnit(other.getUnit())
                self.setInitialValue(other.getInitialValue())
                self.setFixedSize(other.getFixedSize())
                for i in range(len(other)):
                    self[0][i] = other[i]
            else:
                self.setValueType(other.getValueType())
                self.setRowCount(1);
                self.setColumnCount(len(other));
                self.setUnit(other.getUnit())
                self.setInitialValue(other.getInitialValue())
                self.setFixedSize(other.getFixedSize())
                for i in range(len(other)):
                    self[0][i] = other[i]
        else:
            # raises TypeError
            DataType._copyFrom(self,other)


    def dup(self):
        # return a copy of one self
        #return Matrix(self.duplicate())
        # !!!!!!!! deep copy in Java is broken !!!!!!
        returnMatrix=Matrix(self.getRowCount(),self.getColumnCount())
        for i in range(self.getRowCount()):
            for j in range(self.getColumnCount()):
                if isNumberType(self[i][j]):
                    returnMatrix[i][j]=self[i][j]
                else:
                    returnMatrix[i][j]=self[i][j].dup()
        returnMatrix.setUnit(self.getUnit())
        returnMatrix.setInitialValue(self.getInitialValue())
        returnMatrix.setFixedSize(self.isFixedSize())
        returnMatrix.setValueType(self.getValueType())
        return returnMatrix


    # Matrix specific functions, probably will move to DMath.py
    def inverse(self):
        # eee... implement Gauss-Jordan elimination?
        # first matrix must be square
        if (not self.isSquare()):
            raise ValueError(str(self)+' must be sqaure to have an inverse.')
        elif (self.det()==0):
            raise ValueError(str(self)+' is not invertible, determinant == 0.')
        else:
            dim = self.getRowCount();
            augMatrix = self.catH(self.IdentityMatrix(dim,
                                                      dim))

            gauss = augMatrix.gaussianElimination()
            #print '.....Finished gaussian Elimination(): ' + str(gauss)
            # divide through by pivots
            #print '..... size of gauss, rows:' + str(gauss.getRowCount()) + ' cols:' + str(gauss.getColumnCount())
            for i in range(dim):
                pivot = gauss[i][i]
                #print '....pivot=' + str(pivot) + '  i=' + str(i)
                for j in range(2*dim):
                    #print '.... j=' + str(j)
                    #print '.......' + str(gauss[i][j])
                    gauss[i][j]=gauss[i][j]/pivot
                    if gauss[i][j] == 0:
                        gauss[i][j] = 0
            return gauss.subMatrix(0, dim, dim-1, 2*dim-1)

    def gaussianElimination(self):
        returnMatrix = self.dup()
        if (returnMatrix.getValueType()=='Integer'):
            returnMatrix.setValueType('real')
        diagonal = self.getColumnCount()
        if (self.getRowCount()<self.getColumnCount()):
            diagonal = self.getRowCount()
        for j in range(diagonal):
            rowIndex = returnMatrix._rowMax(j)
            returnMatrix._rowSwap(j, rowIndex)
            if (returnMatrix[j][j]==0.0):
                return Matrix(0,0)
            for k in range(self.getRowCount()):
                if (k!=j):
                    returnMatrix._rowZero(j,j,k)
        return returnMatrix


    def catH(self, other):
        # return matrix of [self, other]
        # make sure the number of rows are equal
        ### MAYBE should use the java super's appendHorizontally()
        if not(isinstance(other, Matrix)):
            raise TypeError(str(other)+' is not a Matrix and cannnot be used in catH.')
        elif (self.getRowCount() != other.getRowCount()):
            raise ValueError(str(self)+' catH '+str(other)+' row number not the same.')
        else:
            tRow = self.getRowCount()
            tCol = self.getColumnCount() + other.getColumnCount()
            returnMatrix = Matrix(tRow, tCol)
            for i in range(tRow):
               for j in range(tCol):
                   if j < self.getColumnCount():
                       returnMatrix[i][j] = self[i][j]
                   else:
                       returnMatrix[i][j]=other[i][j-self.getColumnCount()]
            return returnMatrix

    def catV(self, other):
        # return matrix of [self; other]
        # make sure the number of rows are equal
        ### MAYBE should use the java super's appendVertically()
        if not(isinstance(other, Matrix)):
            raise TypeError(str(other)+' is not a Matrix and cannnot be used in catV.')
        elif (self.getColumnCount() != other.getColumnCount()):
            raise ValueError(str(self)+' catV '+str(other)+' column number not the same.')
        else:
            tRow = self.getRowCount() + other.getRowCount()
            tCol = self.getColumnCount()
            returnMatrix = Matrix(tRow, tCol)
            for i in range(tRow):
               for j in range(tCol):
                   if i < self.getRowCount():
                       returnMatrix[i][j] = self[i][j]
                   else:
                       returnMatrix[i][j]=other[i-self.getRowCount()][j]
            return returnMatrix



    def IdentityMatrix(self, rowCount, colCount):
        # errr, as close as it can get if it's not nxn.
        if (not(type(rowCount)==IntType) or not(type(colCount)==IntType)):
            raise TypeError(str(rowCount)+' or '+str(colCount)+' are not integer types')
        returnMatrix = Matrix(rowCount, colCount)
        cC=0
        for rC in range(rowCount):
            returnMatrix[rC][cC]=1
            cC=cC+1
            if cC >= colCount:
                return returnMatrix
        return returnMatrix

    def transpose(self):
        # rows vs. columns.
        returnMatrix = Matrix(self.getColumnCount(), self.getRowCount())
        for i in range(self.getRowCount()):
            for j in range(self.getColumnCount()):
                returnMatrix[j][i] = self[i][j]
        return returnMatrix

    def rowEchelon(self):
        numSwaps = 0
        return self._fwdElimination(numSwaps)[0]



    def subMatrix(self, r1, c1, r2, c2):
        if r2 < r1:
            rHold = r2
            r2 = r1
            r1 = rHold
        if c2 < c1:
            cHold = c2
            c2 = c1
            c1 = cHold
        subRow = r2-r1+1
        subCol = c2-c1+1
        if (r2 < self.getRowCount()) and (c2 < self.getColumnCount()) and r1>=0 and r2 >=0:
            returnMatrix = Matrix(subRow, subCol)
            i2 = 0
            for i in range(r1,r2+1):
                j2 = 0
                for j in range(c1,c2+1):
                    returnMatrix[i2][j2] = self[i][j]
                    j2 = j2+1
                i2 = i2+1
            return returnMatrix
        else:
            raise IndexError('Indices not in range.')

    def det(self):
        changedType = 0;
        if (self.isEmpty() or (not self.isSquare())):
            #print ' self is empty or not square'
            return 0.0
        if (self.getRowCount()==1):
            #print ' row count == 1'
            return self[0][0]
        if (self.getRowCount()==2):
            #print ' row count == 2'
            return ((self[0][0]*self[1][1])-(self[1][0]*self[0][1]))
        # matrix is not Empty, is Square, rows>2, use fwdElimination
        numSwaps = 0
        mtmp = self._fwdElimination(numSwaps)
        m = mtmp[0]
        numSwaps = mtmp[1]
        #print 'numSwaps: ' + str(numSwaps)
        if (m.isEmpty()):
            #print 'm is empty'
            return 0.0
        result = 1.0
        for i in range(self.getColumnCount()):
            result *= m[i][i]
        factor = -1
        if (numSwaps%2)==0:
            1
        result *= factor
        #print ' result: ' + str(result)
        return result


    def trace(self):
        result = 0.0
        diagonal = self.getColumnCount()
        if (self.getRowCount()<self.getColumnCount()):
            diagonal = self.getRowCount()
        for i in range(diagonal):
            result += self[i][i]
        return result


    def euclidNorm(self):
        return sqrt(self.sumOfSquares())


    def sumOfSquares(self):
        result = 0.0
        rows = self.getRowCount()
        cols = self.getColumnCount()
        value = 0.0
        for i in range(rows):
            for j in range(cols):
                value = self[i][j]
                result += value*value
        return result


    def absSum(self):
        result = 0.0
        for i in range(self.getRowCount()):
            for j in range(self.getColumnCount()):
                result += abs(self[i][j])
        return result


    def fill(self, value):
        # integer, real, float, int
        rows = self.getRowCount()
        cols = self.getColumnCount()
        if (isinstance(value, Number)):
            value = value.getValue()
        try:
            #print '...fill value: ' + str(value)
            for i in range(rows):
                for j in range(cols):
                    #self.setItem(i,j,value)
                    self[i][j] = value
        except TypeError, e:
            raise e

    def getRow(self, index):
        # KJT, replaced following with line below: returnRow = self[index].dup()
        returnRow = DomeMatrixData.getRow(self, index)
        returnRow.setUnit(self.getUnit())
        return returnRow

    def getCol(self, index):
        returnCol = DomeMatrixData.getCol(self, index)
        returnCol.setUnit(self.getUnit())
        return returnCol
        #KJT: changed implementation from that below to that above
        #returnVector = Vector.Vector()
        #returnVector.setSize(self.getRowCount())
        #returnVector.setValueType(self.getValueType())
        #returnVector.setInitialValue(self.getInitialValue())
        #returnVector.setUnit(self.getUnit())
        # assuming this is a column vector
        #for i in range(self.getRowCount()):
        #    returnVector[i] = self[i][index]
        #return returnVector


    def numRows(self):
        return self.getRowCount()

    def numCols(self):
        return self.getRowCount()

    def size(self):
        return [self.getRowCount(), self.getColumnCount()]
        #KJT return self.getRowCount() * self.getColumnCount()

    #def setSize(self, rows, cols):
     #   self.setRowCount(rows)
      #  self.setColumnCount(cols)

    def setSize(self, rows, cols, value=0):
        oldRow = self.getRowCount()
        oldCol = self.getColumnCount()
        self.setRowCount(rows)
        self.setColumnCount(cols)
        if rows > oldRow:
            #print '...... set value of extra row'
            for i in range(oldRow, rows):
                for j in range(cols):
                    #print 'i='+str(i)+' j='+str(j)+' val='+str(value)
                    self[i][j] = value
        if cols > oldCol:
            #print '.... set value of extra col'
            for j in range(oldCol, cols):
                for i in range(rows):
                    #print 'i='+str(i)+ ' j=' + str(j) + ' val=' + str(value)
                    self[i][j] = value


    def setNumRows(self, rows, value=0):
        oldRow = self.getRowCount()
        self.setRowCount(rows)
        if rows > oldRow:
            for i in range(oldRow, rows):
                for j in range(self.getColumnCount()):
                    self[i][j] = value
                    #print 'i='+str(i)+' j='+ str(j)+' val='+str(value)


    def setNumCols(self, cols, value=0):
        oldCol = self.getColumnCount()
        self.setColumnCount(cols)
        if cols > oldCol:
            for j in range(oldCol, cols):
                for i in range(self.getRowCount()):
                    self[i][j] = value
                    #print 'i='+str(i)+' j='+str(j)+' val='+str(value)


    def setRow(self, index, value):
        if (not (isinstance(value, Vector.Vector))):
            raise TypeError('setRow(), ' +str(value)+': is not a Vector')
        if (value.getSize() != self.getColumnCount()) :
            raise ValueError('setRow(), '+str(value)+': mis-matched sizes')
        if (index >= self.getRowCount()):
            raise ValueError('setRow(), '+str(index)+': index out of range')
        else:
            for j in range(self.getColumnCount()):
                self[index][j] = value[j]


    def setCol(self, index, value):
        if (not (isinstance(value, Vector.Vector))):
            raise TypeError('setCol(), ' +str(value)+': is not a Vector')
        if (value.getSize() != self.getRowCount()) :
            raise ValueError('setCol(), '+str(value)+': mis-matched sizes')
        if (index >= self.getColumnCount()):
            raise ValueError('setCol(), '+str(index)+': index out of range')
        else:
            for i in range(self.getRowCount()):
                self[i][index] = value[i]

    # Undefined
    def euclideanNorm():
        return sqrt(self.sumOfSquares())



    # Actually implementation of mathematical operators
    def _add(self,other):
        if (isinstance(other, Number)):
            # scalar add
            returnMatrix = self.dup()
            if (isinstance(other, Real)):
                # automatically change return matrix type to real
                returnMatrix.setValueType("real")
            for i in range(returnMatrix.getRowCount()):
                for j in range(returnMatrix.getColumnCount()):
                    returnMatrix[i][j] = returnMatrix[i][j]+other.value
        elif isNumberType(other):
            #scalar add
            returnMatrix = self.dup()
            if (type(other)==FloatType):
                # automatically change return matrix type to real
                returnMatrix.setValueType("real")
            for i in range(returnMatrix.getRowCount()):
                for j in range(returnMatrix.getColumnCount()):
                    returnMatrix[i][j] = returnMatrix[i][j]+other
            return returnMatrix
        elif isinstance(other, Matrix):
            # check the size of other matrix
            if (self.getRowCount()== other.getRowCount()) and (self.getColumnCount() == other.getColumnCount()):
                returnMatrix = self.dup()
                for i in range(returnMatrix.getRowCount()):
                    for j in range(returnMatrix.getColumnCount()):
                        returnMatrix[i][j] = returnMatrix[i][j]+other[i][j]
                if (other.getValueType()=='real'):
                    returnMatrix.setValueType('real')
                return returnMatrix
            else:
                raise ValueError(str(self)+' + '+str(other)+': mis-matched sizes')
        elif isinstance(other, Vector.Vector):
            if (self.getRowCount()==other.getSize()) and (self.getColumnCount()==1):
                returnMatrix = self.dup()
                for i in range(other.getSize()):
                    returnMatrix[i][0] = returnMatrix[i][0] + other[i]
                return returnMatrix
            elif (self.getColumnCount()==other.getSize()) and (self.getRowCount()==1):
                returnMatrix = self.dup()
                for i in range(other.getSize()):
                    returnMatrix[0][i] = returnMatrix[0][i] + other[i]
                return returnMatrix
            else:
                raise ValueError(str(self)+' + '+str(other)+': mis-matched sizes')
        elif isinstance(other, ListType):
            if ((self.getRowCount()==len(other)) and (self.getColumnCount()==1)):
                returnMatrix = self.dup()
                for i in range(len(other)):
                    returnMatrix[i][0] = returnMatrix[i][0]+other[i]
                return returnMatrix
            elif (self.getColumnCount()==len(other)) and (self.getRowCount()==1):
                returnMatrix = self.dup()
                for i in range(len(other)):
                    returnMatrix[0][i] = returnMatrix[0][i]+other[i]
                return returnMatrix
            else:
                raise ValueError(str(self)+' + '+str(other)+': mis-matched sizes')

        else:
            raise TypeError('Matrix cannot be added to with '+
                            other.getClass().getName())

    def _sub(self,other):
        if isinstance(other, Number):
            #scalar sub
            returnMatrix = self.dup()
            for i in range(returnMatrix.getRowCount()):
                for j in range(returnMatrix.getColumnCount()):
                    returnMatrix[i][j] = returnMatrix[i][j]-other.value
        elif isNumberType(other):
            #scalar sub
            returnMatrix = self.dup()
            for i in range(returnMatrix.getRowCount()):
                for j in range(returnMatrix.getColumnCount()):
                    returnMatrix[i][j] = returnMatrix[i][j]-other
            return returnMatrix
        elif isinstance(other, Matrix):
            # check the size of other matrix
            if (self.getRowCount()== other.getRowCount()) and (self.getColumnCount() == other.getColumnCount()):
                returnMatrix = self.dup()
                for i in range(returnMatrix.getRowCount()):
                    for j in range(returnMatrix.getColumnCount()):
                        returnMatrix[i][j] = returnMatrix[i][j]-other[i][j]
                return returnMatrix
            else:
                raise ValueError(str(self)+' - '+str(other)+': mis-matched sizes')
        elif isinstance(other, Vector.Vector):
            if (self.getRowCount()==other.getSize()) and (self.getColumnCount()==1):
                returnMatrix = self.dup()
                for i in range(other.getSize()):
                    returnMatrix[i][0] = returnMatrix[i][0] - other[i]
                return returnMatrix
            elif (self.getColumnCount()==other.getSize()) and (self.getRowCount()==1):
                returnMatrix = self.dup()
                for i in range(other.getSize()):
                    returnMatrix[0][i] = returnMatrix[0][i] - other[i]
                return returnMatrix
            else:
                raise ValueError(str(self)+' - '+str(other)+': mis-matched sizes')
        elif isinstance(other, ListType):
            if ((self.getRowCount()==len(other)) and (self.getColumnCount()==1)):
                returnMatrix = self.dup()
                for i in range(len(other)):
                    returnMatrix[i][0] = returnMatrix[i][0]-other[i]
                return returnMatrix
            elif (self.getColumnCount()==len(other)) and (self.getRowCount()==1):
                returnMatrix = self.dup()
                for i in range(len(other)):
                    returnMatrix[0][i] = returnMatrix[0][i]-other[i]
                return returnMatrix
            else:
                raise ValueError(str(self)+' - '+str(other)+': mis-matched sizes')
        else:
            raise TypeError('Matrix cannot be subtracted to with a '+
                            other.getClass().getName())

    def _mul(self,other):
        if isinstance(other, Number):
            #scalar multiplication
            returnMatrix = self.dup()
            for i in range(returnMatrix.getRowCount()):
                for j in range(returnMatrix.getColumnCount()):
                    returnMatrix[i][j] = returnMatrix[i][j]*other.value
        elif isNumberType(other):
            #scalar multiplication
            returnMatrix = self.dup()

            for i in range(returnMatrix.getRowCount()):
                for j in range(returnMatrix.getColumnCount()):
                    returnMatrix[i][j] = returnMatrix[i][j]*other
            return returnMatrix
        elif isinstance(other, Matrix):
            # check the size of other matrix, size of rows == size of columns
            # or number of columns == # of rows
            if (self.getColumnCount()== other.getRowCount()):
                # return has self # rows and other # columns
                returnMatrix = Matrix(self.getRowCount(),
                                      other.getColumnCount())
                for i in range(returnMatrix.getRowCount()):
                    for j in range(returnMatrix.getColumnCount()):
                        entry = 0
                        for sC in range(selfMatrix.getColumnCount()):
                            for oR in range(selfMatrix.getRowCount()):
                                entry = self[i][sC]*other[oR][j]+entry
                        returnMatrix[i][j] = entry
                return returnMatrix
            else:
                raise ValueError(str(self)+' * '+str(other)+': mis-matched sizes')
        elif isinstance(other, Vector.Vector):
            # !!!! Need to worry about reverse, Vector * Matrix
            if (self.getColumnCount()==other.getSize()):
                returnMatrix = Matrix(self.getRowCount(),
                                      1)

                for i in range(returnMatrix.getRowCount()):
                    entry = 0
                    for sC in range(returnMatrix.getColumnCount()):
                        entry = self[0][sC]*other[sC] + entry
                    returnMatrix[i][0] = entry
                return returnMatrix
            else:
                raise ValueError(str(self)+' * '+str(other)+': mis-matched sizes')
        elif isinstance(other, ListType):
            if (self.getColumnCount()==len(other)):
                returnMatrix = Matrix(self.getRowCount(),
                                      1)
                for i in range(returnMatrix.getRowCount()):
                    entry = 0
                    for sC in range(returnMatrix.getColumnCount):
                        entry = self[0][sC]*other[sC] + entry
                    returnMatrix[i][0] = entry
                return returnMatrix
            else:
                raise ValueError(str(self)+' * '+str(other)+': mis-matched sizes')
        else:
            raise TypeError('Matrix cannot be multiplied by a '+
                            other.getClass().getName())


    def _div(self,other):
        if isinstance(other, Number):
            #scalar multiplication
            returnMatrix = self.dup()
            for i in range(returnMatrix.getRowCount()):
                for j in range(returnMatrix.getColumnCount()):
                    returnMatrix[i][j] = returnMatrix[i][j]/other.value
        elif isNumberType(other):
            #like scalar multiplication
            returnMatrix = self.dup()

            for i in range(returnMatrix.getRowCount()):
                for j in range(returnMatrix.getColumnCount()):
                    returnMatrix[i][j] = returnMatrix[i][j]/other
            return returnMatrix
        else:
            raise TypeError('Matrix cannot be divided by a '+
                            other.getClass().getName())


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
        return (self.getRowCount()==0) or (self.getColumnCount()==0)

    def isSquare(self):
        return self.getRowCount()==self.getColumnCount()

    def isUpperTriangular(self):
        if (self.isEmpty()):
            return false
        rows = self.getRowCount()
        cols = self.getColumnCount()
        for i in range(1,rows):
            #if ((i<cols)&&((self[i][i]==0)) return false
            j = 0
            while (j<i) and (j<cols):
                if (self[i][j]!=0):
                    return false
                j=j+1
        return true

    def isNonSingular(self):
        if (self.isEmpty()):
            return false
        if (self.isUpperTriangular()):
            diagonal = self.getColumnCount()
            if (self.getRowCount()<self.getColumnCount()):
                diagonal = self.getRowCount()
            index = 0
            while (index<diagonal):
                if (self[index][index]==0):
                    return false
                index=index+1
            return true
        rowEchelonForm = self.rowEchelon()
        rowEchelonForm.isNonSingular()




    # private helper functions doing row manipulations

    def _fwdElimination(self, numSwaps):
        # numSwaps should be an int
        returnMatrix = self.dup()
        if (returnMatrix.getValueType() == 'Integer'):
            returnMatrix.setValueType('real')
        rows = self.getRowCount()
        cols = self.getColumnCount()
        diagonal = cols
        if (rows<cols):
            diagonal = rows
        numSwaps=0
        for j in range(diagonal):
            numSwaps += returnMatrix._rowSwap(j, returnMatrix._rowMax(j))
            if (returnMatrix[j][j]==0):
                #print 'found a 0 on the diagonal'
                # Not certain if this is what we really want to return...
                return [Matrix(0,0), numSwaps]
            for k in range(j+1, rows):
                #print '.... returnMatrix after rowSwap:' + str(returnMatrix)
                returnMatrix._rowZero(j,j,k)
                #print 'k=' + str(k) + ' matrix:' + str(returnMatrix)
            #print '???? ' + str(returnMatrix)
        return [returnMatrix, numSwaps]

    def _rowSwap(self, r1, r2):
        # r1, r2 are int
        # assert r1 and r2 are valid
        if (r1==r2):
            return 0
        numCols = self.getColumnCount()
        tmp = 0.0
        for j in range(numCols):
            tmp = self[r1][j]
            self[r1][j]=self[r2][j]
            self[r2][j]=tmp
        return 1

    def _rowScale(self, rIndex, factor):
        # rIndex is int, factor is real
        numCols = self.getColumnCount()
        for j in range(numCols):
            self[rIndex][j]*=factor

    def _rowZero(self, col, r1, r2):
        # assert col, r1 and r2 are valid
        # adding a multiple of r1 to r2, r1 is left unchanged
        #print ' numerator: ' + str(self[r2][col])
        #print ' denominator ' + str(self[r1][col])
        numerator = float(self[r2][col])
        denominator = float(self[r1][col])
        factor = -1.0*(numerator/denominator)
        #print '_rowZero factor=' + str(factor) +' col=' + str(col) + ' r1='+ str(r1) + ' r2=' + str(r2)
        numCols = self.getColumnCount()
        for j in range(numCols):
            self[r2][j] += self[r1][j]*factor
        #print ' after rowZero: ' + str(self)


    def _rowMax(self, c):
        # returns number of row with highest absolute value in specified
        # column
        # starts searching at r=c
        # assert c is valid
        # c is an int
        r = c
        result = r
        #print '_rowMax c = ' + str(c)
        maxValue = fabs(self[result][c])
        #print '.... init maxValue ' + str(maxValue) + ' init result ' +  str(result)
        for i in range(r, self.getRowCount()):
        #while r<self.getRowCount():
            currentValue = fabs(self[i][c])
            #print '...... currentValue at ' + str(i) + ' is:' + str(currentValue)
            if (currentValue > maxValue):
                result = i
                maxValue = currentValue
        #print '_rowMax result=' + str(result)
        return result



    # Conversion functions

    def array(self):
        returnList = []
        for i in range(self.getRowCount()):
            returnList.append(self[i].array())
        return returnList





#Helper functions
def isNumberType(x):
    return type(x)==IntType or type(x)==FloatType




