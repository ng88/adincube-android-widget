package io.nghs.adincube.api;

/**
 * Created by ng on 10/06/16.
 */
public enum AdType
{
    Interstitial, Banner, Native, Rewarded;


    public static final String INTERSTITIAL = "INTERSTITIAL";
    public static final String BANNER = "BANNER";
    public static final String NATIVE = "NATIVE";
    public static final String REWARDED = "REWARDED";

    public static AdType fromString(String s)
    {
        switch(s)
        {
            case INTERSTITIAL: return Interstitial;
            case BANNER: return Banner;
            case NATIVE: return Native;
            case REWARDED: return Rewarded;
        }
        return null;
    }
}
