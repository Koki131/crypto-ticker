package com.stockticker.model;

import java.util.List;

public class CoinMarketData {

	
	private List<Coin> coins;

	public List<Coin> getCoins() {
		return coins;
	}

	public void setCoins(List<Coin> coins) {
		this.coins = coins;
	}

	@Override
	public String toString() {
		return "CoinMarketData [coins=" + coins + "]";
	}
	
	
	
	
}
