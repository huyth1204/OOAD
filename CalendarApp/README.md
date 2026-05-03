# Calendar Appointment App – Java Swing

## Cách chạy

### Cách 1: Dùng VS Code
1. Cài extension "Extension Pack for Java"
2. Mở thư mục này trong VS Code
3. Mở file `src/calendar/Main.java`
4. Nhấn **▶ Run** phía trên method `main()`

### Cách 2: Dùng Terminal / Command Prompt
```
# Windows
run.bat

# Linux/Mac
javac -d bin src/calendar/*.java
java -cp bin calendar.Main
```

## Cấu trúc
- `Appointment.java` – Model cuộc hẹn
- `Calendar.java`    – Business logic (validateAndSubmit, checkTimeConflict, ...)
- `GroupMeeting.java`– Model cuộc họp nhóm
- `Reminder.java`    – Model nhắc nhở
- `CalendarUI.java`  – Giao diện chính (JFrame + JTable)
- `AddAppointmentDialog.java` – Dialog thêm cuộc hẹn (Sequence Diagram flow)
- `Main.java`        – Entry point
