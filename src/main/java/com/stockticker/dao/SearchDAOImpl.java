package com.stockticker.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import com.stockticker.connection.HibernateUtil;
import com.stockticker.entity.Search;



public class SearchDAOImpl implements SearchDAO {
	
	private SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
	

	@Override
	public List<Search> findAll() {
		
		
		Session session = sessionFactory.getCurrentSession();
		
		
		Query<Search> query = session.createQuery("from Search s", Search.class);
		
		List<Search> list = query.getResultList();
		
		
		return list;
		
	}

	@Override
	public void save(Search search) {
		
		
		Session session = sessionFactory.getCurrentSession();

		if (!findAll().contains(new Search(search.getCoinName(), search.getUuid()))) {
		
			session.persist(search);

		} 
			
		
	}


	@Override
	public List<Search> findAllByName(String name) {
		
		Session session = sessionFactory.getCurrentSession();
		
		Query<Search> query = session.createQuery("from Search s where lower(s.coinName) LIKE :theName", Search.class);
		
		query.setParameter("theName", "%" + name.toLowerCase() + "%");
		
		List<Search> values = query.getResultList();
		
		return values;
		
	}

}
