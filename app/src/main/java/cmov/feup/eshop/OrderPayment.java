package cmov.feup.eshop;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cmov.feup.eshop.model.Order;

public class OrderPayment extends AppCompatActivity {

    private static ListViewAdapter adapter;
    final String CURRENCY = "€";
    ListView listView;
    ArrayList<Order> dataModels;
    DBHelper dbHelper;
    String token = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_payment);

        dbHelper = new DBHelper(this);

        listView = (ListView) findViewById(R.id.listView);

        Bundle bundle = getIntent().getExtras();
        dataModels = bundle.getParcelableArrayList("VAR1");
        int previousActivity = bundle.getInt("ACT");
        switch (previousActivity) {
            case ActivityConstants.PREVIOUS_ORDERS_ACTIVITY:
                Button cancel = (Button) findViewById(R.id.button_delete_order);
                Button pay = (Button) findViewById(R.id.button_edit_order);
                pay.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
        }

        adapter = new ListViewAdapter(dataModels, getApplicationContext());

        TextView totalPrice = (TextView) findViewById(R.id.orderPriceTotalTxt);
        double price = 0;
        for (Order o : dataModels) {
            price += o.getQuantity() * o.getProduct().getPrice();
        }
        totalPrice.setText(String.format("%.2f", price) + CURRENCY);


        listView.setAdapter(adapter);

        ListView view_instance = (ListView) findViewById(R.id.listView);
        ViewGroup.LayoutParams params = view_instance.getLayoutParams();
        if (dataModels.size() > 5) {
            params.height = 700;
        } else {
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        }

        view_instance.setLayoutParams(params);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }

    public void OnCancelButtonClick(View view) {
        OrderPayment.this.finish();
    }

    public void OnPayButtonClick(View view) throws NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, InvalidKeyException, UnsupportedEncodingException {
        String itemList = "[";
        for (Order o : dataModels) {
            itemList += "{\"product\": {\"_id\": \"" + o.getProduct().getRef() + "\", \"name\": \""
                    + o.getProduct().getName() + "\", \"desc\": \"" + o.getProduct().getProductDescription()
                    + "\", \"cost\": \"" + o.getProduct().getPrice() + "\"}, \"quantity\": \"" + o.getQuantity() + "\"},";
        }
        itemList = itemList.substring(0, itemList.length() - 1) + "]";

        String username = dbHelper.getUsername();
        String name = dbHelper.getName();
        String surname = dbHelper.getSurname();
        String privateKeyString = dbHelper.getPrivateKey();

        byte[] privateKeyBytes = Base64.decode(privateKeyString, Base64.DEFAULT);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initSign(privateKey, new SecureRandom());
        byte[] msg = itemList.getBytes();
        signature.update(msg);
        byte[] sig = signature.sign();

        String bodyMessage = "username=" + username + "&name=" + name + "&surname=" + surname
                + "&date=" + new SimpleDateFormat("dd/MM/yyyy@HH:mm").format(new Date()) + "&itemlist=" + itemList + "&signature=" + sig;

        Checkout checkout = new Checkout(bodyMessage);
        Thread thr = new Thread(checkout);
        thr.start();
    }

    private void updateUI(String s) {
        final Context c = this;
        final String s1 = s;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (token != null) {
                    Intent intent = new Intent(c, SendPaymentTokenActivity.class);
                    intent.putExtra("message", s1);
                    intent.putExtra("NFCtag", "application/nfc.feup.apm.message.type1");
                    startActivity(intent);
                } else {
                    Toast.makeText(c, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private class Checkout implements Runnable {
        String body = null;

        Checkout(String bodyMessage) {
            body = bodyMessage;
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
                url = new URL("http://" + EshopServer.address + ":8181/checkout");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setRequestMethod("POST");

                DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream());
                outputStream.writeBytes(body);
                outputStream.flush();
                outputStream.close();

                // get response
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == 200) {
                    String response = readStream(urlConnection.getInputStream());
                    JSONObject jsonObject = new JSONObject(response);
                    token = jsonObject.getString("token");
                    updateUI(token);
                }
            } catch (Exception e) {
                Log.d(EshopServer.address, "checkout", e);
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
        }
    }
}