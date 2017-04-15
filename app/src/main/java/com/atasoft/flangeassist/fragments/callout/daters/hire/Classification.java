package com.atasoft.flangeassist.fragments.callout.daters.hire;


import com.atasoft.flangeassist.R;
import com.atasoft.utilities.AtaStringUtils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Classification {
    public static final Classification J_WELDER = new Classification(
            "J-Welder",
            R.drawable.linkicon,
            new String[]{"J WELDER", "JW", "CWB", "ORBITAL"}
    );
    public static final Classification B_WELDER = new Classification(
            "B-Pressure",
            R.drawable.linkicon,
            new String[]{"B WELDER", "TIG", "SS", "INCO", "BW"}
    );
    public static final Classification FITTER = new Classification(
            "Fitter",
            R.drawable.linkicon,
            new String[]{"FITTER", "BOILERMAKER", "BM", "PULLER"}
    );
    public static final Classification RIGGER = new Classification(
            "Rigger",
            R.drawable.linkicon,
            new String[]{"RIGGER"}
    );
    public static final Classification FOREMAN = new Classification(
            "Foreman",
            R.drawable.linkicon,
            new String[]{"FOREMAN", "FM"}
    );

    public static final Classification WELDING_APP = new Classification(
            "Welding Apprentice",
            R.drawable.linkicon,
            new String[]{"W1", "W2", "W3"}
    );

    public static final Classification FITTER_APP = new Classification(
            "Boilermaker Apprentice",
            R.drawable.linkicon,
            new String[]{"B1", "B2", "B3", "APPR", "APPRENTICE", "1ST", "2ND", "3RD"}
    );

    public static final Classification[] ALL = {
            J_WELDER,
            B_WELDER,
            FITTER,
            RIGGER,
            FOREMAN,
            WELDING_APP,
            FITTER_APP
    };

    private static final Pattern WORD_MATCH = Pattern.compile("/(^.)(^.)/");

    private String name;
    private int iconRes;
    private String[] keywords;

    public Classification(String name, int iconRes, String[] keywords) {
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

    private String keywordsJoined() {
        return AtaStringUtils.implode(keywords, "|");
    }

    public boolean matches(HireOrder namehire) {
        return matchesKeywords(namehire.getClassification());
    }

    private boolean matchesKeywords(String haystack) {
        Pattern pattern = Pattern.compile("(^|\\b)"+keywordsJoined()+"(\\b|$)");
        Matcher matcher = pattern.matcher(haystack);
        return matcher.find();
    }
}
