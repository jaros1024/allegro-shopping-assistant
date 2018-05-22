package com.perez.jaroslav.allegrosearchapi.items;

import java.util.List;
import java.util.Objects;

public class ItemPacket {
    private int id;
    private List<Item> items;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemPacket that = (ItemPacket) o;
        return id == that.id &&
                Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, items);
    }
}
