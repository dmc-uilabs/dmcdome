package mit.cadlab.dome3.plugin.catalog.ui;

/**
 * User: Sangmok Han
 * Date: 2006. 9. 8.
 */
public class CellConfig {

    public static final int MIN_LEVEL_OF_DETAIL = 0;
    public static final int MAX_LEVEL_OF_DETAIL = 20;

    int alignType = 1; // 1
    int width = 2; // 1, 2, 3
    int nameRow = 1; // 1, 2 the number of rows available for param name
    int scriptRow = 2; // 1, 2 the number of rows available for script
    boolean showTypeUnit = true;
    //boolean priorityOnParamName = true; // for higher priority on script, set this to false

    int levelOfDetail = 2;

    public static void main(String[] args) {
        CellConfig testCellConfig = createCellConfigFromLevelOfDetail(18);
        System.out.println("testCellConfig = " + testCellConfig);
        System.out.println("testCellConfig.find = " + testCellConfig.findLevelOfDetail());
    }

    public CellConfig(int alignType, int width, int nameRow, int scriptRow, boolean showTypeUnit) {
        this.alignType = alignType;
        this.width = width;
        this.nameRow = nameRow; // for align type 1
        this.scriptRow = scriptRow; // for align type 1
        this.showTypeUnit = showTypeUnit; // for align type 1 and 2
        //this.priorityOnParamName = priorityOnParamName;

        this.levelOfDetail = findLevelOfDetail();
    }


    public String toString() {
        return "[CellConfig: alignType=" + alignType + ", width=" + width + ", nameRow=" + nameRow + ", scriptRow=" + scriptRow + ", showTypeUnit=" + showTypeUnit + "]";
    }

    public static CellConfig createDefaultCellConfig() {
        return new CellConfig(1, 2, 1, 1, true);
    }

    /** find level of detail number based on the current value of alignType, width, nameRow, scriptRow, and showTypeUnit */
    private int findLevelOfDetail() {
//        switch (alignType) {
//            case 1:
//                if (nameRow == 1 && scriptRow == 1 && ! showTypeUnit) {
//                    return (width - 1)* 4 + 0;
//                } else if (nameRow == 1 && scriptRow == 1 && showTypeUnit) {
//                    return (width - 1)* 4 + 1;
//                } else if (nameRow == 1 && scriptRow == 2 && showTypeUnit) {
//                    return (width - 1)* 4 + 2;
//                } else if (nameRow == 2 && scriptRow == 2 && showTypeUnit) {
//                    return (width - 1)* 4 + 3;
//                }
//            case 2:
//                if (nameRow == 1 && scriptRow == 2 && showTypeUnit) {
//                    return (width - 1) + 12;
//                }
//            case 3:
//                if (nameRow == 1 && scriptRow == 1 && ! showTypeUnit) {
//                    return (width - 1) + 15;
//                } else if (nameRow == 1 && scriptRow == 1 && showTypeUnit) {
//                    return (width - 1) + 18;
//                }
//        }
//        return -1;

        switch (alignType) {
            case 1:
                if (nameRow == 1 && scriptRow == 1 && ! showTypeUnit) {
                    return (width - 1) + 0;
                } else if (nameRow == 1 && scriptRow == 1 && showTypeUnit) {
                    return (width - 1) + 3;
                } else if (nameRow == 1 && scriptRow == 2 && showTypeUnit) {
                    return (width - 1) + 6;
                } else if (nameRow == 2 && scriptRow == 2 && showTypeUnit) {
                    return (width - 1) + 9;
                }
            case 2:
                if (nameRow == 1 && scriptRow == 2 && showTypeUnit) {
                    return (width - 1) + 12;
                }
            case 3:
                if (nameRow == 1 && scriptRow == 1 && ! showTypeUnit) {
                    return (width - 1) + 15;
                } else if (nameRow == 1 && scriptRow == 1 && showTypeUnit) {
                    return (width - 1) + 18;
                }
        }
        return -1;
    }


    /** create an instance of CellConfig from levelOfDetail number, an input from the LOD slider */
    public static CellConfig createCellConfigFromLevelOfDetail(int levelOfDetail) {
        switch (levelOfDetail) {
//            /* type 1 */
//            case 0:
//                return new CellConfig(1, 1, 1, 1, false);
//            case 1:
//                return new CellConfig(1, 1, 1, 1, true);
//            case 2:
//                return new CellConfig(1, 1, 1, 2, true); // default level of detail
//            case 3:
//                return new CellConfig(1, 1, 2, 2, true);
//
//            case 0 + 4:
//                return new CellConfig(1, 2, 1, 1, false);
//            case 1 + 4:
//                return new CellConfig(1, 2, 1, 1, true);
//            case 2 + 4:
//                return new CellConfig(1, 2, 1, 2, true);
//            case 3 + 4:
//                return new CellConfig(1, 2, 2, 2, true);
//
//            case 0 + 8:
//                return new CellConfig(1, 3, 1, 1, false);
//            case 1 + 8:
//                return new CellConfig(1, 3, 1, 1, true);
//            case 2 + 8:
//                return new CellConfig(1, 3, 1, 2, true);
//            case 3 + 8:
//                return new CellConfig(1, 3, 2, 2, true);
//
//            /* type 2 */
//            case 0 + 12:
//                return new CellConfig(2, 1, 1, 2, true);
//            case 1 + 12:
//                return new CellConfig(2, 2, 1, 2, true);
//            case 2 + 12:
//                return new CellConfig(2, 3, 1, 2, true);
//
//            /* type 3 */
//            case 0 + 15:
//                return new CellConfig(3, 1, 1, 1, false);
//            case 1 + 15:
//                return new CellConfig(3, 2, 1, 1, false);
//            case 2 + 15:
//                return new CellConfig(3, 3, 1, 1, false);
//
//            case 0 + 18:
//                return new CellConfig(3, 1, 1, 1, true);
//            case 1 + 18:
//                return new CellConfig(3, 2, 1, 1, true);
//            case 2 + 18:
//                return new CellConfig(3, 3, 1, 1, true);
//
//            default:
//                return new CellConfig(1, 1, 1, 2, true);

            /* type 1 */
            case 0:
                return new CellConfig(1, 1, 1, 1, false);
            case 1:
                return new CellConfig(1, 2, 1, 1, false);
            case 2:
                return new CellConfig(1, 3, 1, 1, false);

            case 0 + 3:
                return new CellConfig(1, 1, 1, 1, true);
            case 1 + 3:
                return new CellConfig(1, 2, 1, 1, true);
            case 2 + 3:
                return new CellConfig(1, 3, 1, 1, true);

            case 0 + 6:
                return new CellConfig(1, 1, 1, 2, true);
            case 1 + 6:
                return new CellConfig(1, 2, 1, 2, true);
            case 2 + 6:
                return new CellConfig(1, 3, 1, 2, true);

            case 0 + 9:
                return new CellConfig(1, 1, 2, 2, true);
            case 1 + 9:
                return new CellConfig(1, 2, 2, 2, true);
            case 2 + 9:
                return new CellConfig(1, 3, 2, 2, true);

            case 0 + 12:
                return new CellConfig(2, 1, 1, 2, true);
            case 1 + 12:
                return new CellConfig(2, 2, 1, 2, true);
            case 2 + 12:
                return new CellConfig(2, 3, 1, 2, true);

            /* type 2 */
            case 0 + 15:
                return new CellConfig(3, 1, 1, 1, false);
            case 1 + 15:
                return new CellConfig(3, 2, 1, 1, false);
            case 2 + 15:
                return new CellConfig(3, 3, 1, 1, false);

            /* type 3 */
            case 0 + 18:
                return new CellConfig(3, 1, 1, 1, true);
            case 1 + 18:
                return new CellConfig(3, 2, 1, 1, true);
            case 2 + 18:
                return new CellConfig(3, 3, 1, 1, true);
            default:
                return new CellConfig(1, 1, 1, 2, true);
        }
    }

    /** type 1 easy configurator */
    public static CellConfig createCellConfigForType1(int width, int levelOfDetail) {
        if (levelOfDetail == 1) {
            return new CellConfig(1, width, 1, 1, false);
        } else if (levelOfDetail == 2) {
            return new CellConfig(1, width, 2, 1, false);
        } else if (levelOfDetail == 3) {
            return new CellConfig(1, width, 1, 1, true);
        } else if (levelOfDetail == 4) {
            return new CellConfig(1, width, 1, 2, true);
        } else if (levelOfDetail == 5) {
            return new CellConfig(1, width, 2, 1, true);
        } else if (levelOfDetail == 6) {
            return new CellConfig(1, width, 2, 2, true);
        } else {
            throw new RuntimeException("level of detail should be between 1 and 6");
        }
    }

    /** type 2 easy configurator */
    public static CellConfig createCellConfigForType2(int width, int levelOfDetail) {
        if (levelOfDetail == 1) {
            return new CellConfig(3, width, -1, -1, false);
        } else if (levelOfDetail == 2) {
            return new CellConfig(2, width, -1, -1, true);
        } else if (levelOfDetail == 3) {
            return new CellConfig(3, width, -1, -1, true);
        } else {
            throw new RuntimeException("level of detail should be between 1 and 3");
        }
    }
}
