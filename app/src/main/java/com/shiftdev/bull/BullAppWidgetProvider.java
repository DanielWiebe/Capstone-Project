package com.shiftdev.bull;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.widget.RemoteViews;
import android.widget.Toast;

import timber.log.Timber;

public class BullAppWidgetProvider extends AppWidgetProvider {

     static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Calculation calculation) {
          RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_provider);

          if (calculation != null) {
               views.setTextViewText(R.id.symbol, calculation.getSymbol());
               views.setTextViewText(R.id.shares, String.valueOf(calculation.getShares()));
               views.setTextViewText(R.id.buy, String.valueOf(calculation.getBuy()));
               views.setTextViewText(R.id.sell, String.valueOf(calculation.getSell()));
               views.setTextViewText(R.id.comm, String.valueOf(calculation.getComm()));
          }
          appWidgetManager.updateAppWidget(appWidgetId, views);
     }

     static void manualUpdateWidgets(Context context, Calculation calculation) {
          ComponentName componentName = new ComponentName(context, BullAppWidgetProvider.class);
          Timber.w("Calculation received in manual Update: %s", calculation.toString());
          AppWidgetManager manager = AppWidgetManager.getInstance(context);
          int[] appWidgetIds = manager.getAppWidgetIds(componentName);

          for (int appWidgetId : appWidgetIds) {
               updateAppWidget(context, manager, appWidgetId, calculation);
               Toast.makeText(context, "Widget has updated!", Toast.LENGTH_LONG).show();
          }
     }

     @Override
     public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
          for (int appWidgetId : appWidgetIds) {
               updateAppWidget(context, appWidgetManager, appWidgetId, null);
          }
     }
}