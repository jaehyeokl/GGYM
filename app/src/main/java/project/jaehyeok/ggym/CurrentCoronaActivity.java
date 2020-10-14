package project.jaehyeok.ggym;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class CurrentCoronaActivity extends AppCompatActivity {

    ListView listViewCurrentCorona;
    TextView totalCoronaCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_corona);

        listViewCurrentCorona = findViewById(R.id.listViewCurrentCorona);
        totalCoronaCount = findViewById(R.id.totalCoronaCount);

        // 공공데이터포털에서 코로나 확진자 현황 불러오는 스레드
        GetCurrentCorona getCurrentCorona = new GetCurrentCorona();
        getCurrentCorona.start();
    }

    @SuppressLint("HandlerLeak")
    private Handler getCoronaDataHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {

            Bundle bundle = msg.getData();
            String currentCoronaData = bundle.getString("currentCoronaData");
            System.out.println("데이터 수신 체크" + currentCoronaData);
            try {
                // 공공데이터 스레드를 통해 전달받은 JSONArray 데이터를
                // 리스트뷰의 아이템에서 사용하기 위해서 List 에 저장한다
                JSONArray currentCoronaJson = new JSONArray(currentCoronaData);
                List<JSONObject> currentCoronaList = new ArrayList<>();

                int listLength = currentCoronaJson.length();
                for (int i = 0; i < listLength; i++) {
                    JSONObject locationCoronaJson = (JSONObject) currentCoronaJson.get(i);
                    currentCoronaList.add(locationCoronaJson);
                }
                // 불필요한 가장 첫번째 데이터를 삭제하고
                // 인구가 많은 광역시 순서대로 정렬하기 위해 리스트를 역순으로 정렬한다
                currentCoronaList.remove(0);
                Collections.reverse(currentCoronaList);

                // 전체 확진자 수는 리스트뷰 데이터에서 제외, 별도의 TextView 에 표기
                JSONObject totalCoronaJson = currentCoronaList.remove(0);
                String totalCoronaKey = totalCoronaJson.keys().next();
                String totalCorona = totalCoronaJson.getString(totalCoronaKey);
                totalCoronaCount.setText(totalCorona);

                // 어댑터 생성, 리스트뷰에 어댑터 연결
                CurrentCoronaAdapter adapter = new CurrentCoronaAdapter(currentCoronaList);
                listViewCurrentCorona.setAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    // 공공데이터포털에서 지역별 코로나 현황 데이터를 받아오는 스레드
    class GetCurrentCorona extends Thread {

        public void run() {
            Handler handler  = getCoronaDataHandler;
            Message message = handler.obtainMessage();

            Calendar cal = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

            // 오늘 날짜를 코로나Api 의 파라미터 입력 형식에 맞추어 저장
            String currentDate = dateFormat.format(cal.getTime());
            String serviceKey = "KVMSTiIk9%2BEe%2BFnmAYhGTxlrXWqRJqbYxtJRMqjEN4MsX0yAg107V81jCQ%2FAVczyJLxPINgo1cgDm6u0xECQdg%3D%3D";
            String urlString = "http://openapi.data.go.kr/openapi/service/rest/Covid19/getCovid19SidoInfStateJson?serviceKey="
                    + serviceKey + "&pageNo=1&numOfRows=10&startCreateDt=" + currentDate + "&endCreateDt=" + currentDate;

            try {
                URL url = new URL(urlString);
                // API level 28 부터 안드로이드에서 http 접근을 허용하지 않기 때문에
                // Manifest 에서 [android:usesCleartextTraffic="true"] 하여 http 접근을 허용하도록 한다
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                // 응답 확인 코드, 메세지
                Log.d("ResponseCode : " + urlConnection.getResponseCode(), "ResponseMessage : " + urlConnection.getResponseMessage());

                // 공공데이터 포털에서 요청받은 데이터
                InputStream responseData = urlConnection.getInputStream();

                // Xml 파서
                XmlPullParserFactory xmlParserFactory = XmlPullParserFactory.newInstance();
                XmlPullParser xmlParser = xmlParserFactory.newPullParser();
                xmlParser.setInput(responseData, "UTF-8");

                // Xml 파싱
                // 도시명과 해당 도시의 추가 확진자 수를 JSONObject 에 저장한다
                JSONArray currentCoronaJson = new JSONArray();

                boolean isItemTag = false;
                String tagName = "";
                String location = "";
                String confirmedCaseCount = "";
                int eventType = xmlParser.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {

                    if (eventType == XmlPullParser.START_DOCUMENT) {

                    } else if (eventType == XmlPullParser.START_TAG) {

                        tagName = xmlParser.getName();
                        if (tagName.equals("item")) {
                            isItemTag = true;
                        }

                    } else if (eventType == XmlPullParser.TEXT) {
                        // 지역명
                        if (isItemTag && tagName.equals("gubun")) {
                            location = xmlParser.getText();
                        }
                        // 지역의 확진자 수
                        if (isItemTag && tagName.equals("localOccCnt")) {
                            confirmedCaseCount = xmlParser.getText();
                        }

                    } else if (eventType == XmlPullParser.END_TAG) {
                        tagName = xmlParser.getName();

                        if (tagName.equals("item")) {
                            JSONObject locationCoronaData = new JSONObject();
                            // JSONObject 에 지역별 확진자 수 저장
                            // 확진자수가 1명이상일때 리스트뷰에 보여주기위해 0명일때는 저장하지 않는다
                            if (!confirmedCaseCount.equals("0")) {
                                locationCoronaData.put(location, confirmedCaseCount);
                                currentCoronaJson.put(locationCoronaData);
                            }
                            //Log.d("구분 : " + location, "지역발생 : " + confirmedCaseCount);

                            isItemTag = false;
                            location = "";
                            confirmedCaseCount = "";
                        }
                    }

                    eventType = xmlParser.next();
                }
                //System.out.println(locationCoronaData);

                // 파싱한 지역, 지역별 확진자 수를 key : value 형태로 JSONObject 로 저장하여
                // 메인스레드로 데이터를 전달한다 sendMessage()
                Bundle bundle = new Bundle();
                bundle.putString("currentCoronaData" ,currentCoronaJson.toString());
                message.setData(bundle);
                getCoronaDataHandler.sendMessage(message);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}