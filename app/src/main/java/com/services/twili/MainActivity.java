package com.services.twili;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;



public class MainActivity extends AppCompatActivity {
    public static final String ACCOUNT_SID = "AC5fc62f901cda77980f215fdc51dac629";
    public static final String AUTH_TOKEN = "dfb2f84b7cb76bb1e2df1b11c325dbf3";
    EditText phone;
    Button go;
    Date currentTime;
    ProgressDialog dialog;
    Dialog alarm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        phone=(EditText)findViewById(R.id.editText);
        go=(Button)findViewById(R.id.button);
        currentTime = Calendar.getInstance().getTime();
        dialog=new ProgressDialog(this);
        alarm =new Dialog(this);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(phone.getText().toString().trim())) {
                    phone.setError("Please Enter Phone Number ");
                    phone.requestFocus();


                }

                else if(!(phone.getText().toString().startsWith("+"))){
                        phone.setError("Please Enter a corect phone number must start with + ");
                        phone.requestFocus();
                    }
                else if (phone.getText().toString().length() <10){
                    phone.setError("Please Enter a corect phone number  ");
                    phone.requestFocus();
                }
                else
                    show_alarm(v);

            }
        });
    }
    private void sendMessage(String phone) {
        dialog.show();
        dialog.setMessage("please wait..!");
        String body = "";
        if(currentTime.getHours()>=0&&currentTime.getHours()<=12){
           body="Good morning! Your promocode is AM123";
        }
        if(currentTime.getHours()>12){
            body="Hello! Your promocode is PM456";
        }

        String from = "+12052933764";
        String to = phone;

        String base64EncodedCredentials = "Basic " + Base64.encodeToString(
                (ACCOUNT_SID + ":" + AUTH_TOKEN).getBytes(), Base64.NO_WRAP
        );

        Map<String, String> data = new HashMap<>();
        data.put("From", from);
        data.put("To", to);
        data.put("Body", body);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.twilio.com/2010-04-01/")
                .build();
        TwilioApi api = retrofit.create(TwilioApi.class);



        api.sendMessage(ACCOUNT_SID, base64EncodedCredentials, data).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this,"A message with details will be sent",Toast.LENGTH_LONG).show();
                }
                else {
                    dialog.dismiss();

                    Toast.makeText(MainActivity.this,"Account is demo, only a specific number can be tried ",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(MainActivity.this,"Cannot connect to server\n" +
                        "Please make sure you are connected to the Internet ",Toast.LENGTH_LONG).show();
            }
        });
    }

    interface TwilioApi {
        @FormUrlEncoded
        @POST("Accounts/{ACCOUNT_SID}/SMS/Messages")
        Call<ResponseBody> sendMessage(
                @Path("ACCOUNT_SID") String accountSId,
                @Header("Authorization") String signature,
                @FieldMap Map<String, String> metadata
        );
    }
    private void show_alarm(View view) {


        WebView webView;
        final Button agree;
        final CheckBox arg,age;
        alarm.setContentView(R.layout.content_privacypolicies);
        webView=(WebView)alarm.findViewById(R.id.browser);
        agree=(Button)alarm.findViewById(R.id.agree);
        arg=(CheckBox)alarm.findViewById(R.id.arg);
        age=(CheckBox)alarm.findViewById(R.id.age);
        webView.loadUrl("https://www.privacypolicies.com/terms/view/806ef2285ab3a4c7357c5a5b07f21f1f");
        agree.setVisibility(View.INVISIBLE);
        arg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arg.isChecked()) {
                    if (age.isChecked())
                        agree.setVisibility(View.VISIBLE);
                    else
                        agree.setVisibility(View.INVISIBLE);
                }else {
                    agree.setVisibility(View.INVISIBLE);
                }

            }
        });
        age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arg.isChecked()) {
                    if (age.isChecked())
                        agree.setVisibility(View.VISIBLE);
                    else
                        agree.setVisibility(View.INVISIBLE);
                }else {
                    agree.setVisibility(View.INVISIBLE);
                }
            }
        });
        if(arg.isChecked())
            if(age.isChecked())
                agree.setVisibility(View.VISIBLE);

        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               sendMessage(phone.getText().toString());
               alarm.dismiss();
               phone.setText("");

            }
        });


        alarm.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alarm.show();



    }
}
