package calendar.persistence;

import calendar.Appointment;
import calendar.GroupMeeting;
import calendar.Reminder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DataParser parses text lines into domain objects (Appointment, GroupMeeting, Reminder)
 * according to the defined persistence format.
 * 
 * Format specifications:
 * - Appointment: id|name|location|startTime|endTime|duration|reminder|isGroup
 * - GroupMeeting: id|name|duration|location|participant1,participant2,participant3
 * - Reminder: id|appointmentId|triggerMinutes|message
 * 
 * Special characters are unescaped:
 * - \| → Pipe (|)
 * - \, → Comma (,)
 * - \\ → Backslash (\)
 * - \n → Newline (\n)
 */
public class DataParser {
    
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    /**
     * Parses an Appointment from a text line.
     * Format: id|name|location|startTime|endTime|duration|reminder|isGroup
     * 
     * @param line the text line to parse
     * @return parsed Appointment object
     * @throws ParseException if the line format is invalid
     */
    public static Appointment parseAppointment(String line) throws ParseException {
        if (line == null || line.trim().isEmpty()) {
            throw new ParseException("Line is null or empty");
        }
        
        String[] fields = splitLine(line, 8);
        
        try {
            String id = unescapeString(fields[0]);
            String name = unescapeString(fields[1]);
            String location = unescapeString(fields[2]);
            LocalDateTime startTime = parseDateTime(fields[3]);
            LocalDateTime endTime = parseDateTime(fields[4]);
            int duration = Integer.parseInt(fields[5]);
            Integer reminder = fields[6].isEmpty() ? null : Integer.parseInt(fields[6]);
            boolean isGroup = Boolean.parseBoolean(fields[7]);
            
            // Validate required fields
            if (id.isEmpty()) {
                throw new ParseException("Appointment ID cannot be empty");
            }
            if (name.isEmpty()) {
                throw new ParseException("Appointment name cannot be empty");
            }
            
            return new Appointment(id, name, location, startTime, endTime, duration, reminder, isGroup);
            
        } catch (NumberFormatException e) {
            throw new ParseException("Invalid number format in appointment: " + e.getMessage());
        } catch (DateTimeParseException e) {
            throw new ParseException("Invalid datetime format in appointment: " + e.getMessage());
        }
    }
    
    /**
     * Parses a GroupMeeting from a text line.
     * Format: id|name|duration|location|startTime|endTime|participant1,participant2,participant3
     * 
     * @param line the text line to parse
     * @return parsed GroupMeeting object
     * @throws ParseException if the line format is invalid
     */
    public static GroupMeeting parseGroupMeeting(String line) throws ParseException {
        if (line == null || line.trim().isEmpty()) {
            throw new ParseException("Line is null or empty");
        }
        
        String[] fields = splitLine(line, 7);
        
        try {
            String id = unescapeString(fields[0]);
            String name = unescapeString(fields[1]);
            int duration = Integer.parseInt(fields[2]);
            String location = unescapeString(fields[3]);
            LocalDateTime startTime = parseDateTime(fields[4]);
            LocalDateTime endTime = parseDateTime(fields[5]);
            List<String> participants = parseParticipantsList(fields[6]);
            
            // Validate required fields
            if (id.isEmpty()) {
                throw new ParseException("GroupMeeting ID cannot be empty");
            }
            if (name.isEmpty()) {
                throw new ParseException("GroupMeeting name cannot be empty");
            }
            
            return new GroupMeeting(id, name, duration, location, startTime, endTime, participants);
            
        } catch (NumberFormatException e) {
            throw new ParseException("Invalid number format in group meeting: " + e.getMessage());
        } catch (DateTimeParseException e) {
            throw new ParseException("Invalid datetime format in group meeting: " + e.getMessage());
        }
    }
    
    /**
     * Parses a Reminder from a text line.
     * Format: id|appointmentId|triggerMinutes|message
     * 
     * @param line the text line to parse
     * @return parsed Reminder object
     * @throws ParseException if the line format is invalid
     */
    public static Reminder parseReminder(String line) throws ParseException {
        if (line == null || line.trim().isEmpty()) {
            throw new ParseException("Line is null or empty");
        }
        
        String[] fields = splitLine(line, 4);
        
        try {
            String id = unescapeString(fields[0]);
            String appointmentId = unescapeString(fields[1]);
            int triggerMinutes = Integer.parseInt(fields[2]);
            String message = unescapeString(fields[3]);
            
            // Validate required fields
            if (id.isEmpty()) {
                throw new ParseException("Reminder ID cannot be empty");
            }
            if (appointmentId.isEmpty()) {
                throw new ParseException("Reminder appointmentId cannot be empty");
            }
            
            return new Reminder(id, appointmentId, triggerMinutes, message);
            
        } catch (NumberFormatException e) {
            throw new ParseException("Invalid number format in reminder: " + e.getMessage());
        }
    }
    
    /**
     * Validates if a line is a valid Appointment format.
     * 
     * @param line the line to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidAppointmentLine(String line) {
        try {
            parseAppointment(line);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
    
    /**
     * Validates if a line is a valid GroupMeeting format.
     * 
     * @param line the line to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidGroupMeetingLine(String line) {
        try {
            parseGroupMeeting(line);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
    
    /**
     * Validates if a line is a valid Reminder format.
     * 
     * @param line the line to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidReminderLine(String line) {
        try {
            parseReminder(line);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
    
    /**
     * Unescapes special characters in a string.
     * Unescaping rules:
     * - \\ → Backslash (\)
     * - \| → Pipe (|)
     * - \, → Comma (,)
     * - \n → Newline (\n)
     * - \r → Carriage return (\r)
     * - \t → Tab (\t)
     * 
     * @param escaped the escaped string
     * @return unescaped string
     */
    public static String unescapeString(String escaped) {
        if (escaped == null) {
            return "";
        }
        
        StringBuilder result = new StringBuilder();
        boolean escapeNext = false;
        
        for (int i = 0; i < escaped.length(); i++) {
            char c = escaped.charAt(i);
            
            if (escapeNext) {
                switch (c) {
                    case '\\':
                        result.append('\\');
                        break;
                    case '|':
                        result.append('|');
                        break;
                    case ',':
                        result.append(',');
                        break;
                    case 'n':
                        result.append('\n');
                        break;
                    case 'r':
                        result.append('\r');
                        break;
                    case 't':
                        result.append('\t');
                        break;
                    default:
                        // Unknown escape sequence, keep as-is
                        result.append('\\').append(c);
                        break;
                }
                escapeNext = false;
            } else if (c == '\\') {
                escapeNext = true;
            } else {
                result.append(c);
            }
        }
        
        // Handle trailing backslash
        if (escapeNext) {
            result.append('\\');
        }
        
        return result.toString();
    }
    
    /**
     * Parses a LocalDateTime from ISO-8601 format string.
     * 
     * @param dateTimeStr the datetime string to parse
     * @return parsed LocalDateTime
     * @throws DateTimeParseException if the format is invalid
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) throws DateTimeParseException {
        return LocalDateTime.parse(dateTimeStr, ISO_FORMATTER);
    }
    
    /**
     * Parses a comma-separated list of participant IDs.
     * Each participant ID is unescaped to handle special characters.
     * 
     * @param listStr the comma-separated string
     * @return list of unescaped participant IDs
     */
    public static List<String> parseParticipantsList(String listStr) {
        if (listStr == null || listStr.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        // Split by unescaped commas
        List<String> participants = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean escapeNext = false;
        
        for (int i = 0; i < listStr.length(); i++) {
            char c = listStr.charAt(i);
            
            if (escapeNext) {
                current.append('\\').append(c);
                escapeNext = false;
            } else if (c == '\\') {
                escapeNext = true;
            } else if (c == ',') {
                // Unescaped comma - split here
                participants.add(unescapeString(current.toString()));
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        
        // Add the last participant
        if (current.length() > 0 || escapeNext) {
            if (escapeNext) {
                current.append('\\');
            }
            participants.add(unescapeString(current.toString()));
        }
        
        return participants;
    }
    
    /**
     * Splits a line by unescaped pipe characters.
     * 
     * @param line the line to split
     * @param expectedFields the expected number of fields
     * @return array of field strings
     * @throws ParseException if the field count doesn't match expected
     */
    private static String[] splitLine(String line, int expectedFields) throws ParseException {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean escapeNext = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (escapeNext) {
                current.append('\\').append(c);
                escapeNext = false;
            } else if (c == '\\') {
                escapeNext = true;
            } else if (c == '|') {
                // Unescaped pipe - split here
                fields.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        
        // Add the last field
        if (escapeNext) {
            current.append('\\');
        }
        fields.add(current.toString());
        
        if (fields.size() != expectedFields) {
            throw new ParseException(
                String.format("Expected %d fields but found %d in line: %s", 
                    expectedFields, fields.size(), line));
        }
        
        return fields.toArray(new String[0]);
    }
}
