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
        Configuration configuration = new Configuration();

        try {
            configuration.addAnnotatedClass(Agent.class);
            configuration.addAnnotatedClass(Flight.class);

            // Încarcă hibernate.properties din classpath
            configuration.setProperties(new java.util.Properties() {{
                load(HibernateSessionFactory.class.getClassLoader()
                        .getResourceAsStream("hibernate.properties"));
            }});

            return configuration.buildSessionFactory();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create SessionFactory: " + e.getMessage(), e);
        }
    }
}
