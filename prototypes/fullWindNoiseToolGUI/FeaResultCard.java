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
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JCheckBox;
import javax.swing.ImageIcon;
import javax.swing.JTextField;

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
public class FeaResultCard extends JPanel
{

	public static final GridBagConstraints gbc = null;

    private static final String TITLE = "Seal force deflection curve (CLD)";
    private static final String X_TITLE = "position (mm)";
    private static final String Y_TITLE = "force (gm/mm)";

    private JFreeChart chart;
    private JCheckBox showMovieCheckBox;
    private JTextField contactLength;

	public FeaResultCard()
	{
        JComponent[] comps = {makeTopPanel(), makeCLDPanel()};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 0, 0, 0), 0, 0),
        };
        Templates.layoutGridBag(this, comps, gbcs);
	}

    private JPanel makeTopPanel() {
        JPanel p = new JPanel();
        JLabel analysisLabel = Templates.makeLabel("Seal CLD and animation", Templates.FONT12B);
        JTextArea analysisText1 = Templates.makeDTextArea("This CLD plot shows Abaqus non-linear FEA analysis results for your input configuration.\n\nYou may view an amination of the seal's deformation, " +
                                                          "provided that you have a default program to display AVI files on your computer. " +
                                                          "The animation will update after each run, but is not available to you until after your first analysis." +
                                                          " Immediately after the first run you will be prompted to select where the movie file should be saved on your computer. Check the 'show seal button' for the movie to open in your AVI viewer.\n"
                                                          );
        analysisText1.setOpaque(false);

        ImageIcon image = Templates.makeImageIcon("fullWindNoiseToolGUI/images/sealDeformed.gif");
        JLabel imageLabel = new JLabel(image);
        imageLabel.setToolTipText("Typical seal deformation");

        JComponent[] comps = {analysisLabel, analysisText1, imageLabel};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.NORTHWEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 0, 1, 2, 0.0, 0.0, gbc.NORTHEAST, gbc.NONE, new Insets(5, 5, 0, 10), 0, 0)
        };

        Templates.layoutGridBag(p, comps, gbcs);

        return p;
    }

    private JPanel makeCLDPanel() {

        JPanel p = new JPanel();

        XYSeries xySeries = new XYSeries("");
        //just some dummy data, replaced with the real thing when used with the interface
        xySeries.add(0, 5.0);
        xySeries.add(1, 8.0);
        xySeries.add(2, 0.0);

        chart = ChartFactory.createXYLineChart(/*TITLE*/null, X_TITLE, Y_TITLE, new XYSeriesCollection(xySeries),
                                       PlotOrientation.VERTICAL, false, true, false);
        customizeChart(chart);
        ChartPanel chartPanel = new ChartPanel(chart);

        showMovieCheckBox = Templates.makeCheckBox("show seal animation", false, true);
        showMovieCheckBox.setToolTipText("Open the seal deformation movie. The movie will be automatically updated after each run.");

        JLabel contactLabel = Templates.makeLabel("contact length (mm):");
        contactLabel.setToolTipText("Contact length of seal when in fully deformed position.");
        contactLength = Templates.makeTextField("");
        contactLength.setEditable(false);

        JComponent[] comps = {showMovieCheckBox, contactLabel, contactLength,
                              chartPanel};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 10), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 10), 100, 0),

            new GridBagConstraints(0, 2, 2, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 5, 0, 5), 0, 0),
        };
        Templates.layoutGridBag(p, comps, gbcs);

        return p;
    }

    public void setInterface(ModelInterfaceBase iface) {
        CustomGui.connectStringOrNumberTextField(iface, "Final Contact Length", contactLength);
        CustomGui.connectMatrixElementXYFreeChart(iface, "CLD Matrix", chart);
        CustomGui.connectFileOpenCheckBox(iface, "Deformation and Stress Avi",showMovieCheckBox);
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
		JFrame f = new JFrame("Analysis result card");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new FeaResultCard());
		f.show();
	}
}
