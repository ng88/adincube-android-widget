package io.nghs.adincube.api;


import android.util.Log;
import android.util.LruCache;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;


/**
 * Synchronous server request maker
 * Created by ng on 10/06/16.
 */
class ServerInterface
{

    final static String BASE_URL = "https://api.adincube.com/api/1.0/public/";

    private final LruCache<String, String> cache = new LruCache<String, String>(8);

    private String apiKey;

    ServerInterface()
    {

    }

    void setAPIKey(String apiKey)
    {
        this.apiKey = apiKey;
    }

    void clearCache()
    {
        cache.evictAll();
    }

    Balance getBalance() throws JSONException
    {
        return new Balance(new JSONObject(getJSONWithCache("balance")));
    }

    Application[] getAppList() throws JSONException
    {
        final JSONArray s = new JSONArray(getJSONWithCache("apps"));
        final Application[] r = new Application[s.length()];
        for(int i = 0; i < r.length; i++)
            r[i] = new Application(s.getJSONObject(i));
        return r;
    }

    String[] getAppAttribList(String attr) throws JSONException
    {
        final JSONArray s = new JSONArray(getJSONWithCache("apps"));
        final String[] r = new String[s.length()];
        for(int i = 0; i < r.length; i++)
            r[i] = s.getJSONObject(i).getString(attr);
        return r;
    }

    Object getOverviewReport()
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    ApplicationReport[] getAppReports(String appId) throws JSONException
    {
        final JSONArray s = new JSONArray(getJSONWithCache("reporting/app/" + appId));
        final ApplicationReport[] r = new ApplicationReport[s.length()];
        for(int i = 0; i < r.length; i++)
            r[i] = new ApplicationReport(s.getJSONObject(i));
        return r;
    }

    double getRevenuesForDay(Date day, String excludedNetwork) throws JSONException
    {
        final String dayStr = ApplicationReport.DAY_FORMAT.format(day);
        final String cacheKey = dayStr +"*rev*" + excludedNetwork;
        String cachedValue = cache.get(cacheKey);
        double sum = 0.0;
        if(cachedValue == null)
        {
            for(final String appId : getAppAttribList(Application.ATTR_ID))
            {
                for(final ApplicationReport report : getAppReports(appId))
                {
                    if(dayStr.equals(report.getDateString()) && !excludedNetwork.equals(report.getNetwork()))
                        sum += report.getRevenues();
                }
            }
            cache.put(cacheKey, Double.toString(sum));
        }
        else
        {
            sum = Double.valueOf(cachedValue);
        }
        return sum;
    }


    private String getJSONWithCache(String operation)
    {
        String r = cache.get(operation);
        if(r == null)
        {
            r = getJSON(operation);
            if(r != null)
                cache.put(operation, r);
        }
        return r;
    }


    private String getJSON(String operation)
    {
        try
        {
            final HttpURLConnection c = (HttpURLConnection) new URL(BASE_URL + operation).openConnection();
            c.setRequestMethod("GET");
            c.setConnectTimeout(60 * 1000); // 1 min timeout
            c.setReadTimeout(60 * 1000); // 1 min timeout
            c.setUseCaches(false);
            c.setDoOutput(false);
            c.setDoInput(true);
            c.setRequestProperty("Authorization", "Token " + apiKey);
            c.setInstanceFollowRedirects(true);
            c.connect();
            if(c.getResponseCode() < 300)
            {
                final BufferedReader streamReader = new BufferedReader(new InputStreamReader(c.getInputStream()));
                final StringBuilder responseStrBuilder = new StringBuilder(128);

                String inputStr;
                while((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);

                streamReader.close();
                return responseStrBuilder.toString();
            }
        }
        catch(IOException ex)
        {
            Log.d(AdinCubeAPI.LOG_TAG, "request", ex);
        }
        return null;
    }

   // public void downloadFile()
}
