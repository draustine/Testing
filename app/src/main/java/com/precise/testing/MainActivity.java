package com.precise.testing;

import static java.lang.Integer.parseInt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST = 101;
    private static final int REQUEST_RPS = 0;
    private SmsManager smsManager;
    private SubscriptionInfo subsInfo;
    private SubscriptionManager subsManager;
    //private Button sendButton, previewButton, getButton;
    private EditText phoneInput, messageInput, simSlot;
    private TextView display1, display2;
    private String carrier, provider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        phoneInput = findViewById(R.id.phoneNumber);
        messageInput = findViewById(R.id.message);
        simSlot = findViewById(R.id.simSlot);
        display1 = findViewById(R.id.display1);
        display2 = findViewById(R.id.textDisplay);
        getPermission();

    }


    private void getPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        if (!(permissionCheck == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS},
                    PERMISSION_REQUEST);
        }
        int REQUEST_RPS = 0;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_RPS);
        }
    }

    private SmsManager smsSender(int id) {
        SubscriptionManager localSusManager = this.getSystemService(SubscriptionManager.class);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_RPS);
        }
        SubscriptionInfo subsInfo = localSusManager.getActiveSubscriptionInfoForSimSlotIndex(id);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            smsManager = getApplicationContext().getSystemService(SmsManager.class) .createForSubscriptionId(subsInfo.getSubscriptionId());
        } else {
            smsManager = SmsManager.getSmsManagerForSubscriptionId(id);
        }
        return smsManager;
    }

    private String getSimCarrierName(int id){
        String name = "";
        subsManager =(SubscriptionManager)getApplicationContext().getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_RPS);
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // for ActivityCompat#requestPermissions for more details.
        }
        int i = subsManager.getActiveSubscriptionInfoCount();
        if (i > 1) {
            //if there are two sims in dual sim mobile
            List localList = subsManager.getActiveSubscriptionInfoList();
            SubscriptionInfo simInfo = (SubscriptionInfo) localList.get(id);
            name = simInfo.getDisplayName().toString();
        } else {
            //if there is 1 sim in dual sim mobile
            TelephonyManager tManager = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
            name = tManager.getNetworkOperatorName();
        }
        return name;
    }

    public void getFiles(View view) {




    }




    public void viewMessages(View view) {
    }

    public void sendMessage(View view) {
        String message;
        int slot = parseInt(simSlot.getText().toString());
        message = "Sending the message from sim " + slot;
        String phoneNumber;
        if (TextUtils.isEmpty(phoneInput.getText().toString())){
            phoneNumber = "08108020030";
        } else {
            phoneNumber = phoneInput.getText().toString();
        }
        fillDisplay1(phoneNumber);
        messageSender(message, phoneNumber);
    }

    private void messageSender(String message, String phone) {
        int sim_Slot;
        String carrierName, on, off, shortCode;
        sim_Slot = parseInt(simSlot.getText().toString());
        carrierName = getSimCarrierName(sim_Slot-1);
        String comment = "The carrier is: " + carrierName;
        fillDisplay2(comment);
        String provider = carrierName.substring(0, 3).toUpperCase();
        comment = "The provider is: " + provider;
        fillDisplay1(comment);
        switch(provider){
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
        smsManager = smsSender(sim_Slot-1);
        smsManager.sendTextMessage(phone, null, message, null, null);
        fillDisplay1("Message was sent using " + provider);

    }

    private void fillDisplay1(String content) {
        display1.setText(content);
    }

    private void fillDisplay2(String content) {
        display2.setText(content);
    }
}