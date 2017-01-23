# runAnsys.py

import sys,os,string
import InputOperation, OutputOperation


def test(inputFile,outputfile):
    print "input the param name whose value is going to change"
    paramName=string.strip(sys.stdin.readline())
    print "input new value"
   	ModifyInputValue(paramName,newVal)
    
    os.spawnv(os.P_WAIT,ansys,(ansys,'-b','-i',inputFile,'-o',outputfile))
    displayResult(outputfile)

def displayResult(outputfile):
    OutputOperation.ReadOutFile(outputfile)
    OutputOperation.ParseOutputArgs()
    OutputOperation.displayDictionary()

    
def ModifyInputValue(param, newVal):
    InputOperation.initialize(inputFile)
    InputOperation.modifyInputValue(string.lower(param),newVal)
    InputOperation.updateBuffer()
    InputOperation.writeMacFile(inputFile)
    
if __name__=='__main__':
    ansys = 'D:\\Program Files\\Ansys Inc\\ANSYS57\\bin\\intel\\ansys57.exe'
    inputFile = 'myansysfile.mac'
    logFile = 'log.txt'
    test(inputFile,logFile)
    
