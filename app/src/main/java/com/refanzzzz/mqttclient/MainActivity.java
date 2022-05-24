package com.refanzzzz.mqttclient;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {

    MaterialButton btnPublish, btnConnect, btnDisconnect;
    TextView txtSubs;
    MqttAndroidClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initWidget();

        client = new MqttAndroidClient(MainActivity.this, "tcp://broker.hivemq.com:1883", "");

        try{
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                    setSubscription();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
                }
            });
        }catch(MqttException e){
            e.printStackTrace();
        }

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                txtSubs.setText(new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        btnPublish.setOnClickListener(v -> {
            String topic = "sisterkel2/fancontrol/humidity";
            String message = "Hello Refan";

            try{
                client.publish(topic, message.getBytes(), 0, true);
                Toast.makeText(MainActivity.this, "Published Message", Toast.LENGTH_SHORT).show();
            }catch(MqttException e){
                e.printStackTrace();
            }
        });

        btnConnect.setOnClickListener(v -> {
            try {
                IMqttToken token = client.connect();
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                        setSubscription();
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Toast.makeText(MainActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
                    }
                });
            }catch(MqttException e){
                e.printStackTrace();
            }
        });

        btnDisconnect.setOnClickListener(v -> {
            try{
                IMqttToken token = client.disconnect();
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Toast.makeText(MainActivity.this, "Disconnected!!!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Toast.makeText(MainActivity.this, "Could not disconnet", Toast.LENGTH_SHORT).show();
                    }
                });
            }catch(MqttException e){
                e.printStackTrace();
            }
        });
    }

    private void initWidget(){
        btnPublish = (MaterialButton) findViewById(R.id.btnPublish);
        btnConnect = (MaterialButton) findViewById(R.id.btnConnect);
        btnDisconnect = (MaterialButton) findViewById(R.id.btnDisconnect);
        txtSubs = (TextView) findViewById(R.id.txtSubs);
    }

    private void setSubscription(){
        try{
            client.subscribe("sisterkel2/fancontrol/humidity", 0);
        }catch(MqttException e){
            e.printStackTrace();
        }
    }
}