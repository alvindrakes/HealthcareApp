package com.alvindrakes.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.content.DialogInterface;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Window;
import android.app.AlertDialog;

/**
 * Created by ming on 24/1/2018.
 */

public class Store extends MainActivity{
    TextView coin;

    //for navigation drawer
//    private DrawerLayout myDrawer;
//    private ActionBarDrawerToggle myToggle;
//    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ActionBar bar = getSupportActionBar();
        bar.setTitle("Store");
        getSupportActionBar().show();
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
                                           final AlertDialog.Builder builder3 = new AlertDialog.Builder(Store.this, android.R.style.Theme_Material_Dialog_Alert);
                                           builder3.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                               public void onClick(DialogInterface dialog, int id) {
                                                   if (user.getCoin() >= 50) {
                                                       user.setCoin(user.getCoin() - 50);
                                                       coin.setText(Integer.toString(user.getCoin()));
                                                       StatisticData.updateCoin(user.getCoin());
                                                   } else {
                                                       Toast.makeText(Store.this, "Insufficient coins", Toast.LENGTH_SHORT).show();
                                                   }

                                               }
                                           });
                                           builder3.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                               public void onClick(DialogInterface dialog, int id) {
                                               }

                                           });
                                           builder3.setTitle("Buy the item?");
                                           builder3.setMessage("Buy the item for 50 coins?");
                                           final AlertDialog dialog = builder3.create();
                                           dialog.show();
                                       }
                                   });


        price_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(Store.this, android.R.style.Theme_Material_Dialog_Alert);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
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
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }

                });
                builder.setTitle("Buy the item?");
                builder.setMessage("Buy the item for 100 coins?");
                final AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        price_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               final  AlertDialog.Builder builder2 = new AlertDialog.Builder(Store.this, android.R.style.Theme_Material_Dialog_Alert);
                builder2.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
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
                builder2.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }

                });
                builder2.setTitle("Buy the item?");
                builder2.setMessage("Buy the item for 200 coins?");
                final AlertDialog dialog = builder2.create();
                dialog.show();
            }
        });

//        myDrawer = (DrawerLayout) findViewById(R.id.myDrawer);
//        myToggle = new ActionBarDrawerToggle(this, myDrawer, R.string.open, R.string.close);
//
//        navigationView = (NavigationView) findViewById(R.id.my_navigation);
//        navigationView.bringToFront();
//
//        navigationView.setNavigationItemSelectedListener(this);
//
//        myDrawer.addDrawerListener(myToggle);
//        myToggle.syncState();
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        if (myToggle.onOptionsItemSelected(item)) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    public boolean onNavigationItemSelected(MenuItem item) {
//        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//
//        switch (id) {
//            case R.id.profile_page:
//                Intent i = new Intent(Store.this, ProfilePage.class);
//                startActivity(i);
//                break;
//
//            case R.id.setting_page:
//                Intent h = new Intent(Store.this, SettingPage.class);
//                startActivity(h);
//                break;
//
//            case R.id.home_page:
//                Intent g = new Intent(Store.this, MainActivity.class);
//                startActivity(g);
//                break;
//        }
//
//        return true;
//    }
}