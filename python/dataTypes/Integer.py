# Integer.py
# This class represents integer numbers.

from Number import Number
from mit.cadlab.dome3.objectmodel.dataobject import IntegerData
#from java import lang

class Integer(Number, IntegerData):
#class Integer(Number):
    def __init__(self,value=0):
        self.dataType = "Integer"
        self.value = int(value)

        
        

        
