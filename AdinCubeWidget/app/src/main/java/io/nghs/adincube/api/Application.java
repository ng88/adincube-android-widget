package io.nghs.adincube.api;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ng on 10/06/16.
 */
public class Application
{
    public static final String ATTR_ID = "id";
    public static final String ATTR_BUNDLE_ID = "bundleId";
    public static final String ATTR_KEY = "key";
    public static final String ATTR_NAME = "name";

    private final JSONObject data;

    Application(JSONObject data)
    {
        this.data = data;
    }


    public String getID()
    {
        return getStringValue(ATTR_ID);
    }
    public String getBundleID()
    {
        return getStringValue(ATTR_BUNDLE_ID);
    }
    public String getName()
    {
        return getStringValue(ATTR_NAME);
    }
    public String getKey()
    {
        return getStringValue(ATTR_KEY);
    }


    private String getStringValue(String attr)
    {
        try
        {
            return data.getString(attr);
        }
        catch(JSONException e)
        {
            Log.d(AdinCubeAPI.LOG_TAG, "convert", e);
            return null;
        }
    }
}
