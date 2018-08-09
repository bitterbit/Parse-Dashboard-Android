package com.galtashma.parsedashboard;

import com.appizona.yehiahd.fastsave.FastSave;

import java.util.ArrayList;
import java.util.List;

public class ListPreferenceStore {
    private List<String> list;
    private String key;

    public ListPreferenceStore(String key){
        this.key = key;
        list = load();
    }

    public List<String> getList(){
        return list;
    }

    public void add(String key){
        if (!exists(key)){
            list.add(key);
            save();
        }
    }

    public void remove(String key){
        if (list.contains(key)){
            list.remove(key);
        }
        save();
    }

    public void reset(){
        list = new ArrayList<>();
        save();
    }

    public boolean exists(String key){
        return list.contains(key);
    }

    public boolean isEmpty(){
        return list.isEmpty();
    }

    public int size(){
        return list.size();
    }

    private void save(){
        FastSave.getInstance().saveObjectsList(key, list);
    }

    private List<String> load(){
        List<String> l = FastSave.getInstance().getObjectsList(key, String.class);
        if (l != null) {
            return l;
        }
        return new ArrayList<String>();
    }
}
