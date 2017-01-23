package mit.cadlab.dome3.objectmodel.util.causality;

import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.util.solving.DirectedGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cern.colt.matrix.DoubleMatrix2D;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Apr 16, 2003
 * Time: 1:05:06 PM
 * To change this template use Options | File Templates.
 */
public class DirectedGraphCausalityManager extends AbstractCausalityManager
{


	private DirectedGraph cGraph;

	public DirectedGraphCausalityManager(ModelObjectScope m, DirectedGraph dg)
	{
		super(m);
		cGraph = dg;
		setCausalities();
	}

	private void setCausalities()
	{
		List nodes = cGraph.getNodes();
		//int[][] adjMatrix = cGraph.getAdjMatrix();
        DoubleMatrix2D adjMatrix = cGraph.getAdjMatrix();
		HashMap tempObjectCausality = new HashMap(); // key=object; value=CausalityStatus
		if (adjMatrix == null)
			return; // todo: what is proper behavior here? erase everything?
		//update the causality of the objects and add object if not present
		for (int i = 0; i < adjMatrix.rows(); i++) {
			CausalityStatus status = getCausalityStatus(adjMatrix, i);
			tempObjectCausality.put(nodes.get(i), status);
			if (objectCausality.containsKey(nodes.get(i))) {
				changeCausality(nodes.get(i), status);
			} else { //map does not contain the object
				addObject(nodes.get(i), status);
			}
		}

		//remove the object which are no longer in the graph
		List objToBeRemove = new ArrayList();
		Set objSet = objectCausality.keySet();
		for (Iterator it = objSet.iterator(); it.hasNext();) {
			Object o = it.next();
			if (!tempObjectCausality.containsKey(o))
				objToBeRemove.add(o);
		}
		for (Iterator it = objToBeRemove.iterator(); it.hasNext();)
			removeObject(it.next());

	}

	protected CausalityStatus getCausalityStatus(int[][] adjMatrix, int index)
	{
		int rowSum = 0;
		int colSum = 0;
		for (int i = 0; i < adjMatrix.length; i++)
			rowSum += adjMatrix[index][i];

		for (int i = 0; i < adjMatrix.length; i++)
			colSum += adjMatrix[i][index];

		if (rowSum != 0 && colSum != 0)
			return CausalityStatus.INTERMEDIATE;
		else if (rowSum != 0)
			return CausalityStatus.INDEPENDENT;
		else if (colSum != 0) return CausalityStatus.RESULT;

		return null;
	}
    protected CausalityStatus getCausalityStatus(DoubleMatrix2D adjMatrix, int index)
	{
		int rowSum = 0;
		int colSum = 0;
		for (int i = 0; i < adjMatrix.rows(); i++)
			rowSum += adjMatrix.getQuick(index,i);

		for (int i = 0; i < adjMatrix.columns(); i++)
			colSum += adjMatrix.getQuick(i,index);

		if (rowSum != 0 && colSum != 0)
			return CausalityStatus.INTERMEDIATE;
		else if (rowSum != 0)
			return CausalityStatus.INDEPENDENT;
		else if (colSum != 0) return CausalityStatus.RESULT;

		return null;
	}

	public void setCausalityGraph(DirectedGraph dg)
	{
		cGraph = dg;
		setCausalities();
	}

	protected CausalityStatus getInitialCausality(Object obj)
	{
		return CausalityStatus.INDETERMINATE;
	}


}
