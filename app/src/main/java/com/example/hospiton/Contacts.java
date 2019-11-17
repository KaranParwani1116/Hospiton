package com.example.hospiton;

public class Contacts {
    private String name,image,Destination;

    public Contacts()
    {

    }

    public Contacts(String name,String image,String Destination)
    {
        this.name=name;
        this.image=image;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getDestination() {
        return Destination;
    }
}
