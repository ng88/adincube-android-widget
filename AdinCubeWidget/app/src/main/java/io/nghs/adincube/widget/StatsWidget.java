package io.nghs.adincube.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Adapter;
import android.widget.RemoteViews;

import java.util.Calendar;
import java.util.Date;

import io.nghs.adincube.R;
import io.nghs.adincube.api.AdinCubeAPI;
import io.nghs.fixer.api.FixerAPI;
import io.nghs.fixer.api.Rates;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link StatsWidgetConfigureActivity StatsWidgetConfigureActivity}
 */
public class StatsWidget extends AppWidgetProvider
{

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        // There may be multiple widgets active, so update all of them
        for(int appWidgetId : appWidgetIds)
            updateAppWidget(context, appWidgetManager, appWidgetId);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds)
    {
        // When the user deletes the widget, delete the preference associated with it.
        for(int appWidgetId : appWidgetIds)
            StatsWidgetConfigureActivity.deletePrefs(context, appWidgetId);
    }

    @Override
    public void onEnabled(Context context)
    {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context)
    {
        // Enter relevant functionality for when the last widget is disabled
    }


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId)
    {
        new UpdateFromServerTask(context, appWidgetManager, appWidgetId).execute();
    }

    private static class UpdateFromServerTask extends AsyncTask<Void, Void, Void>
    {
        double accountRevenues[] = new double[6];
        Context context;
        AppWidgetManager appWidgetManager;
        int appWidgetId;
        String authToken;
        String currency;
        boolean includeAdmob;
        Rates rates;


        UpdateFromServerTask(Context context, AppWidgetManager appWidgetManager, int appWidgetId)
        {
            this.context = context;
            this.appWidgetManager = appWidgetManager;
            this.appWidgetId = appWidgetId;
            this.authToken = StatsWidgetConfigureActivity.loadAuthPref(context, appWidgetId);
            this.currency = StatsWidgetConfigureActivity.loadCurrencyPref(context, appWidgetId);
            this.includeAdmob = StatsWidgetConfigureActivity.loadIncludeAdmobPref(context, appWidgetId);
        }

        protected Void doInBackground(Void... p)
        {
            if(authToken == null || authToken.isEmpty())
                return null;

            if(!FixerAPI.USD.equals(currency))
                rates = FixerAPI.getInstance().getRates(FixerAPI.USD);

            AdinCubeAPI api = AdinCubeAPI.getInstance(authToken);
            api.clearCache();

            Calendar cal = Calendar.getInstance();
            accountRevenues[0] = getRevenues(api, cal.getTime());
            for(int i = 1; i < accountRevenues.length; i++)
            {
                cal.add(Calendar.DATE, -1);
                accountRevenues[i] = getRevenues(api, cal.getTime());
            }

            return null;
        }

        private double getRevenues(AdinCubeAPI a, Date d)
        {
            double rate = 1.0;

            if(!FixerAPI.USD.equals(currency))
                rate = rates.getRate(currency);

            if(includeAdmob)
                return a.getRevenues(d) * rate;
            else
                return a.getAccountRevenues(d) * rate;
        }


        protected void onPostExecute(Void result)
        {

            // Construct the RemoteViews object
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.stats_widget);

            StringBuilder text = new StringBuilder();

            for(int i = 0; i < accountRevenues.length; i++)
            {
                if(i == 0)
                    text.append("Today ");
                else
                    text.append("D-").append(i).append("    ");
                text.append(String.format("%.2f", accountRevenues[i])).append('\n');
            }
            views.setTextViewText(R.id.appwidget_text, text.toString());

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }


}

