package ai.bridgee.androidexampleapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.analytics.FirebaseAnalytics;
import ai.bridgee.android.sdk.BridgeeSDK;
import ai.bridgee.android.sdk.MatchBundle;
import ai.bridgee.android.sdk.AnalyticsProvider;
import ai.bridgee.android.sdk.ResponseCallback;
import ai.bridgee.android.sdk.MatchResponse;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText nameEditText;
    private TextInputEditText emailEditText;
    private TextInputEditText phoneEditText;
    private Button sendButton;
    private FirebaseAnalytics mFirebaseAnalytics;
    private BridgeeSDK bridgeeSDK;
    private AnalyticsProvider analyticsProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Analytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        
        // Initialize Analytics Provider and Bridgee SDK
        initializeAnalyticsProvider();
        initializeBridgeeSDK();
        
        initializeViews();
        setupClickListener();
    }

    private void initializeViews() {
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        sendButton = findViewById(R.id.sendButton);
    }

    private void setupClickListener() {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send Firebase Analytics custom event
                sendCustomFirstOpenEvent();
                // Call Bridgee SDK firstOpen
                callBridgeeFirstOpen();
            }
        });
    }

    private void sendCustomFirstOpenEvent() {
        Bundle bundle = new Bundle();
        bundle.putString("button_name", "send_first_open");
        bundle.putString("screen_name", "main_form");
        mFirebaseAnalytics.logEvent("custom_first_open", bundle);
    }

    private void initializeAnalyticsProvider() {
        analyticsProvider = new AnalyticsProvider() {
            @Override
            public void logEvent(String name, Bundle params) {
                // Delegate to Firebase Analytics
                mFirebaseAnalytics.logEvent(name, params);
            }

            @Override
            public void setUserProperty(String name, String value) {
                // Delegate to Firebase Analytics
                mFirebaseAnalytics.setUserProperty(name, value);
            }
        };
    }

    private void initializeBridgeeSDK() {
        // Initialize with required parameters
        // Note: Replace with your actual tenant credentials
        String tenantId = "tenant-id"; // Tenant ID provided by Bridgee
        String tenantKey = "tenant-key"; // Tenant Key provided by Bridgee
        Boolean dryRun = false; // Set to false in production
        
        bridgeeSDK = BridgeeSDK.getInstance(this, analyticsProvider, tenantId, tenantKey, dryRun);
    }

    private void callBridgeeFirstOpen() {
        // Create MatchBundle with form data using proper methods
        MatchBundle matchBundle = new MatchBundle()
                .withName(nameEditText.getText().toString())
                .withEmail(emailEditText.getText().toString())
                .withPhone(phoneEditText.getText().toString());

        // Call firstOpen with callback to capture response
        bridgeeSDK.firstOpen(matchBundle, new ResponseCallback<MatchResponse>() {
            @Override
            public void ok(MatchResponse response) {
                runOnUiThread(() -> showResponseDialog(response));
            }

            @Override
            public void error(Exception e) {
                runOnUiThread(() -> showErrorDialog(e.getMessage()));
            }
        });
    }

    private void showResponseDialog(MatchResponse response) {
        StringBuilder message = new StringBuilder();
        message.append("✅ Bridgee SDK Response:\n\n");
        
        message.append("📊 UTM Parameters:\n");
        message.append("• UTM Source: ").append(response.getUtmSource() != null ? response.getUtmSource() : "null").append("\n");
        message.append("• UTM Medium: ").append(response.getUtmMedium() != null ? response.getUtmMedium() : "null").append("\n");
        message.append("• UTM Campaign: ").append(response.getUtmCampaign() != null ? response.getUtmCampaign() : "null");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("🎯 SDK Success")
                .setMessage(message.toString())
                .setPositiveButton(R.string.dialog_ok, null)
                .show();
    }
    
    private void showErrorDialog(String error) {
        StringBuilder message = new StringBuilder();
        message.append("❌ Bridgee SDK Error:\n\n");
        message.append("🚨 Error Message:\n");
        message.append(error);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("⚠️ SDK Error")
                .setMessage(message.toString())
                .setPositiveButton(R.string.dialog_ok, null)
                .show();
    }
}
