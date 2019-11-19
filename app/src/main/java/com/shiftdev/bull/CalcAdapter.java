package com.shiftdev.bull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CalcAdapter extends FirestoreRecyclerAdapter<Calculation, CalcAdapter.CalcHolder> {
     private onCalcClickListener listener;

     CalcAdapter(@NonNull FirestoreRecyclerOptions<Calculation> options) {
          super(options);
     }

     @Override
     protected void onBindViewHolder(@NonNull CalcHolder holder, int position, @NonNull Calculation model) {
          holder.symbolTV.setText(model.getSymbol());
          holder.sharesTV.setText(String.valueOf(model.getShares()));
          holder.buyTV.setText(String.valueOf(model.getBuy()));
          holder.sellTV.setText(String.valueOf(model.getSell()));
          holder.commTV.setText(String.valueOf(model.getComm()));
     }

     @NonNull
     @Override
     public CalcHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
          View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.calc_layout_item,
                  parent, false);
          return new CalcHolder(v);
     }


     void deleteItem(int position) {
          getSnapshots().getSnapshot(position).getReference().delete();
     }

     void setOnItemClickListener(onCalcClickListener listener) {
          this.listener = listener;
     }

     public interface onCalcClickListener {
          void onCalcClick(DocumentSnapshot documentSnapshot, int position);
     }

     class CalcHolder extends RecyclerView.ViewHolder {
          @BindView(R.id.symbol)
          TextView symbolTV;
          @BindView(R.id.shares)
          TextView sharesTV;
          @BindView(R.id.buy)
          TextView buyTV;
          @BindView(R.id.sell)
          TextView sellTV;
          @BindView(R.id.comm)
          TextView commTV;

          @BindView(R.id.recallCalcBT)
          Button recall;


          CalcHolder(View itemView) {

               super(itemView);
               ButterKnife.bind(this, itemView);
               recall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                         int pos = getAdapterPosition();
                         if (pos != RecyclerView.NO_POSITION && listener != null) {
                              listener.onCalcClick(getSnapshots().getSnapshot(pos), pos);
                         }
                    }
               });
          }
     }
}

