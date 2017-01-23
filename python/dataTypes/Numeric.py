# Numeric.py
# This class is the base class for all data types which support numeric operations.

# The reason standard methods are being wrapped by Numeric is because even
# though the method may be implemented, it may not be implemented for all types
# in that case, a TypeError is thrown and the reverse method is attempted.

from DataType import DataType

class Numeric(DataType):

    # object methods
    def __cmp__(self,other):
        try:
            return self._cmp(other)
        except TypeError, e:
            try: # other must be DataType
                reverseAnswer = other._cmp(self)
                if reverseAnswer > 1:
                    return -1
                elif reverseAnswer < 1:
                    return 1
                else: # reverseAnswer = 0
                    return 0
            except:
                raise e

    # to be overridden by subclasses
    def _cmp(self,other):
        raise TypeError(str(self)+' cmp '+str(other))

    # mathematical operations
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

    def __mod__(self,other):
        try:
            return self._mod(other)
        except TypeError, e:
            try:
                return other.__rmod__(self)
            except:
                raise e

    def __pow__(self,other):
        try:
            return self._pow(other)
        except TypeError, e:
            try:
                return other.__rpow__(self)
            except:
                raise e

# for later
#def __iadd__(self,other):
#def __isub__(self,other):
#def __imul__(self,other):
#def __idiv__(self,other):
#def __imod__(self,other):
#def __ipow__(self,other):

    # to be overridden by subclasses
    def _add(self,other):
        raise TypeError(str(self)+' + '+str(other))

    def _sub(self,other):
        raise TypeError(str(self)+' - '+str(other))

    def _mul(self,other):
        raise TypeError(str(self)+' * '+str(other))

    def _div(self,other):
        raise TypeError(str(self)+' / '+str(other))

    def _mod(self,other):
        raise TypeError(str(self)+' % '+str(other))

    def _pow(self,other):
        raise TypeError(str(self)+' ** '+str(other))

    # comparison operations
    def __lt__(self,other):
        try:
            return self._lt(other)
        except TypeError, e:
            try:
                return other._ge(self)
            except:
                raise e

    def __le__(self,other):
        try:
            return self._le(other)
        except TypeError, e:
            try:
                return other._gt(self)
            except:
                raise e

    def __gt__(self,other):
        try:
            return self._gt(other)
        except TypeError, e:
            try:
                return other._le(self)
            except:
                raise e

    def __ge__(self,other):
        try:
            return self._ge(other)
        except TypeError, e:
            try:
                return other._lt(self)
            except:
                raise e

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

    # to be overridden by subclasses
    def _lt(self,other):
        raise TypeError(str(self)+' < '+str(other))

    def _le(self,other):
        raise TypeError(str(self)+' <= '+str(other))

    def _gt(self,other):
        raise TypeError(str(self)+' > '+str(other))

    def _ge(self,other):
        raise TypeError(str(self)+' >= '+str(other))

    def _eq(self,other):
        raise TypeError(str(self)+' == '+str(other))

    def _ne(self,other):
        raise TypeError(str(self)+' != '+str(other))
