package de.phib.linkchecker;

/**
 * Encapsulates a link url and its http response status code.
 */
public class Link {

    private String url;

    private int status = -1;

    /**
     * Creates a new Link.
     */
    public Link() {
    }

    /**
     * Creates a new Link.
     *
     * @param url    the link url
     * @param status the http response status code
     */
    public Link(String url, int status) {
        this.url = url;
        this.status = status;
    }

    /**
     * Returns the url of this Link.
     *
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the url of this Link.
     *
     * @param url a url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Returns the status of this Link.
     *
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * Sets the status of this Link.
     *
     * @param status a status
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Returns a String representation of this Link.
     *
     * @return a String representation of this Link
     */
    @Override
    public String toString() {
        return "url: " + url + ", status: " + status;
    }

}
