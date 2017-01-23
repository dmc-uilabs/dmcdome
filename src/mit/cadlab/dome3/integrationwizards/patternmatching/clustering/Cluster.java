package mit.cadlab.dome3.integrationwizards.patternmatching.clustering;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Thomas Ligon
 * Date: Feb 2, 2007
 * Time: 1:55:58 PM
 * To change this template use Options | File Templates.
 */
public class Cluster {

    protected double center;
    private double initialCenter;
    protected double variance;
    private ArrayList distances;
    private ArrayList members;
    private ArrayList prevMembers;
    private int numMembers;

    public Cluster(double initialCenter)
    {
        this.center = initialCenter;
        this.initialCenter = initialCenter;
        this.variance = 0;
        this.distances = new ArrayList();
        this.members = new ArrayList();
        this.prevMembers = new ArrayList();
    }

    public double calculateDistance(double value)
    {
        double dist = Math.abs(value-center);
        return dist;
    }

    public void calculateCenter()
    {
        double totalValue = 0;
        double totalDistance = 0;
        numMembers = members.size();
        if(numMembers!=0)
        {
            for(int memberIndex=0;memberIndex<numMembers;memberIndex++)
            {
                totalValue += ((Double)members.get(memberIndex)).doubleValue();
                totalDistance += ((Double)distances.get(memberIndex)).doubleValue();
            }
            center = totalValue/numMembers;
            variance = totalDistance/numMembers;
        }
        else
        {
            center = initialCenter;
            variance = 0;
        }
    }

    public void clearMemeber()
    {
        prevMembers = (ArrayList)members.clone();
        members.clear();
        distances.clear();
    }

    public boolean contained(double value)
    {
        if(prevMembers.contains(new Double(value)))
            return true;
        else
            return false;
    }

    public void addMember(double value, double distance)
    {
        members.add(new Double(value));
        distances.add(new Double(distance));
    }

    public boolean hasMembers()
    {
        if(numMembers!=0)
            return true;
        else
            return false;
    }

    public int numMembers()
    {
        return numMembers;
    }

    public Collection getMembers()
    {
        return members;
    }
}
