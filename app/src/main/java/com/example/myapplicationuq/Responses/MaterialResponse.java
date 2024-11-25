package com.example.myapplicationuq.Responses;

import java.io.Serializable;

// MaterialResponse.java
public class MaterialResponse implements Serializable {
    private Integer materialID;
    private String materialName;
    private Integer facultyID;

    // Getters and Setters
    public Integer getMaterialID() {
        return materialID;
    }

    public void setMaterialID(Integer materialID) {
        this.materialID = materialID;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public Integer getFacultyID() {
        return facultyID;
    }

    public void setFacultyID(Integer facultyID) {
        this.facultyID = facultyID;
    }
}
