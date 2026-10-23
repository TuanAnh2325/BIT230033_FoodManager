# BIT230033_FoodManager 🍲📱

> **Ứng Dụng Quản Lý Món Ăn Chuyên Nghiệp trên Nền Tảng Android (Offline-First)**  
> **Mã số sinh viên (MSSV):** BIT230033  
> **Ngôn ngữ phát triển:** Kotlin  
> **Kiến trúc:** MVVM (Model - View - ViewModel) + Repository Pattern  

---

## 📖 Giới Thiệu Dự Án

**BIT230033_FoodManager** là ứng dụng Android quản lý thực đơn và món ăn toàn diện được thiết kế theo chuẩn kiến trúc hiện đại của Google. Ứng dụng hỗ trợ trải nghiệm **Offline-First**, đảm bảo người dùng có thể thao tác mượt mà ngay cả khi không có kết nối Internet, sau đó tự động đồng bộ hóa lên máy chủ đám mây (MockAPI) khi có mạng trở lại.

---

## ✨ Tính Năng Nổi Bật

### 1. 🗄️ Quản Lý Danh Mục (Categories)
- Thêm, sửa, xem danh sách danh mục món ăn trực quan.
- **Ràng buộc toàn vẹn dữ liệu (ForeignKey RESTRICT):** Ngăn chặn xóa danh mục khi vẫn còn món ăn thuộc danh mục đó, hiển thị thông báo cảnh báo hai lớp (`countDishesByCategory`).

### 2. 🥗 Quản Lý Món Ăn (Dishes) & Đa Phương Tiện
- Thêm mới, chỉnh sửa, xóa món ăn với đầy đủ thông tin: Tên món, danh mục, mô tả, giá cả, thời gian chế biến, trạng thái kinh doanh (Đang bán / Ngừng bán).
- **Phân loại thông minh (`isQuick`):** Tự động đánh dấu món "Nhanh" nếu thời gian chế biến từ 15 phút trở xuống.
- **Ràng buộc duy nhất (Unique Index):** Đảm bảo không trùng tên món ăn trong cùng một danh mục `(BIT230033_CategoryId, BIT230033_DishName)`.
- **Hỗ trợ đa hình ảnh:** Mỗi món ăn có thể lưu trữ nhiều ảnh với 1 ảnh đại diện (Primary / Thumbnail).

### 3. 🔍 Tìm Kiếm & Bộ Lọc Nâng Cao Đa Điều Kiện
- Tìm kiếm theo tên tức thời qua luồng phản ứng `StateFlow`.
- Bộ lọc mở rộng dạng `BottomSheetDialog`:
  - Lọc theo danh mục.
  - Lọc theo trạng thái kinh doanh (Tất cả / Đang bán / Ngừng bán).
  - Lọc theo khoảng giá linh hoạt (Min - Max).
  - Sắp xếp đa dạng: Theo tên món, giá tăng/giảm dần, thời gian chế biến.
- **Bảo lưu trạng thái lọc (`FilterState`):** Không bị mất bộ lọc khi chuyển qua lại giữa các màn hình.

### 4. 🌐 Hỗ Trợ Đa Ngôn Ngữ Song Ngữ (English / Tiếng Việt)
- Nút chuyển đổi nhanh ngôn ngữ (EN / VI) ngay trên thanh công cụ Toolbar.
- Chuyển ngữ tức thời giao diện và chuỗi dữ liệu danh mục (`CategoryLocalizationHelper`).
- Ghi nhớ tùy chọn ngôn ngữ vào `SharedPreferences` cho các lần khởi chạy tiếp theo.

### 5. 🛡️ Kiểm Tra Dữ Liệu Đầu Vào Hai Lớp (Two-Layer Validation)
- Báo lỗi trực tiếp trên từng trường nhập liệu (`TextInputLayout`).
- Hiển thị Toast thông báo chi tiết bằng ngôn ngữ hiện hành khi vi phạm ràng buộc.

### 6. 🔄 Kiến Trúc Offline-First & Tự Động Đồng Bộ (Auto-Sync)
- Lưu trữ cục bộ mạnh mẽ với **Room SQLite Database**. Toàn bộ bảng và cột đều tuân thủ định dạng chuẩn tiền tố `BIT230033_`.
- Theo dõi trạng thái đồng bộ: `SYNCED (0)`, `PENDING_INSERT (1)`, `PENDING_UPDATE (2)`, `PENDING_DELETE (3)`.
- **WorkManager (`SyncWorker`) & NetworkMonitor:** Giám sát trạng thái kết nối mạng và kích hoạt đồng bộ hóa ngầm lên MockAPI một cách an toàn và tối ưu tài nguyên thiết bị.

---

## 🛠️ Công Nghệ & Thư Viện Sử Dụng

| Thành Phần | Công Nghệ / Thư Viện | Phiên Bản | Ghi Chú |
|------------|----------------------|-----------|---------|
| **Ngôn ngữ** | Kotlin | 1.9.x | Chuẩn JVM 17 |
| **Android SDK** | Min SDK 26 / Target SDK 34 | Android 14 (UpsideDownCake) | Tương thích cao |
| **Kiến trúc** | MVVM + Repository Pattern | Jetpack Architecture | Clean Code, Decoupled |
| **Database** | Room Database (SQLite) | 2.6.1 (KSP) | Relational queries (`DishWithDetails`) |
| **Bất đồng bộ** | Kotlin Coroutines & StateFlow | 1.8.0 | Reactive streams |
| **Giao diện (UI)** | Material 3, ViewBinding | 1.12.0 | RecyclerView, ViewPager2 |
| **Mạng (Network)** | Retrofit 2 & OkHttp 3, Gson | 2.11.0 / 4.12.0 | MockAPI Integration |
| **Tác vụ ngầm** | WorkManager | 2.9.0 | Background Auto Sync |
| **Tải ảnh** | Coil Image Loader | 2.6.0 | Caching & Async loading |

---

## 📂 Cấu Trúc Dự Án (Project Structure)

```text
com.example.bit230033_foodmanager
├── FoodApplication.kt               # Entry point, khởi tạo RoomDB & SyncManager
├── data
│   ├── local
│   │   ├── dao                     # Data Access Objects (DishDao, CategoryDao, DishImageDao)
│   │   ├── database                # BIT230033_FoodDatabase (Room DB)
│   │   └── entity                  # Entity: BIT230033_Dish, BIT230033_DishCategory, BIT230033_DishImage
│   ├── remote
│   │   ├── dto                     # Data Transfer Objects cho MockAPI
│   │   ├── MockApiService.kt       # Retrofit API interface
│   │   └── RetrofitClient.kt       # Cấu hình OkHttp & Retrofit
│   └── repository                  # DishRepository, CategoryRepository
├── sync
│   ├── NetworkMonitor.kt           # Theo dõi trạng thái mạng theo thời gian thực
│   ├── SyncManager.kt              # Điều phối tác vụ đồng bộ
│   └── SyncWorker.kt               # Worker xử lý đồng bộ ngầm của WorkManager
├── ui
│   ├── base                        # BaseActivity, BaseViewModel
│   ├── category                    # Quản lý danh mục (ManageCategoryActivity, Adapter, VM)
│   ├── detail                      # Chi tiết món ăn (DishDetailActivity, ImageGalleryAdapter)
│   ├── filter                      # Hộp thoại lọc (FilterBottomSheetDialog)
│   ├── form                        # Thêm / Sửa món ăn (AddEditDishActivity, FormImageAdapter)
│   └── list                        # Màn hình chính danh sách món (MainActivity, DishAdapter, VM)
└── utils
    ├── CategoryLocalizationHelper.kt
    ├── Constants.kt
    ├── LanguageToggleHelper.kt
    ├── LocaleHelper.kt
    └── ValidationUtils.kt
```

---

## 🚀 Hướng Dẫn Cài Đặt & Chạy Ứng Dụng

### Yêu Cầu Hệ Thống
- **Android Studio:** Ladybug / Hedgehog / Jellyfish hoặc mới hơn
- **JDK:** Java 17 hoặc 21
- **Android SDK:** Compile SDK 34, Min SDK 26

### Các Bước Thực Hiện
1. **Clone repository về máy:**
   ```bash
   git clone https://github.com/TuanAnh2325/BIT230033_FoodManager.git
   ```
2. **Mở dự án trong Android Studio:**
   - Chọn `File` -> `Open` -> Trỏ đến thư mục `BIT230033_FoodManager`.
3. **Đồng bộ Gradle (Sync Project with Gradle Files):**
   - Đợi Android Studio tải các dependencies và tạo các file KSP.
4. **Chạy ứng dụng:**
   - Chọn thiết bị máy ảo (Emulator) hoặc máy thật (Android 8.0 Oreo trở lên).
   - Nhấn nút **Run 'app'** (`Shift + F10`).

---

## 👤 Thông Tin Tác Giả
- **Họ và tên / MSSV:** BIT230033
- **GitHub:** [@TuanAnh2325](https://github.com/TuanAnh2325)
- **Repository:** [BIT230033_FoodManager](https://github.com/TuanAnh2325/BIT230033_FoodManager)
