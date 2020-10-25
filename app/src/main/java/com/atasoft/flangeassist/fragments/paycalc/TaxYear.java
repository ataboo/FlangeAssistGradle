package com.atasoft.flangeassist.fragments.paycalc;

public enum TaxYear {
    TY_2013 ("2013", 0),
    TY_2014 ("2014", 1),
    TY_2015 ("2015", 2),
    TY_2016 ("2016", 3),
    TY_2017 ("2017", 4),
    TY_2018 ("2018", 5),
    TY_2019 ("2019", 6),
    TY_2020 ("2020", 7);

    private final String name;
    private final int index;

    TaxYear(String name, int index){
        this.name = name;
        this.index = index;
    }

    public String getName(){
        return name;
    }

    public int getIndex(){
        return index;
    }

    public static String[] getYearStrings(){
        TaxYear[] values = TaxYear.values();
        String[] yearStrings = new String[values.length];

        for(int i=0; i<values.length; i++){
            yearStrings[i] = values[i].getName();
        }

        return yearStrings;
    }
}
