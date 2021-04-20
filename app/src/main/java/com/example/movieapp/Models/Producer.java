package com.example.movieapp.Models;

public class Producer {
    
    private int producer_ID;
    private  String producer_name, producer_email_address, producer_website, producer_status;

    public int getProducer_ID() {
        return producer_ID;
    }

    public void setProducer_ID(int producer_ID) {
        this.producer_ID = producer_ID;
    }

    public String getProducer_name() {
        return producer_name;
    }

    public void setProducer_name(String producer_name) {
        this.producer_name = producer_name;
    }

    public String getProducer_email_address() {
        return producer_email_address;
    }

    public void setProducer_email_address(String producer_email_address) {
        this.producer_email_address = producer_email_address;
    }

    public String getProducer_website() {
        return producer_website;
    }

    public void setProducer_website(String producer_website) {
        this.producer_website = producer_website;
    }

    public String getProducer_status() {
        return producer_status;
    }

    public void setProducer_status(String producer_status) {
        this.producer_status = producer_status;
    }
}
