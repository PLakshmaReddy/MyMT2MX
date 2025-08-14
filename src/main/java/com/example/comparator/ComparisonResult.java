package com.example.comparator;

import java.util.ArrayList;
import java.util.List;

public class ComparisonResult {
    private final String transactionReference;
    private final List<FieldDifference> differences = new ArrayList<>();

    public ComparisonResult(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void addDifference(String fieldName, String mtValue, String pacsValue) {
        this.differences.add(new FieldDifference(fieldName, mtValue, pacsValue));
    }

    public List<FieldDifference> getDifferences() {
        return differences;
    }

    public boolean hasDifferences() {
        return !differences.isEmpty();
    }
}
