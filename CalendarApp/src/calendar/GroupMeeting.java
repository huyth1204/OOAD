package calendar;

import java.util.ArrayList;
import java.util.List;

public class GroupMeeting {
    private String id;
    private String name;
    private int duration;
    private String location;
    private List<String> participants;

    public GroupMeeting(String id, String name, int duration, String location, List<String> participants) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.location = location;
        this.participants = new ArrayList<>(participants);
    }

    public String getId()              { return id; }
    public String getName()            { return name; }
    public int getDuration()           { return duration; }
    public String getLocation()        { return location; }
    public List<String> getParticipants() { return participants; }

    public void addParticipant(String userId) {
        if (!participants.contains(userId)) {
            participants.add(userId);
        }
    }

    @Override
    public String toString() {
        return String.format("GroupMeeting{id='%s', name='%s', duration=%d, location='%s', participants=%s}",
            id, name, duration, location, participants);
    }
}
