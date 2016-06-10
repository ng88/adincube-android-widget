package io.nghs.adincube.api;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ng on 10/06/16.
 */
public class Balance
{
    public static final String ATTR_BALANCE = "balance";
    public static final String ATTR_POST30 = "post30";
    public static final String ATTR_LAST30 = "last30";

    private final JSONObject data;

    Balance(JSONObject data)
    {
        this.data = data;
    }

    /**
     *
     * @return the current balance in $
     */
    public double getBalance()
    {
        return getDollarValue(ATTR_BALANCE);
    }

    /**
     *
     * @return the current post30 in $
     */
    public double getPost30()
    {
        return getDollarValue(ATTR_POST30);
    }

    /**
     *
     * @return the current last30 in $
     */
    public double getLast30()
    {
        return getDollarValue(ATTR_LAST30);
    }

    private double getDollarValue(String attr)
    {
        try
        {
            return ((double)data.getInt(attr)) / 100.0;
        }
        catch(JSONException e)
        {
            Log.d(AdinCubeAPI.LOG_TAG, "convert", e);
            return Double.NaN;
        }
    }
}
