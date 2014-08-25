package com.splinter2.app.Model;

import java.util.Date;

/**
 * Created by geo on 7/19/14.
 */
public class Coordinate {
    //private variables
    String locationId;
    Double latitude;
    Double longitude;
    String description;
    Long createTime;
    Long modTime;

    public Coordinate(){

    }

    public Coordinate(String locationId, Double latitude, Double longitude,
                      String description, Long createTime, Long modTime){
        this.locationId = locationId;
        this.latitude= latitude;
        this.longitude = longitude;
        this.description = description;
        this.createTime = createTime;
        this.modTime = modTime;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getModTime() {
        return modTime;
    }

    public void setModTime(Long modTime) {
        this.modTime = modTime;
    }
}
