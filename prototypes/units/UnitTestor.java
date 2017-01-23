package units;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: weimao
 * Date: Mar 20, 2003
 * Time: 11:57:56 AM
 * To change this template use Options | File Templates.
 */
public class UnitTestor
{
	void read(String url) throws IOException {
	    UnitTab.read(url);
  }

	public static void main(String argv[]) {
		UnitTestor myTestor =  new UnitTestor();
		try{
			myTestor.read("C:/Users/weimao/Research/UCUM units converter/ucum-cs.units");

			//List myList = UnitAtom.getUnitsOfOneCategory("length unit");
			List myList = UnitAtom.getUnits("length unit", "all");
			for (int i = 0; i < myList.size(); i++ ){
                UnitAtom myUnit = (UnitAtom)myList.get(i);
				System.out.println("Name:" + myUnit.name + "; Category:" + myUnit.category + "; Description:" + myUnit.description);
			}


			//System.out.println(UnitAtom.getDescription("Ao"));

			/*for (Enumeration e = UnitAtom.elements(); e.hasMoreElements();) {
				UnitAtom myUnit = (UnitAtom) e.nextElement();
				System.out.println("Name:" + myUnit.name + ";nu:" + myUnit.nu + ";Dimen:" + myUnit.u_vec + ";Func:" + myUnit.cnv + ";Pfix:" + myUnit.cnv_pfx
				        + ";Category:" + myUnit.category + ";Description:" + myUnit.description);

			} */

			//UnitAtom myUnit =  UnitAtom.getUnit("Bi");
			//System.out.println("Name:"+myUnit.name+";nu:"+myUnit.nu+"Dimen:"+myUnit.u_vec+"Func:"+myUnit.cnv+"Pfix:"+myUnit.cnv_pfx);


		} catch(IOException x) {
		    System.out.println("error :" + x.getMessage());
		}
	}

}
