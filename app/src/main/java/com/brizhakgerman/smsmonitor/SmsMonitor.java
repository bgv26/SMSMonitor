package com.brizhakgerman.smsmonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

public class SmsMonitor extends BroadcastReceiver {
    private final static String ACTION = "android.provider.Telephony.SMS_RECEIVED";
    final static String SMS_BODY = "sms_body";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null
                && intent.getAction().equalsIgnoreCase(ACTION)) {
            Object[] pduArray = (Object[]) intent.getExtras().get("pdus");
            if (pduArray != null && pduArray.length > 0) {
                SmsMessage[] messages = new SmsMessage[pduArray.length];
                for (int i = 0; i < pduArray.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pduArray[i]);
                }
                String sms_from = messages[0].getDisplayOriginatingAddress();
                if (sms_from.equalsIgnoreCase(context.getString(R.string.sms_from))) {
                    StringBuilder bodyText = new StringBuilder();
                    for (SmsMessage message : messages) {
                        bodyText.append(message.getMessageBody());
                    }
                    String body = bodyText.toString();
                    Intent mIntent = new Intent(context, SmsService.class);
                    mIntent.putExtra(SMS_BODY, body);
                    context.startService(mIntent);

                    abortBroadcast();
                }
            }
        }

    }
}
