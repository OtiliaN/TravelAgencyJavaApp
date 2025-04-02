package org.example.repository.interfaces;

import org.example.domain.Agent;
import org.example.repository.IRepository;

import java.util.Optional;

public interface IAgentRepository extends IRepository<Long, Agent> {

    Optional<Agent> findByName(String name);
}
