package com.shiftdev.bull;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

     private static final String KEY_SYMBOL = "symbol";
     private static final String KEY_SHARE_COUNT = "shares";
     private static final String KEY_BUY_PRICE = "buy";
     private static final String KEY_SELL_PRICE = "sell";
     private static final String KEY_COMMISSION = "comm";

     //bind views
     @BindView(R.id.ETsymbol)
     EditText ETSymbol;
     @BindView(R.id.ETnumShares)
     EditText ETShares;
     @BindView(R.id.ETbuyPrice)
     EditText ETBuyPrice;
     @BindView(R.id.ETsellPrice)
     EditText ETSellPrice;
     @BindView(R.id.ETcomm)
     EditText ETComm;

     @BindView(R.id.TVbuyAmount)
     TextView buyTV;
     @BindView(R.id.TVsellAmount)
     TextView sellTV;
     @BindView(R.id.TVrealizedValue)
     TextView realizedTV;

     @BindView(R.id.historyCalcRV)
     RecyclerView recyclerView;
     private FirebaseFirestore db = FirebaseFirestore.getInstance();
     private CollectionReference Ref = db.collection("Calculations");
     private DocumentReference calcRef = Ref.document("My Calculations");

     private CalcAdapter adapter;

     public static double calculateRealizedValue(int numOfShares, double buyPrice, double sellPrice, double commission) {
          //# of shares calculateRealizedValue(intCount, doubleBuy, doubleSell, doubleComm));
          //multiplied by stock price
          //plus trade commission = total buy cost
          //# of shares
          //multiplied by new stock price
          //plus trade commission = total sell cost
          //total buy cost - total sell cost
          double valToBeRounded = getSellAmount(numOfShares, sellPrice, commission) - getBuyAmount(numOfShares, buyPrice, commission);

          return (double) Math.round(valToBeRounded * 100d) / 100d;
     }

     private static double getSellAmount(int numOfShares, double sellPrice, double commission) {
          Timber.i("Tcalculating sell amount, %s %s %s ", numOfShares, sellPrice, commission);
          return (numOfShares * sellPrice) - commission;
     }

     private static double getBuyAmount(int numOfShares, double buyPrice, double commission) {
          Timber.i("Tcalculating buy amount, %s %s %s ", numOfShares, buyPrice, commission);
          return (numOfShares * buyPrice) + commission;
     }

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_main);
          ButterKnife.bind(this);
          if (BuildConfig.DEBUG) Timber.plant(new Timber.DebugTree());

          setUpRecyclerView();
     }

     private void setUpRecyclerView() {
          Query query = Ref.orderBy("symbol", Query.Direction.ASCENDING);


          //assigning the query to the adapter
          FirestoreRecyclerOptions<Calculation> options = new FirestoreRecyclerOptions.Builder<Calculation>()
                  .setQuery(query, Calculation.class).build();

          adapter = new CalcAdapter(options);

          recyclerView.setHasFixedSize(false);
          recyclerView.setLayoutManager(new LinearLayoutManager(this));
          recyclerView.setAdapter(adapter);


          new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                  ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
               @Override
               public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
               }

               @Override
               public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    adapter.deleteItem(viewHolder.getAdapterPosition());
               }
          }).attachToRecyclerView(recyclerView);

          adapter.setOnItemClickListener(new CalcAdapter.onCalcClickListener() {
               @Override
               public void onCalcClick(DocumentSnapshot documentSnapshot, int position) {
                    Calculation calc = documentSnapshot.toObject(Calculation.class);
                    assert calc != null;
                    setEditTextWithDataFromFirebase(calc);
                    Toast.makeText(MainActivity.this, "calc retrieved!", Toast.LENGTH_LONG).show();
               }
          });
     }


     @Override
     protected void onStop() {
          super.onStop();
          adapter.stopListening();
     }

     @Override
     protected void onStart() {
          super.onStart();
          adapter.startListening();
          calcRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
               @Override
               public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                    if (e != null) {
                         Toast.makeText(MainActivity.this, "Error while loading!", Toast.LENGTH_LONG).show();
                         Timber.d(e.toString());
                         return;
                    }
                    assert documentSnapshot != null;
                    if (documentSnapshot.exists()) {
                         Calculation calc = documentSnapshot.toObject(Calculation.class);
                         assert calc != null;
                         setEditTextWithDataFromFirebase(calc);
                         setResultFields(calc.getShares(), calc.getBuy(), calc.getSell(), calc.getComm());

                    } else {
                         realizedTV.setText(R.string.errRealized);
                    }
               }
          });
     }


     public void saveCalcInfo(View v) {
          //get the values from the text fields
          String symbol = ETSymbol.getText().toString();
          CalculationUtils calculationUtils = new CalculationUtils().invoke();
          //parse the necessary text fields  to integers
          int intCount = calculationUtils.getIntCount();
          double doubleBuy = calculationUtils.getDoubleBuy();
          double doubleSell = calculationUtils.getDoubleSell();
          double doubleComm = calculationUtils.getDoubleComm();

          //put values into object to save
          Calculation calculation = new Calculation(symbol, intCount, doubleBuy, doubleSell, doubleComm);

          //set the realized text view with the realized value, buy and sell totals
          setResultFields(intCount, doubleBuy, doubleSell, doubleComm);

          //save values to new Firestore document
          Ref.add(calculation).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
               @Override
               public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(MainActivity.this, "Calculations Saved to Cloud", Toast.LENGTH_SHORT).show();
               }
          }).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Save Failed", Toast.LENGTH_SHORT).show();
                    Timber.d(e.toString());
               }
          });
     }

     private void setResultFields(int intCount, double doubleBuy, double doubleSell, double doubleComm) {
          double realizedVal = calculateRealizedValue(intCount, doubleBuy, doubleSell, doubleComm);

          //check values and adjust backgrounds accordingly
          if (realizedVal > 0) realizedTV.setBackgroundResource(R.color.green);
          else realizedTV.setBackgroundResource(R.color.red);

          realizedTV.setText(String.valueOf(realizedVal));
          buyTV.setText(String.valueOf(getBuyAmount(intCount, doubleBuy, doubleComm)));
          sellTV.setText(String.valueOf(getSellAmount(intCount, doubleSell, doubleComm)));
     }


     public void updateCalculation(View v) {
          String symbol = ETSymbol.getText().toString();
          CalculationUtils calculationUtils = new CalculationUtils().invoke();
          //parse the necessary text fields  to integers
          int intCount = calculationUtils.getIntCount();
          double doubleBuy = calculationUtils.getDoubleBuy();
          double doubleSell = calculationUtils.getDoubleSell();
          double doubleComm = calculationUtils.getDoubleComm();

          calcRef.update(KEY_SYMBOL, symbol);
          calcRef.update(KEY_SHARE_COUNT, intCount);
          calcRef.update(KEY_BUY_PRICE, doubleBuy);
          calcRef.update(KEY_SELL_PRICE, doubleSell);
          calcRef.update(KEY_COMMISSION, doubleComm);
     }


     public void loadPrevCalc(View v) {
          calcRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
               @Override
               public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                         Calculation calc = documentSnapshot.toObject(Calculation.class);
                         assert calc != null;
                         setEditTextWithDataFromFirebase(calc);
                    } else {
                         Toast.makeText(MainActivity.this, "Calc does not exist", Toast.LENGTH_SHORT).show();
                    }
               }
          }).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_LONG).show();
               }
          });
     }

     private void setEditTextWithDataFromFirebase(Calculation calculation) {
          ETSymbol.setText(calculation.getSymbol());
          ETShares.setText(String.valueOf(calculation.getShares()));
          ETBuyPrice.setText(String.valueOf(calculation.getBuy()));
          ETSellPrice.setText(String.valueOf(calculation.getSell()));
          ETComm.setText(String.valueOf(calculation.getComm()));
     }

     private class CalculationUtils {
          private int intCount;
          private double doubleBuy;
          private double doubleSell;
          private double doubleComm;

          int getIntCount() {
               return intCount;
          }

          double getDoubleBuy() {
               return doubleBuy;
          }

          double getDoubleSell() {
               return doubleSell;
          }

          double getDoubleComm() {
               return doubleComm;
          }

          CalculationUtils invoke() {

               intCount = 0;
               String countText = ETShares.getText().toString();
               if (!countText.isEmpty()) {
                    try {
                         intCount = Integer.parseInt(countText);
                         // it means it is int
                    } catch (Exception e1) {
                         // this means it is not int
                         Timber.d("Error of parsing int: %s", e1.getMessage());
                    }
               }


               doubleBuy = 0;
               String buyText = ETBuyPrice.getText().toString();
               if (!buyText.isEmpty()) {
                    try {
                         doubleBuy = Double.parseDouble(buyText);
                         // it means it is double
                    } catch (Exception e1) {
                         // this means it is not double
                         Timber.d("Error of parsing double: %s", e1.getMessage());
                    }
               }


               doubleSell = 0;
               String sellText = ETSellPrice.getText().toString();
               if (!sellText.isEmpty()) {
                    try {
                         doubleSell = Double.parseDouble(sellText);
                         // it means it is double
                    } catch (Exception e1) {
                         // this means it is not double
                         Timber.d("Error of parsing double: %s", e1.getMessage());
                    }
               }


               doubleComm = 0;
               String commText = ETComm.getText().toString();
               if (!commText.isEmpty()) {
                    try {
                         doubleComm = Double.parseDouble(commText);
                         // it means it is double
                    } catch (Exception e1) {
                         // this means it is not double
                         Timber.d("Error of parsing commission double, that's okay, defaulting to 0 anyways: %s", e1.getMessage());
                         doubleComm = 0;
                    }

               }
               return this;
          }
     }
}
