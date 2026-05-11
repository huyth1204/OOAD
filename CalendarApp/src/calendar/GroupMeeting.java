package calendar;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GroupMeeting {
    private String id;
    private String name;
    private int duration;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<String> participants;

    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

    public GroupMeeting(String id, String name, int duration, String location, 
                        LocalDateTime startTime, LocalDateTime endTime, List<String> participants) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.participants = new ArrayList<>(participants);
    }

    public String getId()              { return id; }
    public String getName()            { return name; }
    public int getDuration()           { return duration; }
    public String getLocation()        { return location; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime()   { return endTime; }
    public List<String> getParticipants() { return participants; }
    
    public String formatStart() { return startTime.format(FORMATTER); }
    public String formatEnd()   { return endTime.format(FORMATTER); }

    public void addParticipant(String userId) {
        if (!participants.contains(userId)) {
            participants.add(userId);
        }
    }

    @Override
    public String toString() {
        return String.format("GroupMeeting{id='%s', name='%s', duration=%d, location='%s', start='%s', end='%s', participants=%s}",
            id, name, duration, location, formatStart(), formatEnd(), participants);
    }
}
