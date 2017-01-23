/*
 * GroovyUnitTest.java
 *
 * Created on August 22, 2005, 3:21 PM
 *
 */

package mit.cadlab.dome3.plugin.groovy;
import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import java.io.IOException;
import java.io.File;
import java.util.Arrays;

import mit.cadlab.dome3.util.FileUtils;
/**
 *
 * @author Sangmok Han
 */
public class GroovyUnitTest {
    
    /** Creates a new instance of GroovyUnitTest */
    public GroovyUnitTest() {

    }
    
    /** initialize one groovy engine which loads all operation overloading classes */
    public void initializeOneEngine() throws Exception {
        String[] roots = new String[] { "C:/dome3/groovy" };
        GroovyScriptEngine gse = new GroovyScriptEngine(roots);
        Binding binding = new Binding();
        binding.setVariable("input", "world");
	    String[] passed = { "han", "sang", "mok" };
	    binding.setVariable("name_array", passed);
        binding.setVariable("args", new String[] { "cookie", "dolly", "shark" });
        try {
            gse.run("init_datatype.gy", binding);
            gse.run("hello.groovy", binding);
        } catch (groovy.util.ScriptException se) {
            System.out.println(se.getCause());
        }
        System.out.println(binding.getVariable("output1"));
        System.out.println(binding.getVariable("output2"));
        System.out.println(binding.getVariable("ny"));
	    System.out.println("compare this: " + Arrays.asList((String[]) binding.getVariable("name_array")));
	    System.out.println("with that : " + Arrays.asList(passed));


    }

	public static void main(String[] args) throws Exception {
		//(new GroovyUnitTest()).initializeOneEngine();

        File file = new File("hhh.txt");
        File file2 = new File("c:/dome3/hhh.txt");

        String originalDir = file.getParent();
        File fileDir = FileUtils.parent(file);
        System.out.println(originalDir);
        System.out.println(file);
        System.out.println(fileDir);

	}
}
