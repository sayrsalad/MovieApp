package com.example.movieapp.Models;

public class Certificate {

    private int certificate_ID;
    private String certificate_name, certificate_status;

    public int getCertificate_ID() {
        return certificate_ID;
    }

    public void setCertificate_ID(int certificate_ID) {
        this.certificate_ID = certificate_ID;
    }

    public String getCertificate_name() {
        return certificate_name;
    }

    public void setCertificate_name(String certificate_name) {
        this.certificate_name = certificate_name;
    }

    public String getCertificate_status() {
        return certificate_status;
    }

    public void setCertificate_status(String certificate_status) {
        this.certificate_status = certificate_status;
    }
}
