package nonprofit.controller;

import java.util.List;

import nonprofit.model.LookupItem;
import nonprofit.service.LookupService;

public class LookupPanelController {
    private final LookupService service;

    public LookupPanelController(LookupService service) {
        this.service = service;
    }

    public LookupItem add(String name, String category) {
        return service.add(name, category);
    }

    public boolean update(int id, String newName) {
        return service.update(id, newName);
    }

    public boolean delete(int id) {
        return service.delete(id);
    }

    public List<LookupItem> getByCategory(String category) {
        return service.getByCategory(category);
    }
}
