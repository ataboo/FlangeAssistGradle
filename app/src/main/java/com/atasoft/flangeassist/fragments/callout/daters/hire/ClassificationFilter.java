package com.atasoft.flangeassist.fragments.callout.daters.hire;

import com.atasoft.utilities.AtaStringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

/**
 * Created by ataboo on 4/15/2017.
 */

public enum ClassificationFilter {
    J_WELDER("J-Welder", new ClassificationTag[]{ClassificationTag.J_WELDER}),
    B_WELDER("B-Welder", new ClassificationTag[]{ClassificationTag.B_WELDER}),
    WELDER("Welder", new ClassificationTag[]{ClassificationTag.J_WELDER, ClassificationTag.B_WELDER}),
    FITTER("Fitter", new ClassificationTag[]{ClassificationTag.FITTER}),
    RIGGER("Rigger", new ClassificationTag[]{ClassificationTag.RIGGER}),
    APPRENTICE("Apprentice", new ClassificationTag[]{ClassificationTag.WELDING_APP, ClassificationTag.FITTER_APP}),
    WELD_APPR("Welding Apprentice", new ClassificationTag[]{ClassificationTag.WELDING_APP}),
    BM_APPR("Fitter Apprentice", new ClassificationTag[]{ClassificationTag.FITTER_APP});

    public String name;
    public ClassificationTag[] tags;

    ClassificationFilter(String name, ClassificationTag[] tags) {
        this.name = name;
        this.tags = tags;
    }

    public static String serialize(EnumSet<ClassificationFilter> set) {
        ArrayList<String> names = new ArrayList<>();

        for (ClassificationFilter filter : set) {
            names.add(filter.name);
        }
        return AtaStringUtils.implode(names.toArray(new String[names.size()]), "|");
    }

    public static EnumSet<ClassificationFilter> deserialize(String serialized) {
        List<String> names = Arrays.asList(serialized.split("\\|"));
        EnumSet<ClassificationFilter> filters = EnumSet.noneOf(ClassificationFilter.class);
        for (ClassificationFilter filter : ClassificationFilter.values()) {
            if(names.contains(filter.name)) {
                filters.add(filter);
            }
        }

        return filters;
    }
}
