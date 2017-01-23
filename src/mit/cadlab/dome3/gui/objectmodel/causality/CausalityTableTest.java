/*
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: Oct 22, 2002
 * Time: 2:56:12 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package mit.cadlab.dome3.gui.objectmodel.causality;

import mit.cadlab.dome3.objectmodel.util.causality.DependencyInfo;

import javax.swing.JFrame;
import java.awt.FlowLayout;
import java.util.List;
import java.util.ArrayList;

public class CausalityTableTest
{

	public static void main(String args[])
	{

		JFrame frame = new JFrame("Causality Tests");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//frame.getContentPane().setLayout(new FlowLayout(FlowLayout.LEFT,5,5));

		List nodes = new ArrayList();
		nodes.add("a");
		nodes.add("b");
		nodes.add("c");
		nodes.add("d");
		nodes.add("e");
		nodes.add("f");
		nodes.add("g");
		nodes.add("h");
		nodes.add("i");
		nodes.add("j");
		nodes.add("k");

		String[] dependencyKeys = {"b", "c", "d", "f", "g", "h"};
		String[][] dependencies = {{"a"}, //b
		                           {"a", "i"}, //c
		                           {"b"}, //d
		                           {"c", "j"}, //f
		                           {"c", "k"}, //g
		                           {"g"}, //h
		};

		DependencyInfo dt = new DependencyInfo();
		try {
			dt.loadDependenciesFromArrays(dependencyKeys, dependencies);
		} catch (Exception ex) {
			System.out.println(ex);
		}

		CausalityInfoRendererTable cirt = new CausalityInfoRendererTable(nodes, dt);
		frame.getContentPane().add(cirt);
		frame.setSize(400, 400);
		frame.setVisible(true);
	}
}
