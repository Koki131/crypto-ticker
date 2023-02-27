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
import java.util.ArrayList;
import java.util.Date;
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
import com.stockticker.connection.HibernateUtil;
import com.stockticker.dao.SearchDAO;
import com.stockticker.dao.SearchDAOImpl;
import com.stockticker.entity.Search;


public class App {
	
	
	private static final String API_KEY = "PUT YOUR API KEY HERE";
	
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
        JButton submitButton = new JButton("Search");
        
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
        
        JButton undo = new JButton("Undo");
        
        JLabel interval = new JLabel("Interval (sec):");
        JTextField intervalField = new JTextField(5);
        JButton intervalSubmit = new JButton("Submit");
        JLabel defaultInterval = new JLabel("Default 60 seconds");
        
        
        
        
        submitButton.addActionListener(new ActionListener() {
        	
            @Override
            public void actionPerformed(ActionEvent e) {
            	
                String input = inputField.getText();

                
                if (searchDatabase(input).isEmpty()) {
                	System.out.println("API SEARCH");
                	coinSearch = search(input);
                } else {
                	System.out.println("DATABASE SEARCH");
                	coinSearch = searchDatabase(input);
                }
                
                
                comboBox.removeAllItems();
                comboBox.addItem("");
                
                for (String key : coinSearch.keySet()) {
                	comboBox.addItem(key);
                }
                
                inputField.setText("");
                 
            }
        });
        
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
                	undo.setEnabled(true);
                }
                
             
                
            }
        });
        
        undo.setEnabled(false);
        
        undo.addActionListener(new ActionListener() {
        	
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		
        		
        		
        		ids.delete(ids.length() - idsList.get(idsList.size()-1).length(), ids.length());
        		selectedText.delete(selectedText.length() - selectedTextList.get(selectedTextList.size()-1).length(), selectedText.length());
        		
        		idsList.remove(idsList.size()-1);
        		selectedTextList.remove(selectedTextList.size()-1);
        		
        		selected.setText(selectedText.toString());
        		
        		if (ids.length() < 1) {
                	System.out.println("IDS: " + ids);
                	startMonitoring.setEnabled(false);
                	undo.setEnabled(false);
                }

        		
        	}
        	
        });
        
        
        
        trendBox.setSelectedItem("24h");
       	
        trend.append("24h");
        
        trendBox.addActionListener(new ActionListener() {
        	
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		
        		String selectedTrend = (String) trendBox.getSelectedItem();
        		
        		trend.setLength(0);
        		
        		trend.append(selectedTrend);
        		System.out.println(selectedTrend);
        		
        		
        		
        		
        	}
        	
        });

        
        
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
                coinFrame.add(edit, BorderLayout.BEFORE_FIRST_LINE);
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
                        	
                            monitorCoins(label, coinFrame);
                        
                        } catch (InterruptedException ex) {
                        	
                        	Thread.currentThread().interrupt();
                        
                        }
                        return null;
                    }
                };

                worker.execute();
            }
        });
        
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
        
        
        panel.add(inputLabel);
        panel.add(inputField);
        panel.add(startMonitoring);
        panel.add(submitButton);
        panel.add(comboBox);
        panel.add(scrollPane);
        panel.add(trendLabel);
        panel.add(undo);
        panel.add(trendBox);
        intervalPanel.add(interval);
        intervalPanel.add(intervalField);
        intervalPanel.add(intervalSubmit);
        intervalTextPanel.add(defaultInterval);
        panel.add(intervalPanel);
        panel.add(intervalTextPanel);
        frame.add(panel);
        frame.setVisible(true);
        
    }
    
    public static void monitorCoins(JLabel label, JFrame existingWindow) throws InterruptedException {
    	
    	
        HttpEntity<String> entity = new HttpEntity<>(headers);

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        
        existingWindow.add(panel, BorderLayout.CENTER);
        
        int count = 1;
        
        while (true) {
        	

        	System.out.println("Count: " + count++);
        	
        	
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://api.coinranking.com/v2/coins?" + ids.toString() + "&timePeriod=" + trend.toString(),
                    HttpMethod.GET, entity, String.class);

            Gson gson = new Gson();

            JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
            JsonObject dataObject = jsonObject.getAsJsonObject("data");

            CoinMarketData coins = gson.fromJson(dataObject, CoinMarketData.class);
            
            panel.removeAll(); 

            for (Coin coin : coins.getCoins()) {

                double[] sparkline = coin.getSparkline(); 
                TimeSeries series = new TimeSeries(coin.getSymbol()); 
                
                double max = sparkline[0];
                double min = sparkline[0];
                
                for (int i = 0; i < sparkline.length; i++) {
                	
                    Date date = new Date(System.currentTimeMillis() - (sparkline.length - i) * 60000); 
                    series.add(new Minute(date), sparkline[i]); 
                    
                    max = Math.max(max, sparkline[i]);
                    min = Math.min(min, sparkline[i]);
                    
                }
                
                
                TimeSeriesCollection dataset = new TimeSeriesCollection(series); 

                JFreeChart chart = ChartFactory.createTimeSeriesChart(
                        null, 
                        null, 
                        null, 
                        dataset, 
                        false, 
                        false, 
                        false 
                );

                XYPlot plot = (XYPlot) chart.getPlot(); 
                panel.setBackground(Color.WHITE);
                plot.setBackgroundPaint(panel.getBackground()); 
                plot.setDomainGridlinePaint(Color.LIGHT_GRAY); 
                plot.setRangeGridlinePaint(Color.LIGHT_GRAY); 
                plot.setOutlinePaint(null); 
                
                if (coin.getChange() > 0) {
                	XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
                    renderer.setSeriesPaint(0, Color.GREEN);
                } else {
                	XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
                    renderer.setSeriesPaint(0, Color.RED);
                }
                
                
                
                plot.getDomainAxis().setVisible(false);
                

                ChartPanel chartPanel = new ChartPanel(chart);
                chartPanel.setPreferredSize(new Dimension(200, 100)); 

                JPanel chartPanelWrapper = new JPanel(new BorderLayout());
                chartPanelWrapper.add(chartPanel, BorderLayout.CENTER);

                double coinPrice = Double.valueOf(coin.getPrice());
                String result = String.format("%.4f", coinPrice);
                
                String minStr = String.format("%.4f", min);
                String maxStr = String.format("%.4f", max);
                
                String labelHtml = "<html>" +
                
                		"<span style=\"font-weight:bold; color: " + coin.getColor() + ";\">" + " " + coin.getSymbol() + ": " + "</span>" + 
                		
                		"<span style=\"color: " + (coin.getChange() < 0 ? "red" : "green") + ";\">&nbsp;" + result + "</span>  " +  
                		
                		"<br><span style=\"font-weight:bold; color: blue; \"> Change:</span> <span style=\"color: " + 

                		(coin.getChange() < 0 ? "red" : "green") + ";\">&nbsp;" + coin.getChange() + " % </span>" +
                		
                		"<br><span style=\"font-weight:bold; color: green; \">High:</span>" + 
                		
                		"<span> &nbsp;" + maxStr + "</span>" +
						
						"<span style=\"font-weight:bold; color: red;\">&nbsp;&nbsp;Low:</span> &nbsp;" + minStr +
                        
                		"</html>";
                
                
                
                
                JLabel coinLabel = new JLabel(labelHtml);
                coinLabel.setVerticalAlignment(JLabel.TOP);
                
                
                
                JPanel coinPanel = new JPanel(new BorderLayout());
                coinPanel.add(coinLabel, BorderLayout.NORTH);
                coinPanel.add(chartPanelWrapper, BorderLayout.CENTER);
                
                coinPanel.setPreferredSize(new Dimension(200, 100));
                
                

                panel.add(coinPanel); 
            }

            panel.revalidate(); 

            
            if (intervalText.length() <= 0) {
            	Thread.sleep(60000);
            } else {
            	int interval = Integer.parseInt(intervalText.toString());
            	Thread.sleep(interval * 1000);
            }
            
        }
    }



    
    
    
    public static Map<String, String> search(String query) {
    	
    	Map<String, String> values = new HashMap<>();
    	
        
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
        		"https://api.coinranking.com/v2/search-suggestions?query=" + query, 
        		HttpMethod.GET, entity, String.class);
        
        
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
        JsonObject dataObject = jsonObject.getAsJsonObject("data");
        
        CoinMarketData coins = gson.fromJson(dataObject, CoinMarketData.class);
        
        
        Session session = sessionFactory.getCurrentSession();
        
        session.beginTransaction();
        
        for (Coin coin : coins.getCoins()) {
        	   	
        	searchDao.save(new Search(coin.getName(), coin.getUuid()));
        	
        	values.put(coin.getName(), coin.getUuid());
        	
        }
        
        session.getTransaction().commit();
        
        
        return values;
    	 
    }
    
    public static Map<String, String> searchDatabase(String query) {
    	
    	Map<String, String> values = new HashMap<>();
    	
    	Session session = sessionFactory.getCurrentSession();
    	
    	session.beginTransaction();
    	
    	for (Search search : searchDao.findAllByName(query)) {
    		
    		values.put(search.getCoinName(), search.getUuid());
    		
    	}
    	
    	session.getTransaction().commit();
    	
    	
    	return values;
    }

    
    public static void populateTrendBox(JComboBox<String> box) {
    	
    	for (String trend : trends) {
    		
    		box.addItem(trend);
    		
    	}
    	
    }
    
    
}



































