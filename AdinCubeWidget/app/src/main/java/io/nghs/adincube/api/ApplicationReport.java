package io.nghs.adincube.api;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

/**
 * Created by ng on 10/06/16.
 */
public class ApplicationReport
{
    public static final String ATTR_DAY = "day"; // yyyy-MM-dd
    public static final String ATTR_NETWORK = "network";
    public static final String ATTR_ADTYPE = "adType"; // INTERSTITIAL, BANNER, NATIVE or REWARDED.
    public static final String ATTR_IMPRESSIONS = "impressions";
    public static final String ATTR_CLICKS = "clicks";
    public static final String ATTR_REVENUES = "revenues";
    public static final String ATTR_FILLRATE = "fillRates";
    public static final String ATTR_GLOBALFILLRATE = "globalFillRate";

    static DateFormat DAY_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    private final JSONObject data;

    ApplicationReport(JSONObject data)
    {
        this.data = data;
    }


    String getDateString()
    {
        return getStringValue(ATTR_DAY);
    }
    public Date getDate()
    {
        return getDateValue(ATTR_DAY);
    }
    public String getNetwork()
    {
        return getStringValue(ATTR_NETWORK);
    }
    public AdType getAdType()
    {
        return AdType.fromString(getStringValue(ATTR_ADTYPE));
    }
    public int getImpressions()
    {
        return getCountrySum(ATTR_IMPRESSIONS);
    }
    public int getClicks()
    {
        return getCountrySum(ATTR_CLICKS);
    }
    public double getRevenues()
    {
        return ((double)getCountrySum(ATTR_REVENUES)) / 100.0;
    }
    public double getFillRate()
    {
        return getDoubleValue(ATTR_GLOBALFILLRATE);
    }

/*
    impressions 	Impressions by country. ISO 3166-1 country codes.
    clicks 	Clicks by country. ISO 3166-1 country codes.
    revenues 	Revenues in $ cents by country. ISO 3166-1 country codes.
*/
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

    private double getDoubleValue(String attr)
    {
        try
        {
            return data.getDouble(attr);
        }
        catch(JSONException e)
        {
            Log.d(AdinCubeAPI.LOG_TAG, "convert", e);
            return Double.NaN;
        }
    }

    private Date getDateValue(String attr)
    {
        try
        {
            return DAY_FORMAT.parse(data.getString(attr));
        }
        catch(Exception e)
        {
            Log.d(AdinCubeAPI.LOG_TAG, "convert", e);
            return null;
        }
    }



    private int getCountrySum(String attr)
    {
        int acc = 0;
        try
        {
            final JSONObject o = data.getJSONObject(attr);
            final Iterator<String> it = o.keys();
            while(it.hasNext())
                acc += o.getInt(it.next());
        }
        catch(Exception e)
        {
            Log.d(AdinCubeAPI.LOG_TAG, "convert", e);
        }
        return acc;
    }
}
