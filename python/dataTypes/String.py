# String.py
# This class implements String

from DataType import DataType
from types import IntType, FloatType, StringType
from Real import Real
from Integer import Integer
from Boolean import Boolean
from mit.cadlab.dome3.objectmodel.dataobject import StringData
from mit.cadlab.dome3.objectmodel.dataobject import BooleanData

class String(DataType, StringData):
    def __init__(self,value=0):
        self.dataType = "String"
        if isinstance(value, StringData):
            StringData.__init__(self, value)
        elif isinstance(value, BooleanData):
            if value.getValue()==0:
                self.value = "false"
            else:
                self.value = "true"
        else:
            self.value = str(value)

    # Override superclass's methods
    def _copyTo(self,other):
        if isinstance(other, String):
            other.value = self.value
        else:
            DataType._copyTo(self,other)

    def _copyFrom(self,other):
        if isinstance(other, String):
            self.value = other.value
        elif type(other)==IntType or type(other)==FloatType:
            self.value = str(other)
        elif isinstance(other, Real) or isinstance(other, Integer):
            self.value = str(other.value)
            return self
        else:
            DataType._copyFrom(self,other)


    # object methods
    def __repr__(self):
        return self.dataType+'('+`self.value`+')'

    def __str__(self):
        return self.value

    ## leave to default
    #def __cmp__(self, other):

    def __nonzero__(self):
        return self.value


    # method for mathematical operations
    def __add__(self,other):
        try:
            if isinstance(other, String):
                return String(self.value+other.value)
            else:
                return String(self.value+other)
        except TypeError, e:
            try:
                return other.__radd__(self)
            except:
                raise e


    def __radd__(self,other):
        try:
            if isinstance(other, String):
                return String(other.value+self.value)
            else:
                return String(other+self.value)
        except TypeError, e:
            raise e


    #comparison operations
    def __eq__(self,other):
        try:
            if isinstance(other, String):
                return Boolean(self.value==other.value)
            else:
                return Boolean(self.value==other)
        except TypeError, e:
            raise e

    def __ne__(self,other):
        try:
            if isinstance(other, String):
                return Boolean(self.value!=other.value)
            else:
                return Boolean(self.value!=other)
        except TypeError, e:
            raise e


# function that concatinates String objects and str primitives
def join(strA, strB, *args):
    try:
        if (isinstance(strA,String) or type(strA)==str):
            if (isinstance(strB,String) or type(strB)==str):
                strR = strA+strB
                for strK in args:
                    strR = strR+strK
                return strR
            else:
                raise TypeError, strB
        else:
            raise TypeError, strA
    except TypeError, e:
            raise e


def equalsIgnoreCase(strA, strB):
    try:
        if isinstance(strA, String):
            lowerA = strA.value.lower()
        else:
            lowerA = strA.lower()
        if isinstance(strB, String):
            lowerB = strB.value.lower()
        else:
            lowerB = strB.lower()
        return lowerA==lowerB
    except AttributeError, e:
        raise TypeError, 'input not a String or str'
