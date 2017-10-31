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

        ArrayList<Order> dataModels;
        ListView listView;
        private static ListViewAdapter adapter;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_order_payment);
            //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            //setSupportActionBar(toolbar);

            listView=(ListView)findViewById(R.id.listView);

            Bundle bundle = getIntent().getExtras();
            ArrayList<Order> dataModels = bundle.getParcelableArrayList("VAR1");

            //dataModels= new ArrayList<>();

            /*
            dataModels.add(new DataModel("Apple Pie", "Android 1.0", "1","September 23, 2008"));
            dataModels.add(new DataModel("Banana Bread", "Android 1.1", "2","February 9, 2009"));
            dataModels.add(new DataModel("Cupcake", "Android 1.5", "3","April 27, 2009"));
            dataModels.add(new DataModel("Donut","Android 1.6","4","September 15, 2009"));
            dataModels.add(new DataModel("Eclair", "Android 2.0", "5","October 26, 2009"));
            dataModels.add(new DataModel("Froyo", "Android 2.2", "8","May 20, 2010"));
            dataModels.add(new DataModel("Gingerbread", "Android 2.3", "9","December 6, 2010"));
            dataModels.add(new DataModel("Honeycomb","Android 3.0","11","February 22, 2011"));
            dataModels.add(new DataModel("Ice Cream Sandwich", "Android 4.0", "14","October 18, 2011"));
            dataModels.add(new DataModel("Jelly Bean", "Android 4.2", "16","July 9, 2012"));
            dataModels.add(new DataModel("Kitkat", "Android 4.4", "19","October 31, 2013"));
            dataModels.add(new DataModel("Lollipop","Android 5.0","21","November 12, 2014"));
            dataModels.add(new DataModel("Marshmallow", "Android 6.0", "23","October 5, 2015"));
            */

            adapter= new ListViewAdapter(dataModels,getApplicationContext());



            listView.setAdapter(adapter);


            //LinearLayout view_instance = (LinearLayout) findViewById(R.id.layout_linear_list);
            ListView view_instance = (ListView)findViewById(R.id.listView);
            ViewGroup.LayoutParams params=view_instance.getLayoutParams();
            if(dataModels.size() > 5){
                params.height=700;
            }
            else{
                //params.height= dataModels.size()*100;
                params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            }

            view_instance.setLayoutParams(params);


            /*
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Order dataModel= dataModels.get(position);

                    Snackbar.make(view, dataModel.getName()+"\n"+dataModel.getType()+" API: "+dataModel.getVersion_number(), Snackbar.LENGTH_LONG)
                            .setAction("No action", null).show();
                }
            });*/


        }




        public void  OnCancelButtonClick(View view){
            OrderPayment.this.finish();
        }

        public void OnPayButtonClick(View view){
            Toast.makeText(this, "implement the payment!", Toast.LENGTH_SHORT).show();
        }


        /*
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {
                return true;
            }

            return super.onOptionsItemSelected(item);
        }*/

    }