package com.localpro.localproandroid.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.localpro.localproandroid.R;
import com.localpro.localproandroid.models.CategoryModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private final List<CategoryModel> categoryList;
    private final OnCategoryClickListener listener;

    public CategoryAdapter(List<CategoryModel> categoryList, OnCategoryClickListener listener) {
        this.categoryList = categoryList;
        this.listener = listener;
    }

    public interface OnCategoryClickListener {
        void onCategoryClick(CategoryModel category);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int viewType) {
        CategoryModel category = categoryList.get(viewType);
        holder.tvEmoji.setText(category.getEmoji());
        holder.tvTitle.setText(category.getTitle());
        holder.tvSinhala.setText("(" + category.getSinhala() + ")");

        holder.itemView.setOnClickListener(v -> listener.onCategoryClick(category));
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmoji, tvTitle, tvSinhala;

        public ViewHolder(@NotNull View itemView) {
            super(itemView);
            tvEmoji = itemView.findViewById(R.id.tvCategoryEmoji);
            tvTitle = itemView.findViewById(R.id.tvCategoryTitle);
            tvSinhala = itemView.findViewById(R.id.tvCategorySinhala);
        }
    }

}
