package mit.cadlab.dome3.api.demo.hla;

/**
 * Created by IntelliJ IDEA.
 * User: himosqui
 * Date: Nov 11, 2007
 * Time: 2:39:37 AM
 * To change this template use Options | File Templates.
 */
public class DifferentSpeedTester {
    public static void main(String[] args) {
        final SlowFederate slowFed = new SlowFederate("0000", "C:/dome3/out/WarField.xml");
        final FastFederate fastFed = new FastFederate("0000", "C:/dome3/out/WarField.xml");
        try { Thread.sleep(1000); } catch (Exception e) { }
        new Thread() { public void run() { slowFed.setInMove(true); } }.start();
        new Thread() { public void run() { fastFed.setInMove(true); } }.start();
        try { Thread.sleep(20000); } catch (Exception e) { }

//        for (int i = 0; i < 4; i++) {
//            if (i % 2 == 0) {
//                controller.handleFireRocket();
//            } else {
//                controller.handleFireBomb();
//            }
//            System.out.println("location: F=" + fighterFed.getFighterLocation() + ", T=" + tankFed.getTankLocation() + ", S=" + tankFed.getTankShield());
//            try { Thread.sleep(5000); } catch (Exception e) { }
//        }

        slowFed.close();
        fastFed.close();
    }

}
