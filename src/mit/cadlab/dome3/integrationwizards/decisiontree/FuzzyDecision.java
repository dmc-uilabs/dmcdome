package mit.cadlab.dome3.integrationwizards.decisiontree;

/**
 * Created by IntelliJ IDEA.
 * User: Thomas Ligon
 * Date: Feb 7, 2007
 * Time: 2:50:04 PM
 * Fuzzy greater than or less than class, only handles positive values
 */
public class FuzzyDecision {

    public static boolean near(int goal, int test)
    {
        if(goal>0 && test>0)
        {
            double tolerance = getTolerance(goal);
            double upperBound = goal+tolerance;
            double lowerBound = goal-tolerance;
            if(lowerBound<1)
                lowerBound = 1;
            if(greaterThan(lowerBound,test) && lessThan(upperBound,test))
                return true;
        }
        return false;
    }

    public static boolean greaterThan(double lowerBound, int test)
    {
        if(test>0 && lowerBound>0)
        {
            if(test>=lowerBound)
                return true;
            return test(lowerBound,test);
        }
        return false;
    }

    public static boolean lessThan(double upperBound, int test)
    {
        if(test>0 && upperBound>0)
        {
            if(test<=upperBound)
                return true;
            return test(upperBound,test);
        }
        return false;
    }

    private static boolean test(double bound, int test)
    {
        double difference = Math.abs(bound-test);
        for(int iterator=1;iterator<10;iterator++)
        {
            double percent = (double)iterator/test;
            if((difference-iterator)<=0)
                return true;
            else if(percent>0.099)
                break;
        }
        return false;
    }

    private static double getTolerance(int goal)
    {
        double tolerance = 3;
        if(goal>7){
            tolerance = 3;
            if(goal>=14){
                tolerance = 4;
                if(goal>=22){
                    tolerance = 5;
                    if(goal>=30){
                        tolerance = 6;
                        if(goal>=41){
                            tolerance = goal*0.2;
                        }
                    }
                }
            }
        }
        return tolerance;
    }
}
