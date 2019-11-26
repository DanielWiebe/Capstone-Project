package com.shiftdev.bull;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import org.parceler.Parcels;

import timber.log.Timber;

import static com.shiftdev.bull.WidgetAppService.CALC_FROM_ACTIVITY;

public class BullAppWidgetProvider extends AppWidgetProvider {

     static Calculation calculation;
     static String symbol = null;

     static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
          RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_provider);
          Intent intent = new Intent(context, MainActivity.class);
          PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

          Timber.w("bullwidget provider update app widget called?");


          views.setPendingIntentTemplate(R.id.ll_parent_for_widget, pendingIntent);
          appWidgetManager.updateAppWidget(appWidgetId, views);
     }

     private static void manualUpdateWidgets(Context context, AppWidgetManager manager, int[] ids) {
          for (int appWidgetId : ids) {
               updateAppWidget(context, manager, appWidgetId);
          }
     }

     @Override
     public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
          for (int appWidgetId : appWidgetIds) {

               updateAppWidget(context, appWidgetManager, appWidgetId);
          }
     }

     @Override
     public void onEnabled(Context context) {
          // Enter relevant functionality for when the first widget is created
          Timber.w("bullwidget widget enabled and trying to set text with calc symbol as %s", symbol);
          RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_provider);
          views.setTextViewText(R.id.widget_app_title, "Bull Widget");
     }

     @Override
     public void onReceive(Context context, Intent intent) {
          super.onReceive(context, intent);
          AppWidgetManager manager = AppWidgetManager.getInstance(context);
          String action = intent.getAction();
          if (action.equals("android.appwidget.action.APPWIDGET_UPDATE")) {
               int[] ids = manager.getAppWidgetIds(new ComponentName(context, AppWidgetProvider.class));
               manager.notifyAppWidgetViewDataChanged(ids, R.id.ll_parent_for_widget);
               try {
                    calculation = Parcels.unwrap(intent.getParcelableExtra(CALC_FROM_ACTIVITY));
                    symbol = calculation.getSymbol();
                    Timber.w("bullwidget onreceive in provider received calc %s and about to manually update widget", calculation.toString());
               } catch (NullPointerException e) {
                    Timber.w("bullwidget received a null calculation object");
               }


               BullAppWidgetProvider.manualUpdateWidgets(context, manager, ids);
          }
     }

}
