package calendar.persistence;

import calendar.Appointment;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * RoomConflictChecker – doc appointments cua TAT CA user
 * de kiem tra trung phong giua cac user khac nhau.
 */
public class RoomConflictChecker {

    private static final Logger log = Logger.getLogger(RoomConflictChecker.class.getName());
    private static final String BASE_DATA_DIR = "./data";

    /**
     * Kiem tra phong co bi dat boi user khac khong.
     * @param currentUserId user dang dang nhap (bo qua data cua chinh ho)
     * @param location phong can kiem tra
     * @param start thoi gian bat dau
     * @param end thoi gian ket thuc
     * @return danh sach conflict: [userId, appointmentName, start-end]
     */
    public static List<String[]> checkRoomConflictAcrossUsers(
            String currentUserId, String location, LocalDateTime start, LocalDateTime end) {

        List<String[]> conflicts = new ArrayList<>();
        if (location == null || location.isBlank()) return conflicts;

        File dataRoot = new File(BASE_DATA_DIR);
        if (!dataRoot.exists() || !dataRoot.isDirectory()) return conflicts;

        // Duyet qua tung thu muc user trong ./data/
        File[] userDirs = dataRoot.listFiles(File::isDirectory);
        if (userDirs == null) return conflicts;

        for (File userDir : userDirs) {
            String userId = userDir.getName();
            if (userId.equals(currentUserId)) continue; // Bo qua chinh minh

            // Doc appointments.txt cua user do
            try {
                FileHandler fh = new FileHandler(userDir.getPath());
                List<String> lines = fh.readLines("appointments.txt");
                for (String line : lines) {
                    try {
                        Appointment a = DataParser.parseAppointment(line);
                        boolean sameRoom = location.trim().equalsIgnoreCase(
                            a.getLocation() != null ? a.getLocation().trim() : "");
                        boolean timeOverlap = start.isBefore(a.getEndTime())
                            && end.isAfter(a.getStartTime());
                        if (sameRoom && timeOverlap) {
                            conflicts.add(new String[]{
                                userId,
                                a.getName(),
                                a.formatStart() + " - " + a.formatEnd()
                            });
                        }
                    } catch (ParseException e) {
                        // Bo qua dong loi
                    }
                }
            } catch (Exception e) {
                log.warning("Khong doc duoc data cua user " + userId + ": " + e.getMessage());
            }
        }
        return conflicts;
    }
}
