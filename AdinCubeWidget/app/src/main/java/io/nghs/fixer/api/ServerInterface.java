package io.nghs.fixer.api;


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

import io.nghs.adincube.api.AdinCubeAPI;
import io.nghs.adincube.api.Application;
import io.nghs.adincube.api.ApplicationReport;
import io.nghs.adincube.api.Balance;


/**
 * Synchronous server request maker
 */
class ServerInterface
{

    final static String BASE_URL = "http://api.fixer.io/";

    private final LruCache<String, Rates> cache = new LruCache<String, Rates>(4);

    ServerInterface()
    {

    }

    void clearCache()
    {
        cache.evictAll();
    }

    Rates getRates(Date date, String baseCurrency) throws JSONException
    {
        String dateStr = Rates.DAY_FORMAT.format(date);
        String cacheKey = dateStr + "-" + baseCurrency;


        Rates r = cache.get(cacheKey);
        if(r == null)
        {
            String s = getJSON(dateStr);
            if(s != null)
            {
                r = new Rates(new JSONObject(s));
                cache.put(cacheKey, r);
            }
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
            Log.d(FixerAPI.LOG_TAG, "request", ex);
        }
        return null;
    }

   // public void downloadFile()
}
