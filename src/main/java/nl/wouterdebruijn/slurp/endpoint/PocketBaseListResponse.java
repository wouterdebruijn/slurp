package nl.wouterdebruijn.slurp.endpoint;

import java.util.List;

public record PocketBaseListResponse<T>(List<T> items, int totalItems, int page, int perPage) {
    public PocketBaseListResponse(List<T> items, int totalItems, int page, int perPage) {
        this.items = items;
        this.totalItems = totalItems;
        this.page = page;
        this.perPage = perPage;
    }

    public List<T> getItems() {
        return items;
    }

    public T getItem(int index) {
        return items.get(index);
    }

    public T first() {
        return getItem(0);
    }
}
