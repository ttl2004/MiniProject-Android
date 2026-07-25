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
        String[] items = {"Ăn uống", "Di chuyển", "Mua sắm", "Nhà cửa", "Giải trí", "Sức khỏe", "Giáo dục", "Hóa đơn"};
        String[] icons = {"ic_category_eat", "ic_category_move", "ic_category_shopping", "ic_category_home", "ic_category_entertaiment", "ic_category_medical", "ic_category_edu", "ic_category_invoice"};
        String[] colors = {"#F8CACA", "#B9D8F7", "#E8C7E8", "#CDECCB", "#D7C7F5", "#BFE3B2", "#F6C89F", "#F8E7A3"};

        for (int i = 0; i < 8; i ++) {
            categories.add(new Category(items[i], icons[i], colors[i]));
        }

        db.categoryDAO().insertALL(categories);

        Log.d("Category", "done------------");
    }
}