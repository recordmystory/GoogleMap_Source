package com.inhatc.googlemap_source;

import androidx.fragment.app.FragmentActivity;

import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.inhatc.googlemap_source.databinding.ActivityMapsBinding;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private String strServiceUrl, strServiceKey, strRouteId;
    private StringBuilder strUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

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

        strServiceUrl = "http://ws.bus.go.kr/api/rest/busRouteInfo/getStaionByRoute";
        strServiceKey = "HsRNXBqjnN5wPvLYWibtHMPTb0WvozkrwMOG6f3Ci%2Bwg4FlX5DhJ7EwAA%2F6qZr18U1HydLvkm8uKwhHZy8o7IQ%3D%3D";
        strRouteId = "100100063";

        strUrl = new StringBuilder("");
        strUrl.append(strServiceUrl);
        strUrl.append("?serviceKey="+strServiceKey);
        strUrl.append("&busRouteId="+strRouteId);

        System.out.println(strUrl);
        DownloadWebpageTask1 objTask = new DownloadWebpageTask1();
        objTask.execute(strUrl.toString());

        System.out.println("****************");
        System.out.println(strUrl.toString());
        //mDisplayBus(x,y,제목)



        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


    private class DownloadWebpageTask1 extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls){
            try{
                String strData = downloadUrl((String)urls[0]);
                return strData;
            }catch(IOException e){
                return "Fail download !";
            }
        }

        protected void onPostExecute(String result){
            String strHeaderCd = "";
            String strBusRouteId = "";
            String strBusRouteNo = "";

            boolean bSet_HeaderCd = false;
            boolean bSet_BusRouteId = false;
            boolean bSet_BusRouteNo = false;

            //objTV.append("======[ Rounte ID ]====\n");

            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(new StringReader(result));
                int eventType = xpp.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if(eventType == XmlPullParser.START_DOCUMENT) {
                        ;
                    } else if (eventType == XmlPullParser.START_TAG) {
                        String tag_name = xpp.getName();
                        if (tag_name.equals("headerCd")) bSet_HeaderCd = true;
                        if (tag_name.equals("busRouteId")) bSet_BusRouteId = true;
                        if (tag_name.equals("busRouteNm")) bSet_BusRouteNo = true;
                    } else if (eventType == XmlPullParser.TEXT) {
                        if (bSet_HeaderCd) {
                            strHeaderCd = xpp.getText();
                            //objTV.append("headerCd: " + strHeaderCd + "\n");
                            bSet_HeaderCd = false;
                        }
                        if (strHeaderCd.equals("0")) {
                            if (bSet_BusRouteId) {
                                strBusRouteId = xpp.getText();
                                //objTV.append("busRouteId: " + strBusRouteId + "\n");
                                bSet_BusRouteId = false;
                            }
                            if (bSet_BusRouteNo) {
                                strBusRouteNo = xpp.getText();
                                //objTV.append("busRouteNm : " + strBusRouteNo + "\n");
                                bSet_BusRouteNo = false;
                            }
                        }

                    } else if (eventType == XmlPullParser.END_TAG) {
                        ;
                    }
                    eventType = xpp.next();
                }


            } catch (Exception e) {
                //objTV.setText(e.getMessage());
                ;
            }

            //strServiceUrl = "http://ws.bus.go.kr/api/rest/buspos/getBusPosByRtid";
            //strServiceUrl = "http://ws.bus.go.kr/api/rest/busRouteInfo/getStaionByRoute";

            strUrl = new StringBuilder("");
            strUrl.append(strServiceUrl);
            strUrl.append("?ServiceKey=" + strServiceKey);
            strUrl.append("&busRouteId=" + strBusRouteId);

            DownloadWebpageTask2 objTask2 = new DownloadWebpageTask2();
            objTask2.execute(strUrl.toString());
        }


        private class DownloadWebpageTask2 extends DownloadWebpageTask1 {

            protected void onPostExecute(String result) {
                String strHeaderCd = "";
                String strGpsX = "";
                String strGpsY = "";
                String strPlainNo = "";

                boolean bSet_HeaderCd = false;
                boolean bSet_GpsX = false;
                boolean bSet_GpsY = false;
                boolean bSet_PlainNo = false;

                //objTV.append("=====[ Bus Position ]=====\n");

                try {
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(true);
                    XmlPullParser xpp = factory.newPullParser();

                    xpp.setInput(new StringReader(result));
                    int eventType = xpp.getEventType();

                    int nCount = 0;

                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        if (eventType == XmlPullParser.START_DOCUMENT) {
                            ;
                        } else if (eventType == XmlPullParser.START_TAG) {
                            String tag_name = xpp.getName();
                            if (tag_name.equals("headerCd")) bSet_HeaderCd = true;
                            if (tag_name.equals("gpsX")) bSet_GpsX = true;
                            if (tag_name.equals("gpsY")) bSet_GpsY = true;
                            //if (tag_name.equals("plainNo")) bSet_PlainNo = true;
                            if (tag_name.equals("stationNm")) bSet_PlainNo = true;

                        } else if (eventType == XmlPullParser.TEXT) {
                            if (bSet_HeaderCd) {
                                strHeaderCd = xpp.getText();
                                bSet_HeaderCd = false;
                            }
                            if (strHeaderCd.equals("0")) {
                                if (bSet_GpsX) {
                                    strGpsX = xpp.getText();
                                    //objTV.append("[" + nCount + "]" + "gpsX: " + strGpsX + "\n");
                                    bSet_GpsX = false;
                                }
                                if (bSet_GpsY) {
                                    strGpsY = xpp.getText();
                                    //objTV.append("[" + nCount + "]" + "gpsY: " + strGpsY + "\n");
                                    bSet_GpsY = false;
                                }
                                if (bSet_PlainNo) {
                                    strPlainNo = xpp.getText();
                                    //objTV.append("[" + nCount + "]" + "plainNo: " + strPlainNo + "\n");
                                    bSet_PlainNo = false;
                                    mDisplayBus(strGpsX, strGpsY, strPlainNo);
                                }
                            }
                        } else if (eventType == XmlPullParser.END_TAG) {
                            ;
                        }


                        eventType = xpp.next();
                    }
                } catch (Exception e) {
                    //objTV.setText(e.getMessage());
                    ;
                }

            }
        }
        private String downloadUrl(String myUrl) throws IOException{
            String strLine = null;
            String strPage="";

            HttpURLConnection urlConn = null;
            try{
                URL url = new URL(myUrl);
                urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setRequestMethod("GET");
                urlConn.setRequestProperty("Content-type", "application/json");

                BufferedReader bufReader;
                bufReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

                while ((strLine = bufReader.readLine()) != null){
                    strPage+=strLine;
                }
                return strPage;
            }finally{
                urlConn.disconnect();
            }
        }

        private void mDisplayBus(String gpsX, String gpsY, String plainNo) {

            double latitude;
            double longitude;
            //LatLng objLocation = null;

            latitude = Double.parseDouble(gpsY);
            longitude = Double.parseDouble(gpsX);
            LatLng objLocation = new LatLng(latitude, longitude);

            Marker objMK = mMap.addMarker(new MarkerOptions().position(objLocation).title(plainNo).icon(BitmapDescriptorFactory.fromResource(R.drawable.bus)));
            objMK.showInfoWindow();
            mMap.moveCamera(CameraUpdateFactory.newLatLng(objLocation));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(11));
        }


    }

}