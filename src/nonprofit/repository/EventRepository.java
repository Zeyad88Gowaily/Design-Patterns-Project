package nonprofit.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import nonprofit.model.Event;
import nonprofit.model.Volunteer;

public class EventRepository extends SupabaseRepository<Event> {
    public EventRepository(SupabaseRestClient client) {
        super(client, "events", Event.class);
    }

    @Override
    public Event add(Event event) {
        Event saved = super.add(event);
        replaceVolunteerLinks(saved, event.getVolunteers());
        saved.setVolunteers(new ArrayList<Volunteer>(event.getVolunteers()));
        return saved;
    }

    @Override
    public boolean save(Event event) {
        if (!super.save(event)) {
            return false;
        }
        replaceVolunteerLinks(event, event.getVolunteers());
        return true;
    }

    @Override
    public Event getById(int id) {
        Event event = super.getById(id);
        if (event != null) {
            hydrateVolunteers(java.util.Collections.singletonList(event));
        }
        return event;
    }

    @Override
    public List<Event> getAll() {
        List<Event> events = super.getAll();
        hydrateVolunteers(events);
        return events;
    }

    private void hydrateVolunteers(List<Event> events) {
        if (events.isEmpty()) {
            return;
        }
        Map<Integer, Event> eventById = new HashMap<Integer, Event>();
        for (Event event : events) {
            event.getVolunteers().clear();
            eventById.put(event.getId(), event);
        }

        JsonArray links = getClient().selectAll("event_volunteers");
        if (links.size() == 0) {
            return;
        }
        EntityJsonCodec<Volunteer> codec = new EntityJsonCodec<Volunteer>(Volunteer.class);
        Map<Integer, Volunteer> volunteerById = new HashMap<Integer, Volunteer>();
        for (JsonElement rowElement : getClient().selectAll("volunteers")) {
            JsonObject row = rowElement.getAsJsonObject();
            Volunteer volunteer =
                    codec.fromJson(row.getAsJsonObject("data"), row.get("id").getAsInt());
            volunteerById.put(volunteer.getId(), volunteer);
        }
        for (JsonElement linkElement : links) {
            JsonObject link = linkElement.getAsJsonObject();
            Event event = eventById.get(link.get("event_id").getAsInt());
            if (event == null) {
                continue;
            }
            int volunteerId = link.get("volunteer_id").getAsInt();
            Volunteer volunteer = volunteerById.get(volunteerId);
            if (volunteer == null) {
                throw new SupabaseException("Event assignment references missing volunteer " + volunteerId + ".");
            }
            event.getVolunteers().add(volunteer);
        }
    }

    private void replaceVolunteerLinks(Event event, List<Volunteer> volunteers) {
        JsonArray volunteerIds = new JsonArray();
        for (Volunteer volunteer : volunteers) {
            volunteerIds.add(volunteer.getId());
        }
        getClient().replaceEventVolunteers(event.getId(), volunteerIds);
    }
}
