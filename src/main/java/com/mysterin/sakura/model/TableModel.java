package com.mysterin.sakura.model;

/**
 * @author linxb
 */
public class TableModel {

    private String name;
    private String collation;

    public String getName() {
        return name;
    }

    public String getCollation() {
        return collation;
    }

    public void setCollation(String collation) {
        this.collation = collation;
    }

    public void setName(String name) {
        this.name = name;
    }
}
