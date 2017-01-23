package fullWindNoiseToolGUI;

import mit.cadlab.dome3.gui.guiutils.customGui.CustomGui;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.swing.Templates;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.Marker;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.XYSeries;
import org.jfree.data.XYSeriesCollection;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JCheckBox;

/**
 * Created by IntelliJ IDEA.
 * User: wallace
 * Date: Feb 26, 2003
 * Time: 4:26:11 PM
 * To change this template use Options | File Templates.
 */

/**
 * Card for the login step in deployment
 */
public class InterferenceResultCard extends JPanel
{

	public static final GridBagConstraints gbc = null;

    private static final String TITLE = "Seal intersection area at section plane locations";
    private static final String X_TITLE = "Cross section location";
    private static final String Y_TITLE = "Interference area (mm^2)";

    private static JCheckBox showWRL;

    private JFreeChart chart;

	public InterferenceResultCard()
	{
        JComponent[] comps = {makeTopPanel(), makeInterferencePanel()};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 0, 0, 0), 0, 0),
        };
        Templates.layoutGridBag(this, comps, gbcs);
	}

    private JPanel makeTopPanel() {
        JPanel p = new JPanel();
        JLabel interferenceLabel = Templates.makeLabel("Seal interference area", Templates.FONT12B);
        JTextArea interferenceText1 = Templates.makeDTextArea("This plot provides the seal interference area at normal sections spaced along a section line that follows the edge of the door." +
                                                          " This line is made of the edges that makeup the door A line. The analysis is performed by the I-deas based Ford gap tool. Since the parametric geometry is a straight section for this demonstration, the intersection area is the same at every cross section.\n\n" +
                                                          "You may view the intersecting portions of the seal cross sections, " +
                                                            "provided that you have a default program to display VRML/WRL files on your computer by checking off the 'show intersection portion' box. " +
                                                             "The cross sections visualization will update after each run, but is not available to you until after your first analysis." +
                                                            " Immediately after the first run you will be prompted to select where the WRL file should be saved on your computer.\n");
        interferenceText1.setOpaque(false);

        ImageIcon image = Templates.makeImageIcon("fullWindNoiseToolGUI/images/sectionLine.gif");
        JLabel imageLabel = new JLabel(image);
        imageLabel.setToolTipText("a sample of the section line along which the normal section planes are generated");

        JComponent[] comps = {interferenceLabel, interferenceText1, imageLabel};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.NORTHWEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(1, 0, 1, 2, 0.0, 0.0, gbc.NORTHEAST, gbc.NONE, new Insets(5, 5, 5, 10), 0, 0)
        };

        Templates.layoutGridBag(p, comps, gbcs);

        return p;
    }

    private JPanel makeInterferencePanel() {

        JPanel p = new JPanel();
        XYSeries xySeries = new XYSeries("");
        xySeries.add(0, 5.0);
        xySeries.add(1, 8.0);
        xySeries.add(2, 0.0);

        chart = ChartFactory.createXYLineChart(/*TITLE*/null, X_TITLE, Y_TITLE, new XYSeriesCollection(xySeries),
                                       PlotOrientation.VERTICAL, false, true, false);
        customizeChart(chart);
        ChartPanel chartPanel = new ChartPanel(chart);

        showWRL = Templates.makeCheckBox("Show intersecting portion of seal cross sections",false, true);
        showWRL.setToolTipText("Open the intersecting seal geometry. The goemetry will be automatically updated after each run.");

        JComponent[] comps = {showWRL, chartPanel};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(10, 5, 0, 10), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 5, 5, 5), 0, 0),
        };
        Templates.layoutGridBag(p, comps, gbcs);

        return p;
    }

    public void setInterface(ModelInterfaceBase iface) {
        CustomGui.connectMatrixElementXYFreeChart(iface, "interference area data", chart);
        CustomGui.connectFileOpenCheckBox(iface, "interference seal vrml file", showWRL);
    }

    private void customizeChart(JFreeChart c) {

        XYPlot p = c.getXYPlot();
        // customizing the x - axis of the editable chart - domain axis
        p.getDomainAxis().setAutoRange(true);
        p.setDomainAnchor(p.getDomainAxis().getLowerBound(), false);
        p.getDomainAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // customizing the y - axis of the editable chart - range axis
        p.getRangeAxis().setAutoRange(true);
        p.addRangeMarker(new Marker(0));

        //c.getTitle().setFont(Templates.FONT12B);
    }


	public static void main(String[] args)
	{
		JFrame f = new JFrame("Interference result card");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new InterferenceResultCard());
		f.show();
	}
}
