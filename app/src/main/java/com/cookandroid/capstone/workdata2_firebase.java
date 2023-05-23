package com.cookandroid.capstone;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class workdata2_firebase {
    String money;
    String startTime;
    String endTime;
    String name;
    String selectPay;
    String selectRestTime;

    public workdata2_firebase(){};

    public workdata2_firebase(String name,String money, String startTime,String endTime,String selectPay,String selectRestTime){
        this.name = name;
        this.money = money;
        this.startTime = startTime;
        this.endTime = endTime;
        this.selectPay = selectPay;
        this.selectRestTime = selectRestTime;
    }
    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getStartTime(){
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime(){
        return endTime;
    }

    public void setEndTime(String endTIme){
        this.endTime = endTime;
    }

    public String getSelectPay(){
        return selectPay;
    }

    public void setSelectPay(String selectPay){
        this.selectPay = selectPay;
    }

    public String getSelectRestTime(){
        return selectRestTime;
    }

    public void setSelectRestTime(String selectRestTime){
        this.selectRestTime = selectRestTime;
    }


    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                "money='" + money + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime=" + endTime + '\'' +
                ", selectPay=" + selectPay + '\'' +
                ", selectRestTime=" + selectRestTime +
                '}';
    }


}
