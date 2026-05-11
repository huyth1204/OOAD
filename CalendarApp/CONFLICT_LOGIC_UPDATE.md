# Cập nhật Logic Kiểm tra Xung đột

## Thay đổi

Đã cập nhật logic kiểm tra xung đột trong `AddAppointmentDialog.java` để:
1. Ưu tiên kiểm tra **trùng phòng** trước **trùng giờ**
2. **Kiểm tra trùng phòng giữa TẤT CẢ users** (không chỉ user hiện tại)

## Logic mới

### Bước 1: Kiểm tra trùng phòng giữa TẤT CẢ users

**Quan trọng**: Khi kiểm tra trùng phòng, hệ thống sẽ load appointments từ **tất cả user folders** (`./data/u001/`, `./data/u002/`, `./data/u003/`, v.v.) để đảm bảo không có 2 user đặt cùng phòng cùng lúc.

Nếu phát hiện trùng phòng, hiển thị dialog với 2 lựa chọn:

- **✅ Tham gia cuộc hẹn này**: 
  - Tạo appointment mới cho user hiện tại với cùng thông tin (tên, thời gian, địa điểm)
  - Lưu vào file `appointments.txt` của user
  - Appointment sẽ hiển thị trong calendar của user
  - User có thể chọn reminder riêng cho cuộc hẹn này
  
- **🔄 Chọn phòng khác**: Quay lại form, focus vào ô địa điểm để user thay đổi

### Bước 2: Kiểm tra trùng giờ (khác địa điểm) - chỉ user hiện tại

Nếu KHÔNG có trùng phòng, kiểm tra trùng giờ với các cuộc hẹn ở địa điểm khác **của user hiện tại**:

- Hiển thị **cảnh báo** (không chặn)
- Cho phép user tiếp tục tạo cuộc hẹn nếu muốn
- Hiển thị danh sách các cuộc hẹn trùng giờ kèm địa điểm

## Ví dụ

### Trường hợp 1: User khác nhau đặt cùng phòng
```
User u001 tạo cuộc hẹn:
- Thời gian: 10:00 - 11:00
- Địa điểm: Phòng B102

User u002 đã có cuộc hẹn:
- Tên: "Họp nhóm PTTKHTDT"
- Thời gian: 10:00 - 11:00  
- Địa điểm: Phòng B102

→ Hệ thống phát hiện trùng phòng giữa 2 users
→ Hiển thị dialog: "Tham gia cuộc hẹn này" hoặc "Chọn phòng khác"

Nếu u001 chọn "Tham gia":
→ Tạo appointment mới trong ./data/u001/appointments.txt
→ Appointment có cùng tên, thời gian, địa điểm với cuộc hẹn của u002
→ Hiển thị trong calendar của u001
```

### Trường hợp 2: Cùng user, trùng giờ, khác phòng
```
User u001 tạo cuộc hẹn:
- Thời gian: 10:00 - 11:00
- Địa điểm: Phòng B102

User u001 đã có cuộc hẹn:
- Thời gian: 10:00 - 11:00
- Địa điểm: Phòng A101

→ Hiển thị cảnh báo: "Bạn đã có cuộc hẹn trùng giờ (nhưng khác địa điểm)"
→ Cho phép tiếp tục nếu user xác nhận
```

## Files đã thay đổi

### 1. `CalendarApp/src/calendar/persistence/PersistenceManager.java`
- Thêm method `loadAllUsersAppointments()`: Load appointments từ tất cả user folders

### 2. `CalendarApp/src/calendar/persistence/FileHandler.java`
- Thêm method `getDataDirectory()`: Trả về đường dẫn thư mục data

### 3. `CalendarApp/src/calendar/Calendar.java`
- Thêm method `checkLocationConflictAllUsers()`: Kiểm tra trùng phòng giữa tất cả users
- Giữ nguyên method `checkLocationConflict()`: Kiểm tra trùng phòng của user hiện tại
- Giữ nguyên method `checkTimeConflict()`: Kiểm tra trùng giờ của user hiện tại

### 4. `CalendarApp/src/calendar/AddAppointmentDialog.java`
- Thêm import `java.util.ArrayList`
- Thay đổi logic kiểm tra xung đột trong method `runAddFlow()` (dòng ~267-350)
- Sử dụng `calendar.checkLocationConflictAllUsers()` thay vì `checkLocationConflict()`

## Cấu trúc dữ liệu

```
./data/
├── u001/
│   ├── appointments.txt
│   ├── groupmeetings.txt
│   └── reminders.txt
├── u002/
│   ├── appointments.txt
│   ├── groupmeetings.txt
│   └── reminders.txt
└── u003/
    ├── appointments.txt
    ├── groupmeetings.txt
    └── reminders.txt
```

Khi kiểm tra trùng phòng, hệ thống sẽ đọc `appointments.txt` từ **TẤT CẢ** các thư mục user.

## Biên dịch

```bash
javac -encoding UTF-8 -d CalendarApp/bin -cp "CalendarApp/bin;CalendarApp/lib/*" CalendarApp/src/calendar/*.java CalendarApp/src/calendar/persistence/*.java
```

## Chạy ứng dụng

```bash
cd CalendarApp
./run.bat
```

hoặc

```bash
java -cp "bin;lib/*" calendar.Main
```

## Test case

1. **Test trùng phòng giữa users**:
   - Đăng nhập user u001, tạo cuộc hẹn "Họp nhóm PTTKHTDT" tại Phòng B102 lúc 10:00-11:00
   - Đăng nhập user u002, tạo cuộc hẹn tại Phòng B102 lúc 10:00-11:00
   - Kết quả: Hệ thống phát hiện trùng phòng và hiển thị dialog
   - Chọn "Tham gia cuộc hẹn này"
   - Kiểm tra: 
     - File `./data/u002/appointments.txt` có thêm 1 dòng mới
     - Calendar của u002 hiển thị cuộc hẹn "Họp nhóm PTTKHTDT"
     - Cả u001 và u002 đều có cuộc hẹn cùng tên, cùng thời gian, cùng địa điểm

2. **Test trùng giờ, khác phòng (cùng user)**:
   - Đăng nhập user u001, tạo cuộc hẹn tại Phòng A101 lúc 10:00-11:00
   - Tiếp tục tạo cuộc hẹn tại Phòng B102 lúc 10:00-11:00
   - Kết quả: Hiển thị cảnh báo nhưng cho phép tiếp tục
   - Xác nhận tiếp tục
   - Kiểm tra: User u001 có 2 cuộc hẹn trùng giờ nhưng khác phòng

3. **Test reminder khi tham gia cuộc hẹn**:
   - User u001 tạo cuộc hẹn tại Phòng B102
   - User u002 tham gia cuộc hẹn đó và chọn reminder "15 phút trước"
   - Kiểm tra: File `./data/u002/reminders.txt` có reminder mới

