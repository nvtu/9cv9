package infection.application9cv9.ServerWidget;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Tu Van Ninh on 1/14/2017.
 */
public class LoadDataTask extends AsyncTask<String, Integer, String> {
    Context mContext; // context of activity

    public LoadDataTask(Context context) {
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    // details: how to load content of web
    // return data downloaded as string
    // and handle this data in onPostExecute method
    @Override
    protected String doInBackground(String... params) {
        HttpGet httpGet = new HttpGet(params[0]);
        HttpEntity httpEntity = null;
        HttpClient httpClient = new DefaultHttpClient();

        try {
            httpEntity = httpClient.execute(httpGet).getEntity();
        } catch (ClientProtocolException e)
        {
            e.printStackTrace();
            return "";
        } catch (IOException e)
        {
            e.printStackTrace();
            return "";
        }

        if (httpEntity != null)
        {
            try {
                InputStream inputStream = httpEntity.getContent();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null)
                {
                    stringBuilder.append(line);
                }
                inputStream.close();
                return stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
        }

        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        // TODO
    }
}
