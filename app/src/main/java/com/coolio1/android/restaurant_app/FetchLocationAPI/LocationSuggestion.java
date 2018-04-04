
package com.coolio1.android.restaurant_app;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LocationSuggestion {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("country_id")
    @Expose
    private Integer countryId;
    @SerializedName("country_name")
    @Expose
    private String countryName;
    @SerializedName("country_flag_url")
    @Expose
    private String countryFlagUrl;
    @SerializedName("should_experiment_with")
    @Expose
    private Integer shouldExperimentWith;
    @SerializedName("discovery_enabled")
    @Expose
    private Integer discoveryEnabled;
    @SerializedName("has_new_ad_format")
    @Expose
    private Integer hasNewAdFormat;
    @SerializedName("is_state")
    @Expose
    private Integer isState;
    @SerializedName("state_id")
    @Expose
    private Integer stateId;
    @SerializedName("state_name")
    @Expose
    private String stateName;
    @SerializedName("state_code")
    @Expose
    private String stateCode;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryFlagUrl() {
        return countryFlagUrl;
    }

    public void setCountryFlagUrl(String countryFlagUrl) {
        this.countryFlagUrl = countryFlagUrl;
    }

    public Integer getShouldExperimentWith() {
        return shouldExperimentWith;
    }

    public void setShouldExperimentWith(Integer shouldExperimentWith) {
        this.shouldExperimentWith = shouldExperimentWith;
    }

    public Integer getDiscoveryEnabled() {
        return discoveryEnabled;
    }

    public void setDiscoveryEnabled(Integer discoveryEnabled) {
        this.discoveryEnabled = discoveryEnabled;
    }

    public Integer getHasNewAdFormat() {
        return hasNewAdFormat;
    }

    public void setHasNewAdFormat(Integer hasNewAdFormat) {
        this.hasNewAdFormat = hasNewAdFormat;
    }

    public Integer getIsState() {
        return isState;
    }

    public void setIsState(Integer isState) {
        this.isState = isState;
    }

    public Integer getStateId() {
        return stateId;
    }

    public void setStateId(Integer stateId) {
        this.stateId = stateId;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

}
