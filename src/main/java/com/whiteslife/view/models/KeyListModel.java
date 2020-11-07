package com.whiteslife.view.models;

public class KeyListModel implements ListModel {
    private String key;
    private String displayLabel;

    public KeyListModel(String key, String displayLabel) {
        this.key = key;
        this.displayLabel = displayLabel;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getDisplayLabel() {
        return displayLabel;
    }
}
