package infection.application9cv9.Modules;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class DirectionFinder {
    private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String GOOGLE_API_KEY = "AIzaSyDSwXE8lGoqRJjR9cNfNR-_ru3pdnRS70s";
    private DirectionFinderListener listener;
    private String origin;
    private String destination;

    public DirectionFinder(DirectionFinderListener listener, String origin, String destination) {
        this.listener = listener;
        this.origin = origin;
        this.destination = destination;
    }

    public void execute() throws UnsupportedEncodingException {
        listener.onDirectionFinderStart();
        new DownloadRawData().execute(createUrl());
    }

    private String createUrl() throws UnsupportedEncodingException {
        String urlOrigin = URLEncoder.encode(origin, "utf-8");
        String urlDestination = URLEncoder.encode(destination, "utf-8");

        return DIRECTION_URL_API + "origin=" + urlOrigin + "&destination=" + urlDestination + "&key=" + GOOGLE_API_KEY;
//        return "https://maps.googleapis.com/maps/api/directions/json?origin=10.75201684447029,106.65901912611416&destination=10.756377781053402,106.68518281777517" +
//                "&waypoints=10.75201684447029,106.65901912611416|10.752019442860274,106.66001917098178|10.752021119240908,106.66067798857125|10.752023130897669,106.66143789191295|10.752047270778803,106.66172396626826|10.75210192078751,106.66237322848808|10.752134274933752,106.66275711965346|10.752178950477685,106.66328769412434|10.752231421191539,106.66391114008233|10.752271821964825,106.66438454997359|10.752368046213263,106.66553396035579|10.752409788091057,106.6660281573669|10.75246971869876,106.666741541146|10.752519088108443,106.66732701708268|10.75254146778994,106.66759305868942|10.752554962654045,106.66775348831612|10.75260709809177,106.66838322070156|10.752623861898115,106.66939156365333|10.753433553744685,106.66949013483469|10.754156157617272,106.67081766065928|10.754268558938833,106.67101262372711|10.754456481207967,106.67122686517223|10.755069533606076,106.67192532916368|10.755233399813136,106.67279168267567|10.755545374249238,106.6742703342145|10.755778139700368,106.67481976796756|10.755950555448663,106.67522008766309|10.755950555448663,106.67522008766309|10.756099166591923,106.67557103794898|10.75621592650313,106.67585032296273|10.756381972004988,106.67624846336349|10.756820680817098,106.67724507165082|10.757070796807794,106.6778380913003|10.75723625557643,106.67822156337053|10.757624756788543,106.67914709311894|10.758043432852048,106.68017337334351|10.758172597979948,106.68078022313324|10.758495552709235,106.681326220306|10.758957647031195,106.68257856045915|10.759421585371854,106.68382436272782|10.757941257452359,106.68453724359271|10.757488047947788,106.68471426938777|10.756377781053402,106.68518281777517" +
//                "&key=AIzaSyDSwXE8lGoqRJjR9cNfNR-_ru3pdnRS70s";
    }

    private class DownloadRawData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String link = params[0];
            try {
                URL url = new URL(link);
                InputStream is = url.openConnection().getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String res) {
            try {
                parseJSon(res);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void parseJSon(String data) throws JSONException {
        if (data == null)
            return;

        List<Route> routes = new ArrayList<Route>();
        JSONObject jsonData = new JSONObject(data);
        JSONArray jsonRoutes = jsonData.getJSONArray("routes");
        for (int i = 0; i < jsonRoutes.length(); i++) {
            JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
            Route route = new Route();

            JSONObject overview_polylineJson = jsonRoute.getJSONObject("overview_polyline");
            JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
            JSONObject jsonLeg = jsonLegs.getJSONObject(0);
            JSONObject jsonDistance = jsonLeg.getJSONObject("distance");
            JSONObject jsonDuration = jsonLeg.getJSONObject("duration");
            JSONObject jsonEndLocation = jsonLeg.getJSONObject("end_location");
            JSONObject jsonStartLocation = jsonLeg.getJSONObject("start_location");

            route.distance = new Distance(jsonDistance.getString("text"), jsonDistance.getInt("value"));
            route.duration = new Duration(jsonDuration.getString("text"), jsonDuration.getInt("value"));
            route.endAddress = jsonLeg.getString("end_address");
            route.startAddress = jsonLeg.getString("start_address");
            route.startLocation = new LatLng(jsonStartLocation.getDouble("lat"), jsonStartLocation.getDouble("lng"));
            route.endLocation = new LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng"));
            route.points = decodePolyLine(overview_polylineJson.getString("points"));

            route.steps = new ArrayList<Step>();
            JSONArray jsonSteps = jsonLeg.getJSONArray("steps");
            for (int j = 0; j < jsonSteps.length(); j++)
            {
                String jsonHtmlInstruction = null;
                String jsonManeuver = null;
                JSONObject jsonStep = jsonSteps.getJSONObject(j);
                Log.d("Step " + j, jsonStep.toString());
                JSONObject jsonDistanceStep = jsonStep.getJSONObject("distance");
                JSONObject jsonDurationStep = jsonStep.getJSONObject("duration");
                JSONObject jsonStartLocationStep = jsonStep.getJSONObject("start_location");
                JSONObject jsonEndLocationStep = jsonStep.getJSONObject("end_location");
                if (jsonStep.has("html_instructions"))
                    jsonHtmlInstruction = jsonStep.getString("html_instructions");
                if (jsonStep.has("maneuver"))
                    jsonManeuver = jsonStep.getString("maneuver");

                Step step = new Step();
                step.maneuver = jsonManeuver;
                step.htmlInstructions = jsonHtmlInstruction;
                step.distance = new Distance(jsonDistanceStep.getString("text"), jsonDistanceStep.getInt("value"));
                step.duration = new Duration(jsonDurationStep.getString("text"), jsonDurationStep.getInt("value"));
                step.startLocation = new LatLng(jsonStartLocationStep.getDouble("lat"), jsonStartLocationStep.getDouble("lng"));
                step.endLocation = new LatLng(jsonEndLocationStep.getDouble("lat"), jsonEndLocationStep.getDouble("lng"));
                step.points = decodePolyLine(jsonStep.getJSONObject("polyline").getString("points"));
                route.steps.add(step);
            }

            routes.add(route);
        }

        listener.onDirectionFinderSuccess(routes);
    }

    private List<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded;
    }
}
