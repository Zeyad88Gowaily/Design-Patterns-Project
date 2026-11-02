package nonprofit.repository;

import java.time.LocalDate;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import nonprofit.model.CheckDonation;
import nonprofit.model.Donation;
import nonprofit.model.Entity;
import nonprofit.model.Event;
import nonprofit.model.InKindDonation;
import nonprofit.model.OnlineDonation;

public class EntityJsonCodec<T extends Entity> {
    private final Class<T> entityType;
    private final Gson gson;

    public EntityJsonCodec(Class<T> entityType) {
        this.entityType = entityType;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
    }

    public JsonObject toJson(T entity) {
        JsonObject json = gson.toJsonTree(entity, entity.getClass()).getAsJsonObject();
        json.remove("id");
        if (entity instanceof Donation) {
            json.remove("donor");
            json.addProperty("entityKind", donationKind((Donation) entity));
        } else if (entity instanceof Event) {
            json.remove("volunteers");
        }
        return json;
    }

    public T fromJson(JsonObject json, int id) {
        JsonObject data = json.deepCopy();
        Class<?> actualType = entityType;
        if (Donation.class.equals(entityType)) {
            String kind = data.has("entityKind") ? data.get("entityKind").getAsString() : "";
            data.remove("entityKind");
            if ("check".equals(kind)) {
                actualType = CheckDonation.class;
            } else if ("inKind".equals(kind)) {
                actualType = InKindDonation.class;
            } else {
                actualType = OnlineDonation.class;
            }
        }
        T entity = entityType.cast(gson.fromJson(data, actualType));
        entity.setId(id);
        if (entity instanceof Event && ((Event) entity).getVolunteers() == null) {
            ((Event) entity).setVolunteers(new java.util.ArrayList<nonprofit.model.Volunteer>());
        }
        return entity;
    }

    private String donationKind(Donation donation) {
        if (donation instanceof InKindDonation) {
            return "inKind";
        }
        if (donation instanceof CheckDonation) {
            return "check";
        }
        return "online";
    }

    private static class LocalDateAdapter
            implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
        @Override
        public JsonPrimitive serialize(LocalDate date, java.lang.reflect.Type type,
                                       JsonSerializationContext context) {
            return new JsonPrimitive(date.toString());
        }

        @Override
        public LocalDate deserialize(com.google.gson.JsonElement json, java.lang.reflect.Type type,
                                     JsonDeserializationContext context) {
            return LocalDate.parse(json.getAsString());
        }
    }
}
