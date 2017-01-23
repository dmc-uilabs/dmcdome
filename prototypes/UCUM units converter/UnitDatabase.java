/*
 * Created by IntelliJ IDEA.
 * User: sittha
 * Date: Oct 9, 2002
 * Time: 2:39:51 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */

import javax.swing.table.DefaultTableModel;
import java.util.Vector;

public class UnitDatabase extends DefaultTableModel {

    public static Vector unitData;
    public static Vector ucumUnitData;
    public static Vector unitName;
    public static Vector unitDef;
    public static Vector unitClass;
    public static Vector unitDefCopy;
    public static Vector unitClassCopy;
    public static String[] keywords;
    protected int numCol = 4;
    protected int emptyRowIndex;

    public UnitDatabase() {
        initialize();

    }

    public void addRow(String aNewKeyword, String aNewName, String aNewDef, String aNewClass) {
        unitData.add(aNewKeyword);
        unitName.add(aNewName);
        unitDef.add(aNewDef);
        unitClass.add(aNewClass);
    }

    public void removeRow(String keyword, String name, String def, String aClass) {
        unitData.remove(keyword);
        unitName.remove(name);
        unitDef.remove(def);
        unitClass.remove(aClass);
    }

    public static String[] getKeywords() {
        keywords = new String[unitData.size()+ucumUnitData.size()];
        for (int row=0; row<ucumUnitData.size(); row++) {
            keywords[row] = String.valueOf(ucumUnitData.elementAt(row));
        }
        for (int row=0; row<unitData.size(); row++) {
            keywords[row+ucumUnitData.size()] = String.valueOf(unitData.elementAt(row));

        }
        return keywords;
    }

    public boolean scanAbbDuplicate(String newAbb) {
        boolean isAbbDuplicated = false;
        getKeywords();
        for (int row=0; row<keywords.length; row++) {
            if (newAbb.equalsIgnoreCase(keywords[row])) {
                isAbbDuplicated=true;
                break;
            }
        }
        return isAbbDuplicated;
    }

    public boolean isEditable(String newAbb, String name) {
        boolean editable = false;
        // for editing the existing unit
        for (int i=0; i<unitData.size(); i++) {
            if (String.valueOf(unitData.elementAt(i)).equalsIgnoreCase(newAbb)
                    && String.valueOf(unitName.elementAt(i)).equalsIgnoreCase(name)) {
                editable=true;
                break;
            }
        }

        return editable;
    }

    public static void printKeywords(Vector data) {
        for (int row=0; row<data.size(); row++) {
            System.out.println(String.valueOf(data.elementAt(row)));
        }
    }

    public void initialize() {
        ucumUnitData = new Vector();
        unitData = new Vector();
        unitName = new Vector();
        unitDef = new Vector();
        unitClass = new Vector();
        unitDefCopy = new Vector();
        unitClassCopy = new Vector();
        String[] ucum = {

            //prefix
            "YA", "Za","EX","PT","TR","GA","MA","K","H","DA","D","C",
            "M","U","N","P","F","A","ZO","YO","KIB","MIB","GIB","TIB",

            // Base Units
            "m","g","s","rad","K","C","cd",

            "10*","%","[PI]","SR","HZ","N","PAL","J","W","A","V","F",
            "OHM","SIE","WB","CEL","T","H","LM","LX","BQ","GY","SV","DEG",
            "'","''","GON","L","AR","MIN","HR","D","ANN_T","ANN_J","ANN_G",
            "ANN","WK","MO_S","MO_G","MO_J","MO","TNE","BAR","[E]","EV",
            "AMU","ASU","PRS","[C]","[H]","[K]","[eps_0]","[M_E]","[mu_0]",
            "[M_P]","[GC]","[G]","[LY]","KY","GL","DYN","ERG","P","ST","MX",
            "GS","OE","GB","SB","LMB","PHT","CI","ROE","BI","[RAD]","[REM]",
            "[IN_I]","[MESH_I]","[HD_I]","[FT_I]","[YD_I]","[MI_I]","[MIL_I]",
            "[FTH_I]","[NMI_I]","[KN_I]","[SIN_I]", "[SFT_I]", "[SYD_I]","[CIN_I]",
            "[CFT_I]","[BF_I]","[CR_I]","[CYD_I]","[CML_I]","[FT_US]","[YD_US]",
            "[IN_US]","[RD_US]","[CH_US]","[LK_US]","[RCH_US]","[RLK_US]",
            "[FTH_US]","[FUR_US]","[MI_US]","[ACR_US]","[SRD_US]", "[SMI_US]",
            "[MIL_US]","[SCT]","[TWP]","[IN_BR]","[FT_BR]","[RD_BR]","[CH_BR]",
            "[LK_BR]","[FTH_BR]","[PC_BR]","[YD_BR]","[MI_BR]","[NMI_BR]","[KN_BR]",
            "[ACR_BR]","[GAL_US]","[BBL_US]","[QT_US]","[PT_US]","[GIL_US]",
            "[FOZ_US]","[FDR_US]","[MIN_US]","[CRD_US]","[BU_US]","[PK_US]",
            "[DQT_US]","[DPT_US]","[GAL_WI]","[GAL_BR]","[PK_BR]","[BU_BR]",
            "[QT_BR]","[PT_BR]","[GIL_BR]","[FOZ_BR]","[FDR_BR]","[MIN_BR]",
            "[TBS_US]","[TSP_US]","[CUP_US]","[GR]","[LB_AV]","[OZ_AV]",
            "[DR_AV]","[SCWT_AV]","[LCWT_AV]","[STON_AV]","[LTON_AV]","[STONE_AV]",
            "[LBF_AV]","[PWT_TR]","[OZ_TR]","[LB_TR]","[SC_AP]","[DR_AP]","[OZ_AP]",
            "[LB_AP]","CIR]","SPH","[DEGF]","NEP","B","B[SPL]","B[V]","B[MV]",
            "B[UV]","B[W]","B[KW]","MOL","EQ","[PH]","KAT","U","OSM","G%",
            "[DRP]","[DIOP]","[CH]","[LNE]","[PNT]","[PCA]","[PNT_PR]","[PCA_PR]",
            "[PIED]","[POUCE]","[LIGNE]","[DIDOT]","[CICERO]","STR","AO","BRN",
            "ATM","GF","ATT","[PSI]","[S]","M[H20]","M[HG]","[IN_I'H20]",
            "[IN_I'HG]","[CAR_M]","[CAR_AU]","[HPF]","[LPF]","[HNSF'U]","[MET]",
            "[PRU]","[IU]","[ARB'U]","[USP'U]","[GPL'U]","[MPL'U]","[APL'U]",
            "[BETH'U]","[TODD'U]","[DYE'U]","[SMGY'U]","[BDSK'U]","[KA'U]",
            "[KNK'U]","[MCLG'U]","[TB'U]","[PPB]","[PPM]","[PPTH]","[PPTR]",
            "CAL_[15]","CAL[20]","CAL_M","CAL_IT","CAL_TH","CAL","[CAL]",
            "[BTU_39]","[BTU_59]","[BTU_60]","[BTU_M]","[BTU_IT]","[BTU_TH]",
            "[BTU]","[HP]","BIT_S","BIT","BY","Bd"

        };
        int length1 = ucum.length;
        for (int i=0;i<length1;i++) {
            ucumUnitData.addElement(ucum[i]);
        }
        String[] initial = {
            //"Hs"
        };
        int length2 = initial.length;
        for (int i=0;i<length2;i++) {
            unitData.addElement(initial[i]);
        }
    }

    public void makeCopy() {
        unitDefCopy = unitDef;
        unitClassCopy = unitClass;
    }

    public String getOldDef(String keyword) {
        int index = unitData.indexOf(keyword);
        String oldDef = String.valueOf(unitDefCopy.elementAt(index));
        return oldDef;
    }

    public String getOldClass(String keyword) {
        int index = unitData.indexOf(keyword);
        String oldClass = String.valueOf(unitClassCopy.elementAt(index));
        return oldClass;
    }

    public static Vector getUnitData() {
        return unitData;
    }
        public static Vector getUnitName() {
        return unitName;
    }
        public static Vector getUnitDef() {
        return unitDef;
    }
        public static Vector getUnitClass() {
        return unitClass;
    }


}
