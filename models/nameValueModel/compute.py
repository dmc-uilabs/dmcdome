# program reads variables from input file and
# computes sum and product of values and places in output file

def readInputs(inputFile):
  f = open(inputFile, 'r')
  line = f.readline()
  results = {}
  while (line):
    (name,value) = line.split('=')
    name = name.strip()
    value = value.strip()
    results[name] = value
    line = f.readline()
  f.close()
  return results

def calculateValues(inputMap):
  sum = 0
  product = 1
  for item in inputMap.values():
    value = float(item)
    sum += value
    product *= value
  results = {'sum':sum, 'product':product}
  return results

def writeOutputs(valueMap, outputFile):
  f = open(outputFile, 'w')
  for key in valueMap.keys():
    print >> f, key, '=', valueMap[key]
  f.close()

if __name__ == '__main__':
  import sys
  inputFile = sys.argv[1]
  outputFile = sys.argv[2]
  inputs = readInputs(inputFile)
  outputs = calculateValues(inputs)
  writeOutputs(outputs, outputFile)
