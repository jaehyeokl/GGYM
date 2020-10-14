package project.jaehyeok.ggym;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class SearchAddressActivity extends AppCompatActivity {

    private TextView viewSearchAddress;
    private ImageButton buttonSearchAddress;
    private ListView searchAddressList;
    private TextView selectAddress;
    private TextView detailAddress;
    private TextView detailAddressMessage;
    private Button decideAddress;

    private String searchWord = "";

    private ArrayList<String> listData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_address);

        viewSearchAddress = findViewById(R.id.viewSearchAddress);
        buttonSearchAddress = findViewById(R.id.buttonSearchAddress);
        searchAddressList = findViewById(R.id.searchAddressList);
        selectAddress = findViewById(R.id.selectAddress);
        detailAddress = findViewById(R.id.detailAddress);
        detailAddressMessage = findViewById(R.id.detailAddressMessage);
        decideAddress = findViewById(R.id.decideAddress);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 주소검색
        // 카카오 로컬 API 에서 요청받는 스레드를 실행하며
        // 유저가 입력한 키워드를 스레드에서 파라미터로 사용한다
        buttonSearchAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (viewSearchAddress.getText() != null) {
                    // 유저 입력 있을때
                    searchWord = viewSearchAddress.getText().toString();

                    if (searchWord.length() > 0) {
                        // 카카오 API 에서 주소를 가져오는 스레드 실행
                        GetKakaoAddress getKakaoAddress = new GetKakaoAddress();
                        getKakaoAddress.start();
                    } else {
                        Toast.makeText(SearchAddressActivity.this, "주소를 입력해주세요", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SearchAddressActivity.this, "주소를 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 주소 선택
        // 검색 결과(리스트뷰의 Item)에서 유저가 클릭한 주소를 지정된 TextView 에 반영
        // 일반주소를 선택했을때 나머지 주소를 입력할 수 있는 EditText 와 Button 이 보이도록 한다
        searchAddressList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String clickItemAddress = listData.get(i);
                selectAddress.setText(clickItemAddress);
                detailAddress.setVisibility(View.VISIBLE);
                detailAddressMessage.setVisibility(View.VISIBLE);
                decideAddress.setVisibility(View.VISIBLE);
            }
        });

        // 주소 결정 완료
        // 일반주소와 나머지주소를 이전 액티비티 MakeGroup 에 전달한다
        decideAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mainAddress = selectAddress.getText().toString();
                String addAddress = detailAddress.getText().toString();

                Intent intent = new Intent();
                intent.putExtra("mainAddress", mainAddress);
                intent.putExtra("addAddress", addAddress);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    // 핸들러
    @SuppressLint("HandlerLeak")
    private Handler getCoronaDataHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {

            try {
                // 카카오 api 에서 전달받은 데이터에서 주소를 가져와 리스트뷰에 반영한다
                Bundle bundle = msg.getData();
                String searchAddressString = bundle.getString("searchAddressJson");
                JSONArray jsonArray = new JSONArray(searchAddressString);

                // 주소(address_name) 만 리스트에 저장한다
                listData = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    String addressName = jsonObject.getString("address_name");
                    listData.add(addressName);
                }

                // 리스트뷰
                ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, listData);
                searchAddressList.setAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    // 카카오 로컬 API - 주소검색
    class GetKakaoAddress extends Thread {
        public void run() {
            Handler handler = getCoronaDataHandler;
            Message message = handler.obtainMessage();

            // 한번 호출할때마다 최대 10개의 검색결과를 전달받을 수 있기때문에
            // 모든 검색결과를 저장하기 위해서는 페이지 수만큼 호출하여 데이터를 저장해야한다

            // 모든 페이지에서 호출한 검색결과를 저장하기 위해서 while 문 밖에 JSONArray 를 초기화
            int page = 1;
            JSONArray totalSearchAddress = new JSONArray();

            while (true) {

                try {
                    // 유저가 입력한 검색어를 필수파라미터로 전달받는다
                    String urlString = "https://dapi.kakao.com/v2/local/search/address.json?query="
                            + searchWord + "&page=" + page;
                    URL url = new URL(urlString);

                    HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                    urlConnection.setRequestProperty("Authorization", "KakaoAK 5b69b8285fbd1ab617bdcb6790cc746d");
                    urlConnection.setRequestMethod("GET");
                    // 응답 확인 코드, 메세지
                    Log.d("ResponseCode : " + urlConnection.getResponseCode(), "ResponseMessage : " + urlConnection.getResponseMessage());

                    // 전달 받은 데이터를 StringBuild 로 저장한다
                    InputStream responseData = urlConnection.getInputStream();
                    BufferedReader streamReader = new BufferedReader(new InputStreamReader(responseData, "UTF-8"));
                    StringBuilder responseBuilder = new StringBuilder();

                    String inputString;
                    while ((inputString = streamReader.readLine()) != null) {
                        responseBuilder.append(inputString);
                    }
                    //System.out.println("최종 스트링빌더 : " + responseBuilder);

                    // while 문 밖의 JSONArray 에 새로 저장한다
                    JSONObject getJsonPage = new JSONObject(responseBuilder.toString());
                    JSONArray getJsonData = getJsonPage.getJSONArray("documents");

                    for (int i = 0; i < getJsonData.length(); i++) {
                        JSONObject jsonObject = (JSONObject) getJsonData.get(i);

                        totalSearchAddress.put(jsonObject);
                    }

                    // 해당 페이지가 마지막 페이지일때 반복문을 종료한다
                    JSONObject metaJsonObject = getJsonPage.getJSONObject("meta");
                    boolean jsonIsEnd = metaJsonObject.getBoolean("is_end");
                    if (jsonIsEnd == true) {
                        break;
                    } else {
                        // 다음페이지 번호
                        page++;
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // json 데이터를 핸들러를 통해 메인스레드로 전달
            Bundle bundle = new Bundle();
            bundle.putString("searchAddressJson", totalSearchAddress.toString());
            message.setData(bundle);
            getCoronaDataHandler.sendMessage(message);
        }
    }
}