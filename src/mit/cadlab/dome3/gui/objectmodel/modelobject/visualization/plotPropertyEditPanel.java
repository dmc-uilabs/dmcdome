// plotPropertyEditPanel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.modelobject.visualization;


import mit.cadlab.dome3.objectmodel.modelobject.visualization.Visualization;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.CategoryItemRenderer;
import org.jfree.chart.renderer.XYItemRenderer;
import org.jfree.data.*;
import org.jfree.layout.LCBLayout;
import org.jfree.ui.*;
import org.jfreechart.axisPropertyEditPanel;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

/**
 *
 */
public class plotPropertyEditPanel extends JPanel implements ActionListener
{

	/** The paint (color) used to fill the background of the plot. */
	private PaintSample backgroundPaintSample;

	/** The stroke (pen) used to draw the outline of the plot. */
	private StrokeSample outlineStrokeSample;

	/** The paint (color) used to draw the outline of the plot. */
	private PaintSample outlinePaintSample;

	/** A panel used to display/edit the properties of the vertical axis belonging to the plot. */
	private axisPropertyEditPanel verticalAxisPropertyPanel;

	/** A panel used to display/edit the properties of the horizontal axis belonging to the plot. */
	private axisPropertyEditPanel horizontalAxisPropertyPanel;

	/** An array of stroke samples to choose from. */
	private StrokeSample[] availableStrokeSamples;

	/** The insets for the plot. */
	private Insets _insets;
	private InsetsTextField insetsTextField;
	private JComboBox seriesBox;
	private PaintSample seriesPaint;
	private StrokeSample seriesStrokeSample;

	private String[] seriesList;
	private Paint[] paintArray;

	private JTextField bkimgField;

	private Image bkImage;

	private static final String BKIMGNAME = "background image";


	protected VisualizationBuildPanel visPanel;


	public plotPropertyEditPanel(Plot plot, VisualizationBuildPanel vis)
	{
		this.visPanel = vis;
		_insets = plot.getInsets();
		//**bkImage = plot.getChart().getBackgroundImage();
		bkImage = plot.getBackgroundImage();

		backgroundPaintSample = new PaintSample(plot.getBackgroundPaint());
		outlineStrokeSample = new StrokeSample(plot.getOutlineStroke());
		outlinePaintSample = new PaintSample(plot.getOutlinePaint());

		setLayout(new BorderLayout());

		availableStrokeSamples = new StrokeSample[3];
		availableStrokeSamples[0] = new StrokeSample(new BasicStroke(1.0f));
		availableStrokeSamples[1] = new StrokeSample(new BasicStroke(2.0f));
		availableStrokeSamples[2] = new StrokeSample(new BasicStroke(3.0f));

		// create a panel for the settings...
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createTitledBorder(
		        BorderFactory.createEtchedBorder(), plot.getPlotType() + ":"));

		JPanel general = new JPanel(new BorderLayout());
		general.setBorder(BorderFactory.createTitledBorder(
		        BorderFactory.createEtchedBorder(), "General:"));

		JPanel interior = new JPanel(new LCBLayout(5));
		interior.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));


		interior.add(new JLabel("Background Image:"));
		bkimgField = new JTextField();

		if (bkImage != null) {
            if(((Visualization)vis.getDomeObject()).getSelectedSet().getJchartProperties() != null)
	            bkimgField.setText(((Visualization)vis.getDomeObject()).getSelectedSet().getJchartProperties().getAppImage());
			//bkimgField.setText(BKIMGNAME);
		}
		else
			bkimgField.setText("");
		interior.add(bkimgField);

		JButton button = new JButton("Select...");
		button.setActionCommand("BKImage");
		button.addActionListener(this);
		interior.add(button);


		interior.add(new JLabel("Insets:"));
		button = new JButton("Edit...");
		button.setActionCommand("Insets");
		button.addActionListener(this);

		insetsTextField = new InsetsTextField(_insets);
		insetsTextField.setEnabled(false);
		interior.add(insetsTextField);
		interior.add(button);

		interior.add(new JLabel("Outline stroke:"));
		button = new JButton("Select...");
		button.setActionCommand("OutlineStroke");
		button.addActionListener(this);
		interior.add(outlineStrokeSample);
		interior.add(button);

		interior.add(new JLabel("Outline paint:"));
		button = new JButton("Select...");
		button.setActionCommand("OutlinePaint");
		button.addActionListener(this);
		interior.add(outlinePaintSample);
		interior.add(button);

		interior.add(new JLabel("Background paint:"));
		button = new JButton("Select...");
		button.setActionCommand("BackgroundPaint");
		button.addActionListener(this);
		interior.add(backgroundPaintSample);
		interior.add(button);

		general.add(interior, BorderLayout.NORTH);

		JPanel appearance = new JPanel(new BorderLayout());
		appearance.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		appearance.add(general, BorderLayout.NORTH);

        //**add dataset series to this "appearance" tab
		JPanel dataseries = new JPanel(new BorderLayout());
        //** only XY plot can set stroke
		if (plot instanceof XYPlot) dataseries.setBorder(BorderFactory.createTitledBorder(
		        BorderFactory.createEtchedBorder(), "Data Series Stroke and Color:"));
		else dataseries.setBorder(BorderFactory.createTitledBorder(
		        BorderFactory.createEtchedBorder(), "Data Series Color:"));
		JPanel interior2 = new JPanel(new LCBLayout(2));
		interior2.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

		ArrayList seriesArray = new ArrayList();

		Dataset ds = null;
		if (plot instanceof XYPlot)	ds = ((XYPlot)plot).getDataset();
		else ds = ((CategoryPlot)plot).getDataset();
		if (ds != null && ds instanceof XYDataset) {
			int count = ((SeriesDataset) ds).getSeriesCount();

			for (int i = 0; i < count; i++) {
				seriesArray.add(((XYDataset) ds).getSeriesName(i));
			}
		} else if (ds != null && ds instanceof CategoryDataset) {
			int count = ((DefaultCategoryDataset) ds).getRowCount();

			for (int i = 0; i < count; i++) {
				seriesArray.add(((DefaultCategoryDataset) ds).getRowKey(i));
			}
		} else if (ds != null && ds instanceof PieDataset) {
			//do sth else

		}

		//build series array
		seriesList = (String[]) seriesArray.toArray(new String[]{});
		paintArray = new Paint[seriesList.length];
		if (seriesList.length != 0) {
			if (plot instanceof XYPlot) {
				XYItemRenderer itemRenderer = ((XYPlot) plot).getRenderer();
				seriesStrokeSample = new StrokeSample(itemRenderer.getSeriesStroke(0));       //** only XY plot can set stroke
				for (int i = 0; i < seriesList.length; i++) {
					paintArray[i] = itemRenderer.getSeriesPaint(i);
				}
			} else {
				CategoryItemRenderer itemRenderer = ((CategoryPlot) plot).getRenderer();
				for (int i = 0; i < seriesList.length; i++) {
					paintArray[i] = itemRenderer.getSeriesPaint(i);
				}
			}
		}

		seriesBox = new JComboBox(seriesList);
		if (seriesList.length != 0) {
			seriesPaint = new PaintSample(paintArray[0]);
			seriesBox.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{

					int index = seriesBox.getSelectedIndex();
					if (index != -1)
						seriesPaint.setPaint(paintArray[index]);
				}
			});

			JButton selectPaintButton = new JButton("select...");
			selectPaintButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					Color c;
					c = JColorChooser.showDialog(seriesPaint, "Outline Color", Color.blue);
					if (c != null) {
						seriesPaint.setPaint(c);
					}
					int index = seriesBox.getSelectedIndex();
					if (index != -1)
						paintArray[index] = c;
				}
			});

			JButton selectStrokeButton = new JButton("select...");
			selectStrokeButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					StrokeChooserPanel panel = new StrokeChooserPanel(null, availableStrokeSamples);
					int result = JOptionPane.showConfirmDialog(seriesStrokeSample, panel, "Stroke Selection",
					                                           JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
					if (result == JOptionPane.OK_OPTION) {
						seriesStrokeSample.setStroke(panel.getSelectedStroke());
					}

				}
			});

			if (plot instanceof XYPlot) {
				interior2.add(new JLabel("Series stroke:"));
				interior2.add(seriesStrokeSample);
				interior2.add(selectStrokeButton);

			}

			interior2.add(seriesBox);
			//interior2.add(new JLabel(" Color:"));
			interior2.add(seriesPaint);
			interior2.add(selectPaintButton);

		}
		dataseries.add(interior2, BorderLayout.NORTH);

		//**series stroke

		appearance.add(dataseries, BorderLayout.SOUTH);

        //****************************************************
		JTabbedPane tabs = new JTabbedPane();
		tabs.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

		if(plot instanceof XYPlot)
			verticalAxisPropertyPanel = axisPropertyEditPanel.getInstance(((XYPlot)plot).getRangeAxis(), visPanel);
		else
			verticalAxisPropertyPanel = axisPropertyEditPanel.getInstance(((CategoryPlot)plot).getRangeAxis(), visPanel);

		if (verticalAxisPropertyPanel != null) {
			verticalAxisPropertyPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
			tabs.add("Vertical Axis", verticalAxisPropertyPanel);
		}

		if(plot instanceof XYPlot)
		    horizontalAxisPropertyPanel = axisPropertyEditPanel.getInstance(((XYPlot)plot).getDomainAxis(), visPanel);
		else
			horizontalAxisPropertyPanel = axisPropertyEditPanel.getInstance(((CategoryPlot)plot).getDomainAxis(), visPanel);

		if (horizontalAxisPropertyPanel != null) {
			horizontalAxisPropertyPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
			tabs.add("Horizontal Axis", horizontalAxisPropertyPanel);
		}

		tabs.add("Appearance", appearance);
		panel.add(tabs);

		add(panel);
 //**********************************************
	}

	/**
	 * Returns the current plot insets.
	 */
	public Insets getPlotInsets()
	{
		if (_insets == null)
			_insets = new Insets(0, 0, 0, 0);
		return _insets;
	}

	/**
	 * Returns the current background paint.
	 */
	public Paint getBackgroundPaint()
	{
		return backgroundPaintSample.getPaint();
	}

	/**
	 * Returns the current outline stroke.
	 */
	public Stroke getOutlineStroke()
	{
		return outlineStrokeSample.getStroke();
	}

	public Stroke getSeriesStroke()
	{
		return seriesStrokeSample.getStroke();
	}

	/**
	 * Returns the current outline paint.
	 */
	public Paint getOutlinePaint()
	{
		return outlinePaintSample.getPaint();
	}

	public String getBKImageName()
	{
		return bkimgField.getText();
	}

	public String[] getSeriesList()
	{
		return seriesList;
	}

	public Paint[] getPaintArray()
	{
		return paintArray;
	}

	/**
	 * Returns a reference to the panel for editing the properties of the vertical axis.
	 */
	public axisPropertyEditPanel getVerticalAxisPropertyEditPanel()
	{
		return verticalAxisPropertyPanel;
	}

	/**
	 * Returns a reference to the panel for editing the properties of the horizontal axis.
	 */
	public axisPropertyEditPanel getHorizontalAxisPropertyEditPanel()
	{
		return horizontalAxisPropertyPanel;
	}

	/**
	 * Handles user actions generated within the panel.
	 */
	public void actionPerformed(ActionEvent event)
	{
		String command = event.getActionCommand();
		if (command.equals("BackgroundPaint")) {
			attemptBackgroundPaintSelection();
		} else if (command.equals("OutlineStroke")) {
			attemptOutlineStrokeSelection();
		} else if (command.equals("OutlinePaint")) {
			attemptOutlinePaintSelection();
		} else if (command.equals("Insets")) {
			editInsets();
		} else if (command.equals("BKImage")) {
			attemptModifyBKImage();
		}

	}

	/**
	 * Allow the user to change the background paint.
	 */
	private void attemptBackgroundPaintSelection()
	{
		Color c;
		c = JColorChooser.showDialog(this, "Background Color", Color.blue);
		if (c != null) {
			backgroundPaintSample.setPaint(c);
		}
	}

	/**
	 * Allow the user to change the outline stroke.
	 */
	private void attemptOutlineStrokeSelection()
	{
		StrokeChooserPanel panel = new StrokeChooserPanel(null, availableStrokeSamples);
		int result = JOptionPane.showConfirmDialog(this, panel, "Stroke Selection",
		                                           JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (result == JOptionPane.OK_OPTION) {
			outlineStrokeSample.setStroke(panel.getSelectedStroke());
		}

	}

	/**
	 * Allow the user to change the outline paint.  We use JColorChooser, so the user can only
	 * choose colors (a subset of all possible paints).
	 */
	private void attemptOutlinePaintSelection()
	{
		Color c;
		c = JColorChooser.showDialog(this, "Outline Color", Color.blue);
		if (c != null) {
			outlinePaintSample.setPaint(c);
		}
	}

	/**
	 * Allow the user to edit the individual insets' values.
	 */
	private void editInsets()
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


	public Paint[] getSeriesPaint()
	{

		return paintArray;
	}

	/**
	 * Updates the plot properties to match the properties defined on the panel.
	 * @param plot The plot.
	 */

	public void updatePlotProperties(Plot plot)
	{


		// set the plot properties...
		plot.setOutlinePaint(this.getOutlinePaint());
		plot.setOutlineStroke(this.getOutlineStroke());
		plot.setBackgroundPaint(this.getBackgroundPaint());
		plot.setInsets(this.getPlotInsets());

		// then the axis properties...
		if (this.horizontalAxisPropertyPanel != null) {
			if(plot instanceof XYPlot)
      			this.horizontalAxisPropertyPanel.setAxisProperties(((XYPlot)plot).getDomainAxis());
			else
				this.horizontalAxisPropertyPanel.setAxisProperties(((CategoryPlot)plot).getDomainAxis());
		}

		if (this.verticalAxisPropertyPanel != null) {
			if(plot instanceof XYPlot)
      			this.verticalAxisPropertyPanel.setAxisProperties(((XYPlot)plot).getRangeAxis());
			else
				this.verticalAxisPropertyPanel.setAxisProperties(((CategoryPlot)plot).getRangeAxis());
		}


		if (plot instanceof XYPlot) {
			XYItemRenderer itemRenderer = ((XYPlot)plot).getRenderer();
			for (int i = 0; i < paintArray.length; i++) {
				itemRenderer.setSeriesPaint(i, paintArray[i]);
				itemRenderer.setSeriesStroke(i, seriesStrokeSample.getStroke());
			}
		} else {
			CategoryItemRenderer itemRenderer = ((CategoryPlot)plot).getRenderer();
			for (int i = 0; i < paintArray.length; i++) {
				itemRenderer.setSeriesPaint(i, paintArray[i]);
				//**itemRenderer.setSeriesStroke(i, seriesStrokeSample.getStroke());
			}
		}

		//**if ((plot.getBackgroundImage() == null && (!bkimgField.getText().equals(""))) || (visPanel.valuePanel.getChart().getBackgroundImage() != null && (!bkimgField.getText().equals(BKIMGNAME))))
        if(!bkimgField.getText().equals(""))
        {
            plot.setBackgroundImage(new ImageIcon(bkimgField.getText()).getImage());
        }

	}

	private void attemptModifyBKImage()
	{

		JFileChooser chooser = new JFileChooser();


		chooser.setFileFilter(new FileFilter()
		{


			// Accept all directories and all gif, jpg, or tiff files.
			public boolean accept(File f)
			{
				if (f.isDirectory()) {
					return true;
				}

				String extension = getExtension(f);
				if (extension != null) {
					if (extension.equals("tiff") ||
					        extension.equals("tif") ||
					        extension.equals("gif") ||
					        extension.equals("jpeg") ||
					        extension.equals("jpg")) {
						return true;
					} else {
						return false;
					}
				}

				return false;
			}

			// The description of this filter
			public String getDescription()
			{
				return "Just Images";
			}

			public String getExtension(File f)
			{
				String ext = null;
				String s = f.getName();
				int i = s.lastIndexOf('.');

				if (i > 0 && i < s.length() - 1) {
					ext = s.substring(i + 1).toLowerCase();
				}
				return ext;
			}
		});


		int returnVal = chooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			bkimgField.setText(chooser.getSelectedFile().getPath());

		}


	}
}
