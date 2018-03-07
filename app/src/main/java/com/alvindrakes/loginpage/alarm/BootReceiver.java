package com.alvindrakes.loginpage.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.alvindrakes.loginpage.Actions;
import com.alvindrakes.loginpage.App;

import trikita.jedux.Action;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (App.getState().alarm().on()) {
            App.dispatch(new Action<>(Actions.Alarm.RESTART_ALARM));
        }
    }
}
