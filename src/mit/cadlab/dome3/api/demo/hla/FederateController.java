package mit.cadlab.dome3.api.demo.hla;

import hla.rti1516.*;
import se.pitch.prti1516.FederateAmbassadorImpl;
import se.pitch.prti1516.RTI;

import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionBindingEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Iterator;

public class FederateController implements HttpSessionBindingListener {
    private TankFederate tankFed;
    private FighterFederate fighterFed;
    private String sessionId;

    public static void main(String[] args) {
        FederateController controller = new FederateController("0000", "C:/dome3/out/WarField.xml");
        TankFederate tankFed = controller.getTankFed();
        FighterFederate fighterFed = controller.getFighterFed();
        try { Thread.sleep(2000); } catch (Exception e) { }
        tankFed.setInMove(true);
        fighterFed.setInMove(true);

        for (int i = 0; i < 4; i++) {
            if (i % 2 == 0) {
                controller.handleFireRocket();
            } else {
                controller.handleFireBomb();
            }
            System.out.println("location: F=" + fighterFed.getFighterLocation() + ", T=" + tankFed.getTankLocation() + ", S=" + tankFed.getTankShield());
            try { Thread.sleep(5000); } catch (Exception e) { }
        }

        controller.getFighterFed().close();
        controller.getTankFed().close();
    }

    public FederateController(String sessionId, String filePath) {
        this.sessionId = sessionId;
        tankFed = new TankFederate(sessionId, filePath);
        fighterFed = new FighterFederate(sessionId, filePath);
    }

    public TankFederate getTankFed() {
        return tankFed;
    }

    public FighterFederate getFighterFed() {
        return fighterFed;
    }

    public void valueBound(HttpSessionBindingEvent event) {

    }

    public void valueUnbound(HttpSessionBindingEvent event) {
        System.out.println("DomeSession invalidated");
        tankFed.close();
        fighterFed.close();
    }

    public String getSessionId() {
        return sessionId;
    }

    public String handleLoadGame() {
        try {
            tankFed.setLocation(TankFederate.TANK_START_IDX);
            fighterFed.setLocation(FighterFederate.FIGHTER_START_IDX);
            fighterFed.setRocketCount(FighterFederate.ROCKET_FULL_COUNT);
            fighterFed.setBombCount(FighterFederate.BOMB_FULL_COUNT);
            tankFed.setTankShield(TankFederate.FULL_SHEILD);
        } catch (Exception e) {
            return "<fail error=\"" + e.getMessage() + "\"/>";
        }

        return "<done/>";

    }

    public String handleStartGame() {

        try {
            String loadResult = handleLoadGame();
            if (! "<done/>".equals(loadResult)) {
                return loadResult;
            }
            tankFed.setInMove(true);
            fighterFed.setInMove(true);
        } catch (Exception e) {
            return "<fail error=\"" + e.getMessage() + "\"/>";
        }

        return "<done/>";
    }

    public String handleEndGame() {
        try {
            tankFed.setInMove(false);
            fighterFed.setInMove(false);
        } catch (Exception e) {
            return "<fail error=\"" + e.getMessage() + "\"/>";
        }

        return "<done/>";

    }

    public String handleUpdateState() {
        return generateStateXml(0);
    }

    public String generateStateXml(int tankDamage) {
        StringBuffer sb = new StringBuffer();
        sb.append("<state FL=\"").append(fighterFed.getFighterLocation()).append("\" FI=\"").append(fighterFed.getFighterIndex()).append("\" RC=\"").append(fighterFed.getRocketCount()).append("\" BC=\"").append(fighterFed.getBombCount());
        sb.append("\" TL=\"").append(tankFed.getTankLocation()).append("\" TI=\"").append(tankFed.getTankIndex()).append("\" TS=\"").append(tankFed.getTankShield()).append("\" TD=\"").append(tankDamage).append("\"/>");
        return sb.toString();
    }

    public String handleFireRocket() {
        int prevShield = tankFed.getTankShield();
        tankFed.setInMove(false);
        fighterFed.setInMove(false);
        fighterFed.sendFireRocketInteraction();
        synchronized (fighterFed) {
            try {
                fighterFed.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        int afterShield = tankFed.getTankShield();
        tankFed.setInMove(true);
        fighterFed.setInMove(true);
        System.out.println("*** difference: " + (prevShield - afterShield));
        return generateStateXml(prevShield - afterShield);
    }

    public String handleFireBomb() {
        int prevShield = tankFed.getTankShield();
        tankFed.setInMove(false);
        fighterFed.setInMove(false);
        fighterFed.sendFireBombInteraction();
        synchronized (fighterFed) {
            try {
                fighterFed.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        int afterShield = tankFed.getTankShield();
        tankFed.setInMove(true);
        fighterFed.setInMove(true);
//        System.out.println("*** difference: " + (prevShield - afterShield));
        return generateStateXml(prevShield - afterShield);
    }

    public String handleFinishLine() {
        fighterFed.setInMove(false);
        tankFed.setInMove(false);
        return generateStateXml(0);
    }
}
