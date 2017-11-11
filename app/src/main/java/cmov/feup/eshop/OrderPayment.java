package cmov.feup.eshop;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cmov.feup.eshop.model.Order;

public class OrderPayment extends AppCompatActivity{

        ListView listView;
        private static ListViewAdapter adapter;
        final String CURRENCY = "€";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_order_payment);

            listView=(ListView)findViewById(R.id.listView);

            Bundle bundle = getIntent().getExtras();
            ArrayList<Order> dataModels = bundle.getParcelableArrayList("VAR1");
            int previousActivity = bundle.getInt("ACT");
            switch (previousActivity){
                case ActivityConstants.PREVIOUS_ORDERS_ACTIVITY:
                    Button cancel = (Button)findViewById(R.id.button_delete_order);
                    Button pay = (Button)findViewById(R.id.button_edit_order);
                    pay.setVisibility(View.GONE);
                    cancel.setVisibility(View.GONE);
            }


            adapter= new ListViewAdapter(dataModels,getApplicationContext());

            TextView totalPrice = (TextView)findViewById(R.id.orderPriceTotalTxt) ;
            double price = 0;
            for(Order o:dataModels){
                price += o.getQuantity()*o.getProduct().getPrice();
            }
            totalPrice.setText(String.format("%.2f",price) + CURRENCY);



            listView.setAdapter(adapter);

            ListView view_instance = (ListView)findViewById(R.id.listView);
            ViewGroup.LayoutParams params=view_instance.getLayoutParams();
            if(dataModels.size() > 5){
                params.height=700;
            }
            else{
                params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            }

            view_instance.setLayoutParams(params);
        }




        public void  OnCancelButtonClick(View view){
            OrderPayment.this.finish();
        }

        public void OnPayButtonClick(View view){
            //TODO use real token
           String token = "Replace with real token";

            //TODO implement the paying (use data saved during registration)
            //token = realToken;
            //


            Intent intent = new Intent(this, SendPaymentTokenActivity.class);
            intent.putExtra("message", token);
            intent.putExtra("NFCtag", "application/nfc.feup.apm.message.type1");
            startActivity(intent);

        }

    }