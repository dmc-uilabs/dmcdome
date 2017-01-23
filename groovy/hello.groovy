//hello.groovy
println "hello, world"

println 'hello, world banga'

for (arg in this.args ) {
  println "Argument:" + arg;
}
// this is a comment
// this is a comment for block.you can do like this
this.args.each{ arg ->  println "hello, ${arg}"}
this.args.each{ arg | println "hello, ${arg}"}

x = this.args.collect { arg | println "haiyo, ${arg}"; return arg;}
println x

cl = { arg | println "haiyo, ${arg}"; return arg;}
y = cl(this.args);
println y
println this.args
println "haiyodd, ${this.args}";

def list = [1, 2, 'hello', new java.util.Date() ]
assert list.size() == 4
assert list.get(2) == 'hello'
assert list[2] == 'hello'

def map = ['name':'James', 'location':'London']
assert map.size() == 2
assert map.get('name') == 'James'
assert map['name'] == 'James'
println map['name']

for (i in list) { println(i) }

def closure = { param | println("hello ${param}") }
closure.call('korea');

closure = { greeting, name | println(greeting + name) }
closure.call("hellowww ", "korea!")

closure = { println "gogo ${it}" }

closure.call('sangmok')

[1,2,3].each({print("-${it}")})
println ''
["k1":"v1", "k2":"v2"].each {key, value -> println key + "=" + value}

[1, 2, 3].each() { item -> print "${item}-" }
println ''

[1, 2, 3].each({ item -> print "${item}-" })
println ''


def fun(int i, Closure c) {
    c.call(i)
}

fun(4) {item -> println(5 + item);}
def j = 4
[3,5].each({i -> j = i * 2});
println j

def ret = [4, 3, 5, 9].collect() {if (it >= 5) return it; else return 0; }
println ret;

ret = [4, 3, 5, 9].find { it >= 5 }
println ret;

ret = [4, 3, 5, 9].findAll { it >= 5 }
println ret;

ret = [4, 3, 5, 9].findAll { it >= 9 }
println ret;

def h = [1,4,5].inject(0) { sum, item -> sum + item }
println h


def k = [1, 2, 3].inject('counting: ') { str, item -> str + item; }
println k

println ([1, 2, 3].every { it < 5 })

println ([1, 2, 3].any { it > 5 })
println ([1, 2, 3].any { it > 2 })

println ([1, 5, 3, 2].max());
println (['adds', 'bbs', 'sd', 'ee'].min());
println (['adds', 'bbs', 'sd', 'ee'].join('-'));

def start_i = 4
def end_i = 9
for (i in start_i..end_i) {
	print i + ' / ';
}
println()

def stringMap  = [ "Su" : "Sunday", "Mo" : "Monday", "Tu" : "Tuesday",
                   "We" : "Wednesday", "Th" : "Thursday", "Fr" : "Friday",
                   "Sa" : "Saturday" ];

stringMap.eachWithIndex() { obj, i -> println " ${i} ~ ${obj.key} : ${obj.value}" };

def stringList = [ "java", "perl", "python", "ruby", "c#", "cobol",
                   "groovy", "jython", "smalltalk", "prolog", "m", "yacc"  ];

stringList.eachWithIndex() { obj, i -> println " ${i}: ${obj}" };


def process = "ipconfig".execute()
process.in.eachLine { line -> println line }

println "he said 'cheese' once"
println 'he said "cheese!" again'

def s1 = /.*foo.*/
println s1

def s2 = /.*'"'"""foo.*/
println s2

def basename = /[^\/]+$/
println basename

four = new RealData(4.0)
five = new RealData(5.0)
println (four + five)

def text = "nice cheese gromit!"
def named = text[3..1]
assert named == "eci"

test1 = new RealData(1.1)
test2 = new RealData(0.1)
println (test1 + test2)

test1.setCurrentImpl(0);
test1.doCurrentImpl();

test1.setCurrentImpl(1);
test1.doCurrentImpl();

class RealData {
   //double val = 0;
   java.math.BigDecimal val = new java.math.BigDecimal(0);
   Closure currentImpl = null;

   RealData(val) {
      this.val = val;
   }

   RealData plus(RealData added) {
      return new RealData(this.getVal() + added.getVal());
   }

   java.math.BigDecimal getVal() {
      return val;
   }

   String toString() {
      return "RealData: " + val;
   }

   void doImplOne() {
      println 'ONE IMPL';
   }

   void doImplTwo() {
      println 'TWO IMPL';
   }

   void setCurrentImpl(index) {
      if (index == 0) {
         currentImpl = { doImplOne(); }
      } else if (index == 1) {
         currentImpl = { doImplTwo(); }
      }
   }

   void doCurrentImpl() {
      currentImpl.call();
   }
   
   void special() {
   	  println 'I am so special'
   }
}


temperature = [4, 3, 1, 6, 2]
def temperature_cp0 = temperature;
def temperature_cp3 = []; // new java.util.ArrayList();
temperature.eachWithIndex({ item, index -> if (item > 2 && item < 5) temperature_cp3.add(index) });
println temperature_cp3
println temperature_cp3.class
def temperature_cp4 = temperature_cp3;
def temperature_cp5 = temperature_cp3.clone();
temperature_cp4.add(99);
temperature_cp5.add(100);
println temperature_cp3
println temperature_cp4
println temperature_cp5

temperature_cp2=temperature_cp0.inject(0, { sum, item -> sum+ item });
println temperature_cp2;

output0 = 9
def counter = 0;
def output1 = 0;
while (counter < 4) {
	output1 += counter;
	def output2 = 50;
	counter++;
}
println "output0 = " + output0;
println "output1 = " + output1;
println "output2 = " + output2;

class bugp {
	void doit() {
		def x = 1
		def p
		if(x == 1) {
			def q = 'simple'
			println q;
			p = 'hi'
		} else {
			def q = 'complicated'
			println q;
			p = 'goodbye'
		}
		println p
	}
}

bg = new bugp()
bg.doit();

//ny = new NewType();

name_array[0] = 'lee'
