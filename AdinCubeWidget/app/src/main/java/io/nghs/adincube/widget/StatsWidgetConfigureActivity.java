package io.nghs.adincube.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import io.nghs.adincube.R;
import io.nghs.fixer.api.FixerAPI;
import io.nghs.fixer.api.Rates;

/**
 * The configuration screen for the {@link StatsWidget StatsWidget} AppWidget.
 */
public class StatsWidgetConfigureActivity extends Activity implements View.OnClickListener
{

    private static final String PREFS_NAME = "StatsWidget";

    private static final String PREF_INCLUDE_ADMOB = "aw_ic_";
    private static final String PREF_API_KEY = "aw_ak_";
    private static final String PREF_CURRENCY = "aw_c_";

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    EditText mApiKey;
    CheckBox mIncludeAdMob;
    TextView mCurrency;
    Button mRatesBtn;

    public StatsWidgetConfigureActivity()
    {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void savePrefs(Context context, int appWidgetId, String apiKey, boolean includeAdmob, String currency)
    {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_API_KEY + appWidgetId, apiKey);
        prefs.putString(PREF_CURRENCY + appWidgetId, currency);
        prefs.putBoolean(PREF_INCLUDE_ADMOB + appWidgetId, includeAdmob);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadAuthPref(Context context, int appWidgetId)
    {
        return context.getSharedPreferences(PREFS_NAME, 0).getString(PREF_API_KEY + appWidgetId, "");
    }

    static String loadCurrencyPref(Context context, int appWidgetId)
    {
        return context.getSharedPreferences(PREFS_NAME, 0).getString(PREF_CURRENCY + appWidgetId, FixerAPI.USD);
    }

    static boolean loadIncludeAdmobPref(Context context, int appWidgetId)
    {
        return context.getSharedPreferences(PREFS_NAME, 0).getBoolean(PREF_INCLUDE_ADMOB + appWidgetId, false);
    }

    static void deletePrefs(Context context, int appWidgetId)
    {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_API_KEY + appWidgetId);
        prefs.remove(PREF_CURRENCY + appWidgetId);
        prefs.remove(PREF_INCLUDE_ADMOB + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.stats_widget_configure);
        mApiKey = (EditText) findViewById(R.id.appwidget_text);
        mIncludeAdMob = (CheckBox) findViewById(R.id.appwidget_includeadmob);
        mCurrency = (TextView) findViewById(R.id.appwidget_currency);
        mRatesBtn = (Button) findViewById(R.id.appwidget_currency_btn);
        mRatesBtn.setOnClickListener(this);
        findViewById(R.id.add_button).setOnClickListener(this);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null)
        {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if(mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID)
        {
            finish();
            return;
        }

        mApiKey.setText(loadAuthPref(this, mAppWidgetId));
        mCurrency.setText(loadCurrencyPref(this, mAppWidgetId));
        mIncludeAdMob.setChecked(loadIncludeAdmobPref(this, mAppWidgetId));
    }

    public void onClick(View v)
    {
        int id = v.getId();
        if(id == R.id.add_button)
        {
            savePrefs(this, mAppWidgetId, mApiKey.getText().toString(), mIncludeAdMob.isChecked(), mCurrency.getText().toString());

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            StatsWidget.updateAppWidget(this, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
        else if(id == R.id.appwidget_currency_btn)
        {
            showRates();
        }
    }

    void showRates()
    {
        mRatesBtn.setEnabled(false);
        new GetRatesTask().execute();
    }

    void onRatesReceived(Rates rates)
    {
        mRatesBtn.setEnabled(true);

        if(rates == null)
            return;

        int i = 0;
        final String[] currencies = new String[rates.getRatesCount()];
        final Iterator<String> it = rates.getCurrencies();
        while(it.hasNext())
            currencies[i++] = it.next();

        Arrays.sort(currencies);

        new AlertDialog.Builder(this)
        .setTitle(R.string.currency_choice)
        .setItems(currencies, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                mCurrency.setText(currencies[which]);
            }
        })
        .show();
    }

    private class GetRatesTask extends AsyncTask<Void, Void, Rates>
    {
        protected Rates doInBackground(Void... p)
        {
            try
            {
                return FixerAPI.getInstance().getRates(FixerAPI.USD);
            }
            catch(Exception ex)
            {
                return null;
            }
        }

        protected void onPostExecute(Rates result)
        {
            onRatesReceived(result);
        }
    }
}

