package org.example.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

@jakarta.persistence.Entity
@Table(name = "agents_hibernate")
public class Agent extends Entity<Long> {
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "password", nullable = false)
    private String password;

    public Agent() {}

    public Agent(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Agent agent)) return false;
        return Objects.equals(name, agent.name) && Objects.equals(password, agent.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, password);
    }

    @Override
    public String toString() {
        return "Agent{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}