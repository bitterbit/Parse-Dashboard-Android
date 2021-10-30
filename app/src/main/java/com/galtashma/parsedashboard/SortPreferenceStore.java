package com.galtashma.parsedashboard;

import com.appizona.yehiahd.fastsave.FastSave;

public class SortPreferenceStore {
    private String prefId;

    public SortPreferenceStore(String prefId) {
        this.prefId = prefId;
    }

    public void update(String key, boolean asc) {
        FastSave.getInstance().saveObject(this.prefId, new SortPreferenceItem(key, asc));
    }

    public String getKey() {
        if (isEmpty()) {
            return "";
        }
        return getSavedItem().key;
    }

    public boolean isAsc() {
        if (isEmpty()) {
            return false;
        }
        return getSavedItem().asc;
    }

    public boolean isEmpty() {
        return !FastSave.getInstance().isKeyExists(this.prefId);
    }

    private SortPreferenceItem getSavedItem() {
        return FastSave.getInstance().getObject(this.prefId, SortPreferenceItem.class);
    }

    class SortPreferenceItem {
        private String key;
        private boolean asc;

        SortPreferenceItem(String key, boolean asc) {
            this.key = key;
            this.asc = asc;
        }
    }
}
