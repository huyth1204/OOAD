package calendar;

public class Reminder {
    private String id;
    private String appointmentId;
    private int triggerMinutes;
    private String message;

    public Reminder(String id, String appointmentId, int triggerMinutes, String message) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.triggerMinutes = triggerMinutes;
        this.message = message;
    }

    public String getId()              { return id; }
    public String getAppointmentId()   { return appointmentId; }
    public int getTriggerMinutes()     { return triggerMinutes; }
    public String getMessage()         { return message; }

    @Override
    public String toString() {
        return String.format("Reminder[%s phút trước]: %s", triggerMinutes, message);
    }
}
