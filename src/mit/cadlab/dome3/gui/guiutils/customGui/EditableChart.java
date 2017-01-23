package mit.cadlab.dome3.gui.guiutils.customGui;

import org.jfree.chart.ChartMouseEvent;
import mit.cadlab.dome3.objectmodel.dataobject.DomeVectorData;

/**
 * Created by IntelliJ IDEA.
 * User: sittha
 * Date: Mar 1, 2004
 * Time: 4:52:21 PM
 * To change this template use Options | File Templates.
 */
public interface EditableChart {
    void setXAxisLabel(String x);
    void setYAxisLabel(String x);
    void chartMouseClicked(ChartMouseEvent event);
    void chartMouseMoved(ChartMouseEvent event);
    void setMaxXChartBound(int max);
    void updateXChartBound(int num);
    void setMaxYChartBound(double max);
    void setMinYChartBound(double min);
    void loadDataset(DomeVectorData x, DomeVectorData y);
    void plotOutput(DomeVectorData x, DomeVectorData y);
}