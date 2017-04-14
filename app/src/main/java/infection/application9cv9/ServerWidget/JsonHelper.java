package infection.application9cv9.ServerWidget;

import android.util.JsonWriter;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by Tu Van Ninh on 1/3/2017.
 */
public class JsonHelper {

    public JsonHelper(){

    }

    public String writeQuery(String startLat, String startLng, String endLat, String endLng) throws IOException {
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        writer.beginObject();
        writer.name("start");
        writer.beginObject();
        writer.name("lat").value(startLat);
        writer.name("long").value(startLng);
        writer.endObject();

        writer.name("end");
        writer.beginObject();
        writer.name("lat").value(endLat);
        writer.name("long").value(endLng);
        writer.endObject();
        writer.endObject();
        writer.close();
        return sw.toString();
    }
}
