package com.atasoft.shared;

import androidx.fragment.app.Fragment;

public class NavDrawerItem {
    public final String description;
    public final Fragment fragment;

    public NavDrawerItem(String description, Fragment fragment) {
        this.description = description;
        this.fragment = fragment;
    }
}
