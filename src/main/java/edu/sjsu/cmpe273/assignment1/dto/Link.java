package edu.sjsu.cmpe273.assignment1.dto;

/**
 * User: maksim
 * Date: 2/27/14 - 11:05 PM
 */
public class Link {

    String rel;
    String href;
    String method;

    public Link(String rel, String href, String method){
        this.rel = rel;
        this.href = href;
        this.method = method;
    }

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
