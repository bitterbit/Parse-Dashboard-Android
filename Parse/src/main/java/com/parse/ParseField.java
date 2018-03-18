package com.parse;

import android.util.Log;

/**
 * Created by gal on 3/9/18.
 */

public class ParseField {
    public enum FieldType {

        String("String"),
        ACL("ACL"),
        Date("Date"),
        Number("Number"),
        Array("Array"),
        Pointer("Pointer"),
        Object("Object"),
        Boolean("Boolean"),
        Relation("Relation"),
        None("None");

        private String name;

        FieldType(String name) {
            this.name = name;
        }

        public String fieldName(){
            return this.name;
        }
    }

    public static FieldType fromString(String type){
        try{
            return FieldType.valueOf(type);
        } catch (IllegalArgumentException e){
            Log.w("ParseField", "No matching field type found for type: " + type);
            return FieldType.None;
        }
    }
}
