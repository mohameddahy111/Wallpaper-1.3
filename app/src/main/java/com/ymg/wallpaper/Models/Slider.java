package com.ymg.wallpaper.Models;

public class Slider {

    private String slider_id;
    private String slider_name;
    private String slider_image;
    private String slider_link;
    private String slider_type;
    private String slider_category;

    public Slider(String slider_id, String slider_name, String slider_image, String slider_link, String slider_type, String slider_category) {
        this.slider_id = slider_id;
        this.slider_name = slider_name;
        this.slider_image = slider_image;
        this.slider_link = slider_link;
        this.slider_type = slider_type;
        this.slider_category = slider_category;
    }

    public String getSlider_id() {
        return slider_id;
    }

    public void setSlider_id(String slider_id) {
        this.slider_id = slider_id;
    }

    public String getSlider_name() {
        return slider_name;
    }

    public void setSlider_name(String slider_name) {
        this.slider_name = slider_name;
    }

    public String getSlider_image() {
        return slider_image;
    }

    public void setSlider_image(String slider_image) {
        this.slider_image = slider_image;
    }

    public String getSlider_link() {
        return slider_link;
    }

    public void setSlider_link(String slider_link) {
        this.slider_link = slider_link;
    }

    public String getSlider_type() {
        return slider_type;
    }

    public void setSlider_type(String slider_type) {
        this.slider_type = slider_type;
    }

    public String getSlider_category() {
        return slider_category;
    }

    public void setSlider_category(String slider_category) {
        this.slider_category = slider_category;
    }
}
