package com.stockticker.demo;


import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.stockticker.model.Coin;
import com.stockticker.model.CoinMarketData;

public class FindCoins {

	
	

	public Map<String, String> search(String query, HttpHeaders headers, RestTemplate restTemplate) {
		 
		 	Map<String, String> values = new HashMap<>();
	    	
	        
	        HttpEntity<String> entity = new HttpEntity<>(headers);

	        ResponseEntity<String> response = restTemplate.exchange(
	        		"https://api.coinranking.com/v2/search-suggestions?query=" + query, 
	        		HttpMethod.GET, entity, String.class);
	        
	        
	        Gson gson = new Gson();
	        JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
	        JsonObject dataObject = jsonObject.getAsJsonObject("data");


	        CoinMarketData coins = gson.fromJson(dataObject, CoinMarketData.class);


	        // Create a coins.json file to save coins, every concurrent search is additive
	        
	        try {
	        	
	        	File coinsFile = new File("coins.json");
	        	
	        	if (!coinsFile.exists()) {
	        		
	        		FileWriter file = new FileWriter("coins.json");
		        	
		        	gson.toJson(coins, file);
		        	file.close();
		        	
	        	} else {
	        		
	        		FileReader reader = new FileReader("coins.json");
	        		
	        		CoinMarketData existingData = gson.fromJson(reader, CoinMarketData.class);
	        		 
	        		existingData.getCoins().addAll(coins.getCoins());
	        		
	        		reader.close();
	        		
	        		FileWriter writer = new FileWriter("coins.json");
	        		gson.toJson(existingData, writer);
	        		writer.close();
	        		
	        	}
	        	
	        	
	        	
			} catch (Exception e) {
				
				e.printStackTrace();
			
			}
	        
	        
	        for (Coin coin : coins.getCoins()) {
	        	
	        	values.put(coin.getName(), coin.getUuid());
	        	
	        }
	        

	        
	        return values;
		 
	 }


	 public Map<String, String> searchFile(String query) {
		 
		Map<String, String> values = new HashMap<>();
		
		Gson gson = new Gson();
	 	
		
		FileReader reader = null;
		
		CoinMarketData coins = null;
		
	    try {
	    	
	    	File file = new File("coins.json");
	    	
	    	if (file.exists()) {
	    	
	    		reader = new FileReader("coins.json");

	    	    coins = gson.fromJson(reader, CoinMarketData.class);
	    		
		        reader.close();
	    	}
	    	
	        
	        
	    } catch (IOException e) {
	    	
	        e.printStackTrace();
	    
	    }
	
	    
	    String lowerQuery = query.toLowerCase();
        
        
        
        if (coins != null) {
        	
        	for (Coin coin : coins.getCoins()) {
            	
            	if (coin.getName().toLowerCase().startsWith(lowerQuery)) {
            		values.put(coin.getName(), coin.getUuid());
            	}
            	
            }
        }
        
        
	    
        
       return values;
        
	       
	 }
	 
	
	 
}
