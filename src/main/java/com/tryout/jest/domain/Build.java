package com.tryout.jest.domain;

import io.searchbox.annotations.JestId;

import java.io.Serializable;
import java.util.Date;

public class Build implements Serializable {
    private static final long serialVersionUID = -3971912226293959387L;

    @JestId
    private String id;

    private String info;

    private Date createdOn;

    private String userName;

    public Build(final String userName, final String info) {
        this.userName = userName;
        this.info = info;
        this.createdOn = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "Build [id=" + id + ", info=" + info + ", createdOn=" + createdOn
                + ", userName=" + userName + "]";
    }

}
