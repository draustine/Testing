package com.precise.testing;

import static java.lang.Integer.parseInt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST = 101;
    private SmsManager smsManager;
    private SubscriptionInfo subsInfo;
    private SubscriptionManager subsManager;
    //private Button sendButton, previewButton, getButton;
    private EditText phoneInput, messageInput, smsRate, simSlot, carrier;
    private TextView display1, display2;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        phoneInput = findViewById(R.id.phoneNumber);
        messageInput = findViewById(R.id.message);
        smsRate = findViewById(R.id.unitCost);
        simSlot = findViewById(R.id.simSlot);
        carrier = findViewById(R.id.networkSelect);
        display1 = findViewById(R.id.display1);
        display2 = findViewById(R.id.textDisplay);
        smsManager = getApplicationContext().getSystemService(SmsManager.class);
        getPermission();

    }

    private void getPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        if (!(permissionCheck == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS},
                    PERMISSION_REQUEST);
        }
    }

    public void getFiles(View view) {
    }

    public void viewMessages(View view) {
    }

    public void sendMessage(View view) {
        String message;
        int slot = parseInt(simSlot.getText().toString());
        message = "Sending the message from sim " + slot;
        String phoneNumber = phoneInput.getText().toString();
        messageSender(message, phoneNumber);
    }

    private void messageSender(String message, String phone) {
        int sim_Slot;
        String carrierName;
        sim_Slot = parseInt(simSlot.getText().toString());
        subsManager = SubscriptionManager.from(this);
        String on, off, shortCode;
        String serviceProvider = carrier.getText().toString().toUpperCase();
        int simSlotCount = subsManager.getActiveSubscriptionInfoCountMax();

        switch(serviceProvider){
            case "GLO":
                on = "ACN ON";
                off = "ACN OFF";
                shortCode = "3036";
                break;
            default:
                shortCode = "131";
                on = "EOCNON";
                off = "EOCNOFF";
        }
        if(sim_Slot == 1) {
            SmsManager.getSmsManagerForSubscriptionId(0).sendTextMessage(phone, null, message, null, null);
            fillDisplay1("message was sent using sim 1");

        } else if (sim_Slot == 2) {
            SmsManager.getSmsManagerForSubscriptionId(0).sendTextMessage(phone, null, message, null, null);
            fillDisplay1("message was sent using sim 2");
        } else {
            smsManager.sendTextMessage(phone, null, message, null, null);
            fillDisplay1("message was sent using default sim");
        }

    }

    private void fillDisplay1(String content) {
        display1.setText(content);
    }

    private void fillDisplay2(String content) {
        display2.setText(content);
    }
}