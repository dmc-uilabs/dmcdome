# Real.py
# This class represents real numbers.

from Number import Number
from mit.cadlab.dome3.objectmodel.dataobject import RealData


class Real(Number, RealData):
#class Real(Number):
    def __init__(self,value=0.0):
        self.dataType = "Real"
        self.value = float(value)
