package com.example.myapplication.utils;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class ViewUtils {
    public static void disablePaste(EditText editText) {
        if (editText == null) return;

        // 1. Chặn Menu ngữ cảnh (Copy/Paste/Cut) khi nhấn giữ tay
        editText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });

        // 2. Tắt sự kiện đè giữ (Long click)
        editText.setLongClickable(false);

        // 3. Chặn click menu phụ (nếu có)
        editText.setOnContextClickListener(v -> true);
    }
}