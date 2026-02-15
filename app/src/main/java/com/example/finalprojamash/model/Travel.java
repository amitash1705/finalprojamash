package com.example.finalprojamash.model;

import java.util.List;

public class Travel {

    protected  String id;

    protected  String name;


    List<Attraction> attractionList;

    String details;


    public Travel(String id, String name, List<Attraction> attractionList, String details) {
        this.id = id;
        this.name = name;
        this.attractionList = attractionList;
        this.details = details;
    }

    public Travel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Attraction> getAttractionList() {
        return attractionList;
    }

    public void setAttractionList(List<Attraction> attractionList) {
        this.attractionList = attractionList;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "Travel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", attractionList=" + attractionList +
                ", details='" + details + '\'' +
                '}';
    }
}
