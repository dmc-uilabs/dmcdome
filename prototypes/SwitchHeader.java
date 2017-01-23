
import mit.cadlab.dome.util.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;

/**
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: Apr 28, 2003
 * Time: 12:56:25 PM
 * To change this template use Options | File Templates.
 */
public class SwitchHeader
{



	public static void swichHeader(){

		BufferedReader br = null;
		BufferedReader brh = null;
		FileReader header, vrml = null;
		String aLine;
		String[] sa;
		StringBuffer sb = new StringBuffer();
		try{
			header = new FileReader("c:/dome/vrmlHeader.txt");
			vrml = new FileReader("c:/dome/Suspension.wrl");

			br = new BufferedReader(vrml);
			boolean cond = true;
			do{
				aLine = br.readLine();
				sa = aLine.split(" ");
				if(sa.length>1) cond = !sa[1].equals("MeasInfo");
			}while( cond );

			brh = new BufferedReader(header);
			String s;
			while((s = brh.readLine()) != null){
				sb.append(s);
				sb.append("\n");
			}

			sb.append(aLine);
			while((s = br.readLine()) != null){
				sb.append(s);
				sb.append("\n");
			}

			FileUtils.writeStringToFile(sb.toString(), "c:\\dome\\ok.wrl");


		}catch(Exception e){
			e.printStackTrace();
		}

	}


	public static void main(String[] args)
	{
		swichHeader();
	}
}
