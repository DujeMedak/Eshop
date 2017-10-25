package cmov.feup.eshop;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

public class ScanActivity extends AppCompatActivity {

    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    TextView message;
    Animation show_fab_3,show_fab_2;
    Animation hide_fab_3,hide_fab_2;
    boolean menuOn = false;

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
                String contents = data.getStringExtra("SCAN_RESULT");
                String format = data.getStringExtra("SCAN_RESULT_FORMAT");

                //message.setText("Format: " + format + "\nMessage: " + contents);
                new LovelyTextInputDialog(this)
                        .setTopColorRes(R.color.colorPrimary)
                        .setTitle("Format:" + format)
                        .setMessage("Code type:" + contents)
                        .setIcon(R.drawable.logo_wo)
                        .setConfirmButton("Add to basket", new LovelyTextInputDialog.OnTextInputConfirmListener() {
                            @Override
                            public void onTextInputConfirmed(String text) {
                                Toast.makeText(ScanActivity.this, "Add action", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();

            }
        }


    }
}
