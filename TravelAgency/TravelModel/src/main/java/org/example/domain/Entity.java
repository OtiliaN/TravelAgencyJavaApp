package org.example.domain;

import jakarta.persistence.*;
import java.io.Serializable;

@MappedSuperclass
public class Entity<ID> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private ID id;

    public Entity() {}

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }
}