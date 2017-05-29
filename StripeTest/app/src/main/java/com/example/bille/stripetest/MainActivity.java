package com.example.bille.stripetest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Token;
import com.stripe.android.model.Card;
import com.stripe.android.view.CardInputWidget;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    TextView statusTxt;
    CardInputWidget mCardInputWidget;
    static URL API_URL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mCardInputWidget = (CardInputWidget) findViewById(R.id.card_input_widget);
        statusTxt = (TextView)findViewById(R.id.statusTxt);
        try {
            API_URL = new URL("http://127.0.0.1:9000/payments");
        } catch (MalformedURLException e) {
            statusTxt.setText("Error with API URL - " + e.getLocalizedMessage());
        }
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


    public void BuyBtnClicked(final View view) {
        //Sends the encrypted input from CardInputWidget to Stripes API, recieves a token with access to the userdata.
        Card cardToSave = mCardInputWidget.getCard();
        if (cardToSave == null) {
            statusTxt.setText("ERROR - You need to fill out card info. To test, fill out with 42 all the way.");
            return;
        }
        Stripe stripe = new Stripe(view.getContext(), "pk_test_VouLMqO267WSYt2CD1Fd7YcD");
        stripe.createToken(
                cardToSave,
                new TokenCallback() {

                    public void onSuccess(Token token) {
                        statusTxt.setText("Token recieved: " + token); //TODO send the token to a RestAPI, that can charge the users card.
                        /*
                        //Makes a POST request at the API_URL, includes the token.
                        try {
                            HttpURLConnection client = (HttpURLConnection) API_URL.openConnection();
                            client.setRequestMethod("POST");
                            client.setDoOutput(true);
                            OutputStream outputPost = client.getOutputStream();
                            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(outputPost, "UTF-8"));
                            writer.write(token.toString());
                            outputPost.flush();
                            outputPost.close();
                            if(client.getResponseCode() == HttpURLConnection.HTTP_OK)
                            {
                                statusTxt.setText("Succes, API response code: " + client.getResponseCode());
                            }
                        }
                        catch (IOException e) {
                            statusTxt.setText("Error connecting to API - " + e.getLocalizedMessage());
                        }*/
                    }
                    public void onError(Exception error) {
                        statusTxt.setText("No token recieved: " + error);
                    }
                }
        );
    }

}
