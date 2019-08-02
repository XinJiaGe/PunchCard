package com.jiage.punchcard.sharedPreference;


/**
 * 作者：李忻佳
 * 日期：2017/11/30/030.
 * 说明：SharedPreference配置
 */

public class SharedPreferenceConfig extends SharedPreference {
    private String TOP = "top";
    private String BOT = "bot";

    public String getTOP() {
        return getString(TOP, "8-50");
    }

    public void setTOP(String top) {
        setString(TOP, top);
    }

    public String getBOT() {
        return getString(BOT, "18-10");
    }

    public void setBOT(String top) {
        setString(BOT, top);
    }

}
