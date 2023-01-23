package com.isansys.patientgateway.serverlink.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetWardDetailsResponse {

    @SerializedName("WardDetailsId")
    @Expose
    private Integer wardDetailsId;
    @SerializedName("ByUserId")
    @Expose
    private Integer byUserId;
    @SerializedName("WardLocation")
    @Expose
    private String wardLocation;
    @SerializedName("WardDescription")
    @Expose
    private String wardDescription;
    @SerializedName("WardName")
    @Expose
    private String wardName;
    @SerializedName("Status")
    @Expose
    private Integer status;
    @SerializedName("IsDeleted")
    @Expose
    private Integer isDeleted;
    @SerializedName("Timestamp")
    @Expose
    private String timestamp;

    public Integer getWardDetailsId() {
        return wardDetailsId;
    }

    public void setWardDetailsId(Integer wardDetailsId) {
        this.wardDetailsId = wardDetailsId;
    }

    public Integer getByUserId() {
        return byUserId;
    }

    public void setByUserId(Integer byUserId) {
        this.byUserId = byUserId;
    }

    public String getWardLocation() {
        return wardLocation;
    }

    public void setWardLocation(String wardLocation) {
        this.wardLocation = wardLocation;
    }

    public String getWardDescription() {
        return wardDescription;
    }

    public void setWardDescription(String wardDescription) {
        this.wardDescription = wardDescription;
    }

    public String getWardName() {
        return wardName;
    }

    public void setWardName(String wardName) {
        this.wardName = wardName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}
