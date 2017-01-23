//ParseFile.java
//author: Sittha Sukkasi
//last update: 1/28/2003

package mit.cadlab.dome3.plugin.circulation;

import java.io.*;

public class ParseFile
{
	public int[] firstline = new int[4];

	public ParseFile()
	{
	}

	public double[][] readBedTopFile(String fileName)
	{
		int i = 0;
		String[][] data = new String[200][200];
		double[][] outdata = new double[200][200];
		try {
			BufferedReader readIn = new BufferedReader(new FileReader(fileName));
			String line = readIn.readLine();
			while (line != null) {
				data[i] = line.split("\t", -1);
				if (i == 0) { // values on the first line have special meanings
					firstline[0] = Integer.valueOf(data[i][0]).intValue(); //imax
					firstline[1] = Integer.valueOf(data[i][1]).intValue(); //jmax
					firstline[2] = Integer.valueOf(data[i][2]).intValue(); //dx
					firstline[3] = Integer.valueOf(data[i][3]).intValue(); //dy
				} else if (i > 0) {
					for (int j = 0; j <= data[i].length; j++) {
						if (j == 0) {
							outdata[i][j] = -1;
						} else if (j > 0) {
							double d = Double.valueOf(data[i][j - 1].trim()).doubleValue();
							outdata[i][j] = d;
						}
					}
				}

				line = readIn.readLine();
				i++;
			}
			readIn.close();

		} catch (Exception ex) {
			System.out.println("ERROR - reading File");
		}
		return outdata;
	}

	public int[] getFirstLine()
	{
		return firstline;
	}

}