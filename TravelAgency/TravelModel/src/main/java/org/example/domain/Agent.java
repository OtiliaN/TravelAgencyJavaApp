package org.example.domain;

import java.util.Objects;

public class Agent extends Entity<Long>{
    private String name;
    private String password;

    public Agent(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

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
