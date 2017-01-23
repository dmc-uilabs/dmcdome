# TestHelper.py
# Some handy functions to use in test codes

# print expressions so that  type appears
def myPrint(exp, x):
    from Number import Number
    from types import IntType, FloatType
    from Integer import Integer
    from Real import Real
    from Boolean import Boolean
    if type(x)==IntType or type(x)==FloatType:
        print exp,'=',x
    elif isinstance(x, Real):
        print exp,'=',repr(x)
    elif isinstance(x, Integer):
        print exp,'=',repr(x)
    elif isinstance(x, Boolean):
        print exp,'=',repr(x)    
    else:
        print exp,'=',x
    
   

# test expressions (return values)
def evalExpr(exp,global_dict,local_dict):
    try:
        myPrint(exp,eval(exp,global_dict,local_dict))
    except Exception, e:
        printException(exp, e)

# test statements (do not return values)
def execStmt(smt,global_dict,local_dict):
    try:
        print smt,'...',
        exec smt in global_dict, local_dict
        print 'done'
    except Exception, e:
        printException(smt, e)

def printException(exp, e):
    print exp,'=',e.__class__.__name__,':',e
