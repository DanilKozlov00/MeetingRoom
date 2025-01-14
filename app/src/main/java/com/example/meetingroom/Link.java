package com.example.meetingroom;

import java.util.Objects;

public class Link implements Comparable{
    private String name;
    private String url;

    public Link(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    //Считаем равенство только по именам
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Link link = (Link) o;
        return Objects.equals(name, link.name);// && Objects.equals(url, link.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name); //Objects.hash(name, url);
    }

    @Override
    public int compareTo(Object o) {
        if (o==null || !(o instanceof Link))
            return -1;
        return name.trim().compareTo((((Link) o).getName().trim()));
    }
}
