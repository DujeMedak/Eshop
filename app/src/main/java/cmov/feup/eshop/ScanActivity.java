package cmov.feup.eshop;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

import java.util.ArrayList;
import java.util.List;

import cmov.feup.eshop.model.Order;
import cmov.feup.eshop.model.Product;

public class ScanActivity extends AppCompatActivity {

    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    TextView message;
    Animation show_fab_3,show_fab_2;
    Animation hide_fab_3,hide_fab_2;
    boolean menuOn = false;



    List<Order> orderList = new ArrayList<>();
    RecyclerView mRecyclerView;
    OrderAdapter orderAdapter;


    LinearLayout fab2;
    FloatingActionButton fab3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        FloatingActionButton barButton,QRButton;



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

        fab2 = (LinearLayout)findViewById(R.id.fab_2);
        fab3 = (FloatingActionButton)findViewById(R.id.fab_3);

        QRButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ScanActivity.this, R.color.colorPrimary)));
        barButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ScanActivity.this, R.color.upvoteBtnColor)));

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab2.getLayoutParams();
        layoutParams.topMargin += (int) (fab2.getHeight() * 1.5);


        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) fab3.getLayoutParams();
        layoutParams3.topMargin += (int) (fab3.getHeight() * 1.25);

        setupOrderRV();
    }

    public void setupOrderRV(){
        getOrders();

        mRecyclerView = (RecyclerView)findViewById(R.id.rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter();
        mRecyclerView.setAdapter(orderAdapter);
        //mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new OnItemClickListener()));

    }

    public void displayFab2(){
        //QR code
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab2.getLayoutParams();
        layoutParams.topMargin = 0;
        layoutParams.bottomMargin += (int) (fab2.getHeight() * 1.5);
        fab2.setLayoutParams(layoutParams);

        fab2.startAnimation(show_fab_2);
        fab2.setClickable(true);
    }

    public void hideFab2(){
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab2.getLayoutParams();
        layoutParams.bottomMargin -= (int) (fab2.getHeight() * 1.5);
        layoutParams.topMargin += (int) (fab2.getHeight() * 1.5);
        fab2.setLayoutParams(layoutParams);

        fab2.startAnimation(hide_fab_2);
        fab2.setClickable(false);

    }

    public void displayFab3(){
        //Bar code
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab3.getLayoutParams();

        layoutParams.topMargin = 0;
        layoutParams.bottomMargin += (int) (fab3.getHeight() * 1.25);
        fab3.setLayoutParams(layoutParams);

        fab3.startAnimation(show_fab_3);
        fab3.setClickable(true);

    }

    public void hideFab3(){

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab3.getLayoutParams();
        layoutParams.bottomMargin -= (int) (fab3.getHeight() * 1.25);
        layoutParams.topMargin += (int) (fab3.getHeight() * 1.25);
        fab3.setLayoutParams(layoutParams);

        fab3.startAnimation(hide_fab_3);
        fab3.setClickable(false);

    }


    public void displayFABMenu(){
        displayFab3();
        displayFab2();
    }

    public void hideFABMenu(){
        hideFab3();
        hideFab2();
    }


    public void Onfab1Click(View view){
        if(!hide_fab_2.hasStarted() && !show_fab_2.hasStarted() || hide_fab_2.hasEnded() || show_fab_2.hasEnded() ){
            if(menuOn == false){
                displayFABMenu();
                menuOn = !menuOn;
            }
            else{
                hideFABMenu();
                menuOn = !menuOn;
            }
        }

    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putCharSequence("Message", message.getText());
    }

    public void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        message.setText(bundle.getCharSequence("Message"));
    }

    public void scan(boolean qrcode) {
        try {
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", qrcode ? "QR_CODE_MODE" : "PRODUCT_MODE");
            startActivityForResult(intent, 0);
        }
        catch (ActivityNotFoundException anfe) {
            showDialog(this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
    }

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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                final String contents = data.getStringExtra("SCAN_RESULT");
                String format = data.getStringExtra("SCAN_RESULT_FORMAT");

                //message.setText("Format: " + format + "\nMessage: " + contents);


                new LovelyTextInputDialog(this).setNegativeButton("Cancel",null)
                        .setTopColorRes(R.color.colorPrimary)
                        .setTitle("Format:" + format + " Code type:" + contents)
                        .setMessage("Enter quantity")
                        .setIcon(R.drawable.logo_wo)
                        .setConfirmButton("Add to basket", new LovelyTextInputDialog.OnTextInputConfirmListener() {
                            @Override
                            public void onTextInputConfirmed(String text) {
                                int quantity = 0;
                                if (tryParseInt(text)) {
                                    quantity=Integer.parseInt(text);  // We now know that it's safe to parse
                                }
                                //TODO change hardcoded decription to one obtained from server
                                Product p = new Product("Product:"+contents);
                                addOrder(p,quantity);

                            }
                        })
                        .show();

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

    //TODO change offer to product write new function for rest connection

    public void getOrders(){
        /*
            RestConnector.getWishById(this, wish.getIdWish(), 0, 5, new VolleyCallback<Wish>() {
                @Override
                public void onSuccessResponse(Wish result) {
                    if(result != null){
                        if(!result.getOfferList().isEmpty()){
                            orderList.addAll(result.getOfferList());
                            orderAdapter.notifyDataSetChanged();
                            orderAdapter.setLoaded();
                        }
                        else{
                            //Toast.makeText(WishDetailsActivity.this, "No offers", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        //Toast.makeText(WishDetailsActivity.this, "No offers", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    */

        //TODO change later


        //orderList.addAll(orderList);
        //orderAdapter.notifyDataSetChanged();
        //orderAdapter.setLoaded();

    }


    public void addOrder(Product newProduct, int quantity){

        /*
        //TODO UCINI STA OCES S OFFEROM
        if(SessionManager.getCurrentUserProfile() != null){
            if(SessionManager.getCurrentUserProfile().getProfilePicture() != null){
                Offer newOffer = new Offer(wish.getIdWish(),SessionManager.getCurrentUserProfile().getIdUser(),str);
                RestConnector.createOffer(this, newOffer, new VolleyCallback<Offer>() {
                    @Override
                    public void onSuccessResponse(Offer result) {
                        if(result != null){
                            mOffers.clear();
                            getOrders();
                        }
                        else {
                            Toast.makeText(WishDetailsActivity.this, "Error while adding new offer", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
        */
        Order newOrder = new Order(newProduct,quantity);
        orderList.add(newOrder);
        orderAdapter.notifyDataSetChanged();
        orderAdapter.setLoaded();

        LinearLayout l = (LinearLayout)findViewById(R.id.noOrdersTxt);
        l.setVisibility(View.GONE);

    }


//ZA RECYCLER VIEW
class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView orderQuantity;
    public TextView orderDescription;
    public ExpandableLinearLayout expandableLayout;
    public boolean opened = false;

    public OrderViewHolder(View itemView) {
        super(itemView);
        orderDescription = (TextView) itemView.findViewById(R.id.orderTxt);
        orderQuantity = (TextView) itemView.findViewById(R.id.quantityTxt);
        expandableLayout = (ExpandableLinearLayout) itemView.findViewById(R.id.expandableLayout);
        itemView.setOnClickListener(this);
    }
    @Override
    public void onClick(View v){
        if(!opened){
            opened=true;
            orderDescription.setMaxLines(10);
        }else{
            opened=false;
            orderDescription.setMaxLines(4);
        }
        expandableLayout.toggle();
    }

}

class LoadingViewHolder extends RecyclerView.ViewHolder {
    public ProgressBar progressBar;


    public LoadingViewHolder(View itemView) {
        super(itemView);
        //TODO add ProgressBar element to xml
        progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
    }
}


class OrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private OnLoadMoreListener mOnLoadMoreListener;

    private boolean isLoading;
    //TODO adjust after
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    private int lastPosition = -1;

    public OrderAdapter() {

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (mOnLoadMoreListener != null) {
                        mOnLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    /*
    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }
    */

    @Override
    public int getItemViewType(int position) {
        return orderList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(ScanActivity.this).inflate(R.layout.layout_product_item, parent, false);
            return new OrderViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(ScanActivity.this).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Log.d("------>",holder.toString());
        if (holder instanceof OrderViewHolder) {
            Log.d("______>------------>",holder.toString());
            Order order = orderList.get(position);
            final OrderViewHolder orderViewHolder = (OrderViewHolder) holder;

            Log.d("______>------->desc:::",order.getProduct().getProductDescription());
            //Log.d("OFFER-",offer.getOfferDesription());
            //TODO add quantity in xml and bind it here with real data
            orderViewHolder.orderQuantity.setText(String.valueOf(order.getQuantity()));
            orderViewHolder.orderDescription.setText(order.getProduct().getProductDescription());
            orderViewHolder.setIsRecyclable(false);
            orderViewHolder.expandableLayout.setInRecyclerView(true);

        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
        // Ovo je za animiranje ulaska
        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return orderList == null ? 0 : orderList.size();
    }

    public void setLoaded() {
        isLoading = false;
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // animiraj
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(ScanActivity.this, R.anim.fade_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

}



}
