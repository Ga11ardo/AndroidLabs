package com.labs.lab3;

public class Request {
    public int id;
    public String text;
    public int color;
    public String datetime;

    public Request(int id, String text, int color, String datetime) {
        this.id = id;
        this.text = text;
        this.color = color;
        this.datetime = datetime;
    }
}