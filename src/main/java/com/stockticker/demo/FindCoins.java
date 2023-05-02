package com.stockticker.demo;


import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.stockticker.dao.SearchDAO;
import com.stockticker.entity.Search;
import com.stockticker.model.Coin;
import com.stockticker.model.CoinMarketData;

public class FindCoins {

	
	private SearchDAO searchDao;
	private SessionFactory sessionFactory;
	
	
	
	public FindCoins(SearchDAO searchDao, SessionFactory sessionFactory) {
		this.searchDao = searchDao;
		this.sessionFactory = sessionFactory;
	}

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
	        
	        
	        Session session = sessionFactory.getCurrentSession();
	        
	        session.beginTransaction();
	        
	        for (Coin coin : coins.getCoins()) {
	        	   	
	        	searchDao.save(new Search(coin.getName(), coin.getUuid()));
	        	
	        	values.put(coin.getName(), coin.getUuid());
	        	
	        }
	        
	        session.getTransaction().commit();
	        
	        
	        return values;
		 
	 }

	 public Map<String, String> searchDatabase(String query) {
		 
		 	Map<String, String> values = new HashMap<>();
	    	
	    	Session session = sessionFactory.getCurrentSession();
	    	
	    	session.beginTransaction();
	    	
	    	for (Search search : searchDao.findAllByName(query)) {
	    		
	    		values.put(search.getCoinName(), search.getUuid());
	    		
	    	}
	    	
	    	session.getTransaction().commit();
	    	
	    	
	    	return values;
		 
	 }
	 
}
