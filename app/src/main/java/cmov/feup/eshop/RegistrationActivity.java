package cmov.feup.eshop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cooltechworks.creditcarddesign.CardEditActivity;
import com.cooltechworks.creditcarddesign.CreditCardUtils;
import com.cooltechworks.creditcarddesign.CreditCardView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;

import static cmov.feup.eshop.ActivityConstants.PREFS_NAME;

public class RegistrationActivity extends AppCompatActivity {

    DBHelper dbHelper;

    EditText name, surname, address, fiscalNumber, username, password;
    Date birthday = null;
    String gender = null;

    LinearLayout linearViewCard;
    CreditCardView creditCardView;
    boolean creditCardClicked = false;
    String cardHolderName = null;
    String cardNumber = null;
    String expiry = null;
    String cvv = null;
    boolean registrationSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        dbHelper = new DBHelper(this);

        name = (EditText) findViewById(R.id.nameTxt);
        surname = (EditText) findViewById(R.id.surnameTxt);
        address = (EditText) findViewById(R.id.addressTxt);
        fiscalNumber = (EditText) findViewById(R.id.fiscalNumberTxt);
        username = (EditText) findViewById(R.id.usernameTxt);
        password = (EditText) findViewById(R.id.passwordTxt);


        final EditText editText = password;
        linearViewCard = (LinearLayout) findViewById(R.id.linearViewCreditCard);
        creditCardView = (CreditCardView) findViewById(R.id.card_5);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    editText.clearFocus();
                    linearViewCard.requestFocus();
                }
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }

    public void OnCreditCardClick(View view) {


        if (!creditCardClicked) {
            creditCardClicked = true;
            final int GET_NEW_CARD = 2;

            Intent intent = new Intent(RegistrationActivity.this, CardEditActivity.class);
            startActivityForResult(intent, GET_NEW_CARD);
        } else {
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
        if (resultCode == RESULT_OK) {
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

    public void OnRegisterClick(View view) throws NoSuchAlgorithmException {

        final String name, surname, address, username, password, fiscalNumber;

        name = this.name.getText().toString();
        surname = this.surname.getText().toString();
        address = this.address.getText().toString();
        username = this.username.getText().toString();
        password = this.password.getText().toString();
        fiscalNumber = this.fiscalNumber.getText().toString();

        if (name.isEmpty() || surname.isEmpty() || username.isEmpty() || password.isEmpty()
                || fiscalNumber.isEmpty() || cardHolderName.isEmpty() || cardNumber.isEmpty()
                || cvv.isEmpty() || expiry.isEmpty()) {
            Toast.makeText(this, "Please fill out all required data!", Toast.LENGTH_SHORT).show();
        } else {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(368, new SecureRandom());
            KeyPair keyPair = kpg.genKeyPair();
            byte[] privateKey = keyPair.getPrivate().getEncoded();
            byte[] publicKey = keyPair.getPublic().getEncoded();

            String bodyMessage = "username=" + username + "&password=" + password + "&name="
                    + name + "&surname=" + surname + "&address=" + address + "&fis_number="
                    + fiscalNumber + "&card_holder=" + cardHolderName + "&card_number="
                    + cardNumber + "&card_exp_date=" + expiry + "&card_cvv=" + cvv + "&public_key=" + publicKey.toString();

            Register register = new Register("192.168.137.1", bodyMessage);
            Thread thr = new Thread(register);
            thr.start();
            while (thr.isAlive()) {
            }
            if (registrationSuccess) {
                dbHelper.insert(username, privateKey.toString());
                saveLogin();
                Intent next = new Intent(RegistrationActivity.this, ScanActivity.class);
                next.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                RegistrationActivity.this.startActivity(next);
            } else {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
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

    void saveLogin() {
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

    private class Register implements Runnable {
        String address = null;
        String body = null;

        Register(String baseAddress, String bodyMessage) {
            address = baseAddress;
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
                url = new URL("http://" + address + ":8181/register");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setRequestMethod("POST");

                DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream());
                outputStream.writeBytes(body);
                outputStream.flush();
                outputStream.close();

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == 200) {
                    String response = readStream(urlConnection.getInputStream());
                    if (response.compareTo("success") == 0) {
                        registrationSuccess = true;
                    }
                }
            } catch (Exception e) {
                Log.d(address, "register", e);
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
        }
    }
}
