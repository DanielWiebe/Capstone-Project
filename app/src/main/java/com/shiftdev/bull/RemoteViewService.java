package com.shiftdev.bull;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import static com.shiftdev.bull.BullAppWidgetProvider.calculation;

public class RemoteViewService extends RemoteViewsService {

     Calculation calc;

     @Override
     public RemoteViewsFactory onGetViewFactory(Intent intent) {
          return new BullWidgetItemFactory(this.getApplicationContext(), intent);

     }

     class BullWidgetItemFactory implements RemoteViewsFactory {
          private Context context;
          private int appWidgetId;


          BullWidgetItemFactory(Context context, Intent intent) {
               this.context = context;
               appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

          }

          @Override
          public void onCreate() {
          }

          @Override
          public void onDataSetChanged() {
               calc = calculation;
          }

          @Override
          public void onDestroy() {

          }

          @Override
          public int getCount() {
               return 0;
          }

          @Override
          public RemoteViews getViewAt(int i) {
               RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
               views.setTextViewText(R.id.symbol, calc.getSymbol());
               views.setTextViewText(R.id.shares, String.valueOf(calc.getShares()));
               views.setTextViewText(R.id.buy, String.valueOf(calc.getBuy()));
               views.setTextViewText(R.id.sell, String.valueOf(calc.getSell()));
               views.setTextViewText(R.id.comm, String.valueOf(calc.getComm()));
               Intent populateIntent = new Intent();
               views.setOnClickFillInIntent(R.id.ll_parent_for_widget, populateIntent);
               return views;
          }

          @Override
          public RemoteViews getLoadingView() {
               return null;
          }

          @Override
          public int getViewTypeCount() {
               return 1;
          }

          @Override
          public long getItemId(int i) {
               return 0;
          }

          @Override
          public boolean hasStableIds() {
               return false;
          }
     }

}
