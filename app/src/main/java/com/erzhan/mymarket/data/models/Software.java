package com.erzhan.mymarket.data.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class Software {
    @SerializedName("link")
    @Expose
    private String link;

    @SerializedName("version")
    @Expose
    private String appVersion;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("logo50Link")
    @Expose
    private String logo50Link;

    @SerializedName("logo200Link")
    @Expose
    private String logo200Link;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("description")
    @Expose
    private String description;

    public String getLink() {
        return link;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public String getType() {
        return type;
    }

    public String getPackageName() {
        String packageName = Objects.equals(type, "market") ? "com.UCMobile.intl" : type;
        return packageName;
    }

    public String getLogo50Link() {
        return logo50Link;
    }

    public String getLogo200Link() {
        return logo200Link;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}