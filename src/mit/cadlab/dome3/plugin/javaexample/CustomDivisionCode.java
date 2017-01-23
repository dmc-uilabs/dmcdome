package mit.cadlab.dome3.plugin.javaexample;

/**
 * Created by IntelliJ IDEA.
 * Date: Apr 28, 2006
 * Time: 6:39:00 PM
 * This class is an example of a custom java code that can be made into a DOME plugin
 */
public class CustomDivisionCode {

    double numerator, denominator, result;

    public void setNumerator(double numerator) {
        this.numerator = numerator;
    }

    public void setDenominator(double denominator) {
        this.denominator = denominator;
    }

    public double getResult() {
        return result;
    }

    public void doDivision() {
        result = numerator/denominator;
    }
}
