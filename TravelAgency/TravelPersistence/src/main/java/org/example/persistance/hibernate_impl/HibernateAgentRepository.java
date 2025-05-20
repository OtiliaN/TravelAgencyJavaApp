package org.example.persistance.hibernate_impl;

import org.example.domain.Agent;
import org.example.persistance.interfaces.IAgentRepository;
import org.example.validators.ValidationException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class HibernateAgentRepository implements IAgentRepository {

    @Override
    public Optional<Agent> findOne(Long id) {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(Agent.class, id));
        }
    }

    @Override
    public Iterable<Agent> findAll() {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            return session.createQuery("from Agent", Agent.class).list();
        }
    }

    @Override
    public Optional<Agent> save(Agent entity) {
        if (entity == null) throw new IllegalArgumentException("Entity must not be null");
        Transaction tx = null;
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
            return Optional.empty();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new ValidationException("Could not save agent: " + e.getMessage());
        }
    }

    @Override
    public Optional<Agent> delete(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<Agent> update(Agent entity) {
        return Optional.empty();
    }

    @Override
    public Optional<Agent> findByName(String name) {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            Query<Agent> query = session.createQuery("from Agent where name = :name", Agent.class);
            query.setParameter("name", name);
            return query.uniqueResultOptional();
        }
    }
}