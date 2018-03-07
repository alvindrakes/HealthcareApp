package com.alvindrakes.loginpage;

import android.app.Application;

import com.alvindrakes.loginpage.alarm.AlarmController;
import com.alvindrakes.loginpage.alarm.PersistanceController;


import trikita.anvil.Anvil;
import trikita.jedux.Action;
import trikita.jedux.Logger;
import trikita.jedux.Store;

public class App extends Application {

    private static App instance;

    private Store<Action, State> store2;

    public static State dispatch(Action action) {
        return instance.store2.dispatch(action);
    }

    public static State getState() {
        return instance.store2.getState();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        App.instance = this;

        PersistanceController persistanceController = new PersistanceController(this);
        State initialState = persistanceController.getSavedState();
        if (initialState == null) {
            initialState = State.Default.build();
        }

        this.store2 = new Store<>(new State.Reducer(),
                initialState,
                new Logger<>("Talalarmo"),
                persistanceController,
                new AlarmController(this));

        this.store2.subscribe(Anvil::render);
    }
}
