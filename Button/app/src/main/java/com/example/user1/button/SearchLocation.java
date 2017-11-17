package com.example.user1.button;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.List;
import java.util.Locale;

import static com.example.user1.button.R.id.map;

public class SearchLocation extends FragmentActivity implements OnMapReadyCallback {
    static ArrayList<String> A = new ArrayList<String>();
    private GoogleMap mMap;
    private String address="高雄大學";            //地址  因該要改成使用者輸入
    double latitude=22.620397, longitude=120.311993;                   //四維行政中心x,y 座標　　　
    double km=2; //設定距離公里數
    float zoom=17;
    Button btnDo,btnDo1;
    EditText adr;
    //下拉式選單
    private Spinner spinner;
    private String[] list = {"0.5","1","1.5","2","2.5","3","3.5","4","4.5","5","5.5","6","6.5","7"};
    private ArrayAdapter<String> listAdapter;
    private Context mContext;
    boolean net;
    String Title,mx,my;
    //Button to Timer
    private Button btnTime1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        new Thread(runnable).start();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
        readFromFile();
        btnTime1 = (Button)findViewById(R.id.buttonTime1);
        btnTime1.setOnClickListener(btnT1);
        btnDo=(Button)findViewById(R.id.button2);
        btnDo1=(Button)findViewById(R.id.button4);
        btnDo.setOnClickListener(myListener);  // 設定偵聽查詢位址按鈕
        btnDo1.setOnClickListener(myListener2);  // 設定偵聽導航按鈕
        adr=(EditText)findViewById(R.id.edtLng);
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
                mMap.clear();
                addMarker();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                Toast.makeText(mContext, "您沒有選擇任何項目", Toast.LENGTH_LONG).show();
            }
        });
        //mMap.setOnMarkerClickListener(mMapListener);
    }
    private Button.OnClickListener btnT1 = new Button.OnClickListener(){
        public void onClick(View v){
            Intent intent = new Intent();
            intent.setClass(SearchLocation.this,Timer.class);
            startActivity(intent);
        }
    };
    private Button.OnClickListener myListener=new Button.OnClickListener(){
        public void onClick(View v){
            address= (adr.getText().toString());
            Toast.makeText(mContext, "查詢地名:"+address, Toast.LENGTH_LONG).show();
            geo();
            mMap.clear();
            addMarker();
        }
    };
    //按下導航按鈕
    private Button.OnClickListener myListener2=new Button.OnClickListener(){
        public void onClick(View v){
            getMap();
        }
    };
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // 一開始先定位到高雄大學
        LatLng nowlocation = new LatLng(latitude, longitude);
        //mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(nowlocation).title("目前位置"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nowlocation,zoom));
        mMap.setOnMarkerClickListener(mMapListener);
    }

    private void geo() {
        try {
            Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
            List<Address> addressLocation = geoCoder.getFromLocationName(address, 1);
            latitude = addressLocation.get(0).getLatitude();
            longitude = addressLocation.get(0).getLongitude();
            LatLng Point = new LatLng(latitude, longitude);
            zoom=15; //設定放大倍率1(地球)-21(街景)
            mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).position(Point).title("目前位置"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Point, zoom));
        }catch (Exception e){
            net=false;
        }

    }
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            addMarker();
        }
    };
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
    private void addMarker() {
        //z/ BitmapDescriptor icon =
        // BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher);
        if(net==false){}
            //demo就不顯示了
            //Toast.makeText(mContext, "請確認網路連線", Toast.LENGTH_SHORT).show();
        MarkerOptions markerOptions = new MarkerOptions();
        int i=0;

        while(i<=(A.size()-4)) {
            double x=Double.parseDouble(A.get(i+2)),y=Double.parseDouble(A.get(i+3));

            LatLng place = new LatLng(x, y);
            markerOptions.position(place)
                    .title(A.get(i))
                    .snippet(A.get(i+1));
            i=i+4;
            if(D_jw(latitude, longitude,x,y)>=km) continue;
            CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(SearchLocation.this);
            mMap.setInfoWindowAdapter(adapter);
            mMap.addMarker(markerOptions).showInfoWindow();
        }
        LatLng nowlocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(nowlocation).title("目前位置"));
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
        String f = String.valueOf(latitude)+","+String.valueOf(longitude);
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
