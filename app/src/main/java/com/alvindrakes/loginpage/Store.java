package com.alvindrakes.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ming on 24/1/2018.
 */

public class Store extends MainActivity{
    TextView coin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store);
        coin = (TextView) findViewById(R.id.amount2);

        FloatingActionButton back = (FloatingActionButton) findViewById(R.id.close_store);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent StartPageIntent = new Intent(Store.this, MainActivity.class);
                startActivity(StartPageIntent);
            }
        });
        int value= getIntent().getExtras().getInt("coin");
        coin.setText(Integer.toString(value));
        Button price_1 = (Button) findViewById(R.id.button2);
        Button price_2 = (Button) findViewById(R.id.button3);
        Button price_3 = (Button) findViewById(R.id.button4);
        price_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              if (user.getCoin()>= 50) {
                  user.setCoin(user.getCoin() - 50);
                  coin.setText(Integer.toString(user.getCoin()));
                  StatisticData.updateCoin(user.getCoin());
              }
                else {
                  Toast.makeText(Store.this, "Insufficient coins", Toast.LENGTH_SHORT).show();
              }

    }
});
        price_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user.getCoin()>= 100) {
                    user.setCoin(user.getCoin() - 100);
                    coin.setText(Integer.toString(user.getCoin()));
                    StatisticData.updateCoin(user.getCoin());
                }
                else {
                    Toast.makeText(Store.this, "Insufficient coins", Toast.LENGTH_SHORT).show();
                }

            }
        });
        price_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user.getCoin()>= 200) {
                    user.setCoin(user.getCoin() - 200);
                    coin.setText(Integer.toString(user.getCoin()));
                    StatisticData.updateCoin(user.getCoin());
                }
                else {
                    Toast.makeText(Store.this, "Insufficient coins", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}