package calendar.persistence;

import org.junit.jupiter.api.Test;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Smoke test to verify test infrastructure is set up correctly.
 * This test validates that:
 * - JUnit 5 is working
 * - jqwik property-based testing is working
 * - AssertJ assertions are available
 * - Test generators can be used
 */
public class SmokeTest {

    /**
     * Simple JUnit 5 unit test to verify basic test execution.
     */
    @Test
    void junitFrameworkWorks() {
        // Arrange
        int expected = 42;
        
        // Act
        int actual = 40 + 2;
        
        // Assert
        assertEquals(expected, actual, "Basic arithmetic should work");
        assertNotNull(this, "Test instance should not be null");
        assertTrue(true, "True should be true");
    }

    /**
     * Simple AssertJ test to verify fluent assertions work.
     */
    @Test
    void assertjFrameworkWorks() {
        // Arrange
        String text = "Hello, World!";
        
        // Assert
        assertThat(text)
            .isNotNull()
            .isNotEmpty()
            .startsWith("Hello")
            .endsWith("!")
            .contains("World");
    }

    /**
     * Simple jqwik property test to verify property-based testing works.
     * Tests that string concatenation is associative.
     */
    @Property
    void jqwikFrameworkWorks(@ForAll String a, @ForAll String b, @ForAll String c) {
        // Property: String concatenation is associative
        // (a + b) + c == a + (b + c)
        String left = (a + b) + c;
        String right = a + (b + c);
        
        assertEquals(left, right, "String concatenation should be associative");
    }

    /**
     * Property test using custom generator from TestGenerators.
     * Verifies that TestGenerators can be used in property tests.
     */
    @Property
    void testGeneratorsWork(@ForAll("appointments") calendar.Appointment appointment) {
        // Verify that generated appointments have valid properties
        assertThat(appointment).isNotNull();
        assertThat(appointment.getId()).isNotEmpty();
        assertThat(appointment.getName()).isNotEmpty();
        assertThat(appointment.getDuration()).isBetween(15, 240);
        assertThat(appointment.getStartTime()).isNotNull();
        assertThat(appointment.getEndTime()).isNotNull();
        
        // Verify that end time is after start time
        assertTrue(appointment.getEndTime().isAfter(appointment.getStartTime()),
            "End time should be after start time");
    }

    /**
     * Provide method for appointments generator.
     * Delegates to TestGenerators.
     */
    @Provide
    net.jqwik.api.Arbitrary<calendar.Appointment> appointments() {
        return TestGenerators.appointments();
    }

    /**
     * Test that verifies domain objects can be instantiated.
     */
    @Test
    void domainObjectsCanBeInstantiated() {
        // Test Appointment
        calendar.Appointment appointment = new calendar.Appointment(
            "test-id",
            "Test Appointment",
            "Test Location",
            java.time.LocalDateTime.of(2026, 5, 10, 9, 0),
            java.time.LocalDateTime.of(2026, 5, 10, 10, 0),
            60,
            15,
            false
        );
        assertThat(appointment.getId()).isEqualTo("test-id");
        assertThat(appointment.getName()).isEqualTo("Test Appointment");

        // Test GroupMeeting
        calendar.GroupMeeting meeting = new calendar.GroupMeeting(
            "gm-001",
            "Test Meeting",
            90,
            "Room A",
            java.util.Arrays.asList("u001", "u002")
        );
        assertThat(meeting.getId()).isEqualTo("gm-001");
        assertThat(meeting.getParticipants()).hasSize(2);

        // Test Reminder
        calendar.Reminder reminder = new calendar.Reminder(
            "rem-001",
            "appt-001",
            15,
            "Test reminder"
        );
        assertThat(reminder.getId()).isEqualTo("rem-001");
        assertThat(reminder.getTriggerMinutes()).isEqualTo(15);
    }
}
