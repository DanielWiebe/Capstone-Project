package com.shiftdev.bull;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

     private static final String KEY_SYMBOL = "symbol";
     private static final String KEY_SHARE_COUNT = "shareCount";
     private static final String KEY_BUY_PRICE = "buyPrice";
     private static final String KEY_SELL_PRICE = "sellPrice";
     private static final String KEY_COMMISSION = "sellCommission";
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
     private FirebaseFirestore db = FirebaseFirestore.getInstance();
     private DocumentReference calcRef = db.document("Calculations/My Calculations");

     public static double calculateRealizedValue(int numOfShares, double buyPrice, double sellPrice, double commission) {
          //# of shares calculateRealizedValue(intCount, doubleBuy, doubleSell, doubleComm));
          //multiplied by stock price
          //plus trade commission = total buy cost
          //# of shares
          //multiplied by new stock price
          //plus trade commission = total sell cost
          //total buy cost - total sell cost
          double roundedValue = getSellAmount(numOfShares, sellPrice, commission) - getBuyAmount(numOfShares, buyPrice, commission);

          return (double) Math.round(roundedValue * 100d) / 100d;
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

     }

     public void saveCalcInfo(View v) {
          //get the values from the text fields
          String symbol = ETSymbol.getText().toString();

          //parse the necessary text fields  to integers
          int intCount = 0;
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


          double doubleBuy = 0;
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


          double doubleSell = 0;
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


          double doubleComm = 0;
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

          Timber.i("Tcalculate %s %s %s %s", countText, buyText, sellText, commText);


          Timber.i("Tcalculate value %s", calculateRealizedValue(intCount, doubleBuy, doubleSell, doubleComm));


          //put values into map to save
          Map<String, Object> calc = getStringMapOfValues(symbol, countText, buyText, sellText, commText);


          //set the realized text view with the realized value
          realizedTV.setText(String.valueOf(calculateRealizedValue(intCount, doubleBuy, doubleSell, doubleComm)));

          //set the buyTV with buy totals
          buyTV.setText(String.valueOf(getBuyAmount(intCount, doubleBuy, doubleComm)));

          //set the sellTV with sell totals
          sellTV.setText(String.valueOf(getSellAmount(intCount, doubleSell, doubleComm)));


          //save values to Firestore
          calcRef.set(calc)
                  .addOnSuccessListener(new OnSuccessListener<Void>() {
                       @Override
                       public void onSuccess(Void aVoid) {
                            Toast.makeText(MainActivity.this, "Calculations Saved to Cloud", Toast.LENGTH_SHORT).show();
                       }
                  })
                  .addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Save Failed", Toast.LENGTH_SHORT).show();
                            Timber.d(e.toString());
                       }
                  });
     }

     @NotNull
     private Map<String, Object> getStringMapOfValues(String symbol, String count, String buyP, String sellP, String comm) {
          Map<String, Object> calc = new HashMap<>();
          calc.put(KEY_SYMBOL, symbol);
          calc.put(KEY_SHARE_COUNT, count);
          calc.put(KEY_BUY_PRICE, buyP);
          calc.put(KEY_SELL_PRICE, sellP);
          calc.put(KEY_COMMISSION, comm);
          return calc;
     }

     public void loadPrevCalc(View v) {
          calcRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
               @Override
               public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                         setEditTextWithDataFromFirebase(documentSnapshot);
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

     private void setEditTextWithDataFromFirebase(DocumentSnapshot documentSnapshot) {
          ETSymbol.setText(documentSnapshot.getString(KEY_SYMBOL));
          ETShares.setText(documentSnapshot.getString(KEY_SHARE_COUNT));
          ETBuyPrice.setText(documentSnapshot.getString(KEY_BUY_PRICE));
          ETSellPrice.setText(documentSnapshot.getString(KEY_SELL_PRICE));
          ETComm.setText(documentSnapshot.getString(KEY_COMMISSION));
     }
}
