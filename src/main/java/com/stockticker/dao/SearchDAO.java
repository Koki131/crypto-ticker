package com.stockticker.dao;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.stockticker.entity.Search;

public interface SearchDAO {

	List<Search> findAll();	
	
	void save(Search search);
	
	List<Search> findAllByName(String name);
}
