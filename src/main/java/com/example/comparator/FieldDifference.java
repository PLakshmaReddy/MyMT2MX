package com.example.comparator;

public class FieldDifference {
    private final String fieldName;
    private final String mtValue;
    private final String pacsValue;

    public FieldDifference(String fieldName, String mtValue, String pacsValue) {
        this.fieldName = fieldName;
        this.mtValue = mtValue;
        this.pacsValue = pacsValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getMtValue() {
        return mtValue;
    }

    public String getPacsValue() {
        return pacsValue;
    }
}
