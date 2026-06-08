package com.localpro.localproandroid.views;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.localpro.localproandroid.R;
import com.localpro.localproandroid.adapter.CategoryAdapter;
import com.localpro.localproandroid.models.CategoryModel;

import java.util.ArrayList;
import java.util.List;

public class CategorySelectionActivity extends AppCompatActivity {

    private RecyclerView rvCategories;
    private CategoryAdapter adapter;
    private List<CategoryModel> categoryList;
    private double customerLat, customerLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_selection);

        customerLat = getIntent().getDoubleExtra("LAT", 0.0);
        customerLon = getIntent().getDoubleExtra("LON", 0.0);

        rvCategories = findViewById(R.id.rvCategories);

        setupCategoryList();

        adapter = new CategoryAdapter(categoryList, category -> {
            Intent intent = new Intent(CategorySelectionActivity.this, ProviderListActivity.class);
            intent.putExtra("SELECTED_CATEGORY", category.getId());
            intent.putExtra("LAT", customerLat);
            intent.putExtra("LON", customerLon);
            startActivity(intent);
        });

        rvCategories.setAdapter(adapter);
    }

    private void setupCategoryList() {
        categoryList = new ArrayList<>();

        categoryList.add(new CategoryModel("ac-repair", "AC Repair", "වායුසමීකරණ", "❄️"));
        categoryList.add(new CategoryModel("appliance-repair", "Appliance Repair", "විදුලි උපකරණ", "🔌"));
        categoryList.add(new CategoryModel("cctv-installation", "CCTV Camera", "කැමරා සවිකිරීම", "📷"));
        categoryList.add(new CategoryModel("carpentry", "Carpentry", "වඩුවැඩ", "🪚"));
        categoryList.add(new CategoryModel("cleaning", "Cleaning", "පිරිසිදුකිරීම්", "🧹"));
        categoryList.add(new CategoryModel("electrician", "Electrician", "විදුලිවැඩ", "⚡"));
        categoryList.add(new CategoryModel("electronic-repair", "Electronic Repair", "ඉලෙක්ට්‍රොනික", "📺"));
        categoryList.add(new CategoryModel("gardening", "Gardening", "ගෙවතු අලංකරණය", "🏡"));
        categoryList.add(new CategoryModel("masonry", "Masonry", "මේසන්වැඩ", "🧱"));
        categoryList.add(new CategoryModel("painting", "Painting", "තීන්ත ආලේපනය", "🎨"));
        categoryList.add(new CategoryModel("plumber", "Plumbing", "නලපද්ධති", "🚰"));
    }
}