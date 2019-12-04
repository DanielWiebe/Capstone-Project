package com.shiftdev.bull;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.parceler.Parcels;

import timber.log.Timber;

import static com.shiftdev.bull.WidgetAppService.CALC_FROM_ACTIVITY;

public class BullAppWidgetProvider extends AppWidgetProvider {

     static Calculation calculation;
     static String symbol = null;

     static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
          Timber.w("bullwidget provider update app widget called");
          RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_provider);
          Intent intent = new Intent(context, MainActivity.class);
          PendingIntent pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
          views.setRemoteAdapter(R.id.ll_parent_for_widget, new Intent(context, RemoteViewService.class));
          views.setOnClickPendingIntent(R.id.viewCalcBT, pendingIntent);
          views.setPendingIntentTemplate(R.id.ll_parent_for_widget, pendingIntent);
          views.setTextViewText(R.id.widget_app_title, context.getString(R.string.widget_prompt));

          try {
               views.setTextViewText(R.id.symbol, calculation.getSymbol());
               views.setTextViewText(R.id.shares, String.valueOf(calculation.getShares()));
               views.setTextViewText(R.id.buy, String.valueOf(calculation.getBuy()));
               views.setTextViewText(R.id.sell, String.valueOf(calculation.getSell()));
               views.setTextViewText(R.id.comm, String.valueOf(calculation.getComm()));
          } catch (Exception e) {
               e.printStackTrace();
          }
          appWidgetManager.updateAppWidget(appWidgetId, views);
     }

//     private static void manualUpdateWidgets(Context context, AppWidgetManager manager, int[] ids) {
//        //  for (int appWidgetId : ids) {
//               //updateAppWidget(context, manager, appWidgetId);
//       //   }
//     }

     @Override
     public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
          for (int appWidgetId : appWidgetIds) {
               updateAppWidget(context, appWidgetManager, appWidgetId);
               Toast.makeText(context, "Widget has updated!", Toast.LENGTH_LONG).show();
          }
     }

     @Override
     public void onEnabled(Context context) {
          // Enter relevant functionality for when the first widget is created
          Timber.w("bullwidget widget enabled and trying to set text with calc symbol as %s", symbol);
          RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_provider);

     }

     @Override
     public void onReceive(Context context, Intent intent) {
          String action = intent.getAction();
          if (action.equals("android.appwidget.action.APPWIDGET_UPDATE")) {
               AppWidgetManager manager = AppWidgetManager.getInstance(context);
               int[] ids = manager.getAppWidgetIds(new ComponentName(context, BullAppWidgetProvider.class));
               manager.notifyAppWidgetViewDataChanged(ids, R.id.ll_parent_for_widget);

               try {
                    calculation = Parcels.unwrap(intent.getParcelableExtra(CALC_FROM_ACTIVITY));
                    symbol = calculation.getSymbol();


                    Timber.w("bullwidget onreceive in provider received calc %s and about to manually update widget", calculation.toString());
               } catch (NullPointerException e) {
                    Timber.w("bullwidget received a null calculation object");
               }
               super.onReceive(context, intent);
          }
     }


}
