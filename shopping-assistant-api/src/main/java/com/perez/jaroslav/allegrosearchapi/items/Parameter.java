package com.perez.jaroslav.allegrosearchapi.items;

import java.util.Objects;

public class Parameter {
    String id;
    String value;

    public Parameter(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Parameter parameter = (Parameter) o;
        return Objects.equals(id, parameter.id) &&
                Objects.equals(value, parameter.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value);
    }

    @Override
    public String toString() {
        return "Parameter{" +
                "id='" + id + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
