package com.stockticker.demo;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;


public class CoinMonitor {

	private StringBuilder selectedText;	
	private List<String> idsList;
	private List<String> selectedTextList;
	private Map<String, String> coinSearch;
	private JButton searchButton;
	private JComboBox<String> comboBox;
	private JTextField inputField;
	private JTextArea selected;
	private JButton startMonitoring;
	private JButton undoButton;
	private JComboBox<String> trendBox;
	private JFrame frame;
	private JButton edit;
	private JButton intervalSubmit;
	private JTextField intervalField;
	
	protected StringBuilder intervalText;
	protected StringBuilder trend;
	protected HttpHeaders headers;
	protected RestTemplate restTemplate;
	protected StringBuilder ids;
	protected Map<String, Double[]> priceCheck;	
	protected int alarmFrameCount;
	

	public CoinMonitor(HttpHeaders headers,
			RestTemplate restTemplate, JButton searchButton, JComboBox<String> comboBox, JTextField inputField,
			JTextArea selected, JButton startMonitoring, JButton undoButton, JComboBox<String> trendBox, JFrame frame,
			JButton edit, JButton intervalSubmit, JTextField intervalField, StringBuilder intervalText,
			StringBuilder trend, StringBuilder ids, StringBuilder selectedText, List<String> idsList,
			List<String> selectedTextList, Map<String, String> coinSearch, Map<String, Double[]> priceCheck,
			int alarmFrameCount) {
		

		this.headers = headers;
		this.restTemplate = restTemplate;
		this.searchButton = searchButton;
		this.comboBox = comboBox;
		this.inputField = inputField;
		this.selected = selected;
		this.startMonitoring = startMonitoring;
		this.undoButton = undoButton;
		this.trendBox = trendBox;
		this.frame = frame;
		this.edit = edit;
		this.intervalSubmit = intervalSubmit;
		this.intervalField = intervalField;
		this.intervalText = intervalText;
		this.trend = trend;
		this.ids = ids;
		this.selectedText = selectedText;
		this.idsList = idsList;
		this.selectedTextList = selectedTextList;
		this.coinSearch = coinSearch;
		this.priceCheck = priceCheck;
		this.alarmFrameCount = alarmFrameCount;
	}


	public void run() {
		
		searchForCoins();

        populateComboBox();

        undo();
        
        trend();
        
        startMonitoring();

        interval();
		
	}


	public void interval() {
		
		intervalSubmit.addActionListener(new ActionListener() {
        	
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		
        		intervalText.setLength(0);
        		
        		String input = intervalField.getText();
        		
        		try {
        			
        			int intInput = Integer.parseInt(input);
        			intervalText.append(input);
        			JOptionPane.showInternalMessageDialog(null, "Interval set to " + intInput + " seconds");
        			
        		} catch (Exception ex) {
        			
        			intervalField.setText("");
        			JOptionPane.showInternalMessageDialog(null, "Must be a number");
        			
        			
        		}
        			
        		
        	}
        });
		
		
	}


	public void startMonitoring() {
		
		
		
		startMonitoring.setEnabled(false);
    	
   	 	startMonitoring.addActionListener(new ActionListener() {

            // declare worker as a field
            private SwingWorker<Void, String> worker;

            @Override
            public void actionPerformed(ActionEvent e) {
                
                frame.setEnabled(false);
                frame.setVisible(false);
                
                JFrame coinFrame = new JFrame("Price Monitor");
                JLabel label = new JLabel();
                label.setFont(new Font("Arial", Font.PLAIN, 18));
                coinFrame.getContentPane().add(label);
                coinFrame.setLayout(new BorderLayout());
                coinFrame.add(label, BorderLayout.CENTER);
                coinFrame.add(edit, BorderLayout.AFTER_LAST_LINE);
                coinFrame.setVisible(true);
                coinFrame.setAlwaysOnTop(true);
                coinFrame.setSize(500, 280);
                coinFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
                edit.addActionListener(new ActionListener() {
                   
                   @Override
                   public void actionPerformed(ActionEvent e) {
                       
                       worker.cancel(true);
                       coinFrame.dispose();
                       frame.setEnabled(true);
                       frame.setVisible(true);
                       
                   }
                });
                
                // create a new worker instance
                worker = new SwingWorker<Void, String>() {
                    
                    @Override
                    protected Void doInBackground() throws Exception {
                        try {
                        	
                        	MonitorUtility utility = new MonitorUtility(headers, restTemplate, searchButton, comboBox, inputField, selected, startMonitoring,
                    				undoButton, trendBox, frame, edit, intervalSubmit, intervalField, intervalText, trend, ids, selectedText,
                    				idsList, selectedTextList, coinSearch, priceCheck, alarmFrameCount);
                        	
                        	
                        	utility.monitorCoins(label, coinFrame, edit);
                        
                        } catch (InterruptedException ex) {

                        	Thread.currentThread().interrupt();
                        
                        }
                        return null;
                    }
                };

                worker.execute();
            }
        });
		
	}


	public void trend() {
		
		trendBox.setSelectedItem("24h");
       	
        trend.append("24h");
        
        trendBox.addActionListener(new ActionListener() {
        	
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		
        		String selectedTrend = (String) trendBox.getSelectedItem();
        		
        		trend.setLength(0);
        		
        		trend.append(selectedTrend);
        			
        	}
        	
        });
		
	}


	public void undo() {
		
		undoButton.setEnabled(false);
        
        undoButton.addActionListener(new ActionListener() {
        	
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		
        		
        		
        		ids.delete(ids.length() - idsList.get(idsList.size()-1).length(), ids.length());
        		selectedText.delete(selectedText.length() - selectedTextList.get(selectedTextList.size()-1).length(), selectedText.length());
        		
        		idsList.remove(idsList.size()-1);
        		selectedTextList.remove(selectedTextList.size()-1);
        		
        		selected.setText(selectedText.toString());
        		
        		if (ids.length() < 1) {

                	startMonitoring.setEnabled(false);
                	undoButton.setEnabled(false);
                }
        		
        	}
        	
        });
		
	}


	public void populateComboBox() {
		
		comboBox.addActionListener(new ActionListener() {
         	
         	
         	@Override
             public void actionPerformed(ActionEvent e) {
         		
                 String selectedCoin = (String) comboBox.getSelectedItem();
                 String selectedUuid = coinSearch.get(selectedCoin);

                 if (selectedUuid != null) {
                 	
                 	ids.append("&uuids[]=" + selectedUuid);
                 	idsList.add("&uuids[]=" + selectedUuid);
                 	
                 	selectedText.append(selectedCoin + "\n");
                 	selectedTextList.add(selectedCoin + "\n");
                 	selected.setText(selectedText.toString());
                 	
                 	
                 } 
                 
                 
                 if (ids.length() >= 1) {
                 	System.out.println("IDS: " + ids);
                 	startMonitoring.setEnabled(true);
                 	undoButton.setEnabled(true);
                 }
                 
              
                 
             }
         });
		
	}


	public void searchForCoins() {
		
		FindCoins findCoins = new FindCoins();
		
		searchButton.addActionListener(new ActionListener() {
        	
            @Override
            public void actionPerformed(ActionEvent e) {
            	
                String input = inputField.getText();

                
                if (findCoins.searchFile(input).isEmpty()) {

                	System.out.println("API SEARCH");
                	
                	
					coinSearch = findCoins.search(input, headers, restTemplate);
					
                } else {
                	
                	System.out.println("FILE SEARCH");
                	coinSearch = findCoins.searchFile(input);
                }
                
                
                comboBox.removeAllItems();
                comboBox.addItem("");
                
                for (String key : coinSearch.keySet()) {
                	comboBox.addItem(key);
                }
                
                inputField.setText("");
                 
            }
        });
		
	}
}
