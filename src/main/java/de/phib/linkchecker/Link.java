package de.phib.linkchecker;

public class Link {

    private String url;

    private int status = -1;

    public Link() {
    }

    public Link(String url, int status) {
        this.url = url;
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "url: " + url + ", status: " + status;
    }

}
