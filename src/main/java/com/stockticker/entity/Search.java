package com.stockticker.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Search {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	@Column(name = "coin_name")
	private String coinName;
	
	@Column(name = "uuid")
	private String uuid;


	
	
	public Search() {

	}

	public Search(String coinName, String uuid) {
		this.coinName = coinName;
		this.uuid = uuid;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCoinName() {
		return coinName;
	}

	public void setCoinName(String coinName) {
		this.coinName = coinName;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public int hashCode() {
		return Objects.hash(coinName, uuid);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Search other = (Search) obj;
		return Objects.equals(coinName, other.coinName) && Objects.equals(uuid, other.uuid);
	}
	
	
	
	
}
