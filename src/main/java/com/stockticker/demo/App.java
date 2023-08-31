package com.stockticker.demo;



import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;



public class App {
	
	
	private static String API_KEY = "";
	
	private static StringBuilder ids = new StringBuilder();
	
	private static StringBuilder trend = new StringBuilder();
	
	private static StringBuilder selectedText = new StringBuilder();
	
	private static StringBuilder intervalText = new StringBuilder();
	
	private static RestTemplate restTemplate = new RestTemplate();
	
	private static HttpHeaders headers;
	
	private static List<String> idsList = new ArrayList<>();
	
	private static List<String> selectedTextList = new ArrayList<>();
	
	private static Map<String, String> coinSearch = new HashMap<>(); 
	
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
        
        // custom api key
        
        JLabel apiLabel = new JLabel("Enter API key:");
        JTextField apiField = new JTextField(20);
        JButton submitKey = new JButton("Submit");
        
        
        
        
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
        
        
        panel.add(apiLabel);
        panel.add(submitKey);
        panel.add(apiField);
        panel.add(inputLabel);
        panel.add(searchButton);
        panel.add(inputField);
        panel.add(startMonitoring);
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
        
        setEnabled(false, inputLabel, searchButton, inputField, comboBox, scrollPane, 
				trendLabel, undoButton, trendBox, defaultInterval, intervalField, intervalSubmit);

        submitKey.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				API_KEY = apiField.getText();
				apiField.setText("");
				
				if (isValidKey(API_KEY)) {
					
					JOptionPane.showInternalMessageDialog(null, "Valid API key");
					
					setEnabled(true, inputLabel, searchButton, inputField, comboBox, scrollPane, 
							trendLabel, undoButton, trendBox, defaultInterval, intervalField, intervalSubmit);
					
					apiLabel.setVisible(false);
					apiField.setVisible(false);
					submitKey.setVisible(false);
					
				} else {
					
					JOptionPane.showInternalMessageDialog(null, "Invalid API key");
					
					setEnabled(false, inputLabel, searchButton, inputField, comboBox, scrollPane, 
							trendLabel, undoButton, trendBox, defaultInterval, intervalField, intervalSubmit);
					
				}
				
			}
		});
        
        
        CoinMonitor coinMonitor = new CoinMonitor(headers, restTemplate, searchButton, comboBox, inputField, selected, startMonitoring,
				undoButton, trendBox, frame, edit, intervalSubmit, intervalField, intervalText, trend, ids, selectedText,
				idsList, selectedTextList, coinSearch, priceCheck, alarmFrameCount);
        
        coinMonitor.run();
         
        
    }

    
    public static void populateTrendBox(JComboBox<String> box) {
    	
    	for (String trend : trends) {
    		
    		box.addItem(trend);
    		
    	}
    	
    }
    
    public static boolean isValidKey(String apiKey) {
    	
    	
    	if (apiKey.length() <= 0) {
    		
    		return false;
    	
    	}
    	
		HttpHeaders headers = new HttpHeaders();
		
		headers.set("Content-Type", "application/json");
        headers.set("x-access-token", apiKey);
        
    	
    	HttpEntity<String> entity = new HttpEntity<>(headers);
    	
    	
    	try {
    		
            ResponseEntity<String> response = restTemplate.exchange(
            		"https://api.coinranking.com/v2/search-suggestions?query=bitcoin", 
            		HttpMethod.GET, entity, String.class);
                 
		} catch (Exception e) {
			
			return false;
			
		}

        
        
        
        return true;
    	
    }
    
    public static void setEnabled(boolean enabled, JLabel inputLabel, JButton searchButton, JTextField inputField, JComboBox<String> comboBox, JScrollPane scrollPane,
    		JLabel trendLabel, JButton undoButton, JComboBox<String> trendBox, JLabel interval, JTextField intervalField, JButton intervalSubmit) {
    	

    	inputLabel.setEnabled(enabled);
    	searchButton.setEnabled(enabled);
    	inputField.setEnabled(enabled);
    	comboBox.setEnabled(enabled);
    	scrollPane.setEnabled(enabled);
    	trendLabel.setEnabled(enabled);
    	undoButton.setEnabled(enabled);
    	trendBox.setEnabled(enabled);
    	interval.setEnabled(enabled);
    	intervalField.setEnabled(enabled);
    	intervalSubmit.setEnabled(enabled);
    	
    }
}



































