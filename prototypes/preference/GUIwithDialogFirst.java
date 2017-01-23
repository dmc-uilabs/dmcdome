import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.XYSeries;
import org.jfree.data.XYDataset;
import org.jfree.data.XYSeriesCollection;
import org.jfree.chart.event.AxisChangeEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;
import javax.swing.UIManager;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import java.awt.*;
import java.awt.event.*;

//  GUI with Dialog prior to displaying chart

	public class GUIwithDialogFirst implements ActionListener {
		JFrame outerFrame;
		JPanel inputPanel, rightPanel,leftPanel;
		JTextField tempmax, tempmin;
		JLabel maxLabel, minLabel, min, max;
		JButton convertTemp;
		double minDbl, maxDbl;
		XYSeries series = new XYSeries("Preference Value");

		double d = 0;
		double d2 = 0;

		Object[][] data = {
			{new Double(0.0), new Double(0.0)},
			{new Double(0.0), new Double(0.0)},
			{new Double(0.0), new Double(0.0)},
			{new Double(0.0), new Double(0.0)},
			{new Double(0.0), new Double(0.0)}};

		String[] columnNames = {"Attribute Value",
		                        "Preference Value"};
		final JTable table = new JTable(data, columnNames);


		public GUIwithDialogFirst() {
			// Create the frame and container.
			outerFrame = new JFrame("Preference Input");
			outerFrame.setSize(2000, 2000);
			inputPanel = new JPanel();
			rightPanel = new JPanel();
			leftPanel = new JPanel();
			inputPanel.setLayout(new BorderLayout());

			// Add the widgets.
			addWidgets();

			// Add the panel to the frame.
			outerFrame.getContentPane().add(inputPanel, BorderLayout.CENTER);
			outerFrame.getContentPane().add(leftPanel, BorderLayout.WEST);
			outerFrame.getContentPane().add(rightPanel, BorderLayout.EAST);

			// Exit when the window is closed.
			outerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			outerFrame.pack();
			outerFrame.setVisible(true);
		}

		// Create and add the widgets for converter.
		private void addWidgets() {
			// Create widgets.
			tempmax = new JTextField(8);
			tempmin = new JTextField(8);
			maxLabel = new JLabel("maximum", SwingConstants.LEFT);
			convertTemp = new JButton("OK");
			minLabel = new JLabel("minimum", SwingConstants.LEFT);

			// Listen to events from Convert button.
			convertTemp.addActionListener(this);

			//Make input Pane
			JPanel prefInputPanel=new JPanel();
			prefInputPanel.add(new JLabel("Preference Value", SwingConstants.LEFT));

//add Table with Listener for Preference values

//			final JTable table = new JTable(data, columnNames);
			table.setPreferredScrollableViewportSize(new Dimension(500, 70));

			table.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
//					loadData(table);
				}
			});
			//Create the scroll pane and add the table to it.
			JScrollPane scrollPane = new JScrollPane(table);
			//Add the scroll pane to this window.
			inputPanel.add(scrollPane, BorderLayout.CENTER);
///////

			// Add widgets to container.
			inputPanel.add(convertTemp,BorderLayout.NORTH);
			maxLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
			minLabel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

			rightPanel.add(maxLabel, BorderLayout.CENTER);
			leftPanel.add(minLabel, BorderLayout.CENTER);

			rightPanel.add(tempmax, BorderLayout.CENTER);
			leftPanel.add(tempmin, BorderLayout.CENTER);
		}

//   Load the data table
		private void loadData(JTable table) {
			int numRows = table.getRowCount();
			javax.swing.table.TableModel model = table.getModel();

			for (int i = 0; i < numRows; i++) {
				Object obj = model.getValueAt(i, 0);
				Object obj2 = model.getValueAt(i, 1);

				if (obj instanceof String) {
					d = Double.parseDouble((String) obj);
				} else
					System.out.println(obj.getClass());

				if (obj2 instanceof String) {
					d2 = Double.parseDouble((String) obj2);

				} else
					System.out.println(obj2.getClass());

				series.add(d, d2);
				System.out.println(d + " " + d2);
			}
		}
		//ToDo   If the last item is typed into the table, but the cursor isn't moved to anther cell in the table,
		//       that cell will be ignored.  Need to prompt user or fix this.
		// OK button pressed causes chart to be drawn:
		public void actionPerformed(ActionEvent event) {

			minLabel.setText(Double.parseDouble(tempmin.getText()) + " minimum");
			minDbl= Double.parseDouble(tempmin.getText());

			maxLabel.setText(Double.parseDouble(tempmin.getText()) + " maximum");
			maxDbl = Double.parseDouble(tempmax.getText());

			loadData(table);
			//create dataset, draw the chart
			createDataset();
		}

		// create a dataset, draw chart
		public void createDataset() {

			XYDataset xyDataset = new XYSeriesCollection(series);

			JFreeChart chart = ChartFactory.createScatterPlot
			        ("Preference", // Title
			                "Input", // X-Axis label
			                "Preference Value", // Y-Axis label
			                xyDataset, // Dataset
			                PlotOrientation.VERTICAL,
			                true, // Show legend
			                true, // Show tooltips
			                false      // URLs
			        );
			XYPlot plot = chart.getXYPlot();
			NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
			NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
			rangeAxis.setLowerBound(0.0);
			rangeAxis.setUpperBound(1.0);
			domainAxis.setLowerBound(minDbl);
			domainAxis.setUpperBound(maxDbl);

			rangeAxis.setTickUnit(new NumberTickUnit(1.0));
			chart.setBackgroundPaint(new Color(0xBBBBDD));

			ChartPanel panel_1 = new ChartPanel(chart);

			outerFrame.getContentPane().add(panel_1, BorderLayout.NORTH);
			outerFrame.pack();
			outerFrame.setVisible(true);
		}

		public static void main(String[] args) {
			try {
				UIManager.setLookAndFeel(
				        UIManager.getCrossPlatformLookAndFeelClassName());
			} catch (Exception e) {
			}

			GUIwithDialogFirst converter = new GUIwithDialogFirst();
		}
	}