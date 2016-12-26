package com.zgh.livedemo;

import org.json.JSONObject;

abstract class DmsCallback {

    public abstract void run(String topic, JSONObject data);

}