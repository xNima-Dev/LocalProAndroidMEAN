package com.localpro.localproandroid.models;

import com.localpro.localproandroid.adapter.CategoryAdapter;

import java.util.List;

public class CategoryModel {
    private String id;       // Backend එකට යවන නම (e.g., "ac-repair")
    private String title;    // UI එකේ පේන ඉංග්‍රීසි නම (e.g., "AC Repair")
    private String sinhala;  // UI එකේ පේන සිංහල නම (e.g., "වායුසමීකරණ")
    private String emoji;    // අයිකන් එකට දාන ඉමෝජි එක (e.g., "❄️")

    public CategoryModel(String id, String title, String sinhala, String emoji) {
        this.id = id;
        this.title = title;
        this.sinhala = sinhala;
        this.emoji = emoji;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getSinhala() { return sinhala; }
    public String getEmoji() { return emoji; }
}