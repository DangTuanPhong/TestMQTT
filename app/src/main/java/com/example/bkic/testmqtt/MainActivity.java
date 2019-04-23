package com.example.bkic.testmqtt;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;



public class MainActivity extends AppCompatActivity {
    Ringtone myRingtone;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionInternet();
        setContentView(R.layout.activity_main);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        myRingtone = RingtoneManager.getRingtone(getApplicationContext(),uri);
        connect();

    }

    public void connect(){
        String clientId = MqttClient.generateClientId();
        final MqttAndroidClient client = new MqttAndroidClient(this.getApplicationContext(), "tcp://m16.cloudmqtt.com:12902", clientId);

        String username = "smvkbsmu";
        String password = "5Fd26eTVQWFP";
        MqttConnectOptions connectOptions = new MqttConnectOptions();
        connectOptions.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
        connectOptions.setCleanSession(false);
        connectOptions.setUserName(username);
        connectOptions.setPassword(password.toCharArray());


        try {
            IMqttToken token = client.connect(connectOptions);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                    subscribe(client,"topic");
                    subscribe(client, "Độ ẩm");
                    client.setCallback(new MqttCallback() {
                        TextView tvTime = findViewById(R.id.tv_Time);
                        TextView tvTemp = findViewById(R.id.tv_Temp);
                        TextView tvHum = findViewById(R.id.tv_Hum);
                        TextView tvPM1 = findViewById(R.id.tv_pm1);
                        TextView tvPM2_5 = findViewById(R.id.tv_pm2_5);
                        TextView tvPM10 = findViewById(R.id.tv_pm10);
                        @Override
                        public void connectionLost(Throwable cause) {
                            Toast.makeText(MainActivity.this, "Connection is lost",Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void messageArrived(String topic, MqttMessage message) {
                            Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            if (topic.equals("topic")){

                                vibe.vibrate(350);
                                myRingtone.play();
                               // tv1.setText(message.toString());
                                String chuoi = message.toString();
                                String[] output= chuoi.split("-");
                                tvTime.setText(output[0] + " " +output[1]);
                                tvTemp.setText(output[2] + "°C");
                                tvHum.setText(output[3] + "%");
                                tvPM1.setText("PM1: "+output[4] + "µg/m³");
                                tvPM2_5.setText("PM2.5: "+ output[5] + "µg/m³");
                                tvPM10.setText( "PM10: "+ output[6] + "µg/m³");
                            }

                        }

                        @Override
                        public void deliveryComplete(IMqttDeliveryToken token) {
                        }

                    });
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this, "Please check again the connection", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(MqttAndroidClient client , String topic){
        int qos = 1;
        //subscribe
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this, "thất bại cmnr", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public void PermissionInternet() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {

                //Permisson don't granted
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.INTERNET)) {
                    Toast.makeText(MainActivity.this, "Permission isn't granted ", Toast.LENGTH_SHORT).show();
                }
                // Permisson don't granted and dont show dialog again.
                else {
                    Toast.makeText(MainActivity.this, "Permisson don't granted and dont show dialog again ", Toast.LENGTH_SHORT).show();
                }
                //Register permission
                requestPermissions(new String[]{Manifest.permission.INTERNET}, 1);

            }
        }
    }
}

