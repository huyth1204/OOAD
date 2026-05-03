# 📖 Hướng Dẫn Sử Dụng - Calendar App

## 🚀 Cách chạy ứng dụng

### Bước 1: Compile code
```bash
cd CalendarApp
javac -d bin -encoding UTF-8 src/calendar/*.java src/calendar/persistence/*.java
```

### Bước 2: Chạy ứng dụng
```bash
java -cp bin calendar.Main
```

**Hoặc sử dụng file batch (Windows):**
```bash
run.bat
```

---

## ✨ Tính năng mới đã thêm

### 1. 💾 Lưu trữ dữ liệu vào file txt

**Dữ liệu được lưu tự động:**
- ✅ Khi thêm/xóa cuộc hẹn → Lưu ngay lập tức
- ✅ Khi thoát ứng dụng → Lưu tự động
- ✅ Khi khởi động lại → Tự động load dữ liệu

**Vị trí file:**
```
CalendarApp/data/
├── appointments.txt      (Danh sách cuộc hẹn)
├── groupmeetings.txt     (Danh sách họp nhóm)
└── reminders.txt         (Danh sách nhắc nhở)
```

**Format file (human-readable):**
```
appt-001|Họp nhóm PTTKHTDT|Phòng B102|2026-05-12T14:00|2026-05-12T15:30|90|15|true
```

### 2. 👥 Chọn loại cuộc hẹn (Cá nhân / Nhóm)

**Khi tạo cuộc hẹn mới:**
1. Click nút "➕ Thêm cuộc hẹn"
2. Điền thông tin cuộc hẹn
3. **Chọn loại cuộc hẹn:**
   - 👤 **Cá nhân**: Cuộc hẹn cá nhân
   - 👥 **Nhóm**: Cuộc họp nhóm
4. Click "✔ Lưu cuộc hẹn"

**Hiển thị trong danh sách:**
- Cuộc hẹn **Cá nhân**: `[Cá nhân] Tên cuộc hẹn`
- Cuộc hẹn **Nhóm**: `[Nhóm] Tên cuộc hẹn`

---

## 📋 Các tính năng chính

### ✅ Thêm cuộc hẹn
1. Click "➕ Thêm cuộc hẹn"
2. Nhập thông tin:
   - **Tên cuộc hẹn** (bắt buộc)
   - **Địa điểm** (tùy chọn)
   - **Thời gian bắt đầu - kết thúc** (format: yyyy-MM-dd HH:mm)
   - **Nhắc nhở** (5 phút, 15 phút, 30 phút, 1 giờ, 1 ngày trước)
   - **Loại cuộc hẹn** (Cá nhân / Nhóm)
3. Click "✔ Lưu cuộc hẹn"

### 🔍 Kiểm tra xung đột thời gian
- Nếu có cuộc hẹn trùng giờ → Hiển thị cảnh báo
- Bạn có thể:
  - Chọn giờ khác
  - Thay thế cuộc hẹn cũ
  - Hủy bỏ

### 🤝 Tự động phát hiện họp nhóm
- Nếu tên và thời lượng trùng với họp nhóm có sẵn → Hỏi có muốn tham gia không
- Nếu đồng ý → Tự động thêm vào danh sách thành viên

### 🗑️ Xóa cuộc hẹn
1. Chọn cuộc hẹn trong danh sách
2. Click "🗑️ Xóa cuộc hẹn đã chọn"
3. Xác nhận xóa

---

## 🎯 Ví dụ sử dụng

### Tạo cuộc hẹn cá nhân
```
Tên: Đi khám răng
Địa điểm: Nha khoa ABC
Thời gian: 2026-05-15 09:00 → 2026-05-15 10:00
Nhắc nhở: 15 phút trước
Loại: 👤 Cá nhân
```

### Tạo cuộc họp nhóm
```
Tên: Họp nhóm PTTKHTDT
Địa điểm: Phòng B102
Thời gian: 2026-05-20 14:00 → 2026-05-20 15:30
Nhắc nhở: 30 phút trước
Loại: 👥 Nhóm
```

---

## 🔧 Xử lý lỗi

### Lỗi: "Không tìm thấy file dữ liệu"
- **Nguyên nhân**: Lần đầu chạy ứng dụng
- **Giải pháp**: Ứng dụng sẽ tự động tạo dữ liệu mẫu

### Lỗi: "Định dạng thời gian sai"
- **Nguyên nhân**: Nhập sai format thời gian
- **Giải pháp**: Sử dụng format `yyyy-MM-dd HH:mm`
  - Ví dụ: `2026-05-15 09:00`

### Lỗi: "Thời gian kết thúc phải sau thời gian bắt đầu"
- **Nguyên nhân**: Thời gian kết thúc <= thời gian bắt đầu
- **Giải pháp**: Kiểm tra lại thời gian

---

## 📊 Cấu trúc dữ liệu

### File: appointments.txt
```
Format: id|name|location|startTime|endTime|duration|reminder|isGroup

Ví dụ:
appt-001|Họp nhóm PTTKHTDT|Phòng B102|2026-05-12T14:00|2026-05-12T15:30|90|15|true
appt-002|Đi khám răng|Nha khoa ABC|2026-05-15T09:00|2026-05-15T10:00|60|15|false
```

### File: groupmeetings.txt
```
Format: id|name|duration|location|participant1,participant2,participant3

Ví dụ:
gm001|Họp nhóm PTTKHTDT|90|Phòng B102|u002,u003,u005
gm002|Seminar Kỹ thuật phần mềm|120|Hội trường A|u004
```

### File: reminders.txt
```
Format: id|appointmentId|triggerMinutes|message

Ví dụ:
rem-001|appt-001|15|Nhắc họp nhóm PTTKHTDT
rem-002|appt-002|15|Nhắc đi khám răng
```

---

## 💡 Tips & Tricks

1. **Backup dữ liệu**: Copy thư mục `data/` để backup
2. **Chỉnh sửa thủ công**: Có thể mở file `.txt` bằng Notepad để chỉnh sửa
3. **Import dữ liệu**: Copy file `.txt` vào thư mục `data/` để import
4. **Xóa tất cả dữ liệu**: Xóa thư mục `data/` và khởi động lại app

---

## 🐛 Báo lỗi

Nếu gặp lỗi, vui lòng kiểm tra:
1. Java version >= 11
2. Encoding UTF-8 khi compile
3. Quyền ghi file trong thư mục `data/`

---

## 📝 Ghi chú

- Dữ liệu được lưu tự động, không cần thao tác thủ công
- File txt có thể đọc và chỉnh sửa bằng text editor
- Hỗ trợ tiếng Việt và emoji đầy đủ
- Tự động xử lý xung đột thời gian
- Tự động phát hiện và gợi ý tham gia họp nhóm

---

**Phiên bản**: 2.0  
**Ngày cập nhật**: 2026-05-03  
**Môn học**: Phân tích & Thiết kế Hướng Đối Tượng
