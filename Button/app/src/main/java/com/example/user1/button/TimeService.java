package com.example.user1.button;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import java.text.SimpleDateFormat;

public class TimeService extends Service {
    public TimeService() {
    }

    private Long startTime;
    //private MyBinder mBinder;
    private Handler handler = new Handler();
    private Context ctx;
    private int anHour = 3600;
    //private int timeee ;
    int count = 0;
    int y = 0;

    @Override
    public void onCreate() {
        handler.postDelayed(showTime, 1000);
        super.onCreate();
        ctx = this;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String date = sdf.format(new java.util.Date());
        Toast.makeText(ctx, "目前時間：   " + date + "     一小時後即開始收費", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(showTime);
        count = 0;
        //Toast.makeText(ctx,"Stop",Toast.LENGTH_SHORT).show();
        super.onDestroy();

    }

    //  public class R extends Binder {
    private Runnable showTime = new Runnable() {
        public void run() {
            //log目前時間
            //Log.i("time:", new Date().toString());
            //Toast.makeText(ctx, "time:"+new Date().toString(), Toast.LENGTH_SHORT).show();
            handler.postDelayed(this, 1000);
            count++;
            //int x = anHour - count;
            switch (count) {
                case 3000:
                    Toast.makeText(ctx, "再十分鐘就開始收費囉", Toast.LENGTH_SHORT).show();
                    break;
                case 20:
                    Toast.makeText(ctx, "再"+"5"+"分鐘就開始收費囉", Toast.LENGTH_SHORT).show();
                    break;
                case 10:
                    Toast.makeText(ctx, "目前已經經過了 "+count/3600+"："+count%3600/60+"："+count%60+" ", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
//}


    /* class MyBinder extends Binder {

         @Override
         protected boolean onTransact(int code, Parcel data, Parcel reply,
                                      int flags) throws RemoteException {
             int i =data.readInt();
             reply.writeInt(count);
             return super.onTransact(code, data, reply, flags);
         }
     }*/
    public class LocalBinder extends Binder {
        Long getInt(){
            startTime = System.currentTimeMillis();
            return  startTime;

        }
    }
    private final IBinder mBinder = new LocalBinder();


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        // Toast.makeText(ctx,"Start",Toast.LENGTH_SHORT).show();
        return mBinder;
        //return new Binder();
    }

}
