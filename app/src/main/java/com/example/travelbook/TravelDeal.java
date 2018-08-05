package com.example.travelbook;

public class TravelDeal {

    private String id;
    private String title;

    public TravelDeal() {
    }

    public TravelDeal(String pTitle, String pDescription, String pPrice, String pImageUrl) {

        title = pTitle;
        description = pDescription;
        price = pPrice;
        imageUrl = pImageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String pId) {
        id = pId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String pTitle) {
        title = pTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String pDescription) {
        description = pDescription;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String pPrice) {
        price = pPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String pImageUrl) {
        imageUrl = pImageUrl;
    }

    private String description;
    private String price;
    private String imageUrl;
}
