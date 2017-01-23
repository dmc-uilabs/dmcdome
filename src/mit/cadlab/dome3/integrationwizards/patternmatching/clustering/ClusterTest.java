package mit.cadlab.dome3.integrationwizards.patternmatching.clustering;

import mit.cadlab.dome3.integrationwizards.patternmatching.clustering.ClusterAnalysis;

/**
 * Created by IntelliJ IDEA.
 * User: Thomas Ligon
 * Date: Feb 1, 2007
 * Time: 12:05:57 PM
 * To change this template use Options | File Templates.
 */
public class ClusterTest {

    public static void main(String[] args)
	{
        ClusterAnalysis cluster = new ClusterAnalysis(22);
        cluster.data[0] = -2;
        cluster.data[1] = -1.9;
        cluster.data[2] = -1.8;
        cluster.data[3] = -1.95;
        cluster.data[4] = -2.2;
        cluster.data[5] = -2;
        cluster.data[6] = -0.7;
        cluster.data[7] = -0.8;
        cluster.data[8] = -0.6;
        cluster.data[9] = -0.6;
        cluster.data[10] = 1;
        cluster.data[11] = 1.5;
        cluster.data[12] = 0;
        cluster.data[13] = 1.4;
        cluster.data[14] = 0.9;
        cluster.data[15] = 1.2;
        cluster.data[16] = 1.1;
        cluster.data[17] = 1.34;
        cluster.data[18] = 1.3;
        cluster.data[19] = 0.2;
        cluster.data[20] = 1.89;
        cluster.data[21] = 4;
        boolean test = cluster.beginAnalysis();
        System.out.println();
    }
    /*
        cluster.data[0] = -2;
        cluster.data[1] = -1.9;
        cluster.data[2] = -1.8;
        cluster.data[3] = -1.7;
        cluster.data[4] = -1.6;
        cluster.data[5] = -1.5;
        cluster.data[6] = -1.4;
        cluster.data[7] = -1.3;
        cluster.data[8] = -1.2;
        cluster.data[9] = -1.1;
        cluster.data[10] = -1;
        cluster.data[11] = -0.9;
        cluster.data[12] = -0.8;
        cluster.data[13] = -0.7;
        cluster.data[14] = -0.6;
        cluster.data[15] = -0.5;
        cluster.data[16] = -0.4;
        cluster.data[17] = -0.2;
        cluster.data[18] = -0.1;
        cluster.data[19] = 0;
        cluster.data[20] = 0.1;
        cluster.data[21] = 0.2;

        cluster.data[0] = -2;
        cluster.data[1] = -1.9;
        cluster.data[2] = -1.8;
        cluster.data[3] = -1.95;
        cluster.data[4] = -2.2;
        cluster.data[5] = -2;
        cluster.data[6] = -2.11;
        cluster.data[7] = -1.99;
        cluster.data[8] = -1.98;
        cluster.data[9] = -1.99;
        cluster.data[10] = 1;
        cluster.data[11] = 1.5;
        cluster.data[12] = 1;
        cluster.data[13] = 1.4;
        cluster.data[14] = 0.9;
        cluster.data[15] = 1.2;
        cluster.data[16] = 1.1;
        cluster.data[17] = 1.34;
        cluster.data[18] = 1.3;
        cluster.data[19] = 0.2;
        cluster.data[20] = 1.11;
        cluster.data[21] = 4;

        cluster.data[0] = -2;
        cluster.data[1] = -1.9;
        cluster.data[2] = -1.8;
        cluster.data[3] = -1.95;
        cluster.data[4] = -2.2;
        cluster.data[5] = -2;
        cluster.data[6] = -0.7;
        cluster.data[7] = -0.8;
        cluster.data[8] = -0.6;
        cluster.data[9] = -0.6;
        cluster.data[10] = 1;
        cluster.data[11] = 1.5;
        cluster.data[12] = 0;
        cluster.data[13] = 1.4;
        cluster.data[14] = 0.9;
        cluster.data[15] = 1.2;
        cluster.data[16] = 1.1;
        cluster.data[17] = 1.34;
        cluster.data[18] = 1.3;
        cluster.data[19] = 0.2;
        cluster.data[20] = 1.89;
        cluster.data[21] = 4;
    */


}
