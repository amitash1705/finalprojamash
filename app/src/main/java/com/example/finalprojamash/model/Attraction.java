package com.example.finalprojamash.model;

public class Attraction {
    protected String id;

    protected String name;
    protected String country;

    protected String type;
    protected String address;
    protected String city;
    protected double  price;
    protected String pic;
    protected String ages;
    protected String details;
    protected String web;


    public Attraction(String id, String name, String country, String type,
                      String address, String city, double price, String pic,
                      String ages, String details, String web) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.type = type;
        this.address = address;
        this.city = city;
        this.price = price;
        this.pic = pic;
        this.ages = ages;
        this.details = details;
        this.web = web;
    }

    public Attraction() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }



    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getAges() {
        return ages;
    }

    public void setAges(String ages) {
        this.ages = ages;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Attraction{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", type='" + type + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", price=" + price +
                ", pic='" + pic + '\'' +
                ", ages='" + ages + '\'' +
                ", details='" + details + '\'' +
                ", web='" + web + '\'' +
                '}';
    }
}

