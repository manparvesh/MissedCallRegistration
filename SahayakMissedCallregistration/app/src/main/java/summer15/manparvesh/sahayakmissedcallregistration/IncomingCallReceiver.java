package summer15.manparvesh.sahayakmissedcallregistration;

/**
 * Created by Man Parvesh on 6/30/2015.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.Toast;


public class IncomingCallReceiver extends BroadcastReceiver {

    static boolean ring = false;
    static boolean callReceived = false;
    public static String callerPhoneNumber;

    //boolean added=false;

    @Override
    public void onReceive(Context mContext, Intent intent) {

        // Get the current Phone State
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        if (state == null)
            return;

        // If phone state "Rininging"
        if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            ring = true;
            // Get the Caller's Phone Number
            Bundle bundle = intent.getExtras();
            callerPhoneNumber = bundle.getString("incoming_number");
            callerPhoneNumber=callerPhoneNumber.substring(callerPhoneNumber.length()-10);
        }


        // If incoming call is received
        if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
            callReceived = true;
        }


        // If phone is Idle
        if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            // If phone was ringing(ring=true) and not received(callReceived=false) , then it is a missed call
            if (ring && !callReceived) {
                Toast.makeText(mContext, "It was A MISSED CALL from : " + callerPhoneNumber, Toast.LENGTH_LONG).show();
                Toast.makeText(mContext,"Adding: " + callerPhoneNumber+" and opening main app", Toast.LENGTH_LONG).show();
                Toast.makeText(mContext,"Please don't touch anything here..\nUploading to database..", Toast.LENGTH_LONG).show();
                Intent intent1 = new Intent(mContext , UploadNumber.class);
                intent1.putExtra("callerPhoneNumber",callerPhoneNumber);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent1);
            }
        }

    }




}