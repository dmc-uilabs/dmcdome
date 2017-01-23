package mit.cadlab.dome3.api.checkout.demo;

import mit.cadlab.dome3.api.checkout.CheckoutServerConnection;
import mit.cadlab.dome3.api.checkout.CheckoutUtil;

public class CheckoutAPITest {
    public static void main(String[] args) {
        CheckoutAPITest test = new CheckoutAPITest();
        test.test();
        System.out.println("done!");
    }

    public void test() {
        CheckoutServerConnection con = new CheckoutServerConnection("root", "!cAdJ1nx0", CheckoutServerConnection.USER, "18.80.1.101");
        CheckoutUtil util = new CheckoutUtil();
        util.checkoutModel(con, CheckoutServerConnection.SERVER, "Public/PEMS Web/1958367744", "heated gas", "D:/dome3/models/heated gas", true);
    }
}
