package com.elazarhalperin.fluentify.broadcast_receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsReceiverBroadcastReceiver extends BroadcastReceiver {
    SmsVerificationCallback callback;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("receiver_code", "onReceive: SMS received from Firebase");

        // Extract the SMS message from the intent
        SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        if (messages != null && messages.length > 0) {
            StringBuilder smsBody = new StringBuilder();
            for (SmsMessage message : messages) {
                smsBody.append(message.getMessageBody());
            }
            // get the sms that the firebase sended
            String sms = smsBody.toString();

            // Extract verification code from SMS
            String verificationCode = extractVerificationCode(sms);
            if (verificationCode != null) {
                // Perform the desired action with the verification code
                Toast.makeText(context, "Verification code: " + verificationCode, Toast.LENGTH_SHORT).show();

                // You can also perform any other action you want with the verification code here
                // such as updating UI, triggering a notification, etc.
            }
            if (callback != null) {
                // here we send the code to the fragment.
                callback.onVerificationCodeReceived(verificationCode);
            }

        }
    }

    // Helper method to extract verification code from SMS
    private String extractVerificationCode(String smsBody) {
        // Use a regular expression pattern to match 6-digit numeric verification codes
        Pattern pattern = Pattern.compile("\\b\\d{6}\\b"); // Assuming 6-digit numeric verification code
        Matcher matcher = pattern.matcher(smsBody);
        if (matcher.find()) {
            return matcher.group();
        } else {
            return null;
        }
    }

    // Set callback for communication with fragment
    // when we will set it
    public void setCallback(SmsVerificationCallback callback) {
        this.callback = callback;
    }

    // Callback interface
    // so when the sms is received to the Broadcast Receiver
    // we can send it to the fragment.
    public interface SmsVerificationCallback {
        void onVerificationCodeReceived(String verificationCode);
    }
}