package mit.cadlab.dome3.search.datastructure.graph;

import java.util.ArrayList;

/**
 * Date: Sep 1, 2005
 * Time: 9:59:30 AM
 * NubmerArrayList is an ordered Arraylist that contains only Doubles)
 */
public class NumberArrayList  extends ArrayList{
    public void insert(Number newNum){
	  if (!this.contains(newNum))
		{
			boolean inserted=false;
			//insert into an ordered list
			for(int i=0;i<size();i++){
			  if(newNum.doubleValue()<((Number)get(i)).doubleValue())
			    {
				  add(i,newNum);
				  inserted=true;
				  break;
			    }
			}
			//biger than any of the list
			if(!inserted) add(newNum);
		}
	}

	public String toString(){
		String s="";
		for (int i = 0; i < size(); i++) {
			s = s + " " + get(i) + " ";
		}
		return s;
	}

}
