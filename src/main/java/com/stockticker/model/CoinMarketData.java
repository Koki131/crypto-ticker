package com.stockticker.model;

import java.util.ArrayList;
import java.util.List;

public class CoinMarketData {

	
	private ArrayList<Coin> coins;

	public CoinMarketData() {

	}

	public ArrayList<Coin> getCoins() {
		return coins;
	}

	public void setCoins(ArrayList<Coin> coins) {
		this.coins = coins;
	}

	@Override
	public String toString() {
		return "CoinMarketData [coins=" + coins + "]";
	}
	
	
	
	
}
