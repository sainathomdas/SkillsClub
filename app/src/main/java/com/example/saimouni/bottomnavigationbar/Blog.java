package com.example.saimouni.bottomnavigationbar;



public class Blog {
    String title,desc,image,date;


    public Blog(String title, String desc, String image, String date) {
        this.title = title;
        this.desc = desc;
        this.image = image;
        this.date = date;
    }

    public Blog() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
