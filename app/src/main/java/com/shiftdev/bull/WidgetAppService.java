package com.shiftdev.bull;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import org.parceler.Parcels;

import timber.log.Timber;

class WidgetAppService extends IntentService {


     public static final String CALC_FROM_ACTIVITY = "CALC_FROM_ACTIVITY";

     public WidgetAppService(String name) {
          super(name);
     }


     public static void startWidgetService(Context context, Calculation calculationFromActivity) {
          Intent intent = new Intent(context, WidgetAppService.class);
          intent.putExtra(CALC_FROM_ACTIVITY, Parcels.wrap(calculationFromActivity));
          Timber.w("Service class received calculation");
          context.startService(intent);
     }

     @Override
     protected void onHandleIntent(@Nullable Intent intent) {
          if (intent != null) {

               Calculation calc = Parcels.unwrap(intent.getParcelableExtra(CALC_FROM_ACTIVITY));
               Timber.w("service class received calc and passing to handle widget update");
               handleWidgetUpdate(calc);
          }
     }

     private void handleWidgetUpdate(Calculation calc) {
          Intent intent = new Intent("android.appwidget.action.APPWIDGET_UPDATE");
          intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");

          intent.putExtra(CALC_FROM_ACTIVITY, Parcels.wrap(calc));
          sendBroadcast(intent);
     }
}
