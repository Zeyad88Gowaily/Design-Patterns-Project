package nonprofit.repository;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import nonprofit.model.Entity;
import nonprofit.model.Donation;
import nonprofit.model.Registration;
import nonprofit.model.Ticket;

public class SupabaseRepository<T extends Entity> extends Repository<T> {
    private final SupabaseRestClient client;
    private final String table;
    private final EntityJsonCodec<T> codec;

    public SupabaseRepository(SupabaseRestClient client, String table, Class<T> entityType) {
        this.client = client;
        this.table = table;
        this.codec = new EntityJsonCodec<T>(entityType);
    }

    @Override
    public T add(T item) {
        JsonObject row = rowFor(item);
        JsonArray result = client.insert(table, row);
        if (result.size() != 1) {
            throw new SupabaseException("Supabase did not return the inserted " + table + " record.");
        }
        return decode(result.get(0));
    }

    @Override
    public T getById(int id) {
        JsonArray result = client.selectById(table, id);
        return result.size() == 0 ? null : decode(result.get(0));
    }

    @Override
    public List<T> getAll() {
        JsonArray result = client.selectAll(table);
        List<T> records = new ArrayList<T>();
        for (JsonElement row : result) {
            records.add(decode(row));
        }
        return records;
    }

    @Override
    public boolean remove(int id) {
        return client.delete(table, "id=eq." + id).size() > 0;
    }

    @Override
    public boolean save(T item) {
        if (item == null || item.getId() <= 0) {
            return false;
        }
        return client.update(table, item.getId(), rowFor(item)).size() > 0;
    }

    protected SupabaseRestClient getClient() {
        return client;
    }

    private JsonObject rowFor(T item) {
        JsonObject row = new JsonObject();
        row.add("data", codec.toJson(item));
        addRelationshipColumns(row, item);
        return row;
    }

    protected void addRelationshipColumns(JsonObject row, T item) {
        if (item instanceof Donation) {
            row.addProperty("donor_id", ((Donation) item).getDonor().getId());
        } else if (item instanceof Registration) {
            Registration registration = (Registration) item;
            row.addProperty("event_id", registration.getEventId());
            row.addProperty("volunteer_id", registration.getVolunteerId());
        } else if (item instanceof Ticket) {
            Ticket ticket = (Ticket) item;
            row.addProperty("event_id", ticket.getEventId());
            row.addProperty("volunteer_id", ticket.getVolunteerId());
        }
    }

    protected T decode(JsonElement element) {
        JsonObject row = element.getAsJsonObject();
        return codec.fromJson(row.getAsJsonObject("data"), row.get("id").getAsInt());
    }
}
