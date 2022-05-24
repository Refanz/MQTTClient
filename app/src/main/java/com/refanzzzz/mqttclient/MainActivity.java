package com.refanzzzz.mqttclient;

import android.os.Bundle;
import android.widget.EditText;
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
import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    MaterialButton btnPublish, btnConnect, btnDisconnect;
    TextView txtCelcius, txtFahrenheit, txtHumidity, txtPublish;
    EditText etPublish;
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
                if(topic.equals("sisterkel2/fancontrol/humidity")){
                    txtHumidity.setText(new String(message.getPayload()) + " %");
                }else if(topic.equals("sisterkel2/fancontrol/temperature/celcius")){
                    txtCelcius.setText(new String(message.getPayload()) + " °C");
                }else if(topic.equals("sisterkel2/fancontrol/temperature/fahrenheit")){
                    txtFahrenheit.setText(new String(message.getPayload()) + " °F");
                }else if(topic.equals("sisterkel2/fancontrol/test")){
                    txtPublish.setText("Test: "+new String(message.getPayload()));
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        btnPublish.setOnClickListener(v -> {

            try{
                String topic = "sisterkel2/fancontrol/test";
                String message = etPublish.getText().toString();
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
        txtHumidity = (TextView) findViewById(R.id.txtHumidity);
        txtCelcius = (TextView) findViewById(R.id.txtCelcius);
        txtFahrenheit = (TextView) findViewById(R.id.txtFahrenheit);
        txtPublish = (TextView) findViewById(R.id.testPublish);
        etPublish = (EditText) findViewById(R.id.editTestPub);

    }

    private void setSubscription(){
        try{
            client.subscribe("sisterkel2/fancontrol/test", 0);
            client.subscribe("sisterkel2/fancontrol/humidity", 0);
            client.subscribe("sisterkel2/fancontrol/temperature/celcius", 0);
            client.subscribe("sisterkel2/fancontrol/temperature/fahrenheit", 0);
        }catch(MqttException e){
            e.printStackTrace();
        }
    }
}