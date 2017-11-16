package cmov.feup.eshop;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import cmov.feup.eshop.model.Order;
import cmov.feup.eshop.model.Product;

import static cmov.feup.eshop.ActivityConstants.PREFS_NAME;

public class ScanActivity extends AppCompatActivity {

    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    TextView message;
    Animation show_fab_3, show_fab_2;
    Animation hide_fab_3, hide_fab_2;
    boolean menuOn = false;
    int OrderEditing = 0;
    Product product = null;
    ArrayList<Order> orderList = new ArrayList<>();
    RecyclerView mRecyclerView;
    OrderAdapter orderAdapter;
    LinearLayout fab2;
    FloatingActionButton fab3;

    private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                act.startActivity(intent);
            }
        });
        downloadDialog.setNegativeButton(buttonNo, null);
        return downloadDialog.show();
    }

    public int getOrderEditing() {
        return OrderEditing;
    }

    public void setOrderEditing(int orderEditing) {
        OrderEditing = orderEditing;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        FloatingActionButton barButton, QRButton;


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        message = (TextView) findViewById(R.id.message);
        QRButton = (FloatingActionButton) findViewById(R.id.fab2Button);
        barButton = (FloatingActionButton) findViewById(R.id.fab_3);
        QRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan(true);
            }
        });
        barButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan(false);
            }
        });

        show_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab_2_show);
        hide_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab_2_hide);

        show_fab_3 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab_3_show);
        hide_fab_3 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab_3_hide);

        fab2 = (LinearLayout) findViewById(R.id.fab_2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab_3);

        QRButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ScanActivity.this, R.color.colorPrimary)));
        barButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ScanActivity.this, R.color.actionButtonColor)));

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab2.getLayoutParams();
        layoutParams.topMargin += (int) (fab2.getHeight() * 1.5);


        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) fab3.getLayoutParams();
        layoutParams3.topMargin += (int) (fab3.getHeight() * 1.25);

        setupOrderRV();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //In case of adding more options
        switch (item.getItemId()) {
            case R.id.previousOrders:
                Intent intent = new Intent(this, PreviousOrders.class);
                this.startActivity(intent);
                return true;
            case R.id.signOut:
                Intent intent2 = new Intent(this, RegistrationActivity.class);
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("LoggedIn", false);
                // Commit the edits!
                editor.commit();
                this.startActivity(intent2);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("orderNumber", getOrderEditing());
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        setOrderEditing(savedInstanceState.getInt("orderNumber"));
    }

    public void setupOrderRV() {

        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter();
        mRecyclerView.setAdapter(orderAdapter);

    }

    public void displayFab2() {
        //QR code
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab2.getLayoutParams();
        layoutParams.topMargin = 0;
        layoutParams.bottomMargin += (int) (fab2.getHeight() * 1.5);
        fab2.setLayoutParams(layoutParams);

        fab2.startAnimation(show_fab_2);
        fab2.setClickable(true);
    }

    public void hideFab2() {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab2.getLayoutParams();
        layoutParams.bottomMargin -= (int) (fab2.getHeight() * 1.5);
        layoutParams.topMargin += (int) (fab2.getHeight() * 1.5);
        fab2.setLayoutParams(layoutParams);

        fab2.startAnimation(hide_fab_2);
        fab2.setClickable(false);

    }

    public void displayFab3() {
        //Bar code
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab3.getLayoutParams();

        layoutParams.topMargin = 0;
        layoutParams.bottomMargin += (int) (fab3.getHeight() * 1.25);
        fab3.setLayoutParams(layoutParams);

        fab3.startAnimation(show_fab_3);
        fab3.setClickable(true);

    }

    public void hideFab3() {

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab3.getLayoutParams();
        layoutParams.bottomMargin -= (int) (fab3.getHeight() * 1.25);
        layoutParams.topMargin += (int) (fab3.getHeight() * 1.25);
        fab3.setLayoutParams(layoutParams);

        fab3.startAnimation(hide_fab_3);
        fab3.setClickable(false);

    }

    public void displayFABMenu() {
        displayFab3();
        displayFab2();
    }

    public void hideFABMenu() {
        hideFab3();
        hideFab2();
    }

    public void Onfab1Click(View view) {
        if (!hide_fab_2.hasStarted() && !show_fab_2.hasStarted() || hide_fab_2.hasEnded() || show_fab_2.hasEnded()) {
            if (menuOn == false) {
                displayFABMenu();
                menuOn = !menuOn;
            } else {
                hideFABMenu();
                menuOn = !menuOn;
            }
        }

    }

    public void scan(boolean qrcode) {
        try {
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", qrcode ? "QR_CODE_MODE" : "PRODUCT_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            showDialog(this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                final String contents = data.getStringExtra("SCAN_RESULT");
                String format = data.getStringExtra("SCAN_RESULT_FORMAT");
                GetProduct getProduct = new GetProduct("192.168.137.1", contents);
                Thread thr = new Thread(getProduct);
                thr.start();
            }
        }
        //EDITING ORDER QUANTITY
        else if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                String newQuantity = data.getStringExtra("newQuantity");
                //getOrderEditing is saved before calling orderDetails
                if (tryParseInt(newQuantity)) {
                    orderList.get(getOrderEditing()).setQuantity(Integer.parseInt(newQuantity));
                    orderAdapter.notifyDataSetChanged();
                }

            }
        }


    }

    boolean tryParseInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    //-------------------------------product adapter things ---------------------------------


    public void addOrder(Product newProduct, int quantity) {

        Order newOrder = new Order(newProduct, quantity);
        orderList.add(newOrder);
        orderAdapter.notifyDataSetChanged();

        LinearLayout l = (LinearLayout) findViewById(R.id.noOrdersTxt);
        l.setVisibility(View.GONE);
    }

    public void removeOrderFromList(int index) {
        if (orderList.size() > index) {
            orderList.remove(index);
            orderAdapter.notifyDataSetChanged();
            if (orderList.size() == 0) {
                LinearLayout l = (LinearLayout) findViewById(R.id.noOrdersTxt);
                l.setVisibility(View.VISIBLE);
            }
        }
    }

    private void updateUI(String s) {
        final Context c = this;
        final String s1 = s;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new LovelyTextInputDialog(c).setNegativeButton("Cancel", null)
                        .setTopColorRes(R.color.colorPrimary)
                        .setTitle(product.getName() + " - " + s1)
                        .setMessage("Enter quantity")
                        .setIcon(R.drawable.logo2_256px)
                        .setConfirmButton("Add to basket", new LovelyTextInputDialog.OnTextInputConfirmListener() {
                            @Override
                            public void onTextInputConfirmed(String text) {
                                int quantity = 1;
                                if (tryParseInt(text)) {
                                    quantity = Integer.parseInt(text);
                                }

                                addOrder(product, quantity);

                            }
                        })
                        .show();
            }
        });

    }

    //ZA RECYCLER VIEW
    class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView orderTitle;
        public TextView orderDescription;
        public TextView orderTotalPrice;
        public TextView orderPrice;
        public ExpandableLinearLayout expandableLayout;
        public boolean opened = false;
        Button removeOrder, editOrder;

        public OrderViewHolder(View itemView) {
            super(itemView);
            orderDescription = (TextView) itemView.findViewById(R.id.orderTxt);
            orderTitle = (TextView) itemView.findViewById(R.id.orderTitleTxt);
            orderPrice = (TextView) itemView.findViewById(R.id.orderPriceTxt);
            orderTotalPrice = (TextView) itemView.findViewById(R.id.orderPriceTotalTxt);

            expandableLayout = (ExpandableLinearLayout) itemView.findViewById(R.id.expandableLayout);
            itemView.setOnClickListener(this);

            removeOrder = (Button) itemView.findViewById(R.id.button_delete_order);
            editOrder = (Button) itemView.findViewById(R.id.button_edit_order);

            removeOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (orderList.size() > getAdapterPosition()) {
                        removeOrderFromList(getAdapterPosition());
                    } else {
                        Toast.makeText(ScanActivity.this, "Could not remove the order", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            editOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (orderList.size() > getAdapterPosition()) {
                        setOrderEditing(getAdapterPosition());
                        Order o = orderList.get(getAdapterPosition());

                        Intent intent = new Intent(getApplicationContext(), OrderDetails.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("VAR2", o);
                        intent.putExtras(bundle);
                        ScanActivity.this.startActivityForResult(intent, 1);
                    } else {
                        Toast.makeText(ScanActivity.this, "Could load order details", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

        @Override
        public void onClick(View v) {
            if (!opened) {
                opened = true;
                orderDescription.setMaxLines(10);
            } else {
                opened = false;
                orderDescription.setMaxLines(2);
            }
            expandableLayout.toggle();
        }

    }

    class LoadingViewHolder extends RecyclerView.ViewHolder {
        public Button payOrdersButton;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            payOrdersButton = (Button) itemView.findViewById(R.id.button_pay_order);
        }
    }

    class OrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        final String CURRENCY = "€";

        private int lastPosition = -1;

        @Override
        public int getItemViewType(int position) {
            return (position == orderList.size()) ? R.layout.layout_loading_item : R.layout.layout_product_item;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == R.layout.layout_product_item) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_product_item, parent, false);
                return new OrderViewHolder(view);
            } else if (viewType == R.layout.layout_loading_item) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_item, parent, false);
                return new LoadingViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            if (position == orderList.size()) {
                ((LoadingViewHolder) holder).payOrdersButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                  /*  Toast.makeText(ScanActivity.this, "Button Clicked", Toast.LENGTH_SHORT).show();
                    Intent next = new Intent(ScanActivity.this,OrderPayment.class);
                    ScanActivity.this.startActivity(next);*/
                        Intent intent = new Intent(getApplicationContext(), OrderPayment.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("VAR1", orderList);
                        bundle.putInt("ACT", ActivityConstants.SCAN_ACTIVITY);
                        intent.putExtras(bundle);
                        ScanActivity.this.startActivity(intent);
                    }
                });
            } else {
                Order order = orderList.get(position);
                final OrderViewHolder orderViewHolder = (OrderViewHolder) holder;

                orderViewHolder.orderTitle.setText(String.valueOf(order.getQuantity()) + " x " + order.getProduct().getName());
                orderViewHolder.orderDescription.setText(order.getProduct().getProductDescription());
                orderViewHolder.orderPrice.setText(String.valueOf(order.getQuantity()) + " x " + String.format("%.2f", order.getProduct().getPrice()) + CURRENCY + " = ");
                orderViewHolder.orderTotalPrice.setText(String.format("%.2f", order.getProduct().getPrice() * order.getQuantity()) + CURRENCY);

                orderViewHolder.setIsRecyclable(false);
                orderViewHolder.expandableLayout.setInRecyclerView(true);
            }
            // Ovo je za animiranje ulaska
            setAnimation(holder.itemView, position);
        }

        @Override
        public int getItemCount() {

            if (orderList.isEmpty()) return orderList.size();
            return orderList.size() + 1;
        }

        private void setAnimation(View viewToAnimate, int position) {
            // animiraj
            if (position > lastPosition) {
                Animation animation = AnimationUtils.loadAnimation(ScanActivity.this, R.anim.fade_in);
                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }

    }

    private class GetProduct implements Runnable {
        String address = null;
        String ref = null;

        GetProduct(String baseAddress, String reference) {
            address = baseAddress;
            ref = reference;
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
                url = new URL("http://" + address + ":8181/product/" + ref);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setUseCaches(false);
                urlConnection.setRequestMethod("GET");

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == 200) {
                    String response = readStream(urlConnection.getInputStream());
                    JSONObject jsonObject = new JSONObject(response);
                    product = new Product(jsonObject.getString("_id"),
                            jsonObject.getString("name"),
                            jsonObject.getString("desc"),
                            jsonObject.getDouble("cost"));
                    updateUI(ref);
                }
            } catch (Exception e) {
                Log.d(address, "product", e);
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
        }
    }
}
