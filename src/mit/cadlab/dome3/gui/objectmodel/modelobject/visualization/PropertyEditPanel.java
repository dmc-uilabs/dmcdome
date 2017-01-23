// PropertyEditPanel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.modelobject.visualization;

import org.jfree.chart.*;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.*;
import org.jfree.layout.LCBLayout;
import org.jfreechart.axisPropertyEditPanel;
import org.jfreechart.numberAxisPropertyEditPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import mit.cadlab.dome3.objectmodel.modelobject.visualization.Visualization;
import mit.cadlab.dome3.objectmodel.modelobject.visualization.DomeObjectSet;
import mit.cadlab.dome3.objectmodel.modelobject.visualization.ChartProperties;

/**
 *
 */
public class PropertyEditPanel extends JPanel implements ActionListener
{
	/** A panel for displaying/editing the properties of the legend. */
	private legendPropertyEditPanel legendPropertiesPanel;

	/** A panel for displaying/editing the properties of the plot. */
	private plotPropertyEditPanel plotPropertiesPanel;

	/** A checkbox indicating whether or not the chart is drawn with anti-aliasing. */
	private JCheckBox antialias;

	private JCheckBox useDomeChartName;
    private JCheckBox showBorder;


	private JCheckBox reShowLegend;


	private JTextField nameField;

	private TextTitle domeTitle;

	/** The chart background color. */
	private PaintSample background;
    private PaintSample borderColor;
    private StrokeSample borderStroke;

	/** The insets for the plot. */
	//**private Insets _insets;
	private InsetsTextField insetsTextField;

    private StrokeSample[] availableStrokeSamples;

	private VisualizationBuildPanel visPanel;
	private Visualization vis;
	private DomeObjectSet currentSet = null;
	private ChartProperties currentChartProperties = null;

	/**
	 * Standard constructor - the property panel is made up of a number of sub-panels that are
	 * displayed in the tabbed pane.
	 */
	public PropertyEditPanel(VisualizationBuildPanel _visPanel)
	{
		visPanel = _visPanel;
		vis = (Visualization)visPanel.getDomeObject();
		currentSet = vis.getSelectedSet();
        if (currentSet != null) currentChartProperties = currentSet.getJchartProperties();

		JFreeChart chart = visPanel.valuePanel.getChart();

		Legend legend = chart.getLegend();
		Plot plot = chart.getPlot();

        availableStrokeSamples = new StrokeSample[4];
        availableStrokeSamples[0] = new StrokeSample(new BasicStroke(1.0f));
        availableStrokeSamples[1] = new StrokeSample(new BasicStroke(2.0f));
        availableStrokeSamples[2] = new StrokeSample(new BasicStroke(3.0f));
        availableStrokeSamples[3] = new StrokeSample(new BasicStroke(4.0f));

		setLayout(new BorderLayout());

		JPanel chartArea = new JPanel(new BorderLayout());
		chartArea.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		JPanel general = new JPanel(new BorderLayout());
		general.setBorder(BorderFactory.createTitledBorder(
		        BorderFactory.createEtchedBorder(), "General:"));

		JPanel interior = new JPanel(new LCBLayout(6));
		interior.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

		antialias = new JCheckBox("Draw anti-aliased");
		antialias.setSelected(chart.getAntiAlias());
		interior.add(antialias);
		interior.add(new JLabel(""));
		interior.add(new JLabel(""));

		interior.add(new JLabel("Plot Title:"));

		String chartname = "";
/**		int i = chart.getTitleCount();
		// i should always be 1
		if (i == 0)
			chartname = "";
		else {
			for (int j = 0; j < i; j++) {
				AbstractTitle t = chart.getTitle(j);
				if (t instanceof TextTitle)
					chartname += ((TextTitle) t).getText();
			}
		}
**/
		chartname = chart.getTitle().getText();
		nameField = new JTextField(chartname);

		useDomeChartName = new JCheckBox("or Use Dome Chart Name", true);
        nameField.setEnabled(false);

		if (currentChartProperties != null) {
            nameField.setText(currentChartProperties.getPlotTitle());
			if(currentChartProperties.isUseDomeSetName() == true) {
				useDomeChartName.setSelected(true);
				nameField.setEnabled(false);
			} else {
				useDomeChartName.setSelected(false);
				nameField.setEnabled(true);
			}
		}

		//info.setEnabled(false);
/**		if (((Visualization) visPanel.getDomeObject()).getSelectedSet() == null) {
			//empty visualization
			useDomeChartName.setSelected(true);
			nameField.setEnabled(false);
		} else {
			if (chartname.equals(((Visualization) visPanel.getDomeObject()).getSelectedSet().getName())) {

				useDomeChartName.setSelected(true);
				nameField.setEnabled(false);
			} else {
				useDomeChartName.setSelected(false);
				nameField.setEnabled(true);
			}
		}
**/
		useDomeChartName.setHorizontalTextPosition(SwingConstants.LEFT);
		useDomeChartName.setActionCommand("useDomeChartName");
		useDomeChartName.addActionListener(this);
		interior.add(nameField);
		interior.add(useDomeChartName);

		interior.add(new JLabel("Background Color:"));
		background = new PaintSample(chart.getBackgroundPaint());
		interior.add(background);
		JButton button = new JButton("Select...");
		button.setActionCommand("BackgroundPaint");
		button.addActionListener(this);
		interior.add(button);

        showBorder = new JCheckBox("Show border");
        showBorder.setSelected(chart.isBorderVisible());
        interior.add(showBorder);
        interior.add(new JLabel(""));
        interior.add(new JLabel(""));

        borderStroke = new StrokeSample(chart.getBorderStroke());
        interior.add(new JLabel("Border:"));
        interior.add(borderStroke);
        button = new JButton("Select...");
        button.setActionCommand("BorderStroke");
        button.addActionListener(this);
        interior.add(button);

        interior.add(new JLabel("Border Color:"));
        borderColor = new PaintSample(chart.getBorderPaint());
        interior.add(borderColor);
        button = new JButton("Select...");
        button.setActionCommand("BorderPaint");
        button.addActionListener(this);
        interior.add(button);

		general.add(interior, BorderLayout.NORTH);
		chartArea.add(general, BorderLayout.NORTH);

		JPanel parts = new JPanel(new BorderLayout());


		JTabbedPane tabs = new JTabbedPane();
		tabs.add("chartArea", chartArea);

		reShowLegend = new JCheckBox("Show Legend", false);

		if (legend != null) {
			legendPropertiesPanel = new legendPropertyEditPanel(legend);
			legendPropertiesPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
			tabs.addTab("Legend", legendPropertiesPanel);
		} else {

			//reShowLegend.addActionListener(new ActionListener(){
			//public void actionPerformed(ActionEvent e){
			// if(reShowLegend.isSelected())
			//  chart.setShowingLegend(true);
			//}
			// });
			JPanel emptyPanel = new JPanel(new BorderLayout());
			emptyPanel.setBorder(BorderFactory.createTitledBorder(
			        BorderFactory.createEtchedBorder(), "General:"));

			emptyPanel.add(reShowLegend, BorderLayout.NORTH);
			tabs.addTab("Legend", emptyPanel);
		}

		plotPropertiesPanel = new plotPropertyEditPanel(plot, visPanel);
		plotPropertiesPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		tabs.addTab("Plot", plotPropertiesPanel);


		parts.add(tabs, BorderLayout.NORTH);
		add(parts);
	}



	/**
	 * Returns a reference to the title property sub-panel.
	 */
// public TitlePropertyEditPanel getTitlePropertyEditPanel() {
//    return titlePropertiesPanel;
//}

	/**
	 * Returns the current plot insets.
	 */

/**	public Insets getPlotInsets()
	{
		if (_insets == null)
			_insets = new Insets(0, 0, 0, 0);
		return _insets;
	}
**/
	/**
	 * Returns a reference to the legend property sub-panel.
	 */
	public legendPropertyEditPanel getLegendPropertyEditPanel()
	{
		return legendPropertiesPanel;
	}

	/**
	 * Returns a reference to the plot property sub-panel.
	 */
	public plotPropertyEditPanel getPlotPropertyEditPanel()
	{
		return plotPropertiesPanel;
	}

	/**
	 * Returns the current setting of the anti-alias flag.
	 */
	public boolean getAntiAlias()
	{
		return antialias.isSelected();
	}

	/**
	 * Returns the current background paint.
	 */
	public Paint getBackgroundPaint()
	{
		return background.getPaint();
	}

	/**
	 * Handles user interactions with the panel.
	 */
	public void actionPerformed(ActionEvent event)
	{
		String command = event.getActionCommand();
		if (command.equals("BackgroundPaint")) {
			attemptModifyBackgroundPaint();
		} else if (command.equals("useDomeChartName")) {
			attemptModifyChartName();
		} else if (command.equals("BorderStroke")) {
			attemptModifyBorderStroke();
		} else if (command.equals("BorderPaint")) {
			attemptModifyBorderPaint();
		}

	}

	/**
	 * Allows the user the opportunity to select a new background paint.  Uses JColorChooser,
	 * so we are only allowing a subset of all Paint objects to be selected (fix later).
	 */
	private void attemptModifyBackgroundPaint()
	{
		Color c;
		c = JColorChooser.showDialog(this, "Background Color", Color.blue);
		if (c != null) {
			background.setPaint(c);
		}
	}

	/**
	 *  set chart name using dome chart name.
	 */

	private void attemptModifyChartName()
	{

		JFreeChart chart = visPanel.valuePanel.getChart();
		if (useDomeChartName.isSelected()) {
			//**nameField.setText(((Visualization) visPanel.getDomeObject()).getSelectedSet().getName());
			nameField.setEnabled(false);
		} else {
			nameField.setEnabled(true);
		}

	}

	/**
	 * Allow the user to edit the individual insets' values.
	 */

    private void attemptModifyBorderStroke()
    {
        StrokeChooserPanel panel = new StrokeChooserPanel(borderStroke, availableStrokeSamples);
        int result = JOptionPane.showConfirmDialog(this, panel, "Pen/Stroke Selection",
                                                   JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            borderStroke.setStroke(panel.getSelectedStroke());
        }
    }

    private void attemptModifyBorderPaint()
    {
        Color c;
        c = JColorChooser.showDialog(this, "Border Color", Color.blue);
        if (c != null) {
            borderColor.setPaint(c);
        }
    }

    /**    	private void editInsets()
	{
		InsetsChooserPanel panel = new InsetsChooserPanel(_insets);
		int result =
		        JOptionPane.showConfirmDialog(this, panel, "Edit Insets",
		                                      JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
			_insets = panel.getInsets();
			insetsTextField.setInsets(_insets);
		}

	}
**/

	/**
	 * Updates the properties of a chart to match the properties defined on the panel.
	 *
	 */
	public void updateChartProperties()
	{
		JFreeChart chart = visPanel.valuePanel.getChart();
		Plot plot = chart.getPlot();

		if (legendPropertiesPanel != null) {
			//if(legendPropertiesPanel.isShowLegend())setLegendProperties(chart.getLegend());
			//decide to show legend or not
			if (legendPropertiesPanel.isShowLegend()) {
				legendPropertiesPanel.setLegendProperties(chart.getLegend());
				//Legend new_legend = Legend.createInstance(chart);
				//chart.setLegend(new_legend);
			} else {
				chart.setLegend(null);
			}
		} else {
			if (reShowLegend.isSelected()) {
				//**Legend new_legend = Legend.createInstance(chart);
				//**chart.setLegend(new_legend);
			}

		}

		plotPropertiesPanel.updatePlotProperties(chart.getPlot());

		chart.setAntiAlias(this.getAntiAlias());
		chart.setBackgroundPaint(this.getBackgroundPaint());
        chart.setBorderVisible(showBorder.isSelected());
        chart.setBorderStroke(borderStroke.getStroke());
        chart.setBorderPaint(borderColor.getPaint());

		if (useDomeChartName.isSelected()) {
			visPanel.valuePanel.changeTitleUsingDomeName();
		} else {
			visPanel.valuePanel.changeTitleNotUsingDomeName(nameField.getText());
		}

		//** save to chartproperties
		if (currentSet != null) {
			ChartProperties newChartProperties;
			if(currentChartProperties != null)
			     newChartProperties = currentChartProperties;
			else newChartProperties = new ChartProperties();
			int[] newInsets = new int[4];
			int[] newColor = new int[3];

            //** for chartarea
			newChartProperties.setAntiAliased(this.getAntiAlias());
            newChartProperties.setUseDomeSetName(useDomeChartName.isSelected());
			newChartProperties.setPlotTitle(nameField.getText());
            newChartProperties.setShowBorder(showBorder.isSelected());
            newChartProperties.setBorderStroke(((BasicStroke)borderStroke.getStroke()).getLineWidth());
            newColor[0] = ((Color)this.getBackgroundPaint()).getRed();
            newColor[1] = ((Color)this.getBackgroundPaint()).getGreen();
            newColor[2] = ((Color)this.getBackgroundPaint()).getBlue();
            newChartProperties.setChartBackgroundColor(newColor);
            newColor[0] = ((Color)borderColor.getPaint()).getRed();
            newColor[1] = ((Color)borderColor.getPaint()).getGreen();
            newColor[2] = ((Color)borderColor.getPaint()).getBlue();
            newChartProperties.setBorderColor(newColor);

			//** for legend
			if (legendPropertiesPanel != null) {

				newChartProperties.setShowLegend(legendPropertiesPanel.isShowLegend());
                newChartProperties.setLegendOutlineStroke(((BasicStroke)legendPropertiesPanel.getOutlineStroke()).getLineWidth());
				newColor[0] = ((Color)legendPropertiesPanel.getOutlinePaint()).getRed();
				newColor[1] = ((Color)legendPropertiesPanel.getOutlinePaint()).getGreen();
				newColor[2] = ((Color)legendPropertiesPanel.getOutlinePaint()).getBlue();
				newChartProperties.setLegendOutlineColor(newColor);
				newColor[0] = ((Color)legendPropertiesPanel.getBackgroundPaint()).getRed();
				newColor[1] = ((Color)legendPropertiesPanel.getBackgroundPaint()).getGreen();
				newColor[2] = ((Color)legendPropertiesPanel.getBackgroundPaint()).getBlue();
                newChartProperties.setLegendBackgroundColor(newColor);
				newChartProperties.setLegendFontName(legendPropertiesPanel.getSeriesFont().getName());
                newChartProperties.setLegendFontStyle(legendPropertiesPanel.getSeriesFont().getStyle());
				newChartProperties.setLegendFontSize(legendPropertiesPanel.getSeriesFont().getSize());
				newColor[0] = ((Color)legendPropertiesPanel.getSeriesPaint()).getRed();
				newColor[1] = ((Color)legendPropertiesPanel.getSeriesPaint()).getGreen();
				newColor[2] = ((Color)legendPropertiesPanel.getSeriesPaint()).getBlue();
                newChartProperties.setLegendLabelColor(newColor);

			} else {
				if (reShowLegend.isSelected()) {
					StandardLegend new_legend = (StandardLegend)Legend.createInstance(chart);
                    Stroke newStroke = new BasicStroke(newChartProperties.getLegendOutlineStroke());
					new_legend.setOutlineStroke(newStroke);
                    int[] colorInt = new int[3];
					colorInt = newChartProperties.getLegendOutlineColor();
					Color colorA = new Color(colorInt[0],colorInt[1],colorInt[2]);
					new_legend.setOutlinePaint(colorA);
					colorInt = newChartProperties.getLegendBackgroundColor();
					Color colorB = new Color(colorInt[0],colorInt[1],colorInt[2]);
					new_legend.setBackgroundPaint(colorB);
					colorInt = newChartProperties.getLegendLabelColor();
					Color colorC = new Color(colorInt[0],colorInt[1],colorInt[2]);
                    new_legend.setItemPaint(colorC);
					Font newFont = new Font(newChartProperties.getLegendFontName(), newChartProperties.getLegendFontStyle(), newChartProperties.getLegendFontSize());
					new_legend.setItemFont(newFont);

					chart.setLegend(new_legend);

				}

			}

           //** for plot-appearance
			newChartProperties.setAppOutlineStroke(((BasicStroke)plotPropertiesPanel.getOutlineStroke()).getLineWidth());
			if(plot instanceof XYPlot && plotPropertiesPanel.getSeriesList().length != 0) newChartProperties.setAppSeriesStroke(((BasicStroke)plotPropertiesPanel.getSeriesStroke()).getLineWidth());

			newColor[0] = ((Color)plotPropertiesPanel.getOutlinePaint()).getRed();
			newColor[1] = ((Color)plotPropertiesPanel.getOutlinePaint()).getGreen();
			newColor[2] = ((Color)plotPropertiesPanel.getOutlinePaint()).getBlue();
			newChartProperties.setAppOutlineColor(newColor);
			newColor[0] = ((Color)plotPropertiesPanel.getBackgroundPaint()).getRed();
			newColor[1] = ((Color)plotPropertiesPanel.getBackgroundPaint()).getGreen();
			newColor[2] = ((Color)plotPropertiesPanel.getBackgroundPaint()).getBlue();
			newChartProperties.setAppBackgroundColor(newColor);
			newInsets[0] = plotPropertiesPanel.getPlotInsets().top;
			newInsets[1] = plotPropertiesPanel.getPlotInsets().left;
			newInsets[2] = plotPropertiesPanel.getPlotInsets().bottom;
			newInsets[3] = plotPropertiesPanel.getPlotInsets().right;
			newChartProperties.setAppInsets(newInsets);
            newChartProperties.setAppImage(plotPropertiesPanel.getBKImageName());

			if(plot instanceof XYPlot) {
				  for(int i = 0; i < plotPropertiesPanel.getSeriesList().length; i++) {
					  newColor[0] = ((Color)plotPropertiesPanel.getPaintArray()[i]).getRed();
					  newColor[1] = ((Color)plotPropertiesPanel.getPaintArray()[i]).getGreen();
					  newColor[2] = ((Color)plotPropertiesPanel.getPaintArray()[i]).getBlue();
                      vis.getSelectedSet().setSeriesColor(plotPropertiesPanel.getSeriesList()[i],newColor);
				  }
			} else {
                for(int i = 0; i < plotPropertiesPanel.getSeriesList().length; i++) {
                    newColor[0] = ((Color)plotPropertiesPanel.getPaintArray()[i]).getRed();
                    newColor[1] = ((Color)plotPropertiesPanel.getPaintArray()[i]).getGreen();
                    newColor[2] = ((Color)plotPropertiesPanel.getPaintArray()[i]).getBlue();
                    vis.getSelectedSet().setCategoryColor(i,newColor);
                }

			}

            //**for plot-vertical axis
            axisPropertyEditPanel verticalAxisPanel = plotPropertiesPanel.getVerticalAxisPropertyEditPanel();
			if(plot instanceof XYPlot) newChartProperties.setvXYLabel(verticalAxisPanel.getLabel());
			else newChartProperties.setvBarLabel(verticalAxisPanel.getLabel());
			newChartProperties.setvUnit(verticalAxisPanel.getUnit());
			newChartProperties.setvFontName(verticalAxisPanel.getLabelFont().getName());
            newChartProperties.setvFontStyle(verticalAxisPanel.getLabelFont().getStyle());
			newChartProperties.setvFontSize(verticalAxisPanel.getLabelFont().getSize());
			newColor[0] = ((Color)verticalAxisPanel.getLabelPaint()).getRed();
			newColor[1] = ((Color)verticalAxisPanel.getLabelPaint()).getGreen();
			newColor[2] = ((Color)verticalAxisPanel.getLabelPaint()).getBlue();
            newChartProperties.setvColor(newColor);
			newInsets[0] = verticalAxisPanel.getLabelInsets().top;
			newInsets[1] = verticalAxisPanel.getLabelInsets().left;
			newInsets[2] = verticalAxisPanel.getLabelInsets().bottom;
			newInsets[3] = verticalAxisPanel.getLabelInsets().right;
            newChartProperties.setvLabelInsets(newInsets);
			newInsets[0] = verticalAxisPanel.getTickLabelInsets().top;
			newInsets[1] = verticalAxisPanel.getTickLabelInsets().left;
			newInsets[2] = verticalAxisPanel.getTickLabelInsets().bottom;
			newInsets[3] = verticalAxisPanel.getTickLabelInsets().right;
            newChartProperties.setvTickLabelInsets(newInsets);
            newChartProperties.setvShowTickLabel(verticalAxisPanel.isTickLabelsVisible());
			newChartProperties.setvShowTickMarks(verticalAxisPanel.isTickMarksVisible());
            newChartProperties.setvTickLabelFontName(verticalAxisPanel.getTickLabelFont().getName());
			newChartProperties.setvTickLabelFontStyle(verticalAxisPanel.getTickLabelFont().getStyle());
			newChartProperties.setvTickLabelFontSize(verticalAxisPanel.getTickLabelFont().getSize());

            if(verticalAxisPanel instanceof numberAxisPropertyEditPanel) {
	            newChartProperties.setvAutoRange(((numberAxisPropertyEditPanel)verticalAxisPanel).isAutoRange());
	            newChartProperties.setvMinimumRange(((numberAxisPropertyEditPanel)verticalAxisPanel).getMinimumValue());
	            newChartProperties.setvMaximumRange(((numberAxisPropertyEditPanel)verticalAxisPanel).getMaximumValue());
	            newChartProperties.setvShowGrid(((numberAxisPropertyEditPanel)verticalAxisPanel).isShowGridLine());
                newChartProperties.setvGridStroke(((BasicStroke)((numberAxisPropertyEditPanel)verticalAxisPanel).getGridStroke()).getLineWidth());
	            newColor[0] = ((Color)((numberAxisPropertyEditPanel)verticalAxisPanel).getGridPaint()).getRed();
	            newColor[1] = ((Color)((numberAxisPropertyEditPanel)verticalAxisPanel).getGridPaint()).getGreen();
	            newColor[2] = ((Color)((numberAxisPropertyEditPanel)verticalAxisPanel).getGridPaint()).getBlue();
	            newChartProperties.setvGridColor(newColor);

            }

			//**for plot-horizontal axis
			axisPropertyEditPanel horizontalAxisPanel = plotPropertiesPanel.getHorizontalAxisPropertyEditPanel();
			if(plot instanceof XYPlot) newChartProperties.sethXYLabel(horizontalAxisPanel.getLabel());
			else newChartProperties.sethBarLabel(horizontalAxisPanel.getLabel());
			newChartProperties.sethUnit(horizontalAxisPanel.getUnit());
			newChartProperties.sethFontName(horizontalAxisPanel.getLabelFont().getName());
			newChartProperties.sethFontStyle(horizontalAxisPanel.getLabelFont().getStyle());
			newChartProperties.sethFontSize(horizontalAxisPanel.getLabelFont().getSize());
			newColor[0] = ((Color)horizontalAxisPanel.getLabelPaint()).getRed();
			newColor[1] = ((Color)horizontalAxisPanel.getLabelPaint()).getGreen();
			newColor[2] = ((Color)horizontalAxisPanel.getLabelPaint()).getBlue();
			newChartProperties.sethColor(newColor);
			newInsets[0] = horizontalAxisPanel.getLabelInsets().top;
			newInsets[1] = horizontalAxisPanel.getLabelInsets().left;
			newInsets[2] = horizontalAxisPanel.getLabelInsets().bottom;
			newInsets[3] = horizontalAxisPanel.getLabelInsets().right;
			newChartProperties.sethLabelInsets(newInsets);
			newInsets[0] = horizontalAxisPanel.getTickLabelInsets().top;
			newInsets[1] = horizontalAxisPanel.getTickLabelInsets().left;
			newInsets[2] = horizontalAxisPanel.getTickLabelInsets().bottom;
			newInsets[3] = horizontalAxisPanel.getTickLabelInsets().right;
			newChartProperties.sethTickLabelInsets(newInsets);
			newChartProperties.sethShowTickLabel(horizontalAxisPanel.isTickLabelsVisible());
			newChartProperties.sethShowTickMarks(horizontalAxisPanel.isTickMarksVisible());
			newChartProperties.sethTickLabelFontName(horizontalAxisPanel.getTickLabelFont().getName());
			newChartProperties.sethTickLabelFontStyle(horizontalAxisPanel.getTickLabelFont().getStyle());
			newChartProperties.sethTickLabelFontSize(horizontalAxisPanel.getTickLabelFont().getSize());

			if(horizontalAxisPanel instanceof numberAxisPropertyEditPanel) {
				newChartProperties.sethAutoRange(((numberAxisPropertyEditPanel)horizontalAxisPanel).isAutoRange());
				newChartProperties.sethMinimumRange(((numberAxisPropertyEditPanel)horizontalAxisPanel).getMinimumValue());
				newChartProperties.sethMaximumRange(((numberAxisPropertyEditPanel)horizontalAxisPanel).getMaximumValue());
				newChartProperties.sethShowGrid(((numberAxisPropertyEditPanel)horizontalAxisPanel).isShowGridLine());
			    newChartProperties.sethGridStroke(((BasicStroke)((numberAxisPropertyEditPanel)horizontalAxisPanel).getGridStroke()).getLineWidth());
				newColor[0] = ((Color)((numberAxisPropertyEditPanel)horizontalAxisPanel).getGridPaint()).getRed();
				newColor[1] = ((Color)((numberAxisPropertyEditPanel)horizontalAxisPanel).getGridPaint()).getGreen();
				newColor[2] = ((Color)((numberAxisPropertyEditPanel)horizontalAxisPanel).getGridPaint()).getBlue();
				newChartProperties.sethGridColor(newColor);

			}

            currentSet.setJchartProperties(newChartProperties);

		}

	}


}
