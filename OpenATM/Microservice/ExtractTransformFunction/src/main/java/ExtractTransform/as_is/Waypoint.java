package ExtractTransform.as_is;
import com.google.gson.annotations.SerializedName;
import ExtractTransform.AllKeysRequired;
@AllKeysRequired
public class Waypoint {
    @SerializedName("uid")
    private String uid;
    @SerializedName("name")
    private String name;
    @SerializedName("lat")
    private Double lat;
    @SerializedName("lng")
    private Double lng;
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
}
