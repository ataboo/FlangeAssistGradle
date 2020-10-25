package com.atasoft.flangeassist.fragments.paycalc;

import android.util.Log;

import java.util.ArrayList;

public enum Province {
    BC ("British Columbia", "BC - ", true, 0),
    AB ("Alberta", "AB - ", true, 1),
    SK ("Saskatchewan", "SK - ", true, 2),
    MB ("Manitoba", "MB - ", true, 3),
    ON ("Ontario", "ON - ", true, 4),
    QC ("Quebec", "QC - ", true, 5),
    NB ("New Brunswick", "NB - ", true, 6),
    NS ("Nova Scotia", "NS - ", true, 7),
    CB ("Cape Breton", "CB - ", true, 8),
    PE ("Prince Edward Island", "PE - ", true, 9),
    NL ("Newfoundland", "NL - ", true, 10),
    FED ("Federal", "FED - ", false, 11);

    private final String displayName;
    private final boolean isActive;
    private final int index;
    private final String surname;

    Province(String name, String surName, boolean active, int index){
        this.displayName = name;
        this.isActive = active;
        this.index = index;
        this.surname = surName;
    }

    public String getName(){return displayName;}

    public int getIndex(){
        return index;
    }

    public String getSurname(){return surname;}

    public static String[] getActiveProvinceNames(){
        ArrayList<String> nameList = new ArrayList<>();
        for(Province p: Province.values()){
            if(p.isActive)
                nameList.add(p.displayName);
        }
        String[] retArr = new String[nameList.size()];
        nameList.toArray(retArr);
        return retArr;
    }

    public static Province getProvFromName(String provName){
        for(Province prov: Province.values()){
            if(provName.equals(prov.getName())) return prov;
        }
        Log.e("TaxManager", "Couldn't find province matching: "+ provName + " returned Fed.");
        return Province.FED;
    }
}
