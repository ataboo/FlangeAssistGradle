package com.atasoft.flangeassist.fragments.callout.daters.hire;


import android.content.pm.FeatureInfo;

import com.atasoft.flangeassist.R;
import com.atasoft.utilities.AtaStringUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class ClassificationTag {
    public static final ClassificationTag J_WELDER = new ClassificationTag(
            "J-Welder",
            R.drawable.linkicon,
            new String[]{"J WELDER", "JW", "CWB", "ORBITAL"}
    );
    public static final ClassificationTag B_WELDER = new ClassificationTag(
            "B-Pressure",
            R.drawable.linkicon,
            new String[]{"B WELDER", "TIG", "SS", "INCO", "BW"}
    );
    public static final ClassificationTag FITTER = new ClassificationTag(
            "Fitter",
            R.drawable.linkicon,
            new String[]{"FITTER", "BOILERMAKER", "BM", "PULLER"}
    );
    public static final ClassificationTag RIGGER = new ClassificationTag(
            "Rigger",
            R.drawable.linkicon,
            new String[]{"RIGGER"}
    );
    public static final ClassificationTag FOREMAN = new ClassificationTag(
            "Foreman",
            R.drawable.linkicon,
            new String[]{"FOREMAN", "FM"}
    );

    public static final ClassificationTag WELDING_APP = new ClassificationTag(
            "Welding Apprentice",
            R.drawable.linkicon,
            new String[]{"W1", "W2", "W3"}
    );

    public static final ClassificationTag FITTER_APP = new ClassificationTag(
            "Boilermaker Apprentice",
            R.drawable.linkicon,
            new String[]{"B1", "B2", "B3", "APPR", "APPRENTICE", "1ST", "2ND", "3RD"}
    );

    public static final ClassificationTag[] ALL = {
            J_WELDER,
            B_WELDER,
            FITTER,
            RIGGER,
            FOREMAN,
            WELDING_APP,
            FITTER_APP
    };

    private String name;
    private int iconRes;
    private String[] keywords;

    public ClassificationTag(String name, int iconRes, String[] keywords) {
        this.name = name;
        this.iconRes = iconRes;
        this.keywords = keywords;
    }

    public String getName() {
        return name;
    }

    public int getIconRes() {
        return iconRes;
    }

    public String[] getKeywords() {
        return keywords;
    }

    public boolean matches(HireOrder hireOrder) {
        return AtaStringUtils.containsWord(keywords, hireOrder.getClassification());
    }

    public static ClassificationTag[] matchTags(HireOrder hireOrder) {
        ArrayList<ClassificationTag> matches = new ArrayList<>();

        for (ClassificationTag tag: ALL) {
            if (tag.matches(hireOrder)) {
                matches.add(tag);
            }
        }

        return matches.toArray(new ClassificationTag[matches.size()]);
    }

    public static int getIconRes(HireOrder hireOrder) {
        if(hireOrder.hasTag(WELDING_APP) || hireOrder.hasTag(FITTER_APP)) {
            return R.mipmap.ic_school;
        }
        if(hireOrder.hasTag(B_WELDER) || hireOrder.hasTag(J_WELDER)) {
            return R.mipmap.ic_stinger;
        }

        if(hireOrder.hasTag(FOREMAN)) {
            return R.mipmap.ic_clipboard;
        }

        return R.mipmap.ic_wrench;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClassificationTag{");
        sb.append("name='").append(name).append('\'');
        sb.append(", \niconRes=").append(iconRes);
        sb.append(", \nkeywords=").append(Arrays.toString(keywords));
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassificationTag that = (ClassificationTag) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
