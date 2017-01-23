package mit.cadlab.dome3.plugin.catalog.pcc;

import edu.iupui.rg.ucum.CommonUnit;
import edu.iupui.rg.ucum.units.Dimension;
import edu.iupui.rg.ucum.units.Unit;
import edu.iupui.rg.ucum.units.UnitAtom;
import edu.iupui.rg.ucum.units.UnitTab;

import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * User: Sangmok Han
 * Date: 2005. 12. 25.
 */
public class PCCUnitTest {
    public static void main(String[] args) {
        //testUnitCompletion();

//        testCodeBase();
//        testCodeCompletion();

        //a[3][3]=

        testActiveRow();
        //testRuleFinder();
        //testTokenMatrix();
    }

    public static void testUnitCompletion() {
        /* initialize unit system */
        try {
            InputStream is = new ByteArrayInputStream(CommonUnit.DATA.getBytes());
            UnitTab.read(is);
        } catch (IOException e) {
            throw new RuntimeException("Error reading unit data");
        }

        String unitStr = "N"; // [fth_i]
        Unit unit = UnitAtom.getUnit(unitStr);
        if (unit == null) {
            throw new RuntimeException("unknown string representation of unit is given: " + unitStr);
        }

        System.out.println("** analyzed unit: " + unitStr);
        Dimension dim = unit.getUvec();
        String[] dimDesc = { "length", "mass", "time", "plane angle", "temperature", "electric change", "luminous intensity", "dollar" };
        for (int i = 0; i < 8; i++) {
            System.out.println(" " + dimDesc[i] + ": " + dim.elementAt(i));
        }
    }

    public static void testCodeCompletion() {
        CodeCompletion cc = new CodeCompletion("B_8=");
        //CodeCompletion cc = new CodeCompletion("A.pos3=");
        for (int i = 0; i < cc.getCandidateCount(); i++) {
            CodeCompletion.CandidateRow candidate = cc.getCandidateRow(i);
            System.out.println(candidate);
            System.out.println(candidate.getRule());
            System.out.println(candidate.getMatrix());
        }
    }

    public static void testCodeBase() {
        try {
            CodeBase codeBase = CodeBase.getInstance();
            FileReader reader = new FileReader("C:/dome3/indexed.txt");
            codeBase.index("testfile", reader);
            Set signatureSet = codeBase.findSignatures("B_8=");
            System.out.println(signatureSet);
            for (Iterator i = signatureSet.iterator(); i.hasNext(); ) {
                String signature = (String) i.next();
                System.out.println(codeBase.getTokenMatrix(signature));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void testActiveRow() {
        long start = System.currentTimeMillis();
        int count = 0;
        while (count++ < 1) {
            String cueStr = "B_7=";
//            String cueStr = "a[2][6]=";
//            String cueStr = "newDepth=";
            CodeToken[] cueTokens = TokenMatrix.createCodeTokenArray(cueStr);
            String[] srcCodes1 = {
                "A_1=B[1]*5+C[1]*5",
                "A_2=B[2]*5+C[2]*5",
                "A_3=B[3]*5+C[3]*5",
                "D_3=D[3]*2+D[4]*2",
                "D_4=D[4]*2+D[5]*2",
                "E_5=E[5]*2+E[6]*2",
                "A_1=A[1]*2+B[1]*2",
                "A_2=A[2]*3+B[4]*5",
                "A_3=A[3]*4+B[4]*5",
                "A_3=A[3]*4+B[3]*4",

//                "A_1=B[1]*2+C[1]*2",
//                "A_2=B[2]*3+C[4]*5",
//                "A_3=B[3]*4+C[4]*5",
//                "A_3=B[3]*4+C[3]*4",

//                "newHeight = originHeight * 1.1",
//                "newWidth = originWidth * 1.1",

//                "a[1][1]=b[1][1]",
//                "a[2][1]=b[2][1]",
//                "a[1][2]=b[1][2]",

//                "A_1=B[1] * 5 + Height[1] * Height",
//                "A_2=B[2] * 5 + Width[2] * Width",
//                "A_3=B[3] * 5 + Height[3] * Height",
            };

//            String[] srcCodes1 = {
//                "a[1][1]=b[1][1]",
//                "a[2][1]=b[2][1]",
//                "a[1][2]=b[1][2]",
//
//                //"a[3][3]=b[3][3]",
//            };

            String[] srcCodes = srcCodes1;

            TokenMatrix matrix = new TokenMatrix();
            for (int i = 0; i < srcCodes.length; i++) {
                matrix.addCode(srcCodes[i], new CodeReference("testActiveRow", i));
            }

            RuleFinder finder = new RuleFinder(matrix);
            System.out.println("matrix=" + finder.getTokenMatrix());
            System.out.println("unexplained count=" + finder.getUnexplainedCount() + ", row idx=" + finder.getUnexplainedRowIndexes());
            System.out.println("rule set size=" + finder.getRuleCount());
            for (int i = 0; i < finder.getRuleCount(); i++) {
                System.out.println(finder.getRule(i));
                Rule rule = finder.getRule(i);
                CodeCompletion.InconsistancyList inconsistList = CodeCompletion.checkConsistancy(cueTokens, rule);
                System.out.println(inconsistList);
//                for (Iterator j = rule.getAssociationColumnIndexes().iterator(); j.hasNext(); ) {
//                    Integer colIdx = (Integer) j.next();
//                    TokenAssociation asso = rule.getTokenAssociation(colIdx);
//                    if (asso instanceof RepeatedStringAssociation || asso instanceof GappedNumberAssociation) {
//                        System.out.println(CodeCompletion.getUndeterminedTokenExpression(rule, colIdx.intValue()));
//                    }
//                }
                if (inconsistList.size() == 0) {
                    System.out.println("consistant completed code: " + CodeCompletion.getCodeString(cueTokens, rule, matrix));
                } else {
                    System.out.println("inconsistant completed code: " + CodeCompletion.getCodeString(cueTokens, rule, matrix));
                }

            }

            List evalList = finder.getEvaluationList();
            for (Iterator i = evalList.iterator(); i.hasNext(); ) {
                RuleSetEvaluation eval = (RuleSetEvaluation) i.next();
                System.out.println("eval of perm: " + eval);
            }
        }
        long end = System.currentTimeMillis();

        System.out.println(end - start);
    }

    public static void testRuleFinder() {
        long start = System.currentTimeMillis();
        int count = 0;
        while (count++ < 1) {
            String[] srcCodes1 = {
                "A_1=B[1]*5+C[1]*5",
                "A_2=B[2]*5+C[2]*5",
                "A_3=B[3]*5+C[3]*5",
                "D_3=D[3]*2+D[4]*2",
                "D_4=D[4]*2+D[5]*2",
                "E_5=E[5]*2+E[6]*2",
                "A_1=A[1]*2+B[1]*2",
                "A_2=A[2]*3+B[4]*5",
                "A_3=A[3]*4+B[4]*5",
                "A_3=A[3]*4+B[3]*4",
            };


            String[] srcCodes2 = {
                "1 1 1 1 2",
                "1 1 1 1 2",
                "2 2 2 2 3",
                "3 3 3 4 5",
                "3 3 3 4 5"
            };

            String[] srcCodes3 = {
                "K K K K G",
                "K K K K G",
                "A A A A A",
                "A A A A A",
                "B B B C C",
                "B B B C C",
                "E E E F F",
                "E E L K F",
                "E E L G G",
                "E E L S S",
            };

            String[] srcCodes = srcCodes1;

            TokenMatrix matrix = new TokenMatrix();
            for (int i = 0; i < srcCodes.length; i++) {
                matrix.addCode(srcCodes[i], new CodeReference("testRuleFinder", i));
            }

            RuleFinder finder = new RuleFinder(matrix);
            System.out.println("matrix=" + finder.getTokenMatrix());
            System.out.println("unexplained count=" + finder.getUnexplainedCount() + ", row idx=" + finder.getUnexplainedRowIndexes());
            System.out.println("rule set size=" + finder.getRuleCount());
            for (int i = 0; i < finder.getRuleCount(); i++) {
                System.out.println(finder.getRule(i));
            }

            List evalList = finder.getEvaluationList();
            for (Iterator i = evalList.iterator(); i.hasNext(); ) {
                RuleSetEvaluation eval = (RuleSetEvaluation) i.next();
                System.out.println(eval);
            }
        }
        long end = System.currentTimeMillis();

        System.out.println(end - start);
    }

    public static void testMerging() {
        long start = System.currentTimeMillis();
        int count = 0;
        while (count++ < 1) {
            String[] srcCodes1 = {
                "A_1=B[1]*5+C[1]*5",
                "A_2=B[2]*5+C[2]*5",
                "A_3=B[3]*5+C[3]*5",
                "D_3=D[3]*2+D[4]*2",
                "D_4=D[4]*2+D[5]*2",
                "E_5=E[5]*2+E[6]*2",
                "A_1=A[1]*2+B[1]*2",
                "A_2=A[2]*3+B[4]*5",
                "A_3=A[3]*4+B[4]*5",
                "A_3=A[3]*4+B[3]*4",
            };

            String[] srcCodes2 = {
                "1 1 1 1 2",
                "1 1 1 1 2",
                "2 2 2 2 3",
                "3 3 3 4 5",
                "3 3 3 4 5"
            };

            String[] srcCodes3 = {
                "K K K K G",
                "K K K K G",
                "A A A A A",
                "A A A A A",
                "B B B C C",
                "B B B C C",
                "E E E F F"
            };

            String[] srcCodes = srcCodes1;

            TokenMatrix matrix = new TokenMatrix();
            for (int i = 0; i < srcCodes.length; i++) {
                matrix.addCode(srcCodes [i], new CodeReference("testMerging", i));
            }

            /* test what code token signature is generated from the first code, which is the same for other codes, too. */
            //System.out.println(matrix.getCodeTokenSignature());


            /* test if token index is correctly set */
            TokenRow firstRow = matrix.getTokenRow(0);
            String firstSrcCode = srcCodes [0];
            for (int i = 0; i < firstRow.size(); i++) {
                String codeTokenStr = firstRow.get(i).getTokenObject().toString();
                String srcCodeStr = firstSrcCode.substring(firstRow.get(i).getStartIdx(), firstRow.get(i).getEndIdx());
                //System.out.println(codeTokenStr + " should be the same as " + srcCodeStr + ", is success: " + codeTokenStr.equals(srcCodeStr));
            }

            /* test if token index is correctly set */
            //System.out.println("original code: " + srcCodes [0] + ", reconstructed from tokens: " + matrix.getCodeString(0) + ", is success: " + srcCodes [0].equals(matrix.getCodeString(0)));

            /* GappedNumberAssociation Test */
    //        Set gappedColumnIndexSet = new HashSet();
    //        gappedColumnIndexSet.add(new Integer(2));
    //        gappedColumnIndexSet.add(new Integer(6));
    //        gappedColumnIndexSet.add(new Integer(9));
    //
    //        GappedNumberAssociation asso = new GappedNumberAssociation(gappedColumnIndexSet, matrix.getRow(1));
            //System.out.println(asso);
            //System.out.println("GAP" + asso.getGapSize(6));

            RuleSet ruleList = new RuleSet();
            for (int i = 0 ; i < matrix.getRowSize(); i++) {
            for (int j = i + 1 ; j < matrix.getRowSize(); j++) {
                Rule rule = RuleFinder.generalizeRuleFromPair(i, j, matrix);
                System.out.println("created:" + rule);
                if (rule != null) {
                    ruleList.merge(rule);
                    System.out.println("ruleList size:" + ruleList.size());
                    System.out.println("ruleList:" + ruleList);
                }
            }
        }

        // OptimizedRuleList optRuleList = new OptimizedRuleList(ruleList);
        // int[] evalMetric = optRuleList.getEvaluation(); // unexplained row count, rule count
        // for (int i = 0; i < optRuleList.size(); i++) {
        //    Rule optRule = optRuleList.get(i);
        //    System.out.println("optimal rule: " + optRule);
        //    System.out.println("supporting row idx: " + optRule.getRowIndexes());
        // }
    }
        long end = System.currentTimeMillis();

        System.out.println(end - start);
    }

    public static void testTokenMatrix() {

        System.out.println(TokenMatrix.isNumberString("3.0"));
        System.out.println(TokenMatrix.isNumberString("3."));
        System.out.println(TokenMatrix.isNumberString("g"));

        /* test for splitAtCaseChangeOrDigitChange() */
        System.out.println(Arrays.asList(TokenMatrix.splitAtCaseChangeOrDigitChange("2.05HiHello521.0151")));
        System.out.println(Arrays.asList(TokenMatrix.splitAtCaseChangeOrDigitChange("Car.Wheel.G50.500Hello")));
        System.out.println(Arrays.asList(TokenMatrix.splitAtCaseChangeOrDigitChange("myBallHTMLPage300meter5")));

        /* test for createCodeTokenSignature() */
        System.out.println(TokenMatrix.createCodeTokenSignature("pos_2.05HiHello521.0151"));

        /* test for createCodeTokenArray() */
        System.out.println(Arrays.asList(TokenMatrix.createCodeTokenArray("G5.H.game('hhh')")));

        String[] srcCodes = {
            "pos1 * size40 = 5.0 * block.density + block.strain[1,2] * hole2s.r].format(0,2);myHTMLPage2.0",
            "pos2 * size40 = 5.0 * block.density + block.strain[1,3] * hole3s.r].format(0,2);myHTMLPage2.0",
            "pos3 * size40 = 5.0 * block.density + block.strain[1,4] * hole4s.r].format(0,2);myHTMLPage1.0",
            "pos4*size40   =5.0*   block.density    +block.strain[1,5]*hole4s.r].format( 0 , 2 ); myHTMLPage 1.0 ",
        };
        TokenMatrix cc = new TokenMatrix();
        for (int i = 0; i < srcCodes.length; i++) {
            cc.addCode(srcCodes [i], new CodeReference("testTokenMatrix", i));
        }

        /* test what code token signature generated from the first code, which is the same for other codes, too. */
        System.out.println(cc.getCodeTokenSignature());


        /* test if token index is correctly set */
        TokenRow firstRow = cc.getTokenRow(0);
        String firstSrcCode = srcCodes [0];
        for (int i = 0; i < firstRow.size(); i++) {
            String codeTokenStr = firstRow.get(i).getTokenObject().toString();
            String srcCodeStr = firstSrcCode.substring(firstRow.get(i).getStartIdx(), firstRow.get(i).getEndIdx());

            System.out.println(codeTokenStr + " should be the same as " + srcCodeStr + ", is success: " + codeTokenStr.equals(srcCodeStr));
        }



        /* test if token index is correctly set */
        System.out.println("original code: " + srcCodes [0] + ", reconstructed from tokens: " + cc.getCodeString(0) + ", is success: " + srcCodes [0].equals(cc.getCodeString(0)));


        for (int columnIdx = 0; columnIdx < cc.getColumnSize(); columnIdx++) {
            if (cc.geColumnDataType(columnIdx) != CodeToken.DELIM_TYPE) {
                Map groupMap = cc.groupRow(columnIdx, null);
                System.out.println("row grouping: " + groupMap);

//                for (Iterator i = groupMap.values().iterator(); i.hasNext(); ) {
//                    Set rowIdxSet = (Set) i.next();
//                    if (rowIdxSet.size()) {
//
//                    }
//                }
            }
        }

//        System.out.println("3.0A".matches("[0-9]*[\\.]?[0-9]*[^\\.][a-zA-Z]"));
//        System.out.println("A1".matches("[a-zA-Z][0-9]*[\\.]?[0-9]*[^\\.]"));
//        System.out.println("y1".matches("[a-zA-Z][0-9]*[\\.]?[0-9]*[^\\.]"));
//        System.out.println("my".matches("[a-zA-Z]"));

//        System.out.println("5v".matches("[0-9.][^0-9.]"));
//        System.out.println("5v".matches("\\d\\D"));
//        System.out.println("bb".matches("\\d+"));
//        System.out.println("3b".matches("\\d+"));
//        System.out.println("b3".matches("\\d+"));
//
//
    }
}
