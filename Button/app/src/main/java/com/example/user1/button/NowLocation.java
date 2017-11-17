package com.example.user1.button;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static com.example.user1.button.R.id.map;


public class NowLocation extends FragmentActivity implements LocationListener,OnMapReadyCallback {
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 4;
    static ArrayList<String> A = new ArrayList<String>();
    double X=22.620397,Y=120.311993;//四維行政中心x,y 座標　
    LatLng Point = new LatLng(22.620397,120.311993);//四維行政中心x,y 座標　
    GoogleMap gmap; //宣告 google map 物件
    float zoom=15;
    LocationManager locMgr;
    LatLng Taipei101 = new LatLng(22.620397,120.311993);//四維行政中心x,y 座標　
    String bestProv;
    Marker nowLocation_marker;
    boolean net;
    double km=2; //設定距離公里數
    Button btnDo1;
    //下拉式選單
    private Spinner spinner;
    private String[] list = {"0.5","1","1.5","2","2.5","3","3.5","4","4.5","5","5.5","6","6.5","7"};
    private ArrayAdapter<String> listAdapter;
    private Context mContext;
    private Button btnTime2;
    String Title,mx,my;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        nowLocation_marker = gmap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(Point).title("目前位置"));
        addMarker();
        gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(Taipei101, zoom));
        gmap.setOnMarkerClickListener(mMapListener);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_location);
        new Thread(runnable).start();
        // 取得元件
       SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);

        mapFragment.getMapAsync(this);
        btnDo1=(Button)findViewById(R.id.button3);
        btnDo1.setOnClickListener(myListener2);  // 設定偵聽導航按鈕
        btnTime2 = (Button)findViewById(R.id.buttonTime2);
        btnTime2.setOnClickListener(btnT2);
        readFromFile();
        locMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        bestProv = locMgr.getBestProvider(criteria, true);
        // 設定偵聽
        //下拉式選單
        mContext = this.getApplicationContext();
        spinner = (Spinner) findViewById(R.id.mySpinner);

        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(listAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Toast.makeText(mContext, "搜尋範圍變更:" + list[position], Toast.LENGTH_SHORT).show();
                km = Double.parseDouble(list[position]);
                gmap.clear();
                addMarker();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                Toast.makeText(mContext, "您沒有選擇任何項目", Toast.LENGTH_LONG).show();
            }
        });



    }


    private Button.OnClickListener myListener2=new Button.OnClickListener(){
        public void onClick(View v){
           getMap();
        }
    };

    private Button.OnClickListener btnT2 = new Button.OnClickListener(){
        public void onClick(View v){
            Intent intent = new Intent();
            intent.setClass(NowLocation.this,Timer.class);
            startActivity(intent);
        }
    };

    @Override
    public void onLocationChanged(Location location) {
        // 取得地圖座標值:緯度,經度
        X=location.getLatitude();
        Y=location.getLongitude();
        LatLng nowlocation = new LatLng(X, Y);
        Toast.makeText(this, "X:"+X+"Y:"+Y, Toast.LENGTH_LONG).show();
        //移掉前一個mark，重新定位
        nowLocation_marker.remove();
        nowLocation_marker = gmap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).position(nowlocation).title("目前位置"));

        addMarker();
        gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(nowlocation, zoom));
    }

    protected void onResume() {
        super.onResume();
        // 如果GPS或網路定位開啟，更新位置
        if (locMgr.isProviderEnabled(LocationManager.GPS_PROVIDER) || locMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //  確認 ACCESS_FINE_LOCATION 權限是否授權
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            else
            {
                //為了demo 1秒更新一次位置
                locMgr.requestLocationUpdates(bestProv, 1000, 1, this);
            }
        } else {
            Toast.makeText(this, "請開啟定位服務", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (permissions.length == 1 &&
                permissions[0] == android.Manifest.permission.ACCESS_FINE_LOCATION &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            //為了demo 1秒更新一次位置
            locMgr.requestLocationUpdates(bestProv, 1000, 1, this);
        } else {
            // Permission was denied. Display an error message.
        }
    }
    public static double D_jw(double wd1,double jd1,double wd2,double jd2)
    {
        double x,y,out;
        double PI=3.14159265;
        double R=6.371229*1e6;

        x=(jd2-jd1)*PI*R*Math.cos( ((wd1+wd2)/2) *PI/180)/180;
        y=(wd2-wd1)*PI*R/180;
        out=Math.hypot(x,y);
        return out/1000;
    }

    @Override
    protected void onPause() {
        super.onPause();
        //  確認 ACCESS_FINE_LOCATION 權限是否授權
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locMgr.removeUpdates(this);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Criteria criteria = new Criteria();
        bestProv = locMgr.getBestProvider(criteria, true);
    }

    @Override
    public void onProviderEnabled(String provider) {
    }
    @Override
    public void onProviderDisabled(String provider) {
    }
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(net==false)
                //demo這個就別顯示了，不好看
               //Toast.makeText(mContext, "請確認網路連線", Toast.LENGTH_SHORT).show();
            addMarker();
        }
    };
    private void addMarker() {
        //z/ BitmapDescriptor icon =
        // BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher);

        MarkerOptions markerOptions = new MarkerOptions();
        int i=0;

        while(i<=(A.size()-4)) {
            double x=Double.parseDouble(A.get(i+2)),y=Double.parseDouble(A.get(i+3));

            LatLng place = new LatLng(x, y);
            markerOptions.position(place)
                    .title(A.get(i))
                    .snippet(A.get(i+1));
            i=i+4;
            if(D_jw(X,Y,x,y)>=km)
            {
                continue;
            }
            CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(NowLocation.this);
            gmap.setInfoWindowAdapter(adapter);
            gmap.addMarker(markerOptions).showInfoWindow();
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                String url = "http://www.c-bike.com.tw/xml/stationlistopendata.aspx";
                Document xmlDoc = Jsoup.connect(url).ignoreContentType(true).get();
                Elements name = xmlDoc.select("StationName");
                Elements n1 = xmlDoc.select("StationNums1");
                Elements n2 = xmlDoc.select("StationNums2");
                Elements address = xmlDoc.select("StationAddress");
                Elements lat = xmlDoc.select("StationLat");
                Elements lon = xmlDoc.select("StationLon");
                for (int i = 0; ; i++) {
                    A.add(name.get(i).text());
                    A.add(address.get(i).text()+"\n"+"目前數量"+n1.get(i).text()+"\n"+"剩餘空位"+n2.get(i).text());
                    A.add(lat.get(i).text());
                    A.add(lon.get(i).text());

                    if (name.get(i).text().equals("夢時代站")) {

                        break;
                    }
                }
            }catch (Exception e){
                net=false;
            }
            handler.sendEmptyMessage(0);
        }
    };

    private void readFromFile() {
        try{
            InputStream is = getAssets().open("WriteFileTest.txt");
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));
            String s="",s1="",s2="";
            int i=0;
            while ((s =buf.readLine()) != null) {
                A.add(s);
                s =buf.readLine();
                s1 =buf.readLine();
                s2 =buf.readLine();
                A.add(s+"\n"+"目前數量"+s1+"\n"+"剩餘空位"+s2);
                s =buf.readLine();
                A.add(s);
                s =buf.readLine();
                A.add(s);
                s =buf.readLine();
            }
        }catch (Exception e){
            ;
        }
    }

    private GoogleMap.OnMarkerClickListener mMapListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            marker.showInfoWindow();
            Title=marker.getTitle();
            //Toast.makeText(getApplicationContext(),Title, Toast.LENGTH_LONG).show();
            getmarkposition();
            return true;
        }

    };
    public void getmarkposition() {
        int i=0;

        //String stringValue = Integer.toString(A.get(i).length());
        while (i <= (A.size() - 4)) {
            //Toast.makeText(getApplicationContext(),stringValue,Toast.LENGTH_SHORT).show();
            if(A.get(i).equals(Title)){
                mx=A.get(i+2);
                my=A.get(i+3);
                // Toast.makeText(getApplicationContext(),A.get(i+2),Toast.LENGTH_SHORT).show();
                // getMap();
                break;
            }
            i=i+4;
        }
    }

    protected void getMap() {
        String f = String.valueOf(X)+","+String.valueOf(Y);
        String d = mx+","+my;
        Intent i = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://ditu.google.cn/maps?f=d&source=s_d&saddr="+f+"&daddr="+d+"&hl=zh& t=m&"));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        i.setClassName("com.google.android.apps.maps",
                "com.google.android.maps.MapsActivity");
        startActivity(i);
    }

}