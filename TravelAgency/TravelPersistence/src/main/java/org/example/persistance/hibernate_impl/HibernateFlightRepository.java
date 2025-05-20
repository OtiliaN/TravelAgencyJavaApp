package org.example.persistance.hibernate_impl;

import org.example.domain.Flight;
import org.example.persistance.interfaces.IFlightRepository;
import org.example.validators.ValidationException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class HibernateFlightRepository implements IFlightRepository {

    @Override
    public Optional<Flight> findOne(Long id) {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(Flight.class, id));
        }
    }

    @Override
    public Iterable<Flight> findAll() {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            return session.createQuery("from Flight", Flight.class).list();
        }
    }

    @Override
    public Optional<Flight> save(Flight entity) {
        if (entity == null) throw new IllegalArgumentException("Entity must not be null");
        Transaction tx = null;
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
            return Optional.empty();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new ValidationException("Could not save flight: " + e.getMessage());
        }
    }

    @Override
    public Optional<Flight> delete(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<Flight> update(Flight entity) {
        if (entity == null) throw new IllegalArgumentException("Entity must not be null");
        Transaction tx = null;
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            Flight existing = session.get(Flight.class, entity.getId());
            if (existing == null) return Optional.of(entity);
            tx = session.beginTransaction();
            session.merge(entity);
            tx.commit();
            return Optional.empty();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new ValidationException("Could not update flight: " + e.getMessage());
        }
    }

    @Override
    public List<Flight> findByDestinationAndDate(String destination, LocalDate departureDate) {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            var startOfDay = departureDate.atStartOfDay();
            var endOfDay = departureDate.atTime(23, 59, 59);
            Query<Flight> query = session.createQuery(
                    "from Flight where destination = :dest and departureDateTime between :start and :end", Flight.class);
            query.setParameter("dest", destination);
            query.setParameter("start", startOfDay);
            query.setParameter("end", endOfDay);
            return query.list();
        }
    }

}