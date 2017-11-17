package com.example.user1.button;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    //private Button btnDo;
    private Button btnURL;
    private Uri KaohsiungBikeUri = Uri.parse("http://www.c-bike.com.tw/Station1.aspx");
    private ImageButton btnNow;
    private ImageButton btnSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {            //一開始就會執行(onCreate)
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        /*btnDo = (Button)findViewById(R.id.button1);*/
        /*btnDo.setOnClickListener(btnDoListener);*/

        btnURL = (Button)findViewById(R.id.ButtonToURL);
        btnURL.setOnClickListener(btnUrlListener);
        btnNow = (ImageButton)findViewById(R.id.ButtonToNowLocation);
        btnNow.setOnClickListener(btnNowListener);
        btnSearch = (ImageButton)findViewById(R.id.ButtonToSearchLocation);
        btnSearch.setOnClickListener(btnSearchListener);


    }
    private Button.OnClickListener btnNowListener =
            new Button.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intentNow = new Intent();
                    intentNow.setClass(MainActivity.this,NowLocation.class);
                    startActivity(intentNow);
                }
            };
    private Button.OnClickListener btnSearchListener =
            new Button.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intentSearch = new Intent();
                    intentSearch.setClass(MainActivity.this,SearchLocation.class);
                    startActivity(intentSearch);
                }
            };
    private Button.OnClickListener btnUrlListener =
            new Button.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent (Intent.ACTION_VIEW,KaohsiungBikeUri);
                    startActivity(intent);
                }
            };

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}


/*<android.support.design.widget.AppBarLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:theme="@style/AppTheme.AppBarOverlay">

        <android.support.v7.widget.Toolbar
            android:id="@+id/toolbar"
            android:layout_width="match_parent"
            android:layout_height="?attr/actionBarSize"
            android:background="?attr/colorPrimary"
            app:popupTheme="@style/AppTheme.PopupOverlay" />

    </android.support.design.widget.AppBarLayout>*/                //xml-toobar