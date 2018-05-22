package com.perez.jaroslav.allegrosearchapi.items;

import java.util.List;

public class Item {
    private long id;
    private String name;
    private List<Parameter> parameters;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public boolean hasParameterWithId(String id){
        for(Parameter p : parameters){
            if(p.id.equals(id)){
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", parameters=" + parameters +
                '}';
    }
}
