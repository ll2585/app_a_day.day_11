package com.lukeli.appaday.day11;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
    EditText txtMsgEditText, pNumEditText, messagesEditText;
    Button sendButton;
    static String messages = "";

    Handler mHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtMsgEditText = (EditText) findViewById(R.id.txtMsgEditText);
        pNumEditText = (EditText) findViewById(R.id.pNumEditText);
        messagesEditText = (EditText) findViewById(R.id.messagesEditText);
        sendButton = (Button) findViewById(R.id.sendButton);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(5000);
                        mHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                messagesEditText.setText(messages);
                            }
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void sendMessage(View view) {

        // Get the phone number and message to send
        String phoneNum = pNumEditText.getText().toString();
        String message = txtMsgEditText.getText().toString();

        try{

            // Handles sending and receiving data and text
            SmsManager smsManager = SmsManager.getDefault();

            // Sends the text message
            // 2nd is for the service center address or null
            // 4th if not null broadcasts with a successful send
            // 5th if not null broadcasts with a successful delivery
            smsManager.sendTextMessage(phoneNum, null, message, null, null);

            Toast.makeText(this, "Message Sent", Toast.LENGTH_SHORT).show();

        }
        catch (IllegalArgumentException ex){

            Log.e("TEXTING", "Destination Address or Data Empty");
            Toast.makeText(this, "Enter a Phone Number and Message", Toast.LENGTH_LONG).show();
            ex.printStackTrace();

        }
        catch (Exception ex) {
            Toast.makeText(this, "Message Not Sent", Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }

        // Update the message EditText
        messages = messages + "You : " + message + "\n";

    }

    public static class SmsReceiver extends BroadcastReceiver{
        final SmsManager smsManager = SmsManager.getDefault();
        public SmsReceiver(){}
        @Override
        public void onReceive(Context context, Intent intent) {
            final Bundle bundle = intent.getExtras();
            try{
                if(bundle != null){
                    final Object[] pdusObj = (Object[]) bundle.get("pdus");
                    for(int i = 0; i < pdusObj.length; i++){
                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);

                        String phoneNumber = smsMessage.getDisplayOriginatingAddress();
                        String message = smsMessage.getDisplayMessageBody();

                        messages = messages + phoneNumber + " : " + message + "\n";
                    }
                }
            }catch (Exception e){
                Log.e("SmsReceiver", "Exception");
            }
        }
    }

    public class MMSReceiver extends BroadcastReceiver{
        public MMSReceiver(){}
        @Override
        public void onReceive(Context context, Intent intent) {
            throw new UnsupportedOperationException("Not implemented Yet");
        }
    }

    public class HeadlessSmsSendService extends BroadcastReceiver{
        public HeadlessSmsSendService(){}
        @Override
        public void onReceive(Context context, Intent intent) {
            throw new UnsupportedOperationException("Not implemented Yet");
        }
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
