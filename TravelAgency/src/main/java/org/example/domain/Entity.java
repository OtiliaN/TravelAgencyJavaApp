package org.example.domain;

import java.io.Serializable;
import java.util.Objects;

public class Entity<ID> implements Serializable {
    private ID id;
    public ID getId()
    {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }
}
