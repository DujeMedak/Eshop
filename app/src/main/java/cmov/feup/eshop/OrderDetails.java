package cmov.feup.eshop;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cmov.feup.eshop.model.Order;

public class OrderDetails extends AppCompatActivity {

    EditText quantityEditText;
    final String CURRENCY = "€";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        Bundle bundle = getIntent().getExtras();
        Order order = bundle.getParcelable("VAR2");

        quantityEditText = (EditText)findViewById(R.id.editTxtOrderDetailsQuantity);
        quantityEditText.clearFocus();
        TextView productDescription = (TextView)findViewById(R.id.orderDetailsDescriptionTxt);
        TextView totalPrice = (TextView)findViewById(R.id.totalPrice);
        TextView productPrice = (TextView)findViewById(R.id.orderDetailsPriceTxt);

        if(order != null){
            quantityEditText.setText(String.valueOf(order.getQuantity()));
            setTitle(order.getProduct().getName());
            productDescription.setText(order.getProduct().getProductDescription());
            productPrice.setText( " x " + String.valueOf(order.getProduct().getPrice()) + CURRENCY+ " = ");
            totalPrice.setText(String.format("%.2f", order.getQuantity()*order.getProduct().getPrice()) + CURRENCY);
        }
    }
    public void  OnCancelButtonClick(View view){
        OrderDetails.this.finish();
    }

    public void OnSaveButtonClick(View view){
        //TODO handle wrong input
        Intent resultIntent = new Intent();
        resultIntent.putExtra("newQuantity",quantityEditText.getText().toString() );
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
        /*Intent intent=new Intent(OrderDetails.this,ScanActivity.class);
        intent.putExtra("newQuantity",quantityEditText.getText().toString());
        intent.putExtra("newQuantity",quantityEditText.getText().toString());
        //startActivity(intent);
        OrderDetails.this.finish();*/
    }
}
