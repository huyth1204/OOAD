package calendar.persistence;

import calendar.Appointment;
import calendar.GroupMeeting;
import calendar.Reminder;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.Provide;

import java.time.LocalDateTime;
import java.util.List;

/**
 * jqwik generators for property-based testing of the persistence layer.
 * Provides arbitrary instances of domain objects (Appointment, GroupMeeting, Reminder)
 * and test data (strings with special characters, invalid lines, etc.).
 */
public class TestGenerators {

    /**
     * Generates arbitrary Appointment instances with valid data.
     * - IDs: 1-50 character alphanumeric strings
     * - Names: 1-100 character alphanumeric strings
     * - Locations: 0-50 character alphanumeric strings (can be empty)
     * - Start times: Random dates between 2024-2030
     * - Durations: 15-240 minutes
     * - Reminders: Optional, 5-60 minutes
     * - isGroup: Random boolean
     */
    @Provide
    public static Arbitrary<Appointment> appointments() {
        return Combinators.combine(
            Arbitraries.strings().alpha().numeric().ofMinLength(1).ofMaxLength(50), // id
            Arbitraries.strings().alpha().numeric().withChars(' ', '-').ofMinLength(1).ofMaxLength(100), // name
            Arbitraries.strings().alpha().numeric().withChars(' ', '-').ofMaxLength(50), // location
            dateTimes(), // startTime
            Arbitraries.integers().between(15, 240), // duration
            Arbitraries.integers().between(5, 60).optional(), // reminder
            Arbitraries.booleans() // isGroup
        ).as((id, name, location, startTime, duration, reminder, isGroup) -> {
            LocalDateTime endTime = startTime.plusMinutes(duration);
            return new Appointment(id, name, location, startTime, endTime, 
                                   duration, reminder.orElse(null), isGroup);
        });
    }

    /**
     * Generates arbitrary GroupMeeting instances with valid data.
     * - IDs: 1-50 character alphanumeric strings
     * - Names: 1-100 character alphanumeric strings
     * - Durations: 15-240 minutes
     * - Locations: 0-50 character alphanumeric strings
     * - Participants: 1-10 participant IDs (1-20 characters each)
     */
    @Provide
    public static Arbitrary<GroupMeeting> groupMeetings() {
        return Combinators.combine(
            Arbitraries.strings().alpha().numeric().ofMinLength(1).ofMaxLength(50), // id
            Arbitraries.strings().alpha().numeric().withChars(' ', '-').ofMinLength(1).ofMaxLength(100), // name
            Arbitraries.integers().between(15, 240), // duration
            Arbitraries.strings().alpha().numeric().withChars(' ', '-').ofMaxLength(50), // location
            Arbitraries.strings().alpha().numeric().ofMinLength(1).ofMaxLength(20)
                .list().ofMinSize(1).ofMaxSize(10) // participants
        ).as(GroupMeeting::new);
    }

    /**
     * Generates arbitrary Reminder instances with valid data.
     * - IDs: 1-50 character alphanumeric strings
     * - Appointment IDs: 1-50 character alphanumeric strings
     * - Trigger minutes: 5-60 minutes
     * - Messages: 1-200 character alphanumeric strings
     */
    @Provide
    public static Arbitrary<Reminder> reminders() {
        return Combinators.combine(
            Arbitraries.strings().alpha().numeric().ofMinLength(1).ofMaxLength(50), // id
            Arbitraries.strings().alpha().numeric().ofMinLength(1).ofMaxLength(50), // appointmentId
            Arbitraries.integers().between(5, 60), // triggerMinutes
            Arbitraries.strings().alpha().numeric().withChars(' ', '-', '!').ofMinLength(1).ofMaxLength(200) // message
        ).as(Reminder::new);
    }

    /**
     * Generates arbitrary LocalDateTime instances.
     * - Years: 2024-2030
     * - Months: 1-12
     * - Days: 1-28 (to avoid invalid dates)
     * - Hours: 0-23
     * - Minutes: 0-59
     */
    @Provide
    public static Arbitrary<LocalDateTime> dateTimes() {
        return Arbitraries.integers().between(2024, 2030)
            .flatMap(year -> Arbitraries.integers().between(1, 12)
                .flatMap(month -> Arbitraries.integers().between(1, 28)
                    .flatMap(day -> Arbitraries.integers().between(0, 23)
                        .flatMap(hour -> Arbitraries.integers().between(0, 59)
                            .map(minute -> LocalDateTime.of(year, month, day, hour, minute))))));
    }

    /**
     * Generates lists of Appointment instances.
     * - List size: 0-20 appointments
     */
    @Provide
    public static Arbitrary<List<Appointment>> appointmentLists() {
        return appointments().list().ofMinSize(0).ofMaxSize(20);
    }

    /**
     * Generates lists of GroupMeeting instances.
     * - List size: 0-20 group meetings
     */
    @Provide
    public static Arbitrary<List<GroupMeeting>> groupMeetingLists() {
        return groupMeetings().list().ofMinSize(0).ofMaxSize(20);
    }

    /**
     * Generates lists of Reminder instances.
     * - List size: 0-20 reminders
     */
    @Provide
    public static Arbitrary<List<Reminder>> reminderLists() {
        return reminders().list().ofMinSize(0).ofMaxSize(20);
    }

    /**
     * Generates strings containing special characters that need escaping.
     * - Characters: a-z, pipe (|), comma (,), backslash (\), newline (\n), tab (\t)
     * - Length: 0-100 characters
     */
    @Provide
    public static Arbitrary<String> stringsWithSpecialChars() {
        return Arbitraries.strings()
            .withCharRange('a', 'z')
            .withChars('|', ',', '\\', '\n', '\r', '\t', ' ')
            .ofMinLength(0)
            .ofMaxLength(100);
    }

    /**
     * Generates invalid appointment lines for testing resilient parsing.
     * Includes various types of malformed data:
     * - Wrong field count
     * - Invalid datetime format
     * - Invalid integer values
     * - Empty required fields
     * - Malformed boolean values
     */
    @Provide
    public static Arbitrary<String> invalidAppointmentLines() {
        return Arbitraries.oneOf(
            // Wrong field count (too few)
            Arbitraries.just("id|name|location"),
            // Wrong field count (too many)
            Arbitraries.just("id|name|loc|2026-05-10T09:00|2026-05-10T10:00|90||false|extra"),
            // Invalid datetime format
            Arbitraries.just("id|name|loc|invalid-date|2026-05-10T10:00|90||false"),
            Arbitraries.just("id|name|loc|2026-05-10T09:00|not-a-date|90||false"),
            // Invalid integer (duration)
            Arbitraries.just("id|name|loc|2026-05-10T09:00|2026-05-10T10:00|not-a-number||false"),
            // Invalid integer (reminder)
            Arbitraries.just("id|name|loc|2026-05-10T09:00|2026-05-10T10:00|90|invalid|false"),
            // Empty required field (id)
            Arbitraries.just("|name|loc|2026-05-10T09:00|2026-05-10T10:00|90||false"),
            // Empty required field (name)
            Arbitraries.just("id||loc|2026-05-10T09:00|2026-05-10T10:00|90||false"),
            // Malformed boolean
            Arbitraries.just("id|name|loc|2026-05-10T09:00|2026-05-10T10:00|90||maybe"),
            Arbitraries.just("id|name|loc|2026-05-10T09:00|2026-05-10T10:00|90||1"),
            // Completely random garbage
            Arbitraries.strings().alpha().numeric().withChars('|', ',', ' ').ofMinLength(5).ofMaxLength(50)
        );
    }

    /**
     * Generates invalid group meeting lines for testing resilient parsing.
     */
    @Provide
    public static Arbitrary<String> invalidGroupMeetingLines() {
        return Arbitraries.oneOf(
            // Wrong field count
            Arbitraries.just("id|name|duration"),
            // Invalid integer (duration)
            Arbitraries.just("id|name|not-a-number|location|u001,u002"),
            // Empty required field (id)
            Arbitraries.just("|name|90|location|u001,u002"),
            // Empty required field (name)
            Arbitraries.just("id||90|location|u001,u002"),
            // Completely random garbage
            Arbitraries.strings().alpha().numeric().withChars('|', ',', ' ').ofMinLength(5).ofMaxLength(50)
        );
    }

    /**
     * Generates invalid reminder lines for testing resilient parsing.
     */
    @Provide
    public static Arbitrary<String> invalidReminderLines() {
        return Arbitraries.oneOf(
            // Wrong field count
            Arbitraries.just("id|appointmentId"),
            // Invalid integer (triggerMinutes)
            Arbitraries.just("id|appt-001|not-a-number|message"),
            // Empty required field (id)
            Arbitraries.just("|appt-001|15|message"),
            // Empty required field (appointmentId)
            Arbitraries.just("id||15|message"),
            // Completely random garbage
            Arbitraries.strings().alpha().numeric().withChars('|', ',', ' ').ofMinLength(5).ofMaxLength(50)
        );
    }

    /**
     * Generates valid appointment lines for testing mixed valid/invalid parsing.
     */
    @Provide
    public static Arbitrary<List<String>> validAppointmentLines() {
        return appointments().map(appt -> 
            String.format("%s|%s|%s|%s|%s|%d|%s|%s",
                appt.getId(),
                appt.getName(),
                appt.getLocation(),
                appt.getStartTime().toString(),
                appt.getEndTime().toString(),
                appt.getDuration(),
                appt.getReminder() != null ? appt.getReminder().toString() : "",
                appt.isGroup()
            )
        ).list().ofMinSize(1).ofMaxSize(10);
    }

    /**
     * Generates directory paths for testing directory creation.
     * - Format: ./test-dirs/{random-string}
     * - Path component: 5-50 characters (alphanumeric, /, _, -)
     */
    @Provide
    public static Arbitrary<String> directoryPaths() {
        return Arbitraries.strings()
            .alpha()
            .numeric()
            .withChars('/', '_', '-')
            .ofMinLength(5)
            .ofMaxLength(50)
            .map(s -> "./test-dirs/" + s.replace("//", "/"));
    }
}
