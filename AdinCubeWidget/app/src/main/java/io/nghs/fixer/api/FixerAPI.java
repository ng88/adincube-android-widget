package io.nghs.fixer.api;

import org.json.JSONException;

import java.util.Date;

/**
 * Created by ng on 13/06/16.
 */
public class FixerAPI
{
    public static final String AUD = "AUD";
    public static final String CAD = "CAD";
    public static final String CHF = "CHF";
    public static final String CYP = "CYP";
    public static final String CZK = "CZK";
    public static final String DKK = "DKK";
    public static final String EEK = "EEK";
    public static final String EUR = "EUR";
    public static final String GBP = "GBP";
    public static final String HKD = "HKD";
    public static final String HUF = "HUF";
    public static final String ISK = "ISK";
    public static final String JPY = "JPY";
    public static final String KRW = "KRW";
    public static final String LTL = "LTL";
    public static final String LVL = "LVL";
    public static final String MTL = "MTL";
    public static final String NOK = "NOK";
    public static final String NZD = "NZD";
    public static final String PLN = "PLN";
    public static final String ROL = "ROL";
    public static final String SEK = "SEK";
    public static final String SGD = "SGD";
    public static final String SIT = "SIT";
    public static final String SKK = "SKK";
    public static final String TRL = "TRL";
    public static final String USD = "USD";
    public static final String ZAR = "ZAR";


    static final String LOG_TAG = "FIXER_API";
    private static FixerAPI instance = null;
    private final ServerInterface server = new ServerInterface();


    public static FixerAPI getInstance()
    {
        if(instance == null)
            instance = new FixerAPI();

        return instance;
    }

    private FixerAPI() {}


    public Rates getRates(String baseCurrency, Date date)
    {
        try
        {
            return server.getRates(date, baseCurrency);
        }
        catch(JSONException e)
        {
            throw new RuntimeException(e);
        }
    }


    public Rates getRates(String baseCurrency)
    {
        return getRates(baseCurrency, new Date());
    }


    public Rates getRates()
    {
        return getRates(EUR);
    }

}
