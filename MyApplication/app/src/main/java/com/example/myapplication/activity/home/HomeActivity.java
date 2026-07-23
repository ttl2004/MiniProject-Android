package com.example.myapplication.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.activity.account.AccountActivity;
import com.example.myapplication.data.entity.User;
import com.example.myapplication.databinding.ActivityHomeBinding;
import com.example.myapplication.ui.analysis.AnalysisFragment;
import com.example.myapplication.ui.budget.BudgetFragment;
import com.example.myapplication.ui.category.CategoryFragment;
import com.example.myapplication.ui.home.HomeFragment;
import com.example.myapplication.ui.transaction.TransactionFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding activityHomeBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHomeBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(activityHomeBinding.getRoot());

        // Find the BottomNavigationView from the layout
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.navHome);
        // Set the default fragment that should be shown when the app starts
        setCurrentFragment(new HomeFragment());

        // Set a listener to handle item selection on the bottom navigation bar
        bottomNavigationView.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();

            if (id == R.id.navHome) setCurrentFragment(new HomeFragment());
            else if (id == R.id.navAnalysis) setCurrentFragment(new AnalysisFragment());
            else if (id == R.id.navBudget) setCurrentFragment(new BudgetFragment());
            else if (id == R.id.navCategory) setCurrentFragment(new CategoryFragment());
            else setCurrentFragment(new TransactionFragment());
            // Return true to indicate that we handled the item click
            return true;
        });

        activityHomeBinding.btnAccountAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, AccountActivity.class);
                startActivity(i);
            }
        });

        User user = (User) getIntent().getSerializableExtra("EXTRA_USER");
        activityHomeBinding.tvUsername.setText(user.getFullName());
    }

    // This function replaces the current fragment with the one passed as a parameter
    private void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                // Replace the fragment inside the container with the new fragment
                .replace(R.id.fragment_container, fragment)
                // Commit the transaction to actually perform the change
                .commit();
    }


}
