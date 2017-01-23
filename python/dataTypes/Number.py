# Number.py
# This class is the base class for the two number types: Real and Integer
# Note the subclasses will also be subclasses of corresponding Java Data Type
# for "true" division; requires Python 2.2
from DataType import DataType
import Boolean
from Numeric import Numeric
from operator import truth
from types import IntType, FloatType

class Number(Numeric):
    # object methods
    def __repr__(self):
        return self.dataType+'('+`self.value`+')'

    def __str__(self):
        return str(self.value)

    def _cmp(self,other):
        # can only compare with other numeric types?
        # quantities can only compare with other quantities?
        if isinstance(other,Number):
            if self.value<other.value:
                return -1
            elif self.value>other.value:
                return 1
            else:
                return 0
        else:
            Numeric._cmp(self,other)

    def __nonzero__(self):
        return truth(self.value)
            
    # DataType methods
    # !!!! There might be a bug, test if Integer can only copy to Integer, etc.
    # !!!! should there be return statements.
    def _copyTo(self,other):
        if isinstance(other,Number):
            other.setValue(self.value)
        else:
            DataType._copyTo(self,other)

    def _copyFrom(self,other):
        if isinstance(other,Number):
            self.setValue(other.value)
        elif isNumberType(other):
            self.setValue(other)
        else:
            DataType._copyFrom(self,other)

    # Number methods
    def getValue(self):
        return self.value

    def equal(self,other):
        try:
            self._copyFrom(other)
            return self
        except TypeError:
            raise TypeError(str(self)+' equal '+str(other))            

    def dup(self):
        # return a new copy of myself
        return getNumber(self.value)

    # Math operators
    def _add(self,other):
        if isinstance(other,Number):
            return getNumber(self.value + other.value)
        elif isNumberType(other):
            return getNumber(self.value + other)
        else:
            Numeric._add(self,other)
        
    def _sub(self,other):
        if isinstance(other,Number):
            return getNumber(self.value - other.value)
        elif isNumberType(other):
            return getNumber(self.value - other)
        else:
            Numeric._sub(self,other)

    def _mul(self,other):
        if isinstance(other,Number):
            return getNumber(self.value * other.value)
        elif isNumberType(other):
            return getNumber(self.value * other)
        else:
            Numeric._mul(self,other)

    def _div(self,other):
        if isinstance(other,Number):
            return getNumber(self.value / other.value)
        elif isNumberType(other):
            return getNumber(self.value / other)
        else:
            Numeric._div(self,other)

    def _mod(self,other):
        if isinstance(other,Number):
            return getNumber(self.value % other.value)
        elif isNumberType(other):
            return getNumber(self.value % other)
        else:
            Numeric._mod(self,other)

    def _pow(self,other):
        if isinstance(other,Number):
            return getNumber(other.value ** other.value)
        elif isNumberType(other):
            return getNumber(self.value ** other)
        else:
            Numeric._pow(self,other)

    def __radd__(self,other):
        if isNumberType(other):
            return getNumber(other + self.value)
        else:
            raise TypeError(str(other)+' + '+str(self))

    def __rsub__(self,other):
        if isNumberType(other):
            return getNumber(other - self.value)
        else:
            raise TypeError(str(other)+' - '+str(self))

    def __rmul__(self,other):
        if isNumberType(other):
            return getNumber(other * self.value)
        else:
            raise TypeError(str(other)+' * '+str(self))

    def __rdiv__(self,other):
        if isNumberType(other):
            return getNumber(other / self.value)
        else:
            raise TypeError(str(other)+' / '+str(self))

    def __rmod__(self,other):
        if isNumberType(other):
            return getNumber(other % self.value)
        else:
            raise TypeError(str(other)+' % '+str(self))

    def __rpow__(self,other):
        if isNumberType(other):
            return getNumber(other ** self.value)
        else:
            raise TypeError(str(other)+' ** '+str(self))

    def __neg__(self):
        return getNumber(-self.value)

    def __pos__(self):
        return getNumber(self.value)

    def __abs__(self):
        return getNumber(abs(self.value))

    def __int__(self):
        return int(self.value)

    def __float__(self):
        return float(self.value)

    def __long__(self):
        return long(self.value)

    def __complex__(self):
        return complex(self.value)

    # comparison operations
    def _lt(self,other):
        if isNumberType(other):
            return Boolean.Boolean(self.value < other)
        elif isinstance(other,Number):
            return Boolean.Boolean(self.value < other.value)
        else:
            Numeric._lt(self,other)

    def _le(self,other):
        if isNumberType(other):
            return Boolean.Boolean(self.value <= other)
        elif isinstance(other,Number):
            return Boolean.Boolean(self.value <= other.value)
        else:
            Numeric._le(self,other)

    def _gt(self,other):
        if isNumberType(other):
            return Boolean.Boolean(self.value > other)
        elif isinstance(other,Number):
            return Boolean.Boolean(self.value > other.value)
        else:
            Numeric._gt(self,other)

    def _ge(self,other):
        if isNumberType(other):
            return Boolean.Boolean(self.value >= other)
        elif isinstance(other,Number):
            return Boolean.Boolean(self.value >= other.value)
        else:
            Numeric._ge(self,other)

    def _eq(self,other):
        if isNumberType(other):
            return Boolean.Boolean(self.value == other)
        elif isinstance(other, Number):
            return Boolean.Boolean(self.value == other.value)
        elif (other == None):
            return 0
        else:
            Numeric._eq(self,other)

    def _ne(self,other):
        if isNumberType(other):
            return Boolean.Boolean(self.value != other)
        elif isinstance(other,Number):
            return Boolean.Boolean(self.value != other.value)
        elif (other == None):
            return 1
        else:
            Numeric._ne(self,other)

def isNumberType(x):
    return type(x)==IntType or type(x)==FloatType

def getNumber(value):
    from Integer import Integer
    from Real import Real
    if type(value)==IntType:
        return Integer(value)
    elif type(value)==FloatType:
        return Real(value)
    else:
        raise AttributeError('unknown type: '+`value`)
