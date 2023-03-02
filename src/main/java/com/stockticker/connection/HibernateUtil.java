package com.stockticker.connection;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.stockticker.entity.Search;

public class HibernateUtil {

    private static final SessionFactory factory;

    static {
        factory = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(Search.class).buildSessionFactory();
        
        // Runtime shutdown hook to close the SessionFactory when the application is shutting down
        Runtime.getRuntime().addShutdownHook(new Thread(factory::close));
    }

    private HibernateUtil() {}

    public static SessionFactory getSessionFactory() {
        return factory;
    }
}


