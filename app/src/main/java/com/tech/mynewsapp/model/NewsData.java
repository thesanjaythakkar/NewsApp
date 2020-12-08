package com.tech.mynewsapp.model;

public class NewsData {

    private String title;
    private String link;
    private String pubDate;
    private String source;
    private String mediacontent;
    private String mediacredit;
    private long timestemp;


    public long getTimestemp() {
        return timestemp;
    }

    public void setTimestemp(long timestemp) {
        this.timestemp = timestemp;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMediacontent() {
        return mediacontent;
    }

    public void setMediacontent(String mediacontent) {
        this.mediacontent = mediacontent;
    }

    public String getMediacredit() {
        return mediacredit;
    }

    public void setMediacredit(String mediacredit) {
        this.mediacredit = mediacredit;
    }
}
