package mit.cadlab.dome3.integrationwizards.patternmatching.clustering;

import java.util.ArrayList;

import mit.cadlab.dome3.integrationwizards.patternmatching.clustering.Cluster;

/**
 * Created by IntelliJ IDEA.
 * User: Thomas Ligon
 * Date: Feb 2, 2007
 * Time: 1:32:11 PM
 * K-means 1-D algorithm for identifying clusters
 */
public class Kmeans {

    public int numDataPoints;
    public int numClusters;
    private ArrayList clusters;
    private int moved;
    private double[][] distances;
    private double[] data;

    public Kmeans(double[] data, int numClusters)
    {
        this.numDataPoints = data.length;
        this.data = data;
        this.numClusters = numClusters;
        this.distances = new double[numDataPoints][numClusters];
    }

    public void run()
    {
        int iterations = 0;
        clusters = initiateClusters(numClusters);
        calculateDistances();
        assignMembers();
        while(moved!=0 && iterations<20)
        {
            moved = 0;
            calculateCenters();
            calculateDistances();
            assignMembers();
            iterations++;
        }
    }

    //Calculates the new centers and variance of the member data points
    private void calculateCenters()
    {
        Cluster cluster;
        for(int clusterIndex=0;clusterIndex<numClusters;clusterIndex++)
        {
            cluster = ((Cluster)clusters.get(clusterIndex));
            cluster.calculateCenter();
            cluster.clearMemeber();
            clusters.set(clusterIndex,cluster);
        }
    }

    //assigns the data points to the cluster which it is closest to
    private void assignMembers()
    {
        double minValue;
        int index;
        double distance;
        for(int dataIndex=0;dataIndex<numDataPoints;dataIndex++)
        {
            minValue = 100000;
            index = 0;
            for(int clusterIndex=0;clusterIndex<numClusters;clusterIndex++)
            {
                distance = distances[dataIndex][clusterIndex];
                if(distance<minValue)
                {
                    minValue = distance;
                    index = clusterIndex;
                }
            }
            Cluster cluster = ((Cluster)clusters.get(index));
            if(!cluster.contained(data[dataIndex]))
                moved++;
            cluster.addMember(data[dataIndex],minValue);
            clusters.set(index,cluster);
        }
    }

    //Calculates the distance between all of the data points and the centers of all the clusters
    private void calculateDistances()
    {
        for(int dataIndex=0;dataIndex<numDataPoints;dataIndex++)
        {
            for(int clusterIndex=0;clusterIndex<numClusters;clusterIndex++)
            {
                Cluster cluster = (Cluster)clusters.get(clusterIndex);
                distances[dataIndex][clusterIndex] = cluster.calculateDistance(data[dataIndex]);
            }
        }
    }

    public ArrayList getClusters()
    {
        return clusters;
    }

    //Initiates clusters with given centers based on the number of clusters assumed to be present
    private ArrayList initiateClusters(int numClusters)
    {
        ArrayList clusters = new ArrayList();
        double clusterSpacing = 6/((double)numClusters+1);
        double clusterCenter;

        for(int clusterNum=0;clusterNum<numClusters;clusterNum++)
        {
            clusterCenter = -3+(clusterNum+1)*clusterSpacing;
            clusters.add(new Cluster(clusterCenter));
        }
        return clusters;
    }
}
