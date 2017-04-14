package infection.application9cv9.Modules;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by khiem on 4/13/2017.
 */
public class Step {
    public Distance distance;
    public Duration duration;
    public LatLng startLocation;
    public LatLng endLocation;
    public String htmlInstructions;
    public String maneuver;
    public List<LatLng> points;
}
