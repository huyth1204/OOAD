# Test Infrastructure Setup

## Overview

This directory contains the test infrastructure for the file persistence feature. The tests use:

- **jqwik 1.8.2**: Property-based testing framework
- **JUnit 5.10.1**: Test runner and assertions
- **Mockito 5.8.0**: Mocking framework for testing error scenarios
- **AssertJ 3.25.1**: Fluent assertions for better readability

## Directory Structure

```
test/
└── calendar/
    └── persistence/
        ├── TestGenerators.java      # jqwik generators for domain objects
        ├── SmokeTest.java           # Smoke test to verify framework setup
        └── (future test files)
```

## Running Tests

### Prerequisites

1. **Maven**: Install Apache Maven 3.6+ from https://maven.apache.org/download.cgi
2. **Java**: Java 11+ is required (Java 17 is installed)

### Running All Tests

```bash
cd CalendarApp
mvn clean test
```

### Running Specific Test Class

```bash
mvn test -Dtest=SmokeTest
```

### Running with Coverage Report

```bash
mvn clean test jacoco:report
# View report at: target/site/jacoco/index.html
```

## Test Generators

The `TestGenerators.java` class provides jqwik generators for:

- **Appointments**: Random appointments with valid data (100+ iterations)
- **GroupMeetings**: Random group meetings with participants
- **Reminders**: Random reminders linked to appointments
- **Special Characters**: Strings with pipes, commas, backslashes, newlines
- **Invalid Lines**: Malformed data for testing resilient parsing
- **Directory Paths**: Random directory paths for testing directory creation

## Smoke Test

The `SmokeTest.java` verifies that:

1. ✅ JUnit 5 framework works
2. ✅ jqwik property-based testing works
3. ✅ AssertJ fluent assertions work
4. ✅ TestGenerators can be used in property tests
5. ✅ Domain objects (Appointment, GroupMeeting, Reminder) can be instantiated

## Property-Based Testing

Property tests are annotated with `@Property` and run 100+ iterations by default. Each property test validates a universal correctness property across randomized inputs.

Example:
```java
@Property
void appointmentRoundTripPreservesData(@ForAll("appointments") Appointment appt) {
    String formatted = PrettyPrinter.formatAppointment(appt);
    Appointment parsed = DataParser.parseAppointment(formatted);
    assertAppointmentEquals(appt, parsed);
}
```

## Next Steps

After verifying the smoke test passes:

1. Implement `PrettyPrinter` and `DataParser` classes
2. Write property tests for serialization round-trip (Property 1)
3. Write property tests for special character escaping (Property 4)
4. Continue with remaining tasks in the implementation plan

## Troubleshooting

### Maven Not Found

If `mvn` command is not recognized:
1. Download Maven from https://maven.apache.org/download.cgi
2. Extract to a directory (e.g., `C:\Program Files\Apache\maven`)
3. Add `bin` directory to PATH environment variable
4. Restart terminal and verify with `mvn -version`

### Dependency Download Issues

If dependencies fail to download:
1. Check internet connection
2. Try clearing Maven cache: `mvn dependency:purge-local-repository`
3. Check Maven settings in `~/.m2/settings.xml`

### Test Failures

If smoke test fails:
1. Verify Java version: `java -version` (should be 11+)
2. Check Maven version: `mvn -version` (should be 3.6+)
3. Clean and rebuild: `mvn clean compile test`
4. Check test output for specific error messages
