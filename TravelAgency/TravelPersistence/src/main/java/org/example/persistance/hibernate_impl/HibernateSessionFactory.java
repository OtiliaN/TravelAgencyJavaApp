package org.example.persistance.hibernate_impl;

import org.example.domain.Agent;
import org.example.domain.Flight;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateSessionFactory {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null || sessionFactory.isClosed()) {
            sessionFactory = createNewSessionFactory();
        }
        return sessionFactory;
    }

    private static SessionFactory createNewSessionFactory() {
        return new Configuration()
                .addAnnotatedClass(Agent.class)
                .addAnnotatedClass(Flight.class)
                .buildSessionFactory();
    }
}
