package com.filemanager;

public class DirRef {
    private String display;
    private String link;

    public DirRef(String display, String link) {
        this.display = display;
        this.link = link;
    }

    public boolean getIsActive() {
        return link!=null;
    }

    public String getDisplay() {
        return display;
    }

    public String getLink() {
        return link;
    }
}
