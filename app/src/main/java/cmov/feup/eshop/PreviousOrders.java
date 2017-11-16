package cmov.feup.eshop;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import cmov.feup.eshop.model.FinishedOrder;
import cmov.feup.eshop.model.Order;
import cmov.feup.eshop.model.Product;

public class PreviousOrders extends AppCompatActivity {

    private static PreviousOrdersListViewAdapter adapter;
    final String CURRENCY = "€";
    ListView listView;
    DBHelper dbHelper;
    String username;

    ArrayList<FinishedOrder> dataModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_orders);

        dbHelper = new DBHelper(this);
        username = dbHelper.getUsername();

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


        GetPreviousOrders prevOrders = new GetPreviousOrders("192.168.137.1");
        Thread thr = new Thread(prevOrders);
        thr.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();    //Call the back button's method
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateUI(String response) {
        final Context c = this;
        final String r = response;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONArray jsonArrayOrders = new JSONArray(r);
                    for (int i = 0; i < jsonArrayOrders.length(); i++) {
                        ArrayList<Order> arrayOrder = new ArrayList<>();
                        JSONObject jsonOrder = jsonArrayOrders.getJSONObject(i);
                        String orderId = jsonOrder.getString("_id");
                        String date = jsonOrder.getString("date");
                        JSONArray jsonArrayProducts = jsonOrder.getJSONArray("products");
                        for (int j = 0; j < jsonArrayProducts.length(); j++) {
                            JSONObject jsonProduct = jsonArrayProducts.getJSONObject(j);
                            JSONObject jsonProductDetails = jsonProduct.getJSONObject("product");
                            String ref = jsonProductDetails.getString("_id");
                            String name = jsonProductDetails.getString("name");
                            String desc = jsonProductDetails.getString("desc");
                            double cost = jsonProductDetails.getDouble("cost");
                            int quantity = jsonProduct.getInt("quantity");
                            Product product = new Product(ref, name, desc, cost);
                            Order order = new Order(product, quantity);
                            arrayOrder.add(order);
                        }
                        dataModels.add(new FinishedOrder(orderId, new Date(date), arrayOrder));
                    }
                    adapter = new PreviousOrdersListViewAdapter(dataModels, getApplicationContext());

                    listView.setAdapter(adapter);
                } catch (Exception e) {
                }
            }
        });

    }

    private class GetPreviousOrders implements Runnable {
        String address = null;

        GetPreviousOrders(String baseAddress) {
            address = baseAddress;
        }

        private String readStream(InputStream in) {
            BufferedReader reader = null;
            StringBuffer response = new StringBuffer();
            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            } catch (IOException e) {
                return e.getMessage();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        return e.getMessage();
                    }
                }
            }
            return response.toString();
        }

        @Override
        public void run() {
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL("http://" + address + ":8181/salehist/" + username);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setUseCaches(false);
                urlConnection.setRequestMethod("GET");

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == 200) {
                    String response = readStream(urlConnection.getInputStream());
                    updateUI(response);
                }
            } catch (Exception e) {
                Log.d(address, "previousOrders", e);
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
        }
    }
}