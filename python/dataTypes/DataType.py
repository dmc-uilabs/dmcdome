# DataType.py
# This class is the base class for all data types.

import copy

class DataType:

    def copyTo(self,other):
        try:
            self._copyTo(other)
            return other
        except TypeError, e:
            try:
                other._copyFrom(self)
            except:
                raise e

    def copyFrom(self,other):
        try:
            self._copyFrom(other)
            return self
        except TypeError, e:
            # don't call copyTo for fear of loops.
            raise e



    # to be overridden by subclasses
    def _copyTo(self,other):
        raise TypeError(str(self)+' copyTo '+str(other))

    def _copyFrom(self,other):
        raise TypeError(str(self)+' copyFrom '+str(other))


    def dup(self):
        return copy.deepcopy(self)

