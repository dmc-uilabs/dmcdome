package mit.cadlab.dome3.objectmodel.modelobject.visualization;

import mit.cadlab.dome3.util.xml.XMLSupport;
import org.dom4j.Element;
import org.dom4j.DocumentHelper;

/**
 * Created by IntelliJ IDEA.
 * User: Wei Mao
 * Date: Feb 4, 2004
 * Time: 4:15:33 PM
 * To change this template use Options | File Templates.
 */
public class ChartProperties implements XMLSupport
{

	//for chartarea
	private boolean antiAliased = true;
	private boolean useDomeSetName = true;
	private String plotTitle;
	private int[] chartBackgroundColor = new int[3];
    private boolean showBorder = true;
    private float borderStroke;
    private int[] borderColor = new int[3];

	//for legend
	private boolean showLegend = true;
	private float legendOutlineStroke;
	private int[] legendOutlineColor = new int[3];
	private int[] legendBackgroundColor = new int[3];
	private String legendFontName;
	private int legendFontStyle;
	private int legendFontSize;
	private int[] legendLabelColor = new int[3];

	//for plot-vertical axis
    private String vXYLabel;        //for XY plot
	private String vBarLabel;       //for Bar plot
	private String vUnit;
	private String vFontName;
	private int vFontStyle;
	private int vFontSize;
	private int[] vColor = new int[3];
    private int[] vLabelInsets = new int[4];
	private int[] vTickLabelInsets = new int[4];
	private boolean vShowTickLabel = true;
	private boolean vShowTickMarks = true;
	private String vTickLabelFontName;
	private int vTickLabelFontStyle;
	private int vTickLabelFontSize;
	private boolean vAutoRange = true;
	private double vMinimumRange;
	private double vMaximumRange;
	private boolean vShowGrid = true;
	private float vGridStroke;
	private int[] vGridColor = new int[3];

	//for plot-horizontal axis
    private String hXYLabel;        //for XY plot
	private String hBarLabel;       //for Bar plot
	private String hUnit;
	private String hFontName;
	private int hFontStyle;
	private int hFontSize;
	private int[] hColor = new int[3];
    private int[] hLabelInsets = new int[4];
	private int[] hTickLabelInsets = new int[4];
	private boolean hShowTickLabel = true;
	private boolean hShowTickMarks = true;
	private String hTickLabelFontName;
	private int hTickLabelFontStyle;
	private int hTickLabelFontSize;
	private boolean hAutoRange = true;         //different for Bar
	private double hMinimumRange;
	private double hMaximumRange;
	private boolean hShowGrid = true;
	private float hGridStroke;
	private int[] hGridColor = new int[3];

    //for appearance
    private String appImage;
	private int[] appInsets = new int[4];
	private float appOutlineStroke;
	private int[] appOutlineColor = new int[3];
	private int[] appBackgroundColor = new int[3];
	private float appSeriesStroke = 0;




	public static final String XML_TAG = "ChartProperties";

	public boolean isAntiAliased()
	{
		return antiAliased;
	}

	public void setAntiAliased(boolean antiAliased)
	{
		this.antiAliased = antiAliased;
	}

	public boolean isUseDomeSetName()
	{
		return useDomeSetName;
	}

	public void setUseDomeSetName(boolean useDomeSetName)
	{
		this.useDomeSetName = useDomeSetName;
	}

	public String getPlotTitle()
	{
		return plotTitle;
	}

	public void setPlotTitle(String plotTitle)
	{
		this.plotTitle = plotTitle;
	}

	public int[] getChartBackgroundColor()
	{
		return chartBackgroundColor;
	}

	public void setChartBackgroundColor(int[] chartBackgroundColor)
	{
		this.chartBackgroundColor[0] = chartBackgroundColor[0];
		this.chartBackgroundColor[1] = chartBackgroundColor[1];
		this.chartBackgroundColor[2] = chartBackgroundColor[2];
	}

    public boolean isShowBorder() {
        return showBorder;
    }

    public void setShowBorder(boolean showBorder) {
        this.showBorder = showBorder;
    }

    public float getBorderStroke() {
        return borderStroke;
    }

    public void setBorderStroke(float borderStroke) {
        this.borderStroke = borderStroke;
    }

    public int[] getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(int[] borderColor) {
        this.borderColor[0] = borderColor[0];
        this.borderColor[1] = borderColor[1];
        this.borderColor[2] = borderColor[2];
    }

	public boolean isShowLegend()
	{
		return showLegend;
	}

	public void setShowLegend(boolean showLegend)
	{
		this.showLegend = showLegend;
	}

	public float getLegendOutlineStroke()
	{
		return legendOutlineStroke;
	}

	public int[] getLegendOutlineColor()
	{
		return legendOutlineColor;
	}

	public void setLegendOutlineColor(int[] legendOoutlineColor)
	{
		this.legendOutlineColor[0] = legendOoutlineColor[0];
		this.legendOutlineColor[1] = legendOoutlineColor[1];
		this.legendOutlineColor[2] = legendOoutlineColor[2];
	}

	public void setLegendOutlineStroke(float legendOutlineStroke)
	{
		this.legendOutlineStroke = legendOutlineStroke;
	}

	public int[] getLegendBackgroundColor()
	{
		return legendBackgroundColor;
	}

	public void setLegendBackgroundColor(int[] legendBackgroundColor)
	{
		this.legendBackgroundColor[0] = legendBackgroundColor[0];
		this.legendBackgroundColor[1] = legendBackgroundColor[1];
		this.legendBackgroundColor[2] = legendBackgroundColor[2];
	}

	public String getLegendFontName()
	{
		return legendFontName;
	}

	public void setLegendFontName(String legendFontName)
	{
		this.legendFontName = legendFontName;
	}

	public int getLegendFontStyle()
	{
		return legendFontStyle;
	}

	public void setLegendFontStyle(int legendFontStyle)
	{
		this.legendFontStyle = legendFontStyle;
	}

	public int getLegendFontSize()
	{
		return legendFontSize;
	}

	public void setLegendFontSize(int legendFontSize)
	{
		this.legendFontSize = legendFontSize;
	}

	public int[] getLegendLabelColor()
	{
		return legendLabelColor;
	}

	public void setLegendLabelColor(int[] legendLabelColor)
	{
		this.legendLabelColor[0] = legendLabelColor[0];
		this.legendLabelColor[1] = legendLabelColor[1];
		this.legendLabelColor[2] = legendLabelColor[2];
	}

	public String getAppImage()
	{
		return appImage;
	}

	public void setAppImage(String appImage)
	{
		this.appImage = appImage;
	}

	public int[] getAppInsets()
	{
		return appInsets;
	}

	public void setAppInsets(int[] appInsets)
	{
		this.appInsets[0] = appInsets[0];
        this.appInsets[1] = appInsets[1];
        this.appInsets[2] = appInsets[2];
        this.appInsets[3] = appInsets[3];
	}

	public float getAppOutlineStroke()
	{
		return appOutlineStroke;
	}

	public void setAppOutlineStroke(float appOutlineStroke)
	{
		this.appOutlineStroke = appOutlineStroke;
	}

	public float getAppSeriesStroke()
	{
		return appSeriesStroke;
	}

	public void setAppSeriesStroke(float appSeriesStroke)
	{
		this.appSeriesStroke = appSeriesStroke;
	}

	public int[] getAppOutlineColor()
	{
		return appOutlineColor;
	}

	public void setAppOutlineColor(int[] appOutlineColor)
	{
		this.appOutlineColor[0] = appOutlineColor[0];
		this.appOutlineColor[1] = appOutlineColor[1];
		this.appOutlineColor[2] = appOutlineColor[2];
	}

	public int[] getAppBackgroundColor()
	{
		return appBackgroundColor;
	}

	public void setAppBackgroundColor(int[] appBackgroundColor)
	{
		this.appBackgroundColor[0] = appBackgroundColor[0];
		this.appBackgroundColor[1] = appBackgroundColor[1];
		this.appBackgroundColor[2] = appBackgroundColor[2];
	}

	public String getvXYLabel()
	{
		return vXYLabel;
	}

	public void setvXYLabel(String vXYLabel)
	{
		this.vXYLabel = vXYLabel;
	}

	public String getvBarLabel()
	{
		return vBarLabel;
	}

	public void setvBarLabel(String vBarLabel)
	{
		this.vBarLabel = vBarLabel;
	}

	public String getvUnit()
	{
		return vUnit;
	}

	public void setvUnit(String vUnit)
	{
		this.vUnit = vUnit;
	}

	public String getvFontName()
	{
		return vFontName;
	}

	public void setvFontName(String vFontName)
	{
		this.vFontName = vFontName;
	}

	public int getvFontStyle()
	{
		return vFontStyle;
	}

	public void setvFontStyle(int vFontStyle)
	{
		this.vFontStyle = vFontStyle;
	}

	public int getvFontSize()
	{
		return vFontSize;
	}

	public void setvFontSize(int vFontSize)
	{
		this.vFontSize = vFontSize;
	}

	public int[] getvColor()
	{
		return vColor;
	}

	public void setvColor(int[] vColor)
	{
		this.vColor[0] = vColor[0];
		this.vColor[1] = vColor[1];
		this.vColor[2] = vColor[2];
	}

	public int[] getvLabelInsets()
	{
		return vLabelInsets;
	}

	public void setvLabelInsets(int[] vLabelInsets)
	{
		this.vLabelInsets[0] = vLabelInsets[0];
        this.vLabelInsets[1] = vLabelInsets[1];
        this.vLabelInsets[2] = vLabelInsets[2];
        this.vLabelInsets[3] = vLabelInsets[3];
	}

	public int[] getvTickLabelInsets()
	{
		return vTickLabelInsets;
	}

	public void setvTickLabelInsets(int[] vTickLabelInsets)
	{
		this.vTickLabelInsets[0] = vTickLabelInsets[0];
        this.vTickLabelInsets[1] = vTickLabelInsets[1];
        this.vTickLabelInsets[2] = vTickLabelInsets[2];
        this.vTickLabelInsets[3] = vTickLabelInsets[3];
	}

	public boolean isvShowTickLabel()
	{
		return vShowTickLabel;
	}

	public void setvShowTickLabel(boolean vShowTickLabel)
	{
		this.vShowTickLabel = vShowTickLabel;
	}

	public boolean isvShowTickMarks()
	{
		return vShowTickMarks;
	}

	public void setvShowTickMarks(boolean vShowTickMarks)
	{
		this.vShowTickMarks = vShowTickMarks;
	}

	public String getvTickLabelFontName()
	{
		return vTickLabelFontName;
	}

	public void setvTickLabelFontName(String vTickLabelFontName)
	{
		this.vTickLabelFontName = vTickLabelFontName;
	}

	public int getvTickLabelFontSize()
	{
		return vTickLabelFontSize;
	}

	public void setvTickLabelFontSize(int vTickLabelFontSize)
	{
		this.vTickLabelFontSize = vTickLabelFontSize;
	}

	public int getvTickLabelFontStyle()
	{
		return vTickLabelFontStyle;
	}

	public void setvTickLabelFontStyle(int vTickLabelFontStyle)
	{
		this.vTickLabelFontStyle = vTickLabelFontStyle;
	}

	public boolean isvAutoRange()
	{
		return vAutoRange;
	}

	public void setvAutoRange(boolean vAutoRange)
	{
		this.vAutoRange = vAutoRange;
	}

	public double getvMinimumRange()
	{
		return vMinimumRange;
	}

	public void setvMinimumRange(double vMinimumRange)
	{
		this.vMinimumRange = vMinimumRange;
	}

	public double getvMaximumRange()
	{
		return vMaximumRange;
	}

	public void setvMaximumRange(double vMaximumRange)
	{
		this.vMaximumRange = vMaximumRange;
	}

	public boolean isvShowGrid()
	{
		return vShowGrid;
	}

	public void setvShowGrid(boolean vShowGrid)
	{
		this.vShowGrid = vShowGrid;
	}

	public float getvGridStroke()
	{
		return vGridStroke;
	}

	public void setvGridStroke(float vGridStroke)
	{
		this.vGridStroke = vGridStroke;
	}

	public int[] getvGridColor()
	{
		return vGridColor;
	}

	public void setvGridColor(int[] vGridColor)
	{
		this.vGridColor[0] = vGridColor[0];
		this.vGridColor[1] = vGridColor[1];
		this.vGridColor[2] = vGridColor[2];
	}

	public String gethXYLabel()
	{
		return hXYLabel;
	}

	public void sethXYLabel(String hXYLabel)
	{
		this.hXYLabel = hXYLabel;
	}

	public String gethBarLabel()
	{
		return hBarLabel;
	}

	public void sethBarLabel(String hBarLabel)
	{
		this.hBarLabel = hBarLabel;
	}

	public String gethUnit()
	{
		return hUnit;
	}

	public void sethUnit(String hUnit)
	{
		this.hUnit = hUnit;
	}

	public String gethFontName()
	{
		return hFontName;
	}

	public void sethFontName(String hFontName)
	{
		this.hFontName = hFontName;
	}

	public int gethFontStyle()
	{
		return hFontStyle;
	}

	public void sethFontStyle(int hFontStyle)
	{
		this.hFontStyle = hFontStyle;
	}

	public int gethFontSize()
	{
		return hFontSize;
	}

	public void sethFontSize(int hFontSize)
	{
		this.hFontSize = hFontSize;
	}

	public int[] gethColor()
	{
		return hColor;
	}

	public void sethColor(int[] hColor)
	{
		this.hColor[0] = hColor[0];
		this.hColor[1] = hColor[1];
		this.hColor[2] = hColor[2];
	}

	public int[] gethLabelInsets()
	{
		return hLabelInsets;
	}

	public void sethLabelInsets(int[] hLabelInsets)
	{
		this.hLabelInsets[0] = hLabelInsets[0];
        this.hLabelInsets[1] = hLabelInsets[1];
        this.hLabelInsets[2] = hLabelInsets[2];
        this.hLabelInsets[3] = hLabelInsets[3];
	}

	public int[] gethTickLabelInsets()
	{
		return hTickLabelInsets;
	}

	public void sethTickLabelInsets(int[] hTickLabelInsets)
	{
		this.hTickLabelInsets[0] = hTickLabelInsets[0];
        this.hTickLabelInsets[1] = hTickLabelInsets[1];
        this.hTickLabelInsets[2] = hTickLabelInsets[2];
        this.hTickLabelInsets[3] = hTickLabelInsets[3];
	}

	public boolean ishShowTickLabel()
	{
		return hShowTickLabel;
	}

	public void sethShowTickLabel(boolean hShowTickLabel)
	{
		this.hShowTickLabel = hShowTickLabel;
	}

	public boolean ishShowTickMarks()
	{
		return hShowTickMarks;
	}

	public void sethShowTickMarks(boolean hShowTickMarks)
	{
		this.hShowTickMarks = hShowTickMarks;
	}

	public String gethTickLabelFontName()
	{
		return hTickLabelFontName;
	}

	public void sethTickLabelFontName(String hTickLabelFontName)
	{
		this.hTickLabelFontName = hTickLabelFontName;
	}

	public int gethTickLabelFontStyle()
	{
		return hTickLabelFontStyle;
	}

	public void sethTickLabelFontStyle(int hTickLabelFontStyle)
	{
		this.hTickLabelFontStyle = hTickLabelFontStyle;
	}

	public int gethTickLabelFontSize()
	{
		return hTickLabelFontSize;
	}

	public void sethTickLabelFontSize(int hTickLabelFontSize)
	{
		this.hTickLabelFontSize = hTickLabelFontSize;
	}

	public boolean ishAutoRange()
	{
		return hAutoRange;
	}

	public void sethAutoRange(boolean hAutoRange)
	{
		this.hAutoRange = hAutoRange;
	}

	public double gethMinimumRange()
	{
		return hMinimumRange;
	}

	public void sethMinimumRange(double hMinimumRange)
	{
		this.hMinimumRange = hMinimumRange;
	}

	public double gethMaximumRange()
	{
		return hMaximumRange;
	}

	public void sethMaximumRange(double hMaximumRange)
	{
		this.hMaximumRange = hMaximumRange;
	}

	public boolean ishShowGrid()
	{
		return hShowGrid;
	}

	public void sethShowGrid(boolean hShowGrid)
	{
		this.hShowGrid = hShowGrid;
	}

	public float gethGridStroke()
	{
		return hGridStroke;
	}

	public void sethGridStroke(float hGridStroke)
	{
		this.hGridStroke = hGridStroke;
	}

	public int[] gethGridColor()
	{
		return hGridColor;
	}

	public void sethGridColor(int[] hGridColor)
	{
		this.hGridColor[0] = hGridColor[0];
		this.hGridColor[1] = hGridColor[1];
		this.hGridColor[2] = hGridColor[2];
	}

	public Element toXmlElement()
    {
	    Element xml = DocumentHelper.createElement(this.XML_TAG);
	    return xml;

    }

	public String getXmlTag()
	{
	    return this.XML_TAG;
	}

}
