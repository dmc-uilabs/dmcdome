package mit.cadlab.dome3.api.domewc.vensimdemo;

import mit.cadlab.dome3.api.domewc.AfterValueChangedTask;
import mit.cadlab.dome3.api.domewc.WebClientUtil;

import java.util.Map;
import java.util.List;

/**
 * User: Sangmok Han
 * Date: 2006. 9. 1.
 */
public class ChartGenerationTask extends AfterValueChangedTask {

    String filePath;
    int width;
    int height;
    String xLabel;
    String yLabel;

    /** two instance of ChartGenerationTask will be used in Vensim demo:
     * ChartGenerationTask("estimated population", ...)
     * ChartGenerationTask("estimated pollution", ...) */
    public ChartGenerationTask(String paramName, String filePath, int width, int height, String xLabel, String yLabel) {
        super(paramName);
        this.filePath = filePath;
        this.width = width;
        this.height = height;
        this.xLabel = xLabel;
        this.yLabel = yLabel;
    }

    public void run(String changedParamName, Object newValue, Map paramMap) {
        List yValueList = (List) newValue;
        double[] xValues = new double[yValueList.size()];
        double[] yValues = new double[yValueList.size()];
        int increment = (2100 - 1900) / (yValueList.size() - 1);
        for (int i = 0; i < yValueList.size(); i++) {
            Number yNum = (Number) yValueList.get(i);
            xValues[i] = 1900 + i * increment;
            yValues[i] = yNum.doubleValue();
        }
        WebClientUtil.createGraphImage(filePath, width, height, null, xLabel, yLabel, xValues, yValues);
    }
}
