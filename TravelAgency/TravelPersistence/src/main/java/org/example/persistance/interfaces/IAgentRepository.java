package org.example.persistance.interfaces;

import org.example.domain.Agent;
import org.example.persistance.IRepository;

import java.util.Optional;

public interface IAgentRepository extends IRepository<Long, Agent> {

    Optional<Agent> findByName(String name);
}
