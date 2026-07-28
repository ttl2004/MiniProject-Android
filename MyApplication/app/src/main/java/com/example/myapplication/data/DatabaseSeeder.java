package com.example.myapplication.data;

import android.util.Log;

import com.example.myapplication.data.entity.Category;

import java.util.ArrayList;
import java.util.List;

public class DatabaseSeeder {

    public static void seed(AppDatabase db) {
        seedCategory(db);
    }

    private static void seedCategory(AppDatabase db) {
        Log.d("Category", "start------------");
        List<Category> categories = new ArrayList<>();
//        String[] items = {"Ăn uống", "Di chuyển", "Mua sắm", "Nhà cửa", "Giải trí", "Sức khỏe", "Giáo dục", "Hóa đơn"};
//        String[] icons = {"ic_category_eat", "ic_category_move", "ic_category_shopping", "ic_category_home", "ic_category_entertaiment", "ic_category_medical", "ic_category_edu", "ic_category_invoice"};
//        String[] colors = {"#F8CACA", "#B9D8F7", "#E8C7E8", "#CDECCB", "#D7C7F5", "#BFE3B2", "#F6C89F", "#F8E7A3"};
        String[] items = {
                "Ăn uống",
                "Di chuyển",
                "Mua sắm",
                "Nhà ở",
                "Giải trí",
                "Sức khỏe",
                "Giáo dục",
                "Hóa đơn",
                "Công việc",
                "Gia đình",
                "Du lịch",
                "Chi khác",
                "Lương",
                "Thưởng",
                "Phụ cấp",
                "Kinh doanh",
                "Đầu tư",
                "Tiền lãi",
                "Quà tặng",
                "Thu khác"
        };
        String[] icons = {
                "ic_category_eat", // Ăn uống
                "ic_category_move", // Di chuyển
                "ic_category_shopping", // Mua sắm
                "ic_category_home", // Nhà ở
                "ic_category_entertaiment", // Giải trí
                "ic_category_medical", // Sức khỏe
                "ic_category_edu", // Giáo dục
                "ic_category_invoice", // Hóa đơn
                "icon_category_work", // Công việc
                "icon_category_family", // Gia đình
                "icon_category_travel", // Du lịch
                "ic_category_tag", // Khác
                "icon_category_dolar", // Lương
                "icon_category_reward", // Thưởng
                "icon_category_money", // Phụ cấp
                "icon_category_business", // Kinh doanh
                "icon_category_invest", // Đầu tư
                "icon_category_account_balance", // Tiền lãi
                "icon_category_gift", // Quà tặng
                "ic_category_tag"  // Thu khác
        };
        String[] colors = {
                "#F8CACA", // Ăn uống
                "#B9D8F7", // Di chuyển
                "#E8C7E8", // Mua sắm
                "#CDECCB", // Nhà ở
                "#D7C7F5", // Giải trí
                "#BFE3B2", // Sức khỏe
                "#F6C89F", // Giáo dục
                "#F8E7A3", // Hóa đơn
                "#D6E4FF", // Công việc
                "#FFD6E7", // Gia đình
                "#CFF4F1", // Du lịch
                "#E5E7EB", // Khác
                "#D9F7BE", // Lương
                "#FFE7BA", // Thưởng
                "#FFF1B8", // Phụ cấp
                "#D6F5D6", // Kinh doanh
                "#CDEAFF", // Đầu tư
                "#E6D5FF", // Tiền lãi
                "#FFD6D6", // Quà tặng
                "#E2E8F0"  // Thu khác
        };

        String[] types = {
                "EXPENSE", // Ăn uống
                "EXPENSE", // Di chuyển
                "EXPENSE", // Mua sắm
                "EXPENSE", // Nhà ở
                "EXPENSE", // Giải trí
                "EXPENSE", // Sức khỏe
                "EXPENSE", // Giáo dục
                "EXPENSE", // Hóa đơn
                "EXPENSE", // Công việc
                "EXPENSE", // Gia đình
                "EXPENSE", // Du lịch
                "EXPENSE", // Khác
                "INCOME",  // Lương
                "INCOME",  // Thưởng
                "INCOME",  // Phụ cấp
                "INCOME",  // Kinh doanh
                "INCOME",  // Đầu tư
                "INCOME",  // Tiền lãi
                "INCOME",  // Quà tặng
                "INCOME"   // Thu khác
        };

        int f = icons.length;
        for (int i = 0; i < f; i ++) {
            categories.add(new Category(items[i], icons[i], colors[i], types[i]));
        }

        db.categoryDAO().insertALL(categories);

        Log.d("Category", "done------------");
    }
}
