package com.parse;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import bolts.Continuation;
import bolts.Task;

public class ParseSchema {

    private static final String TAG = "ParseSchema";
    private static final String KEY_SCHEMA_NAME = "className";
    private static final String KEY_SCHEMA_FIELDS = "fields";
    private static final String KEY_SCHEMA_CLP = "classLevelPermissions";
    private static final String KEY_SCHEMA_FIELD_TYPE = "type";

    public static Task<List<ParseSchema>> getParseSchemasAsync(){
        return ParseUser.getCurrentSessionTokenAsync().onSuccessTask(new Continuation<String, Task<List<ParseSchema>>>() {
            @Override
            public Task<List<ParseSchema>> then(Task<String> task) throws Exception {
                return ParseCorePlugins.getInstance().getSchemaController().getSchemasInBackground(task.getResult());
            }
        });
    }

    private String schemaName;
    private HashMap<String, FieldType> fields;
    private JSONObject classLevelPermissions;

    /* Package */ ParseSchema(JSONObject json){
        schemaName = parseSchemaNameFromJson(json);
        fields = parseFieldsFromJson(json);
        classLevelPermissions = parseCLPFromJson(json);
    }

    public String getName(){
        return schemaName;
    }

    public Map<String, FieldType> getFields() {
        return fields;
    }
    public HashMap<String, FieldType> getFieldsHashMap() {
        return fields;
    }

    public JSONObject getCLP(){
        return classLevelPermissions;
    }

    private String parseSchemaNameFromJson(JSONObject json){
        try {
            return json.getString(KEY_SCHEMA_NAME);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private HashMap<String, FieldType> parseFieldsFromJson(JSONObject json){
        HashMap<String, FieldType> fields = new HashMap<>();
        JSONObject object = null;
        try {
            object = json.getJSONObject(KEY_SCHEMA_FIELDS);
        } catch (JSONException e) {
            e.printStackTrace();
            return fields;
        }

        Iterator<?> keys = object.keys();

        while( keys.hasNext() ) {
            String key = (String)keys.next();
            FieldType type = getFieldType(object, key);
            fields.put(key, type);
        }

        return fields;
    }

    private FieldType getFieldType(JSONObject jsonFields, String key){
        try {
            JSONObject fieldMeta = jsonFields.getJSONObject(key);
            String type = fieldMeta.getString(KEY_SCHEMA_FIELD_TYPE);
            return fieldTypeFromString(type);
        } catch (JSONException e) {
            e.printStackTrace();
            return FieldType.None;
        }
    }

    private JSONObject parseCLPFromJson(JSONObject json){
        try {
            return json.getJSONObject(KEY_SCHEMA_CLP);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


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

    public static FieldType fieldTypeFromString(String type){
        try{
            return FieldType.valueOf(type);
        } catch (IllegalArgumentException e){
            Log.w(TAG, "No matching field type found for type: " + type);
            return FieldType.None;
        }
    }

}
