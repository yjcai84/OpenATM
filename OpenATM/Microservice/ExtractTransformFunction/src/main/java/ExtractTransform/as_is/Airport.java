package ExtractTransform.as_is;
import com.google.gson.annotations.SerializedName;

import ExtractTransform.AllKeysRequired;
@AllKeysRequired
public class Airport {
    @SerializedName("uid")
    private String uid;
    @SerializedName("name")
    private String name;
    @SerializedName("icao")
    private String icao;
    @SerializedName("lat")
    private Double lat;
    @SerializedName("lng")
    private Double lng;
    @SerializedName("alt")
    private Integer alt;
    @SerializedName("iata")
    private Object iata;
    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getIcao() {
        return icao;
    }
    public void setIcao(String icao) {
        this.icao = icao;
    }
    public Double getLat() {
        return lat;
    }
    public void setLat(Double lat) {
        this.lat = lat;
    }
    public Double getLng() {
        return lng;
    }
    public void setLng(Double lng) {
        this.lng = lng;
    }
    public Integer getAlt() {
        return alt;
    }
    public void setAlt(Integer alt) {
        this.alt = alt;
    }
    public Object getIata() {
        return iata;
    }
    public void setIata(Object iata) {
        this.iata = iata;
    }
}