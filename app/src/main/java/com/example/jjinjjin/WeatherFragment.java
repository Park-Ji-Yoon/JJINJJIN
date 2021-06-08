//package com.example.jjinjjin;
//
//import android.os.Bundle;
//
//import androidx.fragment.app.Fragment;
//
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//import jxl.Sheet;
//import jxl.Workbook;
//import jxl.read.biff.BiffException;
//
///**
// * A simple {@link Fragment} subclass.
// * Use the {@link WeatherFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
//public class WeatherFragment extends Fragment {
//
//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//
//    EditText location;
//    ImageButton search;
//    TextView result;
//
//    String result_x = "", result_y = "";
//
//    public WeatherFragment() {
//        // Required empty public constructor
//    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment WeatherFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static WeatherFragment newInstance(String param1, String param2) {
//        WeatherFragment fragment = new WeatherFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_weather, container, false);
//
//        location = getView().findViewById(R.id.location);
//        search = getView().findViewById(R.id.search);
//        result = getView().findViewById(R.id.result);
//
//        search.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(location.getText().toString().equals("")){
//                    Toast.makeText(container.getContext(), "학교 위치를 입력해주세요", Toast.LENGTH_SHORT).show();
//                }else{
//                    FindXY(location.getText().toString());
//                    getWeather(result_x, result_y);
//                }
//            }
//        });
//
//        return view;
//    }
//
//    private void FindXY(String location_txt) {
//        try{
//            Workbook wb = Workbook.getWorkbook(getActivity().getBaseContext().getResources().getAssets().open("location.xls"));
//
//            if(wb != null) {
//                Sheet sheet = wb.getSheet(0);
//                if (sheet != null) {
//                    int colTotal = sheet.getColumns();
//                    int rowTotal = sheet.getColumn(colTotal - 1).length;
//
//                    for (int row = 0; row < rowTotal; row++) {
//                        for (int col = 1; col < colTotal; col++) {
//                            if (location_txt.equals(sheet.getCell(col, row).getContents())) {
//                                result_x = sheet.getCell(3, row).getContents();
//                                result_y = sheet.getCell(4, row).getContents();
//                            }
//                        }
//                    }
//                    if (result_x == "" || result_y == "") {
//                        Toast.makeText(getContext(), "학교 위치를 다시 입력해주세요", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Log.d("시트 오류 : ", location_txt + "찾을 수 없음");
//                }
//            }else{
//                Log.d("엑셀 오류 : ", location_txt + "찾을 수 없음");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (BiffException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    public void  getWeather(String x, String y) {
//        final String url = "http://apis.data.go.kr/1360000/VilageFcstInfoService/getVilageFcst?serviceKey=FXg%2Ft9dxkuprU0w78fsjMQ6UuJka073zQlNU1CPrvzyR1ktiy4Pg7BbitEdn%2FwYeaVJ6oZmiJdgFuKeBVMqO2g%3D%3D&pageNo=1&numOfRows=62&dataType=JSON&base_date=";
//        String ymd = "";
//        final String url_end08 = "&base_time=2000&nx=" + x + "&ny=" + y;
//        final String url_end924 = "&base_time=0200&nx=" + x + "&ny=" + y;
//        String weatherUrl = "";
//
//        final int nowH = Integer.parseInt(new SimpleDateFormat("H").format(new Date(System.currentTimeMillis())));
//
//        if(nowH >= 0 && nowH <= 8){
//            ymd = new SimpleDateFormat("yyyyMMdd").format(new Date(new Date().getTime()+(1000*60*60*24*-1)));
//            weatherUrl = url + ymd + url_end08;
//        }else{
//            ymd = new SimpleDateFormat("yyyyMMdd").format(new Date());
//            weatherUrl = url + ymd + url_end924;
//        }
//
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, weatherUrl, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try{
//                    String output = "";
//                    String pop = "", reh = "", sky = "", t3h = "", tmn = "" ,tmx = "";
//                    /*
//                    강수확률% pop     습도% reh
//                    하늘상태 sky(1:맑음, 3:구름많음, 4:흐림)
//                    기온 t3h    최저 tmn    최고 tmx
//                     */
//
//                    JSONObject jsonResponse = new JSONObject(response);
//                    JSONArray jsonArray = jsonResponse.getJSONObject("response").getJSONObject("body")
//                            .getJSONObject("items").getJSONArray("item");
//
//                    switch (nowH){
//                        case 0: case 1: case 2:
//                            pop = jsonArray.getJSONObject(0).getString("fcstValue");
//                            reh = jsonArray.getJSONObject(3).getString("fcstValue");
//                            sky = jsonArray.getJSONObject(5).getString("fcstValue");
//                            t3h = jsonArray.getJSONObject(6).getString("fcstValue");
//                            tmn = jsonArray.getJSONObject(27).getString("fcstValue");
//                            tmx = jsonArray.getJSONObject(57).getString("fcstValue");
//                            break;
//
//                        case 3: case 4: case 5:
//                            pop = jsonArray.getJSONObject(11).getString("fcstValue");
//                            reh = jsonArray.getJSONObject(13).getString("fcstValue");
//                            sky = jsonArray.getJSONObject(14).getString("fcstValue");
//                            t3h = jsonArray.getJSONObject(15).getString("fcstValue");
//                            tmn = jsonArray.getJSONObject(27).getString("fcstValue");
//                            tmx = jsonArray.getJSONObject(57).getString("fcstValue");
//                            break;
//
//                        case 6: case 7: case 8:
//                            pop = jsonArray.getJSONObject(20).getString("fcstValue");
//                            reh = jsonArray.getJSONObject(23).getString("fcstValue");
//                            sky = jsonArray.getJSONObject(25).getString("fcstValue");
//                            t3h = jsonArray.getJSONObject(26).getString("fcstValue");
//                            tmn = jsonArray.getJSONObject(27).getString("fcstValue");
//                            tmx = jsonArray.getJSONObject(57).getString("fcstValue");
//                            break;
//
//                        case 9: case 10: case 11:
//                            pop = jsonArray.getJSONObject(12).getString("fcstValue");
//                            reh = jsonArray.getJSONObject(14).getString("fcstValue");
//                            sky = jsonArray.getJSONObject(15).getString("fcstValue");
//                            t3h = jsonArray.getJSONObject(16).getString("fcstValue");
//                            tmn = jsonArray.getJSONObject(7).getString("fcstValue");
//                            tmx = jsonArray.getJSONObject(37).getString("fcstValue");
//                            break;
//
//                        case 12: case 13: case 14:
//                            pop = jsonArray.getJSONObject(21).getString("fcstValue");
//                            reh = jsonArray.getJSONObject(24).getString("fcstValue");
//                            sky = jsonArray.getJSONObject(26).getString("fcstValue");
//                            t3h = jsonArray.getJSONObject(27).getString("fcstValue");
//                            tmn = jsonArray.getJSONObject(7).getString("fcstValue");
//                            tmx = jsonArray.getJSONObject(37).getString("fcstValue");
//                            break;
//
//                        case 15: case 16: case 17:
//                            pop = jsonArray.getJSONObject(32).getString("fcstValue");
//                            reh = jsonArray.getJSONObject(34).getString("fcstValue");
//                            sky = jsonArray.getJSONObject(35).getString("fcstValue");
//                            t3h = jsonArray.getJSONObject(36).getString("fcstValue");
//                            tmn = jsonArray.getJSONObject(7).getString("fcstValue");
//                            tmx = jsonArray.getJSONObject(37).getString("fcstValue");
//                            break;
//
//                        case 18: case 19: case 20:
//                            pop = jsonArray.getJSONObject(42).getString("fcstValue");
//                            reh = jsonArray.getJSONObject(45).getString("fcstValue");
//                            sky = jsonArray.getJSONObject(47).getString("fcstValue");
//                            t3h = jsonArray.getJSONObject(48).getString("fcstValue");
//                            tmn = jsonArray.getJSONObject(7).getString("fcstValue");
//                            tmx = jsonArray.getJSONObject(37).getString("fcstValue");
//                            break;
//
//                        case 21: case 22: case 23:
//                            pop = jsonArray.getJSONObject(53).getString("fcstValue");
//                            reh = jsonArray.getJSONObject(55).getString("fcstValue");
//                            sky = jsonArray.getJSONObject(56).getString("fcstValue");
//                            t3h = jsonArray.getJSONObject(57).getString("fcstValue");
//                            tmn = jsonArray.getJSONObject(7).getString("fcstValue");
//                            tmx = jsonArray.getJSONObject(37).getString("fcstValue");
//                            break;
//                    }
//
//                    switch (sky){
//                        case "1":
//                            sky = "맑음";
//                            break;
//                        case "3":
//                            sky = "구름 많음";
//                            break;
//                        case "4":
//                            sky = "흐림";
//                            break;
//                    }
//
//                    output = "강수 확률 : " + pop
//                            + "%\n습도 : " + reh
//                            + "%\n날씨 : " + sky
//                            + "\n기온 : " + t3h
//                            + "°C\n최저 기온 : " + tmn
//                            + "°C\n최고 기온 : " + tmx + "°C";
//                    Log.d("Log : ", output);
//                    result.setText(output);
//
//                }catch(JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
//            }
//        });
//        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
//        requestQueue.add(stringRequest);
//    }
//}