package com.example.myapplication.data.entity;

import android.content.Intent;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "categories")
public class Category implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private Integer userId;

    private String name;
    private String icon;
    private String color;

    private boolean system;

    private long createdAt;

    private long updatedAt;

    public Category() {
    }

    @Ignore
    public Category(String name, String icon, String color) {
        this.userId = null;
        this.name = name;
        this.system = true;
        this.icon = icon;
        this.color = color;
        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @Ignore
    public Category(int userId, String name, boolean system, String icon, String color) {
        this.userId = userId;
        this.name = name;
        this.system = system;

        this.icon = icon;
        this.color = color;

        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSystem() {
        return system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}