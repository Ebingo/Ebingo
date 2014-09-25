package com.promote.ebingo.impl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * JsonObjct的包装类
 * Created by acer on 2014/9/25.
 */
public class JsonObjectWrapper {
    private JSONObject jsonObject;

    public JsonObjectWrapper(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public int length() {
        return jsonObject.length();
    }

    public boolean isNull(String name) {
        return jsonObject.isNull(name);
    }

    public long optLong(String name) {
        return jsonObject.optLong(name);
    }

    public JSONObject put(String name, int value) throws JSONException {
        return jsonObject.put(name, value);
    }

    public Iterator<String> keys() {
        return jsonObject.keys();
    }

    public JSONObject accumulate(String name, Object value) throws JSONException {
        return jsonObject.accumulate(name, value);
    }

    public static String quote(String data) {
        return JSONObject.quote(data);
    }

    public String getString(String name) throws JSONException {

        return jsonObject.getString(name);
    }

    public JSONArray getJSONArray(String name) throws JSONException {
        return jsonObject.getJSONArray(name);
    }

    public Object get(String name) throws JSONException {
        return jsonObject.get(name);
    }

    public String toString(int indentSpaces) throws JSONException {
        return jsonObject.toString(indentSpaces);
    }

    public JSONObject optJSONObject(String name) {
        return jsonObject.optJSONObject(name);
    }

    public boolean optBoolean(String name, boolean fallback) {
        return jsonObject.optBoolean(name, fallback);
    }

    public long optLong(String name, long fallback) {
        return jsonObject.optLong(name, fallback);
    }

    public JSONObject put(String name, double value) throws JSONException {
        return jsonObject.put(name, value);
    }

    public JSONArray toJSONArray(JSONArray names) throws JSONException {
        return jsonObject.toJSONArray(names);
    }

    public double optDouble(String name, double fallback) {
        return jsonObject.optDouble(name, fallback);
    }

    public double getDouble(String name) throws JSONException {
        return jsonObject.getDouble(name);
    }

    public long getLong(String name) throws JSONException {
        return jsonObject.getLong(name);
    }

    public JSONArray optJSONArray(String name) {
        return jsonObject.optJSONArray(name);
    }

    public Object remove(String name) {
        return jsonObject.remove(name);
    }

    public String optString(String name) {
        return jsonObject.optString(name);
    }

    public int getInt(String name) throws JSONException {
        return jsonObject.getInt(name);
    }

    public double optDouble(String name) {
        return jsonObject.optDouble(name);
    }

    public int optInt(String name, int fallback) {
        return jsonObject.optInt(name, fallback);
    }

    public JSONObject put(String name, boolean value) throws JSONException {
        return jsonObject.put(name, value);
    }

    public int optInt(String name) {
        return jsonObject.optInt(name);
    }

    public JSONObject getJSONObject(String name) throws JSONException {
        return jsonObject.getJSONObject(name);
    }

    public boolean has(String name) {
        return jsonObject.has(name);
    }

    public JSONObject putOpt(String name, Object value) throws JSONException {
        return jsonObject.putOpt(name, value);
    }

    public boolean optBoolean(String name) {
        return jsonObject.optBoolean(name);
    }

    public JSONArray names() {
        return jsonObject.names();
    }

    public JSONObject put(String name, Object value) throws JSONException {
        return jsonObject.put(name, value);
    }

    public static String numberToString(Number number) throws JSONException {
        return JSONObject.numberToString(number);
    }

    public String optString(String name, String fallback) {
        return jsonObject.optString(name, fallback);
    }

    public boolean getBoolean(String name) throws JSONException {
        return jsonObject.getBoolean(name);
    }

    public Object opt(String name) {
        return jsonObject.opt(name);
    }

    public JSONObject put(String name, long value) throws JSONException {
        return jsonObject.put(name, value);
    }

}
