# Boolean.py
# This class represents booleans.  Is it just a subclass of
# DataType, or of Numeric?  #Can it support numerica ops?

from DataType import DataType
from types import IntType, FloatType
import Number
from operator import truth
from types import IntType, FloatType
from mit.cadlab.dome3.objectmodel.dataobject import BooleanData
from mit.cadlab.dome3.objectmodel.dataobject import RealData
from mit.cadlab.dome3.objectmodel.dataobject import IntegerData

class Boolean(DataType, BooleanData):
    def __init__(self,value=0):
        self.dataType = "Boolean"
        # In Python primitive booleans are simply 0 and 1 (actually non-zero).
        if isinstance(value,RealData):
            self.value = 0!=value.getRealValue()
        elif isinstance(value,IntegerData):
            self.value = 0!=value.getValue()
        else:
			self.value = truth(value)

    # Override superclass's methods
    def _copyTo(self,other):
        if isinstance(other, Boolean):
            other.value = self.value
        else:
            DataType._copyTo(self,other)

    def _copyFrom(self,other):
        if isinstance(other, Boolean):
            self.value = other.value
        elif type(other)==IntType or type(other)==FloatType:
            self.value = truth(other)
        elif isinstance(other, Number.Number):
            self.value = truth(other.value)
            return self
        else:
            DataType._copyFrom(self,other)





    # object methods
    def __repr__(self):
        return self.dataType+'('+`self.value`+')'

    def __str__(self):
        if self.value==0:
            return "false"
        else:
            return "true"

    ## leave to default
    #def __cmp__(self, other):

    def __nonzero__(self):
        return self.value

    # Numeric Type Conversion

    def __int__(self):
        return int(self.value)

    def __float__(self):
        return float(self.value)

    # comparison operations
    def __eq__(self,other):
        try:
            return self._eq(other)
        except TypeError, e:
            try:
                return other._eq(self)
            except:
                raise e

    def __ne__(self,other):
        try:
            return self._ne(other)
        except TypeError, e:
            try:
                return other._ne(self)
            except:
                raise e

    def _eq(self,other):
        if isinstance(other, Boolean):
            ans = self.value == other.value
        elif isinstance(other, Number.Number):
            ans = self.value == truth(other.value)
        elif isNumberType(other):
            ans = self.value == truth(other)
        elif (other == None):
            ans = 0
        else:
            raise TypeError(str(self)+' == '+str(other))
        return Boolean(ans)

    def _ne(self,other):
        if isinstance(other, Boolean):
            ans = self.value != other.value
        elif isinstance(other,Number.Number):
            ans = self.value != truth(other.value)
        elif isNumberType(other):
            ans = self.value != truth(other)
        elif (other == None):
            ans = 1
        else:
            raise TypeError(str(self)+' != '+str(other))
        return Boolean(ans)

def isNumberType(x):
    return type(x)==IntType or type(x)==FloatType
