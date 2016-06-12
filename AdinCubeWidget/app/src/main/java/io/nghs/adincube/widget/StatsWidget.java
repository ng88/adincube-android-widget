package io.nghs.adincube.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Adapter;
import android.widget.RemoteViews;

import java.util.Calendar;

import io.nghs.adincube.R;
import io.nghs.adincube.api.AdinCubeAPI;

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
        {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds)
    {
        // When the user deletes the widget, delete the preference associated with it.
        for(int appWidgetId : appWidgetIds)
        {
            StatsWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
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
        String authToken = StatsWidgetConfigureActivity.loadAuthPref(context, appWidgetId);

        new UpdateFromServerTask(context, appWidgetManager, appWidgetId).execute(authToken);
    }

    private static class UpdateFromServerTask extends AsyncTask<String, Void, Void>
    {
        double todayRevenues = 0.0;
        double yesterdayRevenues = 0.0;
        Context context;
        AppWidgetManager appWidgetManager;
        int appWidgetId;

        UpdateFromServerTask(Context context, AppWidgetManager appWidgetManager, int appWidgetId)
        {
            this.context = context;
            this.appWidgetManager = appWidgetManager;
            this.appWidgetId = appWidgetId;
        }

        protected Void doInBackground(String... p)
        {
            if(p[0] == null || p[0].isEmpty())
                return null;

            AdinCubeAPI api = AdinCubeAPI.getInstance(p[0]);
            api.clearCache();

            Calendar cal = Calendar.getInstance();
            todayRevenues = api.getAccountRevenues(cal.getTime());
            cal.add(Calendar.DATE, -1);
            yesterdayRevenues = api.getAccountRevenues(cal.getTime());

            return null;
        }


        protected void onPostExecute(Void result)
        {

            // Construct the RemoteViews object
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.stats_widget);


            String text = String.format("%.2f", todayRevenues) + "\n" + String.format("%.2f", yesterdayRevenues);

            views.setTextViewText(R.id.appwidget_text, text);

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }


}

