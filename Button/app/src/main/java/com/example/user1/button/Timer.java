package com.example.user1.button;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;

public class Timer extends AppCompatActivity {
    private Button btnTStart;
    private Button btnTStop;
    private Button btnTCheck;
    public  String date;
    private Long startTime ;
    private Handler handler = new Handler();
    private TimeService mService;
    private IBinder binder;
    private  TextView hh;
    private  TextView mm;
    private  TextView ss;
    private Button btnRe;
    private Context ctx;
    private Button btnInterest;
    int catchTime=0;
    Boolean ButtonClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        btnTStart = (Button) findViewById(R.id.ButtonTimeStart);
        btnTStart.setOnClickListener(btnTimeStartListener);
        btnTStop = (Button) findViewById(R.id.ButtonTimeStop);
        btnTStop.setOnClickListener(btnTimeStopListener);
        btnTCheck = (Button) findViewById(R.id.ButtonTimeCheck);
        btnTCheck.setOnClickListener(btnTimeCheckListener);
       // btnRe = (Button) findViewById(R.id.ButtonReload);
       // btnRe.setOnClickListener(btnReload);
        btnInterest = (Button) findViewById(R.id.interest);
        btnInterest.setOnClickListener(btnInteresting);

        hh = (TextView)findViewById(R.id.TextHour);
        mm = (TextView)findViewById(R.id.TextMinute);
        ss = (TextView)findViewById(R.id.TextSecond);

    }

    Runnable a = new Runnable() {
        @Override
        public void run() {
            Long spentTime = System.currentTimeMillis() - startTime;
            spentTime = spentTime / 1000;
            handler.postDelayed(this, 1000);
            hh.setText(spentTime / 3600 + ":");
            mm.setText((spentTime % 3600) / 60 + " :");
            ss.setText(spentTime % 60 + "s");
            catchTime++;
            //Toast.makeText(ctx, "Try "+catchTime, Toast.LENGTH_SHORT).show();
        }
    };
     private Button.OnClickListener btnInteresting =
             new Button.OnClickListener(){
                 @Override
                 public  void onClick(View v){
                     AlertDialog.Builder dialog4 = new AlertDialog.Builder(Timer.this);
                     dialog4.setTitle("費率");
                     dialog4.setMessage("106年1月1日起新租賃費率如下：\n00~30分鐘 ：   0元\n30~60分鐘 ：   5元\n60~90分鐘 ： 10元\n90~120分鐘 ： 20元\n120之後 每30分鐘 ： 20元");
                     dialog4.setNegativeButton("        ",new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface arg0, int arg1) {
                         }
                     });
                     dialog4.setPositiveButton("        ",new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface arg0, int arg1) {
                         }
                     });
                     dialog4.setNeutralButton("確認",new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface arg0, int arg1) {
                         }
                     });
                     dialog4.show();
                 }
     };
    private Button.OnClickListener btnTimeStartListener =
            new Button.OnClickListener(){
                @Override
                public void onClick(View v){
                    Intent intentTStart = new Intent(Timer.this,TimeService.class);
                    //startService(intentTStart);
                    bindService(intentTStart,conn,BIND_AUTO_CREATE);
                    SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");
                    date=sdf.format(new java.util.Date());
                    startTime = System.currentTimeMillis();
                    if (!ButtonClicked){
                        ButtonClicked = true;
                        a.run();}
                }
            };
    private Button.OnClickListener btnTimeStopListener =
            new Button.OnClickListener(){
                @Override
                public void onClick(View v){
                    Intent intentTStop = new Intent(Timer.this,TimeService.class);
                    //stopService(intentTStop);
                    unbindService(conn);
                    //unbindService(conn2);
                    handler.removeCallbacks(a);
                    ButtonClicked = false;
                }
            };
    /*private Button.OnClickListener btnReload =
            new Button.OnClickListener(){
                @Override
                public void onClick(View v){
                    //Intent intentRe = new Intent(Timer.this,TimeService.class);
                    // bindService(intentRe,conn2,BIND_AUTO_CREATE);
                    if (!ButtonClicked){
                        ButtonClicked = true;
                        a.run();}
                }
            };*/
    private Button.OnClickListener btnTimeCheckListener =
            new Button.OnClickListener(){
                @Override
                public void onClick(View v){AlertDialog.Builder dialog3 = new AlertDialog.Builder(Timer.this);
                    dialog3.setTitle("開始時間");
                    dialog3.setMessage(""+date);
                    dialog3.setNegativeButton("",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                    });
                    dialog3.setPositiveButton("",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                    });
                    dialog3.setNeutralButton("確認",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                    });
                    dialog3.show();
                }
            };

    ServiceConnection conn = new ServiceConnection(){
        //當如果調用上面的bindService去綁定service成功的話，就會去調用                                                 onServiceConnected
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Timer.this.binder = binder;
            startTime = ((TimeService.LocalBinder)service).getInt();
            //hh.setText("is " + startTime);

        }
        //當綁定service斷開時候，就會調用onServiceDisconnected
        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            //mService = null;
            mService = null;
        }

    };
    ServiceConnection conn2 = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //catchTime = ((TimeService.LocalBinder)service).getInt();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };




}
