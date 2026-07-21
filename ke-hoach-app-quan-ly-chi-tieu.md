# KẾ HOẠCH MINI PROJECT: APP QUẢN LÝ CHI TIÊU (Android - Java)

**Team:** 3 người | **Thời gian:** 8 ngày

---

## 1. PHÂN TÍCH ĐỀ TÀI

App Quản lý chi tiêu cá nhân giúp người dùng:
- Ghi lại các khoản **thu** (income) và **chi** (expense)
- Phân loại theo **danh mục** (ăn uống, di chuyển, lương, v.v.)
- Xem **tổng quan** số dư, thống kê theo thời gian
- (Nâng cao) Lập **ngân sách**, xem **biểu đồ**, quản lý **nhiều ví**

Về mặt kỹ thuật, đây là app CRUD cơ bản trên **Room Database (SQLite)**, không cần backend/server, phù hợp với thời gian 8 ngày và trình độ nhóm. Kiến trúc đề xuất: **MVVM đơn giản** (Activity/Fragment → ViewModel → Repository → Room DAO), giúp mỗi người làm 1 màn hình mà không đụng code người khác.

---

## 2. NGHIỆP VỤ CẦN LÀM

### 🟢 HARD REQUIREMENT (bắt buộc phải có – điểm cơ bản)

| # | Chức năng | Mô tả |
|---|-----------|-------|
| 1 | Quản lý danh mục (Category) | Thêm/Sửa/Xóa danh mục thu/chi, có icon + tên |
| 2 | Thêm/Sửa/Xóa giao dịch | Nhập số tiền, chọn danh mục, ngày, ghi chú, loại (thu/chi) |
| 3 | Danh sách giao dịch | Hiển thị theo ngày/tháng, có thể xóa/sửa từ danh sách |
| 4 | Tính tổng thu – tổng chi – số dư | Hiển thị ở màn hình tổng quan (Home) |
| 5 | Lưu trữ dữ liệu local | Dùng Room Database (SQLite) |
| 6 | Lọc/xem theo tháng | Chuyển qua lại giữa các tháng để xem giao dịch |
| 7 | Giao diện Material Design cơ bản | RecyclerView, FAB thêm giao dịch, form nhập liệu rõ ràng |

### 🔴 VERY HARD REQUIREMENT (nâng cao – để ăn điểm cao)

Chọn khoảng **2–3 mục** phù hợp với thời gian còn lại sau khi xong phần Hard, ưu tiên theo độ khó tăng dần:

| # | Chức năng | Mô tả | Độ khó |
|---|-----------|-------|--------|
| 1 | Biểu đồ thống kê | Pie chart (theo danh mục) + Bar/Line chart (theo tháng) dùng thư viện **MPAndroidChart** | ⭐⭐ |
| 2 | Tìm kiếm & lọc nâng cao | Lọc theo khoảng ngày, khoảng tiền, danh mục cùng lúc | ⭐⭐ |
| 3 | Ngân sách (Budget) + cảnh báo | Đặt hạn mức chi tiêu/tháng theo danh mục, cảnh báo (đổi màu đỏ/notification) khi vượt | ⭐⭐⭐ |
| 4 | Đa ví/tài khoản | Nhiều ví (tiền mặt, ngân hàng...), chuyển tiền giữa ví | ⭐⭐⭐ |
| 5 | Giao dịch định kỳ | Tự động lặp lại giao dịch cố định hàng tháng (tiền nhà, internet...) | ⭐⭐⭐ |
| 6 | Xuất báo cáo Excel/PDF hoặc chia sẻ | Export dữ liệu tháng ra file | ⭐⭐⭐ |
| 7 | Dark mode | Chuyển đổi giao diện sáng/tối | ⭐ |
| 8 | Notification nhắc nhập chi tiêu | Nhắc hàng ngày qua AlarmManager/WorkManager | ⭐⭐ |
| 9 | Backup/Sync Firebase | Đồng bộ cloud | ⭐⭐⭐⭐ (khá rủi ro trong 8 ngày, chỉ làm nếu dư thời gian) |

**Gợi ý chọn:** #1 (Biểu đồ) + #3 (Ngân sách) là combo "ăn điểm" tốt nhất so với công sức bỏ ra, vì giám khảo dễ thấy trực quan. #7 (Dark mode) gần như miễn phí, nên làm thêm nếu còn thời gian ngày cuối.

---

## 3. KIẾN TRÚC & CHIA PACKAGE (để tránh conflict Git)

Chia code theo **package riêng cho từng tính năng** — nguyên tắc quan trọng nhất để 3 người không đụng file của nhau:

```
com.example.quanlychitieu
├── data
│   ├── model        (Transaction.java, Category.java, Wallet.java...)
│   ├── dao           (TransactionDao, CategoryDao...)
│   ├── AppDatabase.java
│   └── repository    (TransactionRepository, CategoryRepository...)
├── ui
│   ├── home          (Người B phụ trách)
│   ├── category       (Người B phụ trách)
│   ├── transaction     (Người C phụ trách)
│   ├── statistic       (Người A phụ trách)
│   └── budget          (Người A phụ trách)
└── utils              (DateUtils, CurrencyFormatter...)
```

Nguyên tắc: **mỗi người chỉ code trong package `ui.xxx` của mình**, phần `data` (model/dao/database) do 1 người dựng khung sẵn từ ngày 1–2, sau đó ai cần thêm field/table thì báo nhóm trước khi sửa (tránh 2 người cùng sửa 1 file `AppDatabase.java`).

---

## 4. PHÂN CHIA NHIỆM VỤ (3 người)

### 👨 Người A (code mạnh) — Kiến trúc + phần khó nhất
- Ngày 1–2: Dựng khung project, thiết kế schema DB (Entity, DAO, Repository, AppDatabase), setup Git repo + branch strategy
- Ngày 3–6: Màn hình **Thống kê (biểu đồ)** + **Ngân sách & cảnh báo**
- Ngày 7: Hỗ trợ merge, fix conflict, tích hợp toàn bộ
- Ngày 8: Rà soát code, chuẩn bị demo/báo cáo kỹ thuật

### 👩 Người B — Danh mục + Trang chủ
- Ngày 1–2: Học Room cơ bản qua ví dụ Người A dựng, chuẩn bị UI mockup (Figma/giấy)
- Ngày 3–6: Màn hình **Quản lý danh mục** (CRUD category) + Màn hình **Home/Tổng quan** (tổng thu-chi-số dư, chuyển tháng)
- Ngày 7: Test tính năng của mình, báo lỗi cho nhóm
- Ngày 8: Chuẩn bị slide/thuyết trình phần mình làm

### 👩 Người C — Giao dịch + Danh sách/Lọc
- Ngày 1–2: Giống Người B (học Room + chuẩn bị UI)
- Ngày 3–6: Màn hình **Thêm/Sửa giao dịch** (form nhập liệu) + **Danh sách giao dịch** (RecyclerView, tìm kiếm/lọc)
- Ngày 7: Test tính năng của mình
- Ngày 8: Chuẩn bị slide/thuyết trình phần mình làm

> Nếu chọn thêm Dark mode hoặc Notification, có thể giao cho Người B hoặc C làm thêm ở ngày 7–8 vì đây là việc nhỏ, không ảnh hưởng cấu trúc.

---

## 5. LỊCH TRÌNH 8 NGÀY

| Ngày | Việc chung | Việc riêng |
|------|-----------|-----------|
| 1 | Họp nhóm: chốt đề tài, chốt các Very Hard sẽ làm, vẽ sơ đồ màn hình, thiết kế DB schema | A dựng khung project + Git |
| 2 | Cả nhóm review khung code, thống nhất tên biến/hàm dùng chung (Repository interface) | A hoàn thiện data layer, B/C đọc hiểu code mẫu |
| 3–4 | Code song song trên các branch riêng | A: thống kê; B: category+home; C: transaction |
| 5–6 | Tiếp tục code, mỗi ngày pull code A cập nhật (nếu data layer đổi) | A: ngân sách; B/C: hoàn thiện UI + xử lý logic |
| 7 | **Merge tất cả về 1 branch, test toàn bộ app, fix conflict/bug chung** | Cả nhóm cùng test chéo tính năng nhau |
| 8 | Hoàn thiện UI, viết báo cáo, chuẩn bị slide thuyết trình, quay video demo (phòng khi lỗi lúc demo trực tiếp) | Chia phần thuyết trình theo phần đã làm |

---

## 6. QUY TẮC GIT ĐỂ TRÁNH CONFLICT

1. **Branch:** `main` (bản chạy ổn định) + `develop` (tích hợp) + branch riêng từng người: `feature/statistic-budget`, `feature/category-home`, `feature/transaction-list`
2. Mỗi ngày **pull `develop` về branch mình trước khi code**, để cập nhật sớm nếu có thay đổi ở `data` layer
3. **Không ai tự sửa file trong package của người khác.** Cần sửa chung (VD: thêm field vào `Transaction.java`) → báo nhóm trong group chat trước, để người kia biết mà pull lại
4. Merge vào `develop` bằng Pull Request, có ít nhất 1 người khác review nhanh trước khi merge
5. Commit thường xuyên, message rõ ràng (VD: `feat: add category CRUD screen`), tránh commit gộp nhiều tính năng 1 lúc
6. Thêm `.gitignore` chuẩn Android Studio ngay từ đầu (bỏ qua `/build`, `.idea`, `local.properties`...) để tránh conflict do file rác

---

## 7. CÔNG NGHỆ SỬ DỤNG

### Bắt buộc / nền tảng chính

| Công nghệ | Vai trò | Ghi chú |
|---|---|---|
| **Java** | Ngôn ngữ chính | Đúng yêu cầu đề bài |
| **Android Studio** | IDE | Bản mới nhất |
| **Room Database** (androidx.room) | Lưu trữ local (SQLite) | Sinh ra Entity/DAO/Database tự động, ít lỗi hơn viết SQL tay |
| **ViewBinding** | Truy cập view không cần `findViewById` | Bật trong `build.gradle`: `viewBinding { enabled = true }` |
| **RecyclerView + ListAdapter/DiffUtil** | Hiển thị danh sách giao dịch | ListAdapter tự xử lý cập nhật danh sách mượt hơn notifyDataSetChanged |
| **LiveData + ViewModel** (Android Architecture Components) | Kết nối data ↔ UI theo mô hình MVVM | Giúp UI tự cập nhật khi data đổi, tách biệt logic khỏi Activity/Fragment |
| **Material Components for Android** | Giao diện chuẩn Material Design | TextInputLayout, FAB, BottomNavigation, CardView... |
| **Navigation Component** (tùy chọn) | Điều hướng giữa các Fragment | Có thể thay bằng chuyển Activity/Fragment thủ công nếu nhóm chưa quen |

### Cho phần Very Hard (nâng cao)

| Công nghệ | Dùng cho |
|---|---|
| **MPAndroidChart** | Vẽ Pie chart, Bar chart, Line chart (thống kê) |
| **WorkManager** hoặc **AlarmManager** | Nhắc nhở nhập chi tiêu hàng ngày, kiểm tra giao dịch định kỳ |
| **NotificationCompat** | Hiển thị thông báo cảnh báo vượt ngân sách |
| **Apache POI** hoặc **iText/PDFBox** | Xuất file Excel/PDF (nếu làm export báo cáo) |
| **SharedPreferences** hoặc **DataStore** | Lưu setting nhỏ (dark mode, ví mặc định...) |
| **DiffUtil + Espresso/JUnit** (nếu có thời gian) | Test đơn giản cho báo cáo (cộng điểm phần kiểm thử) |

### Quản lý dependency (thêm vào `build.gradle` module `app`)

```gradle
dependencies {
    // Room
    implementation "androidx.room:room-runtime:2.6.1"
    annotationProcessor "androidx.room:room-compiler:2.6.1"

    // Lifecycle / ViewModel / LiveData
    implementation "androidx.lifecycle:lifecycle-viewmodel:2.7.0"
    implementation "androidx.lifecycle:lifecycle-livedata:2.7.0"

    // RecyclerView & Material
    implementation "androidx.recyclerview:recyclerview:1.3.2"
    implementation "com.google.android.material:material:1.11.0"

    // Biểu đồ (nếu làm phần thống kê)
    implementation "com.github.PhilJay:MPAndroidChart:v3.1.0"
}
```
*(Lưu ý: MPAndroidChart cần thêm `maven { url 'https://jitpack.io' }` vào `settings.gradle`)*

---

## 8. CẤU TRÚC DỰ ÁN ANDROID (chi tiết)

```
app/
└── src/main/
    ├── java/com/example/quanlychitieu/
    │   ├── data/
    │   │   ├── model/
    │   │   │   ├── Transaction.java        (@Entity)
    │   │   │   ├── Category.java           (@Entity)
    │   │   │   └── Wallet.java             (@Entity, nếu làm đa ví)
    │   │   ├── dao/
    │   │   │   ├── TransactionDao.java     (@Dao)
    │   │   │   ├── CategoryDao.java
    │   │   │   └── WalletDao.java
    │   │   ├── AppDatabase.java            (@Database - điểm nối tất cả DAO)
    │   │   └── repository/
    │   │       ├── TransactionRepository.java
    │   │       ├── CategoryRepository.java
    │   │       └── WalletRepository.java
    │   │
    │   ├── ui/
    │   │   ├── home/
    │   │   │   ├── HomeFragment.java
    │   │   │   └── HomeViewModel.java
    │   │   ├── category/
    │   │   │   ├── CategoryFragment.java
    │   │   │   ├── CategoryViewModel.java
    │   │   │   ├── CategoryAdapter.java
    │   │   │   └── AddEditCategoryActivity.java
    │   │   ├── transaction/
    │   │   │   ├── TransactionListFragment.java
    │   │   │   ├── TransactionViewModel.java
    │   │   │   ├── TransactionAdapter.java
    │   │   │   └── AddEditTransactionActivity.java
    │   │   ├── statistic/
    │   │   │   ├── StatisticFragment.java
    │   │   │   └── StatisticViewModel.java
    │   │   └── budget/
    │   │       ├── BudgetFragment.java
    │   │       └── BudgetViewModel.java
    │   │
    │   ├── utils/
    │   │   ├── DateUtils.java              (format ngày tháng)
    │   │   ├── CurrencyFormatter.java      (format tiền VNĐ)
    │   │   └── Constants.java
    │   │
    │   └── MainActivity.java               (chứa BottomNavigation, host các Fragment)
    │
    └── res/
        ├── layout/
        │   ├── activity_main.xml
        │   ├── fragment_home.xml
        │   ├── fragment_category.xml
        │   ├── fragment_transaction_list.xml
        │   ├── fragment_statistic.xml
        │   ├── fragment_budget.xml
        │   ├── activity_add_edit_transaction.xml
        │   ├── activity_add_edit_category.xml
        │   ├── item_transaction.xml         (layout 1 dòng RecyclerView)
        │   └── item_category.xml
        ├── menu/
        │   └── bottom_nav_menu.xml
        ├── drawable/                        (icon danh mục, icon chung)
        ├── values/
        │   ├── colors.xml
        │   ├── strings.xml
        │   └── themes.xml
        └── navigation/ (nếu dùng Navigation Component)
            └── nav_graph.xml
```

**Ý nghĩa cách chia này:**
- `data/` = "kho dữ liệu dùng chung" → **chỉ Người A chỉnh sửa**, người khác chỉ *gọi* qua Repository, không sửa trực tiếp Entity/DAO.
- Mỗi thư mục con trong `ui/` = 1 người phụ trách trọn vẹn (Fragment + ViewModel + Adapter + Activity thêm/sửa của tính năng đó) → tách biệt hoàn toàn, gần như không bao giờ 2 người cùng sửa 1 file.
- File layout `.xml` trong `res/layout/` cũng đặt tên theo tính năng tương ứng, nên mỗi người chỉ động vào layout của màn hình mình → tránh conflict cả ở phần giao diện.
- `MainActivity.java` (chứa BottomNavigation) là file **dùng chung duy nhất cần cẩn thận** — nên để Người A dựng sẵn khung điều hướng từ đầu, 2 bạn còn lại không cần sửa file này trừ khi thêm tab mới (báo trước nhóm).

---

## 9. GỢI Ý THƯ VIỆN (tổng hợp nhanh)

- **Room** – lưu trữ dữ liệu local
- **RecyclerView + ViewBinding** – hiển thị danh sách
- **MPAndroidChart** – vẽ biểu đồ (nếu làm phần thống kê)
- **Material Components** – giao diện chuẩn Material Design
- **WorkManager/AlarmManager** – nếu làm notification nhắc nhở

Chúc nhóm làm project thuận lợi! Nếu cần mình có thể giúp thiết kế chi tiết schema DB (các bảng, cột) hoặc code mẫu cho từng phần cụ thể.
