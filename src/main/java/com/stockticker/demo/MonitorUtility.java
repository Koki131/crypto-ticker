package com.stockticker.demo;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.*;

import com.stockticker.model.Children;
import com.stockticker.model.RedditData;
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
import com.stockticker.model.Coin;
import com.stockticker.model.CoinMarketData;



public class MonitorUtility extends CoinMonitor {


	public MonitorUtility(HttpHeaders headers,
						  RestTemplate restTemplate, JButton searchButton, JComboBox<String> comboBox, JTextField inputField,
						  JTextArea selected, JButton startMonitoring, JButton undoButton, JComboBox<String> trendBox, JFrame frame,
						  JButton edit, JButton intervalSubmit, JTextField intervalField, StringBuilder intervalText,
						  StringBuilder trend, StringBuilder ids, StringBuilder selectedText, List<String> idsList,
						  List<String> selectedTextList, Map<String, String> coinSearch, Map<String, Double[]> priceCheck,
						  int alarmFrameCount) {


		super(headers, restTemplate, searchButton, comboBox, inputField, selected, startMonitoring,
				undoButton, trendBox, frame, edit, intervalSubmit, intervalField, intervalText, trend, ids, selectedText,
				idsList, selectedTextList, coinSearch, priceCheck, alarmFrameCount);
	}


	public void monitorCoins(JLabel label, JFrame existingWindow, JButton editButton) throws InterruptedException {


		HttpEntity<String> entity = new HttpEntity<>(headers);

        JPanel monitoringPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        
        existingWindow.add(monitoringPanel, BorderLayout.CENTER);


        while (true) {

			CoinMarketData coins = getCoinData(entity);

            monitoringPanel.removeAll(); 

            for (Coin coin : coins.getCoins()) {

				Double[] sparkline = coin.getSparkline();

				Double max = sparkline[0];
				Double min = sparkline[0];


				TimeSeries series = new TimeSeries(coin.getSymbol());

                for (int i = 0; i < sparkline.length; i++) {

					Date date = new Date(System.currentTimeMillis() - (sparkline.length - i) * 60000);

					if (sparkline[i] != null) {
						series.add(new Minute(date), sparkline[i]);
						max = Math.max(max, sparkline[i]);
						min = Math.min(min, sparkline[i]);
					}

                    
                }
                

                ChartPanel chartPanel = new ChartPanel(populateChart(series, monitoringPanel, coin));
                chartPanel.setPreferredSize(new Dimension(200, 100)); 

                JPanel chartPanelWrapper = new JPanel(new BorderLayout());
                chartPanelWrapper.add(chartPanel, BorderLayout.CENTER);


                
                JButton alarmButton = new JButton("Alarm");
                alarmButton.setPreferredSize(new Dimension(Integer.MAX_VALUE, 20));
                alarmButton.setMinimumSize(new Dimension(Integer.MAX_VALUE, 20));
                alarmButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));


				JButton redditButton = new JButton("Reddit");
				redditButton.setPreferredSize(new Dimension(Integer.MAX_VALUE, 20));
				redditButton.setMinimumSize(new Dimension(Integer.MAX_VALUE, 20));
				redditButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

                
                alarm(alarmButton, editButton, coin);

				reddit(redditButton, coin);
                
                checkPrices(coin);
                
                monitoringPanel.add(populateCoinPanel(coin, chartPanelWrapper, alarmButton, redditButton, min, max));


            }
            
            monitoringPanel.revalidate(); 

            
            if (intervalText.length() <= 0) {
				Thread.sleep(60000);
            } else {
				int interval = Integer.parseInt(intervalText.toString());
				Thread.sleep(interval * 1000);
            }
            
        }
    }


	public JPanel populateCoinPanel(Coin coin, JPanel chartPanelWrapper, JButton alarmButton, JButton redditButton, double min, double max) {

		double coinPrice = Double.valueOf(coin.getPrice());
         
         JLabel coinLabel = new JLabel(populateLabel(coinPrice, min, max, coin));
         coinLabel.setVerticalAlignment(JLabel.TOP);
         
         JPanel labelButtonPanel = new JPanel(new BorderLayout());
         labelButtonPanel.add(coinLabel, BorderLayout.CENTER);
         labelButtonPanel.add(alarmButton, BorderLayout.NORTH);
		 labelButtonPanel.add(redditButton, BorderLayout.SOUTH);
         
         JPanel coinPanel = new JPanel(new BorderLayout());
         
         coinPanel.add(labelButtonPanel, BorderLayout.NORTH);
         coinPanel.add(chartPanelWrapper, BorderLayout.CENTER);
         coinPanel.setPreferredSize(new Dimension(200, 100));
         
         return coinPanel;

	}


	public String populateLabel(double coinPrice, double min, double max, Coin coin) {

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


		return labelHtml;


	}


	public void checkPrices(Coin coin) {

		String fileName = "sound-test.wav";

		Alarm alarm = new Alarm();

		Double[] prices = priceCheck.get(coin.getUuid());

		if (prices != null) {


			if (prices[0] != null && prices[0] < coin.getPrice()) {

				alarm.playAlarm(fileName, coin.getSymbol() + " price has risen above " + prices[0]);
				prices[0] = null;

			}

			if (prices[1] != null && prices[1] > coin.getPrice()) {

				alarm.playAlarm(fileName, coin.getSymbol() + " price has fallen below " + prices[1]);
				prices[1] = null;

			}

		}

	}


	public void alarm(JButton alarmButton, JButton editButton, Coin coin) {

		alarmButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {


				if (alarmFrameCount == 0) {

					editButton.setEnabled(false);
					alarmButton.setEnabled(false);

					JFrame alarmFrame = new JFrame(coin.getSymbol() + " Alarm");
					alarmFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

					alarmFrameCount++;

					alarmFrame.addWindowListener(new WindowAdapter() {

						@Override
						public void windowClosed(WindowEvent e) {

							alarmButton.setEnabled(true);
							editButton.setEnabled(true);
							alarmFrameCount = 0;

						}

					});

					JPanel labelPanel = new JPanel();
					JLabel label = new JLabel("Above/Below");

					labelPanel.add(label);

					alarmFrame.setVisible(true);
                    alarmFrame.setSize(500, 280);
                    
                    JPanel alarmPanel = new JPanel();

					StringBuilder aboveValue = new StringBuilder();
					StringBuilder belowValue = new StringBuilder();


					if (priceCheck.get(coin.getUuid()) != null) {

						aboveValue.setLength(0);
						belowValue.setLength(0);

						aboveValue.append(String.valueOf(priceCheck.get(coin.getUuid())[0]));
						belowValue.append(String.valueOf(priceCheck.get(coin.getUuid())[1]));

					}
                    
                    JTextField abovePrice = new JTextField(aboveValue.toString(), 10);
                    JTextField belowPrice = new JTextField(belowValue.toString(), 10);
                    
                    
                    JButton submitPrices = new JButton("Submit");
                    
                    
                    submitPrices(submitPrices, abovePrice, belowPrice, coin);
                    
                    
                    
                    
                    alarmPanel.add(labelPanel);
                    alarmPanel.add(abovePrice);
                    alarmPanel.add(belowPrice);
                    alarmPanel.add(submitPrices);
                    
                    alarmFrame.add(alarmPanel);

				}


			}
        });

	}

	public void reddit(JButton redditButton, Coin coin) {


		redditButton.addActionListener(new ActionListener() {

			boolean isButtonActive = false;


			@Override
			public void actionPerformed(ActionEvent e) {

				if (!isButtonActive) {

					isButtonActive = true;

					JFrame frame = new JFrame("Reddit Page Viewer");
					frame.setLayout(new BorderLayout()); // Set a layout manager for the frame
					frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

					JPanel redditPanel = new JPanel();
					redditPanel.setLayout(new GridLayout(0, 1)); // Set a layout manager for the panel


					for (Children child : getRedditData(coin)) {

						JPanel threadPanel = new JPanel();
						threadPanel.setLayout(new BoxLayout(threadPanel, BoxLayout.Y_AXIS));
						threadPanel.setPreferredSize(new Dimension(500, 300));

						JLabel redditLabel = new JLabel("<html><a href=" + child.getData().getUrl() + ">" + child.getData().getTitle() + "</a></html>");
						redditLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Change the cursor to a hand icon when hovering over the link
						redditLabel.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								try {
									Desktop.getDesktop().browse(new URI(child.getData().getUrl())); // Open the link in the user's default web browser
								} catch (IOException | URISyntaxException ex) {
									ex.printStackTrace();
								}
							}
						});
						redditLabel.setPreferredSize(new Dimension(500, 100));
						threadPanel.add(redditLabel);


						JTextArea redditArea = new JTextArea(child.getData().getSelftext());
						redditArea.setLineWrap(true);
						redditArea.setWrapStyleWord(true);
						redditArea.setEditable(false);

						JScrollPane redditAreaScrollPane = new JScrollPane(redditArea); // Add a JScrollPane to the JTextArea
						redditAreaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); // Show the scrollbar always

						threadPanel.add(redditAreaScrollPane);

						redditPanel.add(threadPanel);
					}

					JScrollPane redditScrollPane = new JScrollPane(redditPanel); // create a new JScrollPane
					redditScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
					redditScrollPane.getVerticalScrollBar().setUnitIncrement(16);

					frame.add(redditScrollPane, BorderLayout.CENTER); // add the JScrollPane to the center of the frame


					frame.setSize(800, 600);
					frame.setVisible(true);
				}
			}
		});
	}




	public void submitPrices(JButton submitPrices, JTextField abovePrice, JTextField belowPrice, Coin coin) {

		submitPrices.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				Double[] thresholds = new Double[2];

				try {

					double abovePriceDouble = Double.parseDouble(abovePrice.getText());

					if (abovePriceDouble > coin.getPrice()) {

						thresholds[0] = abovePriceDouble;
						JOptionPane.showMessageDialog(null, "Above price set");

					} else {

						abovePrice.setText("");
						JOptionPane.showMessageDialog(null, "Must be above current price");

					}


				} catch (Exception e2) {

					if (!abovePrice.getText().isEmpty()) {
						JOptionPane.showMessageDialog(null, "Above price must be a number");
					}

					abovePrice.setText("");


				}

				try {


					double belowPriceDouble = Double.parseDouble(belowPrice.getText());


					if (belowPriceDouble < coin.getPrice()) {

						thresholds[1] = belowPriceDouble;
						JOptionPane.showMessageDialog(null, "Below price set");

					} else {

						belowPrice.setText("");
						JOptionPane.showMessageDialog(null, "Must be below current price");

					}

				} catch (Exception e2) {

					if (!belowPrice.getText().isEmpty()) {
						JOptionPane.showMessageDialog(null, "Below price must be a number");
					}

					belowPrice.setText("");


				}

				priceCheck.put(coin.getUuid(), thresholds);


			}

		});

	}


	public JFreeChart populateChart(TimeSeries series, JPanel panel, Coin coin) {

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
        
        return chart;

	}


	public  CoinMarketData getCoinData(HttpEntity<String> entity) {

		ResponseEntity<String> response = restTemplate.exchange(
				"https://api.coinranking.com/v2/coins?" + ids.toString() + "&timePeriod=" + trend.toString(),
				HttpMethod.GET, entity, String.class);

		Gson gson = new Gson();

		JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
		JsonObject dataObject = jsonObject.getAsJsonObject("data");

		CoinMarketData coins = null;

		try {
			coins = gson.fromJson(dataObject, CoinMarketData.class);
		} catch (Exception e) {
			e.printStackTrace();

		}

		return coins;
	}

	public List<Children> getRedditData(Coin coin) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("User-Agent", "CryptoTicker v1.0.0");

		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<String> response = restTemplate.exchange(
				"https://www.reddit.com/search.json?q=" + coin.getName() + "&sort=relevance", HttpMethod.GET, entity, String.class
		);

		Gson gson = new Gson();

		JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
		JsonObject dataObject = jsonObject.getAsJsonObject("data");

		RedditData data = gson.fromJson(dataObject, RedditData.class);

		List<Children> children = data.getChildren();

		return children;

	}

}
