package com.perez.jaroslav.allegrosearchapi.filters;

import java.util.List;
import java.util.Objects;

public class SelectFilter implements Filter{
    private String id;
    private String name;
    private List<FilterOption> options;

    public SelectFilter(String id, String name, List<FilterOption> options) {
        this.id = id;
        this.name = name;
        this.options = options;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FilterOption> getOptions() {
        return options;
    }

    public void setOptions(List<FilterOption> options) {
        this.options = options;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SelectFilter that = (SelectFilter) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(options, that.options);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, options);
    }

    @Override
    public String toString() {
        return "SelectFilter{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", options=" + options +
                '}';
    }
}
