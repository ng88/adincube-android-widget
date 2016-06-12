package io.nghs.adincube.api;

import android.util.Log;

import org.json.JSONException;

import java.security.InvalidParameterException;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by ng on 10/06/16.
 */
public class AdinCubeAPI
{
    static final String LOG_TAG = "ADINCUBE_API";
    private static HashMap<String, AdinCubeAPI> instances = null;
    private final ServerInterface server = new ServerInterface();

    /**
     * Call this with your authorization token
     * You can get one at https://dashboard.adincube.com/dashboard/#/api/auth
     * @return the instance of the AdinCube API manager
     */
    public static AdinCubeAPI getInstance(String authKey)
    {
        if(authKey == null || authKey.isEmpty())
            throw new InvalidParameterException();

        if(instances == null)
            instances = new HashMap<>();

        AdinCubeAPI instance = instances.get(authKey);
        if(instance == null)
        {
            instance = new AdinCubeAPI();
            instance.server.setAPIKey(authKey);
            instances.put(authKey, instance);
        }

        return instance;
    }

    private AdinCubeAPI() {}


    public Balance getAccountBalance()
    {
        try
        {
            return server.getBalance();
        }
        catch(JSONException e)
        {
            throw new RuntimeException(e);
        }
    }

    public ApplicationReport[] getApplicationReports(String appId)
    {
        try
        {
            return server.getAppReports(appId);
        }
        catch(JSONException e)
        {
            throw new RuntimeException(e);
        }
    }


    public Application[] getApplications()
    {
        try
        {
            return server.getAppList();
        }
        catch(JSONException e)
        {
            throw new RuntimeException(e);
        }
    }


    /**
     *
     * @param day
     * @return all the revenues in $ for the given day (ie including admob)
     */
    public double getRevenues(Date day)
    {
        return getRevenues(day, "");
    }
    /**
     *
     * @param day
     * @return revenues in $ that are on the Adincube account for the given day (ie excluding admob)
     */
    public double getAccountRevenues(Date day)
    {
        return getRevenues(day, "AdMob");
    }

    private double getRevenues(Date day, String excludedNetwork)
    {
        try
        {
           return server.getRevenuesForDay(day, excludedNetwork);
        }
        catch(JSONException e)
        {
            Log.d(AdinCubeAPI.LOG_TAG, "revenues", e);
            return Double.NaN;
        }
    }

    public void clearCache()
    {
        server.clearCache();
    }
}
