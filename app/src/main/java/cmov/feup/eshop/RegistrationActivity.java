package cmov.feup.eshop;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cooltechworks.creditcarddesign.CardEditActivity;
import com.cooltechworks.creditcarddesign.CreditCardUtils;
import com.cooltechworks.creditcarddesign.CreditCardView;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static cmov.feup.eshop.ActivityConstants.PREFS_NAME;

public class RegistrationActivity extends AppCompatActivity{

    EditText name,surname,address,fiscalNumber,username,password;
    Date birthday = null;
    String gender = null;

    LinearLayout linearViewCard;
    CreditCardView creditCardView;
    boolean creditCardClicked = false;
    String cardHolderName = null;
    String cardNumber = null;
    String expiry = null;
    String cvv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        name = (EditText)findViewById(R.id.nameTxt);
        surname = (EditText)findViewById(R.id.surnameTxt);
        address = (EditText)findViewById(R.id.addressTxt);
        fiscalNumber = (EditText)findViewById(R.id.fiscalNumberTxt);
        username = (EditText)findViewById(R.id.usernameTxt);
        password = (EditText)findViewById(R.id.passwordTxt);


        final EditText editText = password;
        linearViewCard = (LinearLayout)findViewById(R.id.linearViewCreditCard);
        creditCardView = (CreditCardView)findViewById(R.id.card_5);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    editText.clearFocus();
                    linearViewCard.requestFocus();
                }
                return false;
            }
        });
    }



    public void OnCreditCardClick(View view){


        if(!creditCardClicked){
            creditCardClicked = true;
            final int GET_NEW_CARD = 2;

            Intent intent = new Intent(RegistrationActivity.this, CardEditActivity.class);
            startActivityForResult(intent,GET_NEW_CARD);
        }
        else{
            final int EDIT_CARD = 5;
            Intent intent = new Intent(RegistrationActivity.this, CardEditActivity.class);
            intent.putExtra(CreditCardUtils.EXTRA_CARD_HOLDER_NAME, cardHolderName);
            intent.putExtra(CreditCardUtils.EXTRA_CARD_NUMBER, cardNumber);
            intent.putExtra(CreditCardUtils.EXTRA_CARD_EXPIRY, expiry);
            intent.putExtra(CreditCardUtils.EXTRA_CARD_SHOW_CARD_SIDE, CreditCardUtils.CARD_SIDE_FRONT);
            intent.putExtra(CreditCardUtils.EXTRA_VALIDATE_EXPIRY_DATE, true); // pass "false" to discard expiry date validation.


            startActivityForResult(intent, EDIT_CARD);
        }
    }

    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            cardHolderName = data.getStringExtra(CreditCardUtils.EXTRA_CARD_HOLDER_NAME);
            cardNumber = data.getStringExtra(CreditCardUtils.EXTRA_CARD_NUMBER);
            expiry = data.getStringExtra(CreditCardUtils.EXTRA_CARD_EXPIRY);
            cvv = data.getStringExtra(CreditCardUtils.EXTRA_CARD_CVV);

            // Your processing goes here.


            creditCardView.setCVV(cvv);
            creditCardView.setCardHolderName(cardHolderName);
            creditCardView.setCardExpiry(expiry);
            creditCardView.setCardNumber(cardNumber);

        }
    }



    public void OnRegisterClick(View view) {

        final String name,surname,address,username,password;


        name = this.name.getText().toString();
        surname = this.surname.getText().toString();
        address = this.address.getText().toString();
        username = this.username.getText().toString();
        password = this.password.getText().toString();

        // You can also use this member variables (maybe need to check if data is valid before)
        // cardHolderName, cardNumber, expiry, cvv (all of them are Strings)
        //TODO add more conditions if needed
        if(name.isEmpty() || surname.isEmpty() || username.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please fill out all required data!", Toast.LENGTH_SHORT).show();
        }
        else{

            //TODO encrypt data
            //TODO try registration
            //TODO if succesful save all the data needed for payment in some file or something
            //for instance register(name,cardNumber,...) or something like that
            /*if(result == ok){

                saveLogin();
                Intent next = new Intent(RegistrationActivity.this,ScanActivity.class);
                RegistrationActivity.this.startActivity(next);
            }
            else{
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }


             */
        }
        //TODO remove this 2 lines after implementing rest connection
        //save login should only be if the result is OK
        saveLogin();
        Intent next = new Intent(RegistrationActivity.this,ScanActivity.class);
        next.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        RegistrationActivity.this.startActivity(next);

    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }

    void saveLogin(){
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("LoggedIn", true);

        // Commit the edits!
        editor.commit();

    }


    public void OnMaleRadioButtonClick(View view) {
        gender = "M";
    }

    public void OnFemaleRadioButtonClick(View view) {
        gender = "F";
    }



}
