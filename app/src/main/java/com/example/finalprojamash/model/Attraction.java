package com.example.finalprojamash.model;

public class Attraction {
    protected String id;
    protected String country;
    protected String type;
    protected String address;
    protected String city;
    protected String price;
    protected String pic;
    protected String ages;
    protected String details;


    public Attraction(String id, String country, String type, String address, String city, String price, String pic, String ages, String details) {
        this.id = id;
        this.country = country;
        this.type = type;
        this.address = address;
        this.city = city;
        this.price = price;
        this.pic = pic;
        this.ages = ages;
        this.details = details;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
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

    @Override
    public String toString() {
        return "Attraction{" +
                "id='" + id + '\'' +
                ", country='" + country + '\'' +
                ", type='" + type + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", price='" + price + '\'' +
                ", pic='" + pic + '\'' +
                ", ages='" + ages + '\'' +
                ", details='" + details + '\'' +
                '}';
    }
}

