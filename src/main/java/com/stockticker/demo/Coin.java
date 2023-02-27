package com.stockticker.demo;

import java.util.Arrays;
import java.util.List;

public class Coin {

	private String symbol;
	private double price;
	private String uuid;
	private String name;
	private double[] sparkline;
	private double change;
	private String color;
	
	
	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}


	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public double getChange() {
		return change;
	}

	public void setChange(double change) {
		this.change = change;
	}

	public double[] getSparkline() {
		return sparkline;
	}

	public void setSparkline(double[] sparkline) {
		this.sparkline = sparkline;
	}

	@Override
	public String toString() {
		return "Coin [symbol=" + symbol + ", price=" + price + ", uuid=" + uuid + ", name=" + name + ", sparkline="
				+ Arrays.toString(sparkline) + ", change=" + change + ", color=" + color + "]";
	}

	

	
	
}
