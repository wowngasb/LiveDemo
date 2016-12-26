package com.zgh.livedemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;

import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {
    protected static final int SUCCESS = 1;
    protected static final int ERROR = 2;

    private EditText et_nick;
    private EditText et_room_id;
    private EditText et_uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        et_nick = (EditText) findViewById(R.id.et_nick);
        et_room_id = (EditText) findViewById(R.id.et_room_id);
        et_uid = (EditText) findViewById(R.id.et_uid);

        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nick = et_nick.getText().toString();
                if (TextUtils.isEmpty(nick)) {
                    Toast.makeText(LoginActivity.this, "昵称不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                String uid = et_uid.getText().toString();
                if (TextUtils.isEmpty(uid)) {
                    Toast.makeText(LoginActivity.this, "用户id不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                String room_id_str = et_room_id.getText().toString();
                int room_id = Integer.parseInt(room_id_str);
                if ( room_id <= 0 ) {
                    Toast.makeText(LoginActivity.this, "房间id必须大于0", Toast.LENGTH_SHORT).show();
                    return;
                }
                login(room_id, nick,  uid);
            }
        });
    }

    public void login(final int room_id, String nick, String uid){
        this.login(room_id, nick, uid, "", "", 0);
    }

    public void login(final int room_id, String nick, String uid, String ava, String ext, int rank) {
        final String nick_copy = nick;
        final String uid_copy = uid;

        new Thread(){
            public void run() {
                try {
                    URL url = new URL("http://" + DmsHub.api_host + "/api/RoomAssist/getDmsConfig?room_id=" + room_id + "&nick=" + nick_copy + "&uid=" + uid_copy);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");//声明请求方式 默认get
                    //conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; U; Android 2.3.3; zh-cn; sdk Build/GRI34) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1 MicroMessenger/6.0.0.57_r870003.501 NetType/internet");
                    int code = conn.getResponseCode();
                    if(code ==200){
                        InputStream is = conn.getInputStream();
                        String result = StreamTools.readStream(is);

                        Message msg = Message.obtain();//减少消息创建的数量
                        msg.obj = new JSONObject(result);
                        msg.what = SUCCESS;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    Message msg = Message.obtain();//减少消息创建的数量
                    msg.what = ERROR;
                    handler.sendMessage(msg);
                    e.printStackTrace();
                }
            };
        }.start();

    }

    private Handler handler = new Handler(){
        DmsHubCallback callback = new DmsHubCallback() {
            @Override
            public void run() {

            }
        };
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SUCCESS:
                    JSONObject dmsRst = (JSONObject) msg.obj;
                    try{
                        if( dmsRst.getInt("Flag")==100 ){
                            DmsHub.dmsConfig = dmsRst.getJSONObject("Info");
                            DmsHub.initDmsClient(callback, getApplicationContext(),
                                    DmsHub.dmsConfig.getString("dms_pub_key"),
                                    DmsHub.dmsConfig.getString("dms_sub_key"),
                                    DmsHub.dmsConfig.getString("client_id"));
                            break;
                        }
                    } catch (JSONException ex1){
                        ex1.printStackTrace();
                    } catch (MqttException ex2){
                        ex2.printStackTrace();
                    }
                case ERROR:
                    Toast.makeText(LoginActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        };
    };

}
