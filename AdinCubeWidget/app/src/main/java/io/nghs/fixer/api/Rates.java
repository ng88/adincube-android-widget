package io.nghs.fixer.api;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

public class Rates
{

    public static final String ATTR_BASE = "base";
    public static final String ATTR_DATE = "date"; // yyyy-MM-dd
    public static final String ATTR_RATES = "rates";

    static DateFormat DAY_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    private final JSONObject data;
    private final JSONObject rates;

    Rates(JSONObject data) throws JSONException
    {
        this.data = data;
        this.rates = data.getJSONObject(ATTR_RATES);
    }


    String getDateString()
    {
        return getStringValue(ATTR_DATE);
    }
    public Date getDate()
    {
        return getDateValue(ATTR_DATE);
    }
    public String getBaseCurrency()
    {
        return getStringValue(ATTR_BASE);
    }

    public int getRatesCount()
    {
        return rates.length();
    }

    public Iterator<String> getCurrencies()
    {
        return rates.keys();
    }

    public double getRate(String currency)
    {
        try
        {
            return rates.getDouble(currency);
        }
        catch(JSONException e)
        {
            Log.d(FixerAPI.LOG_TAG, "getRate", e);
            return Double.NaN;
        }
    }

    private String getStringValue(String attr)
    {
        try
        {
            return data.getString(attr);
        }
        catch(JSONException e)
        {
            Log.d(FixerAPI.LOG_TAG, "convert", e);
            return null;
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
            Log.d(FixerAPI.LOG_TAG, "convert", e);
            return null;
        }
    }


}
