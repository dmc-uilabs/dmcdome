package mit.cadlab.dome3.plugin.catalog.pcc;

import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: Sangmok Han
 * Date: 2005. 12. 5.
 * TokenMatrix is a collection of code lines which share the same structure (called code token signature).
 * The collection has a form of matrix consisting of CodeTokens with each row representing a code fragment.
 * The structure (called code token signature) of code lines is represented by a sequence of tokens, which consists of numbers, words, and symbols.
 * To extract the tokenized structure, StringTokenizer intiailized with delimiters such as "[](){}/*-+|&^%$#@!~_, " is applied to create an array of tokens,
 * and then literal tokens such as minWeight and MyName10 are tokenized into min+Weight and My+Name+10 by several regexp rules. the regexp rules are described in detail below.
 * the reason why code fragments are tranformed into a matrix form is to infer rules that determine the value of each token.
 * for example, we have following token matrix:
 *
 *              [A] [1] [=] [B] [0] [*] [3.0]
 *              [A] [2] [=] [B] [1] [*] [3.0]
 *              [B] [1] [=] [B] [1] [*] [2.0]
 *              [B] [2] [=] [B] [3] [*] [2.0]
 *              [B] [3] [=] [B] [3] [*] [2.0]
 *              [C] [1] [=] [B] [1] [*] [5.0]
 *
 * - basis for generalizing regularity from small samples (qualitative explanation)
 *
 * let's say that we collected these lines of code fragments from many different locations in a big code base.
 * then it may be hard to justify that some regularities found in numbers and literals --
 * regardlessly, the recognition of such regularity is quite a easy task for human being -- are likely to be kept
 * for a new line of code being written.
 * however, for a case when these code fragments are from relatively small code base that manipulates
 * similar set of objects to accomplish an analogous goals, we are good to assume the regularities
 * will be kept consistant for new lines of code.
 * In the sample matrix, we have three rows where [B] is repeated in the beginning, the number columns at indexes of 1 and 4 are
 * the same, and the rows ends with the same fixed number of 2.0. we base this generalization on three occurances,
 * which is quite small, but the assumption made on the code make this generalization serve for our task of
 * estimating what code fragments a programmer will write: this feature is used to implement sophisticated
 * code reuse or code completion features.
 *
 *
 * - algorithm for generalization of fixed columns and inter-related columns
 *
 * we call a generalization found for columns as a generalization rule, or in short, a rule.
 * though there can be various ways of relating columns, we chose to work with three most frequently
 * found rules: fixed value rule for a single column, duplicated value rule for multiple columns,
 * and gapped value rule for multiple number columns. Restricting the number of rules is reasonable
 * because they explain most regularity found in code fragments, and including a number of hardly used rules,
 * especially for number columns, would result in wrong generalization of false positive patterns
 * , which is less preferred than false-negative by most code completion users. it has also practical value
 * since it reduces search space, saving computational costs. From a optimization point of view,
 * this algorithm tries to find a set of code fragments that has the most non-fixed generalization rules in it.
 * From a probabilistic point of view, it is looking for a sequence of rules assigned to columns which
 * explain the subset of code fragments most likely.
 *
 */
public class TokenMatrix {

    private static String DELIM = "[]{}()/*-+|&^%$#@!~_, ;:\\=<>?`";

    /* code list is a list of CodeToken[].
       a line of code is represented as Object[] which contains either Integer, String, or Delim */
    private List codeList;
    private String codeTokenSignature;
    private int cueEndIdx;

    /* ref list a list of references to sources of a token row  */
    private List refList;

    public int getCueEndIndex() {
        return cueEndIdx;
    }

    public TokenMatrix() {
        codeList = new ArrayList();
        refList = new ArrayList();
    }

    /**
     * if there has been no code added, -1 returned.
     * otherwise, the number of tokens in each row is returned; it is the column size.
     * @return
     */
    public int getColumnSize() {
        if (codeList.size() == 0) {
            return -1;
        } else {
            return getRow(0).length;
        }
    }

    /**
     * the number of rows of this token matrix. it is the cound of codes added to this matrix
     * @return
     */
    public int getRowSize() {
        return codeList.size();
    }

    /**
     * String codeStr is added to codeList as an CodeToken[]
     * srcId and lineNo information is collected and stored in refList
     */
    public void addCode(String codeStr, CodeReference codeRef) {
        /* when code reference is not given, use default , */
        if (codeRef == null) {
            codeRef = CodeReference.NULL_REFERENCE;
        }

        if (codeTokenSignature == null) {
            codeTokenSignature = createCodeTokenSignature(codeStr);
            codeList.add(createCodeTokenArray(codeStr));
            refList.add(codeRef);

            /* init cueEndIdx */ // todo: is it really useful?
            CodeToken[] codeTokens = getRow(0);
            for (int i = 0; i < codeTokens.length; i++) {
                if ("=".equals(codeTokens[i].getTokenObject())) {
                    cueEndIdx = i-1;
                    break;
                }
            }
        } else if (codeTokenSignature.equals(createCodeTokenSignature(codeStr))) {
            codeList.add(createCodeTokenArray(codeStr));
            refList.add(codeRef);
        } else {
            throw new RuntimeException("Added code has a different token signature from previously existing codes in this matrix: it should have a code token signature of '" + codeTokenSignature + "', but the given code is " + codeStr + ", whose code token signature is '" + createCodeTokenSignature(codeStr) + "'");
        }
    }

    public String getCodeTokenSignature() {
        return codeTokenSignature;
    }

    public CodeReference getCodeReference(int rowIdx) {
        return (CodeReference) refList.get(rowIdx);
    }

    public String getCodeString(int rowIdx) {
        StringBuffer sb = new StringBuffer();
        CodeToken[] tokens = getRow(rowIdx);
        for (int i = 0; i < tokens.length; i++) {
            CodeToken token = tokens [i];
            while (sb.length() < token.getStartIdx()) {
                sb.append(' ');
            }
            sb.append(token.getTokenObject());
        }
        return sb.toString();
    }

    public static String createCodeTokenSignature(String code) {
        StringBuffer ret = new StringBuffer();

        StringTokenizer st = new StringTokenizer(code, DELIM, true);
        for (; st.hasMoreTokens(); ) {
            String token = st.nextToken();
            /* space is not included in a token signature */
            if (! " ".equals(token)) {
                if (DELIM.indexOf(token) != -1) {
                    /* if token is a non-space symbol, it is included */
                    ret.append(token);
                } else if (isNumberString(token)) {
                    /* for number token 'N' is appended */
                    ret.append("N");
                } else {
                    String[] innerTokens = splitAtCaseChangeOrDigitChange(token);
                    for (int j = 0; j < innerTokens.length; j++) {
                        if (isNumberString(innerTokens[j])) {
                            /* for number token 'N' is appended */
                            ret.append("N");
                        } else {
                            /* if string is equal to ".", it is regarded as delimiter */
                            if (".".equals(innerTokens[j])) {
                                ret.append(".");
                            } else {
                                /* for literal token 'S' is appended */
                                ret.append("S");
                            }
                        }
                    }
                }
            }

//            //if (! " ".equals(token)) {
//                if (DELIM.indexOf(token) != -1) {
//                    /* if token is a non-space symbol, it is included */
//                    if (" ".equals(token)) {
//                        if (justAppendedDelim) {
//                            // ignore space after delim
//                        } else {
//                            ret.append(token);
//                        }
//                    } else {
//                        if (justAppendedDelim && ret.charAt(ret.length() - 1) == ' ') {
//                            // overwrite previous delim if it is a space
//                            ret.setCharAt(ret.length() - 1, token.charAt(0));
//                        } else {
//                            ret.append(token);
//                        }
//                    }
//                    justAppendedDelim = true;
//                } else if (isNumberString(token)) {
//                    /* for number token 'N' is appended */
//                    ret.append("N");
//                    justAppendedDelim = false;
//                } else {
//                    String[] innerTokens = splitAtCaseChangeOrDigitChange(token);
//                    for (int j = 0; j < innerTokens.length; j++) {
//                        if (isNumberString(innerTokens[j])) {
//                            /* for number token 'N' is appended */
//                            ret.append("N");
//                        } else {
//                            /* if string is equal to ".", it is regarded as delimiter */
//                            if (".".equals(innerTokens[j])) {
//                                ret.append(".");
//                            } else {
//                                /* for literal token 'S' is appended */
//                                ret.append("S");
//                            }
//                        }
//                        justAppendedDelim = false;
//                    }
//                }
//            //}
        }

        return ret.toString();
    }



    /**
     * applicable to string type column
     * if rowSet is given as null, it will search for all rows
     * column value is key, the set of row idx is value
     * @param columnIdx
     * @param rowSet
     * @return
     */
    public Map groupRow(int columnIdx, Set rowSet) {
        Map ret = new HashMap();

        /* for DELIM type, all rows are the same */
        if (geColumnDataType(columnIdx) == CodeToken.DELIM_TYPE) {
            String delim = getRow(0) [columnIdx].getString();
            if (rowSet == null) {
                Set rowIdxSet = new HashSet();
                for (int i = 0; i < getRowSize(); i++) {
                    rowIdxSet.add(new Integer(i));
                }
                ret.put(delim, rowIdxSet);
                return ret;
            } else {
                ret.put(delim, rowSet);
                return ret;
            }
        }

        if (rowSet == null) {
            for (int rowIdx = 0; rowIdx < codeList.size(); rowIdx++) {
                CodeToken codeToken = getRow(rowIdx) [columnIdx];
                Object tokenObj= codeToken.getTokenObject();
                Set rowIdxSet = (Set) ret.get(tokenObj);
                if (rowIdxSet == null) {
                    rowIdxSet = new HashSet();
                    rowIdxSet.add(new Integer(rowIdx));
                    ret.put(codeToken.getTokenObject(), rowIdxSet);
                } else {
                    rowIdxSet.add(new Integer(rowIdx));
                }
            }
        }
        return ret;
    }

    /**
     * columnDataType is one of CodeToken.DELIM_TYPE, STRING_TYPE, INTEGER_TYPE, DOUBLE_TYPE, or UNKNOWN_TYPE
     * they are defined as public static final of CodeToken
     * @param columnIdx
     * @return
     */
    public int geColumnDataType(int columnIdx) {
        if (codeList.size() == 0) {
            return CodeToken.UNKNOWN_TYPE;
        } else {
            return getRow(0) [columnIdx].getDataType();
        }
    }

    /**
     * this method should be called against CodeToken.DELIM_TYPE type columns
     * exception is thrown when it is called inappropriately
     * @param columnIdx
     * @return
     */
    public String getDelim(int columnIdx) {
        if (codeList.size() == 0) {
            throw new RuntimeException("Delim is undeterminable because TokenMatrix is empty.");
        } else if (getRow(0) [columnIdx].getDataType() != CodeToken.DELIM_TYPE) {
            throw new RuntimeException("getDelim(columnIdx) should be invoked against columns with DELIM_TYPE, but the given column index " + columnIdx + " is " + CodeToken.convertColumnTypeToString(getRow(0) [columnIdx].getDataType()) + "type.");
        }
        return getRow(0) [columnIdx].getString();
    }


    private CodeToken[] getRow(int rowIdx) {
        return (CodeToken[]) codeList.get(rowIdx);
    }

    /** returns the equivalent information as getRow(int), but its returned value is packaged as TokenRow */
    public TokenRow getTokenRow(int rowIdx) {
        return new TokenRow((CodeToken[]) codeList.get(rowIdx));
    }

    /**
     * create CodeToken[] from a given code String
     * @param code
     * @return
     */
    public static CodeToken[] createCodeTokenArray(String code) {
        List tokenList = new ArrayList();
        StringTokenizer st = new StringTokenizer(code, DELIM, true);

//        boolean justAppendedDelim = false;
        for (int index = 0; st.hasMoreTokens(); ) {
            String token = st.nextToken();
            /* space is not included in a token signature */
            if (! " ".equals(token)) {
                boolean isDelim = false;
                if (DELIM.indexOf(token) != -1) {
                    /* for delim token, CodeToken is created with String obj and added */
                    isDelim = true;
                    tokenList.add(new CodeToken(index, index + token.length(), token, isDelim));
                    index = index + token.length();
//                    justAppendedDelim = true;
                } else if (isNumberString(token)) {
                    /* for number token, CodeToken is created with Integer obj and added */
                    if (token.indexOf(".") == -1) {
                        tokenList.add(new CodeToken(index, index + token.length(), new Integer(token), false));
                    } else {
                        tokenList.add(new CodeToken(index, index + token.length(), new Double(token), false));
                    }
                    index = index + token.length();
//                    justAppendedDelim = false;
                } else {
                    String[] innerTokens = splitAtCaseChangeOrDigitChange(token);
                    for (int j = 0; j < innerTokens.length; j++) {
                        if (isNumberString(innerTokens[j])) {
                            /* for number token, CodeToken is created with Integer obj and added */
                            /* if . is found it is double number */
                            if (innerTokens[j].indexOf(".") == -1) {
                                tokenList.add(new CodeToken(index, index + innerTokens[j].length(), new Integer(innerTokens[j]), false));
                            } else {
                                tokenList.add(new CodeToken(index, index + innerTokens[j].length(), new Double(innerTokens[j]), false));
                            }
                            index = index + innerTokens[j].length();
//                            justAppendedDelim = false;
                        } else {
                            /* for string token, CodeToken is created with String obj and added */
                            /* if string is equal to ".", it is regarded as delimiter */
                            if (! ".".equals(innerTokens[j])) {
                                tokenList.add(new CodeToken(index, index + innerTokens[j].length(), innerTokens[j], false));
//                                justAppendedDelim = false;
                            } else {
                                tokenList.add(new CodeToken(index, index + innerTokens[j].length(), innerTokens[j], true));
//                                justAppendedDelim = true;
                            }
                            index = index + innerTokens[j].length();
                        }
                    }
                }
            } else {
//                if (justAppendedDelim) {
//                    // ignore space after delim
//                } else {
//                    boolean isDelim = true;
//                    tokenList.add(new CodeToken(index, index + token.length(), token, isDelim));
//                    justAppendedDelim = true;
//                }
                /* increase index for a space */
                index++;
            }
        }
        return (CodeToken[]) tokenList.toArray(new CodeToken[tokenList.size()]);
    }

    protected static boolean isNumberString(String input) {
        return input.matches("[0-9]\\d{0,}(\\.\\d{1,})?");
//        if (! input.matches("[0-9.]+")) {
//            /* if there is alphabet, it is not number string */
//            return false;
//        } else if ((input.lastIndexOf(".") != input.indexOf(".")) || (input.indexOf(".") == input.length() - 1)) {
//            /* if more than one periods or if it ends with a period, it is not number string */
//            return false;
//        }
//        return true;
    }

    /**
     * Split input string into consisting words based on following case change rules
     *
     * rule 1: split when lowercase changes to uppercase (ex) "myBall" into "my" + "Ball" or "BallHTML" into "Ball" + "HTML"
     * rule 2: split when two uppercase changes to lowercase (ex) "HTMLPage" into "HTML" + "Page"
     * rule 3: split when non-digit changes to digit (ex) "Page300" into "Page" + "300"
     * rule 4: split when digit changes to non-digit (ex) "300meter" into "300" + "meter"
     * rule 5: split when digit+.+non-digit (ex) "5.Hello" into "5" + ".Hello" or "5..Bob" into "5" + "..Bob"
     * rule 6: split when .+non-digit (ex) .Hello into "." + "Hello" or "..Hello" into "." + ".Hello"
     * rule 6.5: split when .+non-digit (ex) .Hello into "." + "Hello" or "..Hello" into "." + ".Hello"
     * rule 7: split when non-digit+. (ex) Hello.5 into "Hello" + ".5"
     * rule 8: split when .+digit+. (ex) .5. into "." + "5."
     *
     * (ex) "myBallHTMLPage300meter" will be splitted into "my", "Ball", "HTML", "Page", "300", "meter"
     */
    protected static String[] splitAtCaseChangeOrDigitChange(String input) {
        /* Splitting Rule 1: (\\p{Lower}\\p{Upper}) find a point to split "myBall" into "my" + "Ball" */
        /* Splitting Rule 2: (\\p{Upper}{2,}\\p{Lower}) find a point to split "HTMLPage" into "HTML" + "Page" or "XFile" into "X"+"File"*/
        /* Splitting Rule 3: ([^0-9.][0-9.]) find a point to split "Page300" into "Page" + "300" or "Bob2.0John" into "Bob" + "2.0John" */
        /* Splitting Rule 4: ([0-9.][^0-9.]) find a point to split "300meter" into "300" + "meter" or "Bob2.0John" into "Bob2.0" + "John" */
        /* Splitting Rule 5: ([0-9]\\.[^0-9]) find a point to split "5.Hello" into "5" + ".Hello" or "5..Bob" into "5" + "..Bob" */
        /* Splitting Rule 6: (\\.[^0-9]) find a point to split ".Hello" into "." + "Hello" or "..Hello" into "." + ".Hello" */
        /* Splitting Rule 6.5: ([^0-9]\\.) find a point to split "Hello.5" into "Hello" + ".5" */
        /* Splitting Rule 7: ([^0-9]\\.) find a point to split "Hello.5" into "Hello" + ".5" */
        /* Splitting Rule 8: (\\.[0-9]\\.) find a point to split ".5." into "." + "5." */
        Pattern p = Pattern.compile("(\\p{Lower}\\p{Upper})|(\\p{Upper}{2}\\p{Lower})|([^0-9.][0-9])|([0-9][^0-9.])|([0-9]\\.[^0-9])|(\\.[^0-9])|([^0-9]\\.)|(\\.[0-9]\\.)");
        Matcher m = p.matcher(input);
        List ret = new ArrayList();
        int previousEnd = 0;
        while (m.find(previousEnd)) {
            /* input.substring(previousStart, m.start() + 1)
              will work for all four rules to pick out a found word
              for your understanding, followings are sample group() results for those rules
              rule 1 (ex) yB,
              rule 2 (ex) LPa,
              rule 3 (ex) e3,
              rule 4 (ex) 0m
              rule 5 (ex) for 50.Hello, 0.H
              rule 6 (ex) for .Hello, .H
              rule 7 (ex) for Hello.5.0, o.
              rule 8 (ex) for .5.0, .5.
             */
            String token = input.substring(previousEnd, m.start() + 1);
            ret.add(token);
            previousEnd = m.start() + 1;
        }
        if (previousEnd < input.length()) {
            String lastToken= input.substring(previousEnd);
            ret.add(lastToken);
        }
        return (String[]) ret.toArray(new String[ret.size()]);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[TokenMatrix: codeTokenSignature=" + getCodeTokenSignature());
        sb.append(", cue idx=" + getCueEndIndex());
        sb.append(", codes=\n");
        DecimalFormat decimalFormat = new DecimalFormat("00");

        for (int i = 0; i < codeList.size(); i++) {
            sb.append("  (" + decimalFormat.format(i) + ", ref=" + getCodeReference(i) + ")  " + getCodeString(i) + "\r\n");
        }
        sb.append("]");
        return sb.toString();
    }
}
