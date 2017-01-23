package mit.cadlab.dome3.integrationwizards.patternmatching.clustering;

import cern.colt.matrix.impl.DenseDoubleMatrix2D;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Thomas Ligon
 * Date: Feb 5, 2007
 * Time: 2:19:41 PM
 * To change this template use Options | File Templates.
 */
public class ClusterAnalysis {

    private static DenseDoubleMatrix2D similarityMatrix;
    private int numModelParameters;
    private static ArrayList nodePairings;
    private HashMap clusters;
    public double[] data;

    public ClusterAnalysis(DenseDoubleMatrix2D simMatrix, ArrayList nodePairs)
    {
        this.similarityMatrix = simMatrix;
        this.nodePairings = nodePairs;
        this.numModelParameters = nodePairings.size();
        this.data = new double[numModelParameters];
        this.clusters = new HashMap();
        inputData();
    }

    public ClusterAnalysis(int numDataPoints)
    {
        this.numModelParameters = numDataPoints;
        this.data = new double[numModelParameters];
        this.clusters = new HashMap();
    }

    public boolean beginAnalysis()
    {
        //Perform cluster analysis starting with 4 and up through 8 clusters
        for(int numClusters=4;numClusters<9;numClusters++)
        {
            Kmeans clusters = new Kmeans(data,numClusters);
            clusters.run();
            if(!newCenter(clusters))
                break;
        }
        return postProcess();
    }

    private boolean postProcess()
    {
        Cluster cluster;
        Integer occurances;
        ArrayList totalMembers = new ArrayList();
        ArrayList multiClusters = new ArrayList(); //Clusters which were found multiple times
        Set kclusters = clusters.keySet();

        //Identify clusters which were found multiple times during the cluster analysis and have tight variance tolerances
        for(Iterator iterator=kclusters.iterator();iterator.hasNext();)
        {
            double varianceTolerance = 0.301;
            cluster = (Cluster)iterator.next();
            Collection members = cluster.getMembers();
            occurances = (Integer)clusters.get(cluster);
            if(occurances.intValue()>1 && members.size()>0.025*numModelParameters && cluster.variance<varianceTolerance)
            {
                totalMembers.addAll(members);
                multiClusters.add(cluster);
            }
        }
        if(multiClusters.size()==0 || totalMembers.size()<0.49*numModelParameters);
        else
        {
            if(distinctClusters(multiClusters))
                return true;
        }
        return false;
    }

    //Return true If the clusters which do not overlap out to 2 standard deviations
    //contain at least 50% of the model parameters
    private boolean distinctClusters(ArrayList clusters)
    {
        Cluster cluster;
        double mean;
        double sigma;
        ArrayList totalMembers = new ArrayList();
        Cluster checkCluster;
        boolean keep;
        for(int clusterIndex=0;clusterIndex<clusters.size();clusterIndex++)
        {
            keep = true;
            cluster = (Cluster)clusters.get(clusterIndex);
            mean = cluster.center;
            sigma = cluster.variance;
            for(int checkIndex=clusterIndex+1;checkIndex<clusters.size();checkIndex++)
            {
                checkCluster = (Cluster)clusters.get(checkIndex);
                if((mean+2*sigma)<checkCluster.center || checkCluster.center<(mean-2*sigma));
                else
                {
                    if(cluster.numMembers()>checkCluster.numMembers())
                        clusters.remove(checkCluster);
                    else
                    {
                        keep = false;
                        break;
                    }
                }
            }
            if(keep)
                totalMembers.addAll(cluster.getMembers());
        }
        if(totalMembers.size()>0.49*numModelParameters)
            return true;
        return false;
    }

    //If a new cluster center was found, return true
    private boolean newCenter(Kmeans kClusters)
    {
        boolean newCenter = false;
        ArrayList clusters;
        Cluster cluster;
        clusters = kClusters.getClusters();
        for(int clusterIndex=0;clusterIndex<clusters.size();clusterIndex++)
        {
            cluster = (Cluster)clusters.get(clusterIndex);
            if(cluster.hasMembers())
            {
                Double center = new Double(cluster.center);
                if(containsCenter(center, cluster.variance));
                else
                {
                     this.clusters.put(cluster,new Integer(1));
                     newCenter = true;
                }
            }
        }
        return newCenter;
    }

    //If the two center averages are similar based on a given
    private boolean containsCenter(Double center, double variance)
    {
        Cluster cluster;
        double tolerance = 0.05;
        Set centers = clusters.keySet();
        for(Iterator iterator=centers.iterator();iterator.hasNext();)
        {
            cluster = ((Cluster)iterator.next());
            if((cluster.center-tolerance)<center.doubleValue() && center.doubleValue()<(cluster.center+tolerance))
            {
                double aveVariance = cluster.variance;
                int count = ((Integer)clusters.get(cluster)).intValue();
                aveVariance = (aveVariance*count+variance)/(count+1);
                clusters.remove(cluster);
                cluster.variance = aveVariance;
                clusters.put(cluster,new Integer(count+1));
                return true;
            }
        }
        return false;
    }

    private void inputData()
    {
        int objNodeIndex;
        int tempNodeIndex;
        for(int pairIndex=0;pairIndex<numModelParameters;pairIndex++)
        {
            objNodeIndex = ((Integer)((ArrayList)nodePairings.get(pairIndex)).get(1)).intValue();
            tempNodeIndex = ((Integer)((ArrayList)nodePairings.get(pairIndex)).get(0)).intValue();
            data[pairIndex] = (1-similarityMatrix.getQuick(tempNodeIndex,objNodeIndex))/0.1;
        }
    }
}
