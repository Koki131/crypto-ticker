package com.stockticker.demo;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.Border;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.stockticker.alarm.Alarm;
import com.stockticker.connection.HibernateUtil;
import com.stockticker.dao.SearchDAO;
import com.stockticker.dao.SearchDAOImpl;
import com.stockticker.entity.Search;
import com.stockticker.model.Coin;
import com.stockticker.model.CoinMarketData;


public class App {
	
	
	private static final String API_KEY = "YOUR COINRANKING API KEY";
	
	private static StringBuilder ids = new StringBuilder();
	
	private static StringBuilder trend = new StringBuilder();
	
	private static StringBuilder selectedText = new StringBuilder();
	
	private static StringBuilder intervalText = new StringBuilder();
	
	private static RestTemplate restTemplate = new RestTemplate();
	
	private static HttpHeaders headers;
	
	private static List<String> idsList = new ArrayList<>();
	
	private static List<String> selectedTextList = new ArrayList<>();
	
	private static Map<String, String> coinSearch = new HashMap<>(); 
	
	private static SearchDAO searchDao = new SearchDAOImpl();
	
	private static SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
	
	private static final String[] trends = {"1h", "3h", "12h", "24h", "7d", "30d", "3m", "1y", "3y", "5y"};
	
	private static int alarmFrameCount = 0;
	
	private static Map<String, Double[]> priceCheck = new HashMap<>();
	
	
	
	public App() {
		 
		
		headers = new HttpHeaders();
	
		headers.set("Content-Type", "application/json");
        headers.set("x-access-token", API_KEY);
        

	}
	
    public static void main( String[] args ) throws InterruptedException {
        
    	
    	
    	JFrame frame = new JFrame("Coin Price Monitor");
        frame.setSize(300, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        
        JPanel intervalPanel = new JPanel();
        JPanel intervalTextPanel = new JPanel();
        
        JLabel inputLabel = new JLabel("Enter Coin Name:");
        JTextField inputField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        
        JButton startMonitoring = new JButton("Start Monitoring");
        
        JButton edit = new JButton("Edit");
        edit.setMaximumSize(new Dimension(10, 20));
       	edit.setPreferredSize(new Dimension(10, 20));
       	edit.setMinimumSize(new Dimension(10, 20));
        
        JTextArea selected = new JTextArea();
        selected.setEditable(false);
        selected.setLineWrap(true);
        selected.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(250, 80));
        scrollPane.setViewportView(selected);

        
        JLabel trendLabel = new JLabel("Trend:");
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.setEditable(false);
        comboBox.addItem("");
        

        
        JComboBox<String> trendBox = new JComboBox<>();
        comboBox.setEditable(false);
        
        populateTrendBox(trendBox);
        
        JButton undoButton = new JButton("Undo");
        
        JLabel interval = new JLabel("Interval (sec):");
        JTextField intervalField = new JTextField(5);
        JButton intervalSubmit = new JButton("Submit");
        JLabel defaultInterval = new JLabel("Default 60 seconds");
        
        
        
        panel.add(inputLabel);
        panel.add(inputField);
        panel.add(startMonitoring);
        panel.add(searchButton);
        panel.add(comboBox);
        panel.add(scrollPane);
        panel.add(trendLabel);
        panel.add(undoButton);
        panel.add(trendBox);
        intervalPanel.add(interval);
        intervalPanel.add(intervalField);
        intervalPanel.add(intervalSubmit);
        intervalTextPanel.add(defaultInterval);
        panel.add(intervalPanel);
        panel.add(intervalTextPanel);
        frame.add(panel);
        frame.setVisible(true);
        
        
        CoinMonitor coinMonitor = new CoinMonitor(searchDao, sessionFactory, headers, restTemplate, searchButton, comboBox, inputField, selected, startMonitoring,
				undoButton, trendBox, frame, edit, intervalSubmit, intervalField, intervalText, trend, ids, selectedText,
				idsList, selectedTextList, coinSearch, priceCheck, alarmFrameCount);
        
        coinMonitor.run();
         
        
    }

    
    public static void populateTrendBox(JComboBox<String> box) {
    	
    	for (String trend : trends) {
    		
    		box.addItem(trend);
    		
    	}
    	
    }
}



































