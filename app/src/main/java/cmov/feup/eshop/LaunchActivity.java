package cmov.feup.eshop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import static cmov.feup.eshop.ActivityConstants.PREFS_LOGGED;
import static cmov.feup.eshop.ActivityConstants.PREFS_NAME;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean loggedIn = settings.getBoolean(PREFS_LOGGED, false);
        Intent intent;

        if (loggedIn) {
            intent = new Intent(this,ScanActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        else {
            intent = new Intent(this,RegistrationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        startActivity(intent);
        finish();
    }
}
