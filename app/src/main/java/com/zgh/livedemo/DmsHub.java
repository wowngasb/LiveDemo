package com.zgh.livedemo;

import org.json.JSONObject;

import com.aodianyun.dms.android.DMS;

import android.content.Context;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;



import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


import java.util.HashMap;

class DmsHub {
    protected static final int SUCCESS = 1;
    protected static final int ERROR = 2;

    public static boolean connected = false;
    public static JSONObject dmsConfig;
    protected static String api_host = "finance.aodianyun.com";
    protected static HashMap<String, DmsCallback> event_map;
    protected static HashMap<String, JSONObject> cache_user_map;

    public static void initDmsClient(DmsHubCallback callback, Context ctx, String dms_pub_key, String dms_sub_key, String client_id) throws MqttException{
        DMS.init2(ctx, dms_pub_key, dms_sub_key, client_id, new MqttCallback() {
            @Override
            public void messageArrived(String topic, MqttMessage msg) {
                //收到话题消息d
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
            @Override
            public void connectionLost(Throwable t) {
                //连接中断
            }
        });

        final DmsHubCallback callback_copy = callback;
        DMS.connect(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                DmsHub.connected = true;
                callback_copy.run();
            }
            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

            }
        });
    }

    public static void onMsg(String cmd, DmsCallback callback, DmsCallback filter){

    }
}
