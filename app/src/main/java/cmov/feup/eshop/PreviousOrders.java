package cmov.feup.eshop;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import cmov.feup.eshop.model.FinishedOrder;
import cmov.feup.eshop.model.Order;
import cmov.feup.eshop.model.Product;

public class PreviousOrders extends AppCompatActivity {

    ListView listView;
    private static PreviousOrdersListViewAdapter adapter;
    final String CURRENCY = "€";

    ArrayList<FinishedOrder> dataModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_orders);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        listView=(ListView)findViewById(R.id.listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                ArrayList<Order> o = new ArrayList<Order>();
                o = (dataModels.get(myItemInt)).getOrders();

                Intent intent=new Intent(getApplicationContext(),OrderPayment.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("VAR1",o);
                bundle.putInt("ACT",ActivityConstants.PREVIOUS_ORDERS_ACTIVITY);
                intent.putExtras(bundle);
                PreviousOrders.this.startActivity(intent);
            }
        });

        //TODO get previous orders from server
        //
        //dataModels = getPreviousOrders(username)  -get username from file saved during registration
        //

        //TODO remove mock data below
        //-----------MOCK DATA------------------
        ArrayList<Order> order1 = new ArrayList<>();
        ArrayList<Order> order2 = new ArrayList<>();
        Order o1 = new Order(new Product("Product 1","Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",1d), 1);
        Order o2 = new Order(new Product("Product 2","Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",1d), 5);
        Order o3 = new Order(new Product("Product 3","Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",1d), 7);
        order1.add(o1);
        order1.add(o2);
        order2.add(o1);
        order2.add(o3);
        dataModels.add(new FinishedOrder("gh45",new Date(System.currentTimeMillis()),order1));
        dataModels.add(new FinishedOrder("second1223",new Date(System.currentTimeMillis()),order2));
        //-----------MOCK DATA------------------



        adapter= new PreviousOrdersListViewAdapter(dataModels,getApplicationContext());

        listView.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();    //Call the back button's method
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}