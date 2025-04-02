package org.example.repository.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.example.domain.Agent;
import org.example.repository.interfaces.IAgentRepository;
import org.example.utils.PasswordUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;


public class AgentRepositoryImpl implements IAgentRepository {
    private JdbcUtils jdbcUtils;
    private static final Logger logger = LogManager.getLogger();

    public AgentRepositoryImpl(Properties properties) {
        logger.info("Initialising Agent Repository with properties: {}", properties);
        jdbcUtils = new JdbcUtils(properties);
    }

    @Override
    public Optional<Agent> findOne(Long id) {
        Connection connection = jdbcUtils.getConnection();
        try (PreparedStatement preStmt = connection.prepareStatement("select * from agents where id=?")) {
            preStmt.setLong(1, id);
            try (ResultSet result = preStmt.executeQuery()) {
                if (result.next()) {
                    String name = result.getString("name");
                    String password = result.getString("password");
                    Agent agent = new Agent(name, password);
                    agent.setId(id);
                    return Optional.of(agent);
                }
            }
        } catch (SQLException exception) {
            logger.error(exception);
            System.out.println("Error DB " + exception);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Agent> findAll() {
        Connection connection = jdbcUtils.getConnection();
        List<Agent> agents = new ArrayList<>();
        try (PreparedStatement preStmt = connection.prepareStatement("select * from agents")) {
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    Long id = result.getLong("id");
                    String name = result.getString("name");
                    String password = result.getString("password");
                    Agent agent = new Agent(name, password);
                    agent.setId(id);
                    agents.add(agent);
                }
            }
        } catch (SQLException exception) {
            logger.error(exception);
            System.out.println("Error DB " + exception);
        }
        return agents;
    }

    @Override
    public Optional<Agent> save(Agent entity) {
        logger.traceEntry("saving agent {} ", entity);
        Connection connection = jdbcUtils.getConnection();
        try (PreparedStatement preStmt = connection.prepareStatement("insert into agents(name,password) values (?,?)")) {
            preStmt.setString(1, entity.getName());
            String hashedPassword = PasswordUtils.hashPassword(entity.getPassword());
            preStmt.setString(2, hashedPassword);
            int result = preStmt.executeUpdate();
            logger.trace("Saved {} instances", result);
            return Optional.of(entity);
        } catch (SQLException exception) {
            logger.error(exception);
            System.out.println("Error DB " + exception);
        }
        logger.traceExit();
        return Optional.empty();
    }

    @Override
    public Optional<Agent> delete(Long aLong) {
        return Optional.empty();
    }

    @Override
    public Optional<Agent> update(Agent entity) {
        return Optional.empty();
    }

    @Override
    public Optional<Agent> findByName(String name) {
        logger.traceEntry("searching agent with name {} ", name);

        Connection connection = jdbcUtils.getConnection();
        try (PreparedStatement preStmt = connection.prepareStatement("Select * from Agents where name=?")) {
            preStmt.setString(1, name);
            try (ResultSet result = preStmt.executeQuery()) {
                if (result.next()) {
                    Long id = result.getLong("id");
                    String hashedPassword = result.getString("password");

                    Agent agent = new Agent(name, hashedPassword);
                    agent.setId(id);
                    return Optional.of(agent);
                }
            }
        } catch (SQLException exception) {
            logger.error(exception);
            System.out.println("Error DB " + exception);
        }
        return Optional.empty();
    }
}
