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

     static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
          RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
          Intent intent = new Intent(context, MainActivity.class);
          PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

          Timber.w("provider received calc as ");


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
     public void onReceive(Context context, Intent intent) {
          AppWidgetManager manager = AppWidgetManager.getInstance(context);
          int[] ids = manager.getAppWidgetIds(new ComponentName(context, AppWidgetProvider.class));
          String action = intent.getAction();
          if (action.equals("android.appwidget.action.APPWIDGET_UPDATE")) {
               calculation = Parcels.unwrap(intent.getParcelableExtra(CALC_FROM_ACTIVITY));
               Timber.w("onreceive in provider received calc as");
               manager.notifyAppWidgetViewDataChanged(ids, R.id.ll_parent_for_widget);
               BullAppWidgetProvider.manualUpdateWidgets(context, manager, ids);
               super.onReceive(context, intent);
          }
     }

}
