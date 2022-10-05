/**
 * Name: Cai Yuejun Leon
 * 
 * Reverse Engineer POJO class by Visual Paradigm from json.
 */
package ExtractTransform.as_is;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import ExtractTransform.AllKeysRequired;
@AllKeysRequired
public class Root {
    @SerializedName("name")
    private String name;
    @SerializedName("airport")
    private Airport airport;
    @SerializedName("waypoints")
    private List<Waypoint> waypoints = new ArrayList<Waypoint>();
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Airport getAirport() {
        return airport;
    }
    public void setAirport(Airport airport) {
        this.airport = airport;
    }
    public List<Waypoint> getWaypoints() {
        return waypoints;
    }
    public void setWaypoints(List<Waypoint> waypoints) {
        this.waypoints = waypoints;
    }
}