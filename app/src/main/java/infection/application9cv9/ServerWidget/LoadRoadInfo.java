package infection.application9cv9.ServerWidget;

import android.content.Context;
import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import infection.application9cv9.Modules.Route;

/**
 * Created by Tu Van Ninh on 4/13/2017.
 */
public class LoadRoadInfo extends LoadDataTask {

    ArrayList<Route> listRoad = new ArrayList<>();
    public OnFinishLoadRoadInfo delegate = null;

    public LoadRoadInfo(Context context) {
        super(context);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            JSONArray array = new JSONArray(s);
            for (int i=0; i<array.length(); i++){
                JSONArray road = array.getJSONObject(i).getJSONArray("road");

                JSONObject object = road.getJSONObject(0);
                Double slat = object.getDouble("lat");
                Double slng = object.getDouble("long");

                object = road.getJSONObject(1);
                Double elat = object.getDouble("lat");
                Double elng = object.getDouble("long");

                Route route = new Route();
                route.points = new ArrayList<>();
                route.points.add(new LatLng(slat, slng));
                route.points.add(new LatLng(elat, elng));

                listRoad.add(route);
            }
            delegate.processFinish(listRoad);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public interface OnFinishLoadRoadInfo{
        void processFinish(ArrayList<Route> listRoadInfo);
    }
}
