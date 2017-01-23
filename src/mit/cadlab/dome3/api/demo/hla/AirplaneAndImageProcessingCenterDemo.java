package mit.cadlab.dome3.api.demo.hla;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.XYDataset;
import org.jfree.data.time.*;

import javax.swing.*;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;

public class AirplaneAndImageProcessingCenterDemo {
    private AirplaneFederate airplane1Fed;
    private AirplaneFederate airplane2Fed;
    private ImageProcessorFederate imgProcFed;
    private String sessionId = null;
    public boolean STRICTLY_PLOT_LOCAL_TIME = false;

    private JTextArea airplane1LogTextArea;
    private JTextArea airplane2LogTextArea;
    private JTextArea imgProcLogTextArea;

    JLabel airplane1LogLabel;
    JLabel airplane2LogLabel;
    JLabel imgProcLogLabel;

    JLabel tickInRealTime1Lb;
    JLabel tickInRealTime2Lb;
    boolean hasFederateStarted = false;

    TimeSeries airplane1Ts;
    TimeSeries airplane2Ts;
    TimeSeries imgProcTs;
    TimeSeriesCollection dataset;
    JFreeChart chart;

    final String IMGPROC_LABEL_TEXT = "Image Process Ctr (EXTEND Ghost)";
    final String AIRPLANE1_LABEL_TEXT = "Airplane-1 (HLA Federate)";
    final String AIRPLANE2_LABEL_TEXT = "Airplane-2 (HLA Federate)";

    final Color INIT_BG = new Color(0xff,0xff,0xff);
//    final Color WAITING_BG = new Color(0xee,0xee,0xee);
//    final Color RUNNING_BG = new Color(0xff,0xff,0xa8);
    final Color WAITING_BG = new Color(0xe0,0xe0,0xe0);
    final Color RUNNING_BG = new Color(0xf0,0xf0,0xf0);

    final Color COMPLETE_BG = new Color(0xdd,0xdd, 0xff);

    final long dispUpdatePeriod = 300;

    public static void main(String[] args) {
        AirplaneAndImageProcessingCenterDemo controller = new AirplaneAndImageProcessingCenterDemo("0000", "C:/dome3/out/WarField.xml");
//        double[] pictureTimes = new double[] { 3.5, 5, 8.5, 10, 12.5, 15, 16.5, 19, 21, 22.5, 24, 25.5, 27 };
//        controller.run(pictureTimes, 2000, 5);
    }

    public AirplaneAndImageProcessingCenterDemo(String sessionId, String filePath) {
        airplane1Fed = new AirplaneFederate(AirplaneFederate.AIRPLANE_1_ID, sessionId, filePath);
        airplane2Fed = new AirplaneFederate(AirplaneFederate.AIRPLANE_2_ID, sessionId, filePath);
        imgProcFed = new ImageProcessorFederate(sessionId, filePath);

        /* config airplane federates */
        double[] airplane1PicTimes = new double[] { 2.5, 3, 4.5, 6, 7.5, 8, 11.5, 13, 14, 16.5, 18.5, 19.0, 22.5 };
        double[] airplane2PicTimes = new double[] { 3.5, 6, 8.5, 10, 14.5, 16, 19.5, 22.5, 24.5, 26, 28, 29.5, 32.5 };
        int targetCount = 3;
        airplane1Fed.setPictureTakingSchedule(airplane1PicTimes);
        airplane2Fed.setPictureTakingSchedule(airplane2PicTimes);
        airplane1Fed.setTargetCount(targetCount);
        airplane2Fed.setTargetCount(targetCount);
        airplane1Fed.setTickInRealTime(1000);
        airplane2Fed.setTickInRealTime(5000);

        createFrame();
//        caller = new ExtendSimPluginCaller("ExtendSimPlugin");
//        caller.setDebug(false);

//        try {
//            caller.openModel(filePath);
//        } catch (Exception e) { System.out.println("fail to open the model"); }
    }

    public void addDataPoint() {
        Calendar c = Calendar.getInstance();
        Date now = c.getTime();

        if (! STRICTLY_PLOT_LOCAL_TIME && getAirplane1Federate().isPaused()) {
            airplane1Ts.add(new Millisecond(now), getAirplane1Federate().getNextLocalTime());
        } else {
            airplane1Ts.add(new Millisecond(now), getAirplane1Federate().getLocalTime());
        }

        if (! STRICTLY_PLOT_LOCAL_TIME && getAirplane2Federate().isPaused()) {
            airplane2Ts.add(new Millisecond(now), getAirplane2Federate().getNextLocalTime());
        } else {
            airplane2Ts.add(new Millisecond(now), getAirplane2Federate().getLocalTime());
        }

        if (! STRICTLY_PLOT_LOCAL_TIME && getImageProcessorFed().isPaused()) {
            imgProcTs.add(new Millisecond(now), getImageProcessorFed().getNextLocalTime());
        } else {
            imgProcTs.add(new Millisecond(now), getImageProcessorFed().getLocalTime());
        }

        adjustMaximumDate(now);
    }

    public void adjustMaximumDate(Date lastestDate) {
        if (chart == null) {
            return;
        }

        DateAxis domainAxis = (DateAxis) ((XYPlot) chart.getPlot()).getDomainAxis();
        if (domainAxis.getMaximumDate().before(lastestDate)) {
            Calendar c = Calendar.getInstance();
            c.setTime(domainAxis.getMaximumDate());
            c.add(Calendar.SECOND, 10);
            domainAxis.setMaximumDate(c.getTime());
        }
    }

    public void adjustMinimumDate() {
        DateAxis domainAxis = (DateAxis) ((XYPlot) chart.getPlot()).getDomainAxis();
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c2.add(Calendar.SECOND, 100);
        domainAxis.setRange(c1.getTime(), c2.getTime());
    }


    public void initDataset() {
        airplane1Ts = new TimeSeries(AirplaneFederate.AIRPLANE_1_ID, Millisecond.class);
        airplane2Ts = new TimeSeries(AirplaneFederate.AIRPLANE_2_ID, Millisecond.class);
        imgProcTs = new TimeSeries(ImageProcessorFederate.IMGPROC_ID, Millisecond.class);

        dataset = new TimeSeriesCollection();
        dataset.addSeries(airplane1Ts);
        dataset.addSeries(airplane2Ts);
        dataset.addSeries(imgProcTs);
    }

    public ChartPanel createChartPanel() {
        initDataset();

		chart = ChartFactory.createTimeSeriesChart(null, "real-world time [hh:mm:ss]", "logical time (t)", dataset, true, false, false);

		chart.setBackgroundPaint(Color.white);

		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.WHITE);
		plot.setDomainGridlinePaint(Color.GRAY);
		plot.setRangeGridlinePaint(Color.GRAY);
//		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		plot.setDomainCrosshairVisible(false);
		plot.setRangeCrosshairVisible(false);
        plot.setNoDataMessage("Press 'Start' Button for Time-Synchronized Messaging Demo");
//        Color SERIES_0_COLOR = new Color(0x64,0x8e,0x15);
//        Color SERIES_1_COLOR = new Color(0x64,0xd2,0x22);
//        Color SERIES_2_COLOR = new Color(0x38,0x47,0xcf);
//        plot.getRenderer().setSeriesPaint(0, SERIES_0_COLOR);
//        plot.getRenderer().setSeriesPaint(1, SERIES_1_COLOR);
//        plot.getRenderer().setSeriesPaint(2, SERIES_2_COLOR);
//        plot.getRenderer().setBaseStroke(new BasicStroke(2));
////		turnRenderer.setSeriesOutlineStroke(series, new BasicStroke(1.0f), true);
////		turnRenderer.setSeriesShapesFilled(series, true);
//		turnRenderer.setBaseItemLabelsVisible(true);
//		turnRenderer.setBaseItemLabelGenerator(new XYItemLabelGenerator() {
//			public String generateLabel(XYDataset dataset, int series, int item) {
//				try {
//					if (series == curMouseOverSeries && item == curMouseOverItem) {
//						Date mouseOverDate = new Date((long) dataset.getXValue(series, item));
//						String dateStr = shortLabelDateFormat.format(mouseOverDate);
//						return dateStr;
//					} else {
//						Date curDate = new Date((long) dataset.getXValue(series, item));
//						Date mouseOverDate = new Date((long) dataset.getXValue(curMouseOverSeries, curMouseOverItem));
//						long diff = (curDate.getTime() - mouseOverDate.getTime()) / (24 * 60 * 60 * 1000);
//						if (Math.abs(diff) <= boxingCriteria) {
//							String dateStr = shortLabelDateFormat.format(curDate);
//							return dateStr + " (" + diff + ")";
////							return "(" + diff + ")";
//						}
//					}
//					return null;
//				} catch (IndexOutOfBoundsException e) { return null; }
//			}
//		});

//		plot.setRenderer(turnRenderer);

		DateAxis axis = (DateAxis) plot.getDomainAxis();
		axis.setDateFormatOverride(new SimpleDateFormat("h:mm:ss"));

		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setAutoRangeMinimumSize(36);
        rangeAxis.setLowerBound(0);

        DateAxis domainAxis = (DateAxis) plot.getDomainAxis();
        Calendar c = Calendar.getInstance();
        Date startTime = c.getTime();
        c.add(Calendar.SECOND, 100);
        Date endTime = c.getTime();
//        domainAxis.setAutoRangeMinimumSize(endTime.getTime() - startTime.getTime());
//        domainAxis.setLowerBound(startTime.getTime());
//        domainAxis.setAutoRangeMinimumSize(1000 * 20);
        domainAxis.setRange(startTime, endTime);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMaximumDrawWidth(1440);
        chartPanel.setMaximumDrawHeight(900);
        chartPanel.setPreferredSize(new Dimension(1000, 320));
        return chartPanel;
//        chart.setAntiAlias(true);
    }

    public String getSessionId() {
        return sessionId;
    }

    public AirplaneFederate getAirplane1Federate() {
        return airplane1Fed;
    }

    public AirplaneFederate getAirplane2Federate() {
        return airplane2Fed;
    }

    public ImageProcessorFederate getImageProcessorFed() {
        return imgProcFed;
    }

    public void updateTickInRealTimeLabel() {
        String msg1 = " (increases every " + airplane1Fed.getTickInRealTime() + " ms)";
        tickInRealTime1Lb.setText(msg1);
        String msg2 = " (increases every " + airplane2Fed.getTickInRealTime() + " ms)";
        tickInRealTime2Lb.setText(msg2);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JPanel airplaneControlPanel = new JPanel();
        airplaneControlPanel.setLayout(new GridLayout(2, 1));

        final JButton startBt = new JButton("Start");
        startBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                hasFederateStarted = true;
                startAllFederates();
                startBt.setText("Start");
                startBt.setEnabled(false);
            }
        });

        final JButton quitBt = new JButton("Quit");
        quitBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });

        final JCheckBox plotTimeAwaitedCb = new JCheckBox("plot time being waited for");
        plotTimeAwaitedCb.setSelected(true);
        plotTimeAwaitedCb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                STRICTLY_PLOT_LOCAL_TIME = ! plotTimeAwaitedCb.isSelected();
            }
        });

        JLabel airplane1ClockLb = new JLabel("Airplane-1 clock: ");
        JButton faster1Bt = new JButton("faster");
        faster1Bt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (airplane1Fed.getTickInRealTime() > 500) {
                    airplane1Fed.setTickInRealTime(airplane1Fed.getTickInRealTime() - 500);
                    updateTickInRealTimeLabel();
                }
            }
        });

        JButton slower1Bt = new JButton("slower");
        slower1Bt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                airplane1Fed.setTickInRealTime(airplane1Fed.getTickInRealTime() + 500);
                updateTickInRealTimeLabel();
            }
        });

        JLabel airplane2ClockLb = new JLabel("Airplane-2 clock: ");
        JButton faster2Bt = new JButton("faster");
        faster2Bt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (airplane2Fed.getTickInRealTime() > 500) {
                    airplane2Fed.setTickInRealTime(airplane2Fed.getTickInRealTime() - 500);
                    updateTickInRealTimeLabel();
                }
            }
        });

        JButton slower2Bt = new JButton("slower");
        slower2Bt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                airplane2Fed.setTickInRealTime(airplane2Fed.getTickInRealTime() + 500);
                updateTickInRealTimeLabel();
            }
        });

        tickInRealTime1Lb = new JLabel();
        tickInRealTime2Lb = new JLabel();

//        tickInRealTime1Lb.setPreferredSize(new Dimension(300, 20));
//        tickInRealTime2Lb.setPreferredSize(new Dimension(300, 20));

        updateTickInRealTimeLabel();

        JPanel airplane1ButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel airplane2ButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        airplaneControlPanel.add(airplane1ButtonPanel);
        airplaneControlPanel.add(airplane2ButtonPanel);

        airplane1ButtonPanel.add(airplane1ClockLb);
        airplane1ButtonPanel.add(slower1Bt);
        airplane1ButtonPanel.add(faster1Bt);
        airplane1ButtonPanel.add(tickInRealTime1Lb);

        airplane2ButtonPanel.add(airplane2ClockLb);
        airplane2ButtonPanel.add(slower2Bt);
        airplane2ButtonPanel.add(faster2Bt);
        airplane2ButtonPanel.add(tickInRealTime2Lb);

        buttonPanel.add(Box.createRigidArea(new Dimension(10, 20)));
        buttonPanel.add(new JLabel("Demo:"));
        buttonPanel.add(startBt);
        buttonPanel.add(quitBt);

        buttonPanel.add(Box.createRigidArea(new Dimension(30, 20)));
        buttonPanel.add(airplaneControlPanel);

        buttonPanel.add(Box.createRigidArea(new Dimension(20, 20)));
        buttonPanel.add(new JLabel("Graph option:"));
        buttonPanel.add(plotTimeAwaitedCb);
        return buttonPanel;
    }

    public JPanel createLogPanel() {
        airplane1LogLabel = new JLabel("<html>" + AIRPLANE1_LABEL_TEXT + " initialized </html>");
        airplane2LogLabel = new JLabel("<html>" + AIRPLANE2_LABEL_TEXT + " initialized </html>");
        imgProcLogLabel = new JLabel("<html>" + IMGPROC_LABEL_TEXT + " initialized </html>");
        imgProcLogLabel = new JLabel("<html>" + IMGPROC_LABEL_TEXT + " initialized </html>");
        airplane1LogLabel.setPreferredSize(new Dimension(200, 30));
        airplane2LogLabel.setPreferredSize(new Dimension(200, 30));
        imgProcLogLabel.setPreferredSize(new Dimension(200, 30));

        airplane1LogTextArea = new JTextArea();
        airplane2LogTextArea = new JTextArea();
        imgProcLogTextArea = new JTextArea();

        JPanel logPanel = new JPanel(new GridLayout(1, 3));
        JPanel airplane1LogPanel = new JPanel();
        airplane1LogPanel.setLayout(new BoxLayout(airplane1LogPanel, BoxLayout.Y_AXIS));
        airplane1LogLabel.setAlignmentX(0.0f);
        airplane1LogPanel.add(airplane1LogLabel);
        JScrollPane airplane1LogScrollPane = new JScrollPane(airplane1LogTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        airplane1LogScrollPane.setAlignmentX(0.0f);
        airplane1LogScrollPane.setPreferredSize(new Dimension(380, 200));
        airplane1LogPanel.add(airplane1LogScrollPane);
        logPanel.add(airplane1LogPanel);

        JPanel airplane2LogPanel = new JPanel();
        airplane2LogPanel.setLayout(new BoxLayout(airplane2LogPanel, BoxLayout.Y_AXIS));
        airplane2LogLabel.setAlignmentX(0.0f);
        airplane2LogPanel.add(airplane2LogLabel);
        JScrollPane airplane2LogScrollPane = new JScrollPane(airplane2LogTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        airplane2LogScrollPane.setAlignmentX(0.0f);
        airplane2LogScrollPane.setPreferredSize(new Dimension(380, 200));
        airplane2LogPanel.add(airplane2LogScrollPane);
        logPanel.add(airplane2LogPanel);

        JPanel imgProcPanel = new JPanel();
        imgProcPanel.setLayout(new BoxLayout(imgProcPanel, BoxLayout.Y_AXIS));
        imgProcLogLabel.setAlignmentX(0.0f);
        imgProcPanel.add(imgProcLogLabel);
        JScrollPane imgProcLogScrollPane = new JScrollPane(imgProcLogTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        imgProcLogScrollPane.setAlignmentX(0.0f);
        imgProcLogScrollPane.setPreferredSize(new Dimension(380, 200));
        imgProcPanel.add(imgProcLogScrollPane);
        logPanel.add(imgProcPanel);
        return logPanel;
    }

    public void createFrame() {
        JFrame controlFrame = new JFrame("Time-Synchronized Messaging Demo between EXTEND and HLA");
        controlFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel logPanel = createLogPanel();
        JPanel buttonPanel = createButtonPanel();
        ChartPanel chartPanel = createChartPanel();

        controlFrame.getContentPane().add(buttonPanel, BorderLayout.NORTH);
        controlFrame.getContentPane().add(logPanel, BorderLayout.CENTER);
        controlFrame.getContentPane().add(chartPanel, BorderLayout.SOUTH);

        controlFrame.pack();
        controlFrame.show();

        startDisplayTimer();
    }

    private void startDisplayTimer() {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                airplane1LogTextArea.setText(generateAirplane1Display());
                airplane1LogTextArea.setCaretPosition(airplane1LogTextArea.getDocument().getLength());
                if (! hasFederateStarted) {
                    airplane1LogTextArea.setBackground(INIT_BG);
                } else if (getAirplane1Federate().isPaused()) {
                    airplane1LogTextArea.setBackground(WAITING_BG);
                    airplane1LogLabel.setText("<html>" + AIRPLANE1_LABEL_TEXT + " <font color='red'>WAITING</font> <font color='black'>to advance to t=" + getAirplane1Federate().getNextLocalTime() + "</font></html>");
                } else {
                    airplane1LogTextArea.setBackground(RUNNING_BG);
                    airplane1LogLabel.setText("<html>" + AIRPLANE1_LABEL_TEXT + " <font color='green'>RUNNING</font> <font color='black'>t=" + getAirplane1Federate().getLocalTime() + " ==> "+ getAirplane1Federate().getNextLocalTime() + "</font></html>");
                }

                airplane2LogTextArea.setText(generateAirplane2Display());
                airplane2LogTextArea.setCaretPosition(airplane2LogTextArea.getDocument().getLength());
                if (! hasFederateStarted) {
                    airplane1LogTextArea.setBackground(INIT_BG);
                } else if (getAirplane2Federate().isPaused()) {
                    airplane2LogTextArea.setBackground(WAITING_BG);
                    airplane2LogLabel.setText("<html>" + AIRPLANE2_LABEL_TEXT + " <font color='red'>WAITING</font> <font color='black'>to advance to t=" + getAirplane2Federate().getNextLocalTime() + "</font></html>");
                } else {
                    airplane2LogTextArea.setBackground(RUNNING_BG);
                    airplane2LogLabel.setText("<html>" + AIRPLANE2_LABEL_TEXT + " <font color='green'>RUNNING</font> <font color='black'>from t=" + getAirplane2Federate().getLocalTime() + " ==> "+ getAirplane2Federate().getNextLocalTime() + "</font></html>");
                }

                imgProcLogTextArea.setText(generateImageProcessorDisplay());
                imgProcLogTextArea.setCaretPosition(imgProcLogTextArea.getDocument().getLength());
                if (! hasFederateStarted) {
                    airplane1LogTextArea.setBackground(INIT_BG);
                } else if (getImageProcessorFed().isImageProcessorPaused()) {
                    imgProcLogTextArea.setBackground(WAITING_BG);
                    imgProcLogLabel.setText("<html>" + IMGPROC_LABEL_TEXT + " <font color='red'>WAITING</font> <font color='black'>to advance to t=" + getImageProcessorFed().getNextLocalTime() + "</font></html>");
                } else {
                    imgProcLogTextArea.setBackground(RUNNING_BG);
                    imgProcLogLabel.setText("<html>" + IMGPROC_LABEL_TEXT + " <font color='green'>RUNNING</font> <font color='black'>from t=" + getImageProcessorFed().getLocalTime() + " ==> "+ getImageProcessorFed().getNextLocalTime() + "</font></html>");
                }

                if (airplane1Fed.isFinished() && airplane2Fed.isFinished()) {
                    timer.cancel();

                    imgProcLogTextArea.setBackground(COMPLETE_BG);
                    airplane1LogTextArea.setBackground(COMPLETE_BG);
                    airplane2LogTextArea.setBackground(COMPLETE_BG);

                    airplane1LogLabel.setText("<html>" + AIRPLANE1_LABEL_TEXT + " - <font color='blue'>MISSION COMPLETE at t = " + getAirplane1Federate().getLocalTime() + ".</font></html>");
                    airplane2LogLabel.setText("<html>" + AIRPLANE2_LABEL_TEXT + " - <font color='blue'>MISSION COMPLETE at t = " + getAirplane2Federate().getLocalTime() + ".</font></html>");
                    imgProcLogLabel.setText("<html>" + IMGPROC_LABEL_TEXT + " - <font color='blue'>MISSION COMPLETE at t = " + getImageProcessorFed().getLocalTime() + ".</font></html>");
                }
                if (hasFederateStarted) {
                    addDataPoint();
                }


            }
        }, 0, dispUpdatePeriod);
    }

    public void startAllFederates() {
        adjustMinimumDate();

        Thread t1 = new Thread() {
            public void run() {
                AirplaneAndImageProcessingCenterDemo.this.getAirplane1Federate().run();
            }
        };
        t1.start();

        Thread t2 = new Thread() {
            public void run() {
                AirplaneAndImageProcessingCenterDemo.this.getAirplane2Federate().run();
            }
        };
        t2.start();

        Thread t3 = new Thread() {
            public void run() {
                AirplaneAndImageProcessingCenterDemo.this.getImageProcessorFed().run();
            }
        };
        t3.start();

//        try{
//            t1.join();
//            t2.join();
//        } catch (Exception e) { System.out.println("run() finishes."); }
    }

    public void close() {
        this.getAirplane1Federate().close();
        this.getAirplane2Federate().close();
        this.getImageProcessorFed().close();

//        try {
//            caller.closeModel(filePath);
//        } catch (Exception e) { System.out.println("fail to close the model"); }
//        caller.finishUsingThisCaller();
    }

    public String generateImageProcessorDisplay() {
        StringBuffer sb = new StringBuffer();
        List eventLog = getImageProcessorFed().getEventLog();
        for (int i = 0; i < eventLog.size(); i++) {
            sb.append(eventLog.get(i) + "\n");
        }
        return sb.toString();
    }

    public String generateAirplane1Display() {
        StringBuffer sb = new StringBuffer();
        List eventLog = getAirplane1Federate().getEventLog();
        for (int i = 0; i < eventLog.size(); i++) {
            sb.append(eventLog.get(i) + "\n");
        }
        return sb.toString();
    }

    public String generateAirplane2Display() {
        StringBuffer sb = new StringBuffer();
        List eventLog = getAirplane2Federate().getEventLog();
        for (int i = 0; i < eventLog.size(); i++) {
            sb.append(eventLog.get(i) + "\n");
        }
        return sb.toString();
    }
}
