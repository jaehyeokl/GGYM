package project.jaehyeok.ggym;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GroupListActivity extends AppCompatActivity {

    private Button buttonJoinedGroup;
    private Button buttonSearchGroup;

    private EditText inputSearchGroup;
    private ImageView buttonGroupSearch;
    private View groupSearchBar;

    private RecyclerView joinedListRecyclerView;
    private RecyclerView searchListRecyclerView;
    private ListView relationKeywordListView;

    private ArrayList<JSONObject> joinedGroupList;
    private ArrayList<JSONObject> searchGroupList;

    private GroupListAdapter joinedGroupAdapter;
    private GroupListAdapter searchGroupAdapter;

    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        buttonJoinedGroup = findViewById(R.id.buttonJoinedGroup);
        buttonSearchGroup = findViewById(R.id.buttonSearchGroup);

        inputSearchGroup = findViewById(R.id.inputSearchGroup);
        buttonGroupSearch = findViewById(R.id.buttonGroupSearch);
        groupSearchBar = findViewById(R.id.groupSearchBar);

        boolean viewJoined = true;
        boolean viewSearch = false;

        Intent getIntent = getIntent();
        String userDataString = getIntent.getStringExtra("userData");
        try {
            JSONObject userDataJson = new JSONObject(userDataString);
            userId = userDataJson.getString("userId");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 리사이클러뷰의 데이터 ArrayList<JSONObject> 사용 이유
        // SharedPreferences 에 저장된 JSON 데이터를 사용함으로써 인텐트를 통해
        // 데이터 전달하기 쉽거나, 전달하지 않을 수 있다
        // 리사이클러뷰 데이터
        // 가입 그룹
        joinedGroupList = new ArrayList<>();
        try {
            joinedGroupList = groupDataToList(viewJoined);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // 그룹 탐색
        searchGroupList = new ArrayList<>();
        try {
            searchGroupList = groupDataToList(viewSearch);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //System.out.println("JSON확인 / " + groupList);

        // 리사이클러뷰
        // 가입 그룹
        joinedListRecyclerView = findViewById(R.id.recycleJoinedGroupList);
        joinedListRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        joinedListRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), 1));
        // 그룹 탐색
        searchListRecyclerView = findViewById(R.id.recycleGroupList);
        searchListRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        searchListRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), 1));

        //어댑터
        // 가입 그룹
        joinedGroupAdapter = new GroupListAdapter(joinedGroupList);
        joinedListRecyclerView.setAdapter(joinedGroupAdapter);
        // 그룹 탐색
        searchGroupAdapter = new GroupListAdapter(searchGroupList);
        searchListRecyclerView.setAdapter(searchGroupAdapter);


        // 검색어를 포함하는 그룹 보여주기 (리스트뷰)
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        relationKeywordListView = (ListView) findViewById(R.id.listRelationKeyword);
        relationKeywordListView.setAdapter(adapter);
        //List<String> searchGroupName = new ArrayList<>();

        for (JSONObject searchGroupData: searchGroupList) {
            try {
                String groupName = searchGroupData.getString("groupName");
                adapter.add(groupName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        inputSearchGroup.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               //relationKeywordListView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String filterText = editable.toString();
                if (filterText.length() > 0) {
                    relationKeywordListView.setVisibility(View.VISIBLE);
                    relationKeywordListView.setFilterText(filterText);
                } else {
                    //relationKeywordListView.clearTextFilter();
                    relationKeywordListView.setFilterText(" ");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 가입한 그룹 보기
        buttonJoinedGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputSearchGroup.setVisibility(View.INVISIBLE);
                buttonGroupSearch.setVisibility(View.INVISIBLE);
                groupSearchBar.setVisibility(View.INVISIBLE);
                searchListRecyclerView.setVisibility(View.INVISIBLE);
                relationKeywordListView.setVisibility(View.INVISIBLE);

                joinedListRecyclerView.setVisibility(View.VISIBLE);
            }
        });

        // 그룹 탐색
        buttonSearchGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputSearchGroup.setVisibility(View.VISIBLE);
                buttonGroupSearch.setVisibility(View.VISIBLE);
                groupSearchBar.setVisibility(View.VISIBLE);
                searchListRecyclerView.setVisibility(View.VISIBLE);
//                relationKeywordListView.setVisibility(View.VISIBLE);

                joinedListRecyclerView.setVisibility(View.INVISIBLE);
            }
        });

        // 가입 그룹 목록 아이템 클릭
        joinedGroupAdapter.setOnItemClickListener(new GroupScheduleAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {

                Intent intent = new Intent(getApplicationContext(), GroupInformationActivity.class);
                intent.putExtra("userId", userId);
                // 인텐트를 통해 해당 그룹데이터 JSONObject -> String 변환하여 전달
                intent.putExtra("groupJson", joinedGroupList.get(position).toString());
                intent.putExtra("selectPosition", position);
                // 가입중인 그룹에서는 액티비티에서 그룹 참가가 아닌 탈퇴버튼이 보여지도록
                // 하기 위한 조건 boolean 값을 전달한다
                intent.putExtra("setJoinedButton", true);
                // 같은 액티비티를 사용하면서
                startActivityForResult(intent, 5001);
            }
        });

        // 그룹 탐색 목록 아이템 클릭
        searchGroupAdapter.setOnItemClickListener(new GroupScheduleAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {

                Intent intent = new Intent(getApplicationContext(), GroupInformationActivity.class);
                intent.putExtra("userId", userId);
                // 인텐트를 통해 해당 그룹데이터 JSONObject -> String 변환하여 전달
                intent.putExtra("groupJson", searchGroupList.get(position).toString());
                intent.putExtra("selectPosition", position);
                startActivityForResult(intent, 5002);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 5001 && resultCode == RESULT_OK) {
            // 가입 그룹에서 탈퇴하기를 눌렀을때
            int outGroupPosition = data.getIntExtra("outGroupPosition", 1);

            searchGroupList.add(joinedGroupList.remove(outGroupPosition));

            joinedGroupAdapter.notifyDataSetChanged();
            searchGroupAdapter.notifyDataSetChanged();
        }

        if (requestCode == 5002 && resultCode == RESULT_OK) {
            // 그룹 탐색에서 가입하기를 눌렀을때
            int joinGroupPosition = data.getIntExtra("joinGroupPosition", 1);

            // 그룹 탐색목록에서 가입한 그룹을 제거, 이때 제거한 그룹을 가입 그룹목록에 추가
            // 그룹 가입 액티비티에서 가입완료 후 백버튼을 통해 현재 액티비티로 돌아왔을때 (onRestart)
            // 즉각적으로 변화된 데이터가 리사이클러뷰에 반영될 수 있도록 onActivityResult 에서 구현
            joinedGroupList.add(searchGroupList.remove(joinGroupPosition));

            joinedGroupAdapter.notifyDataSetChanged();
            searchGroupAdapter.notifyDataSetChanged();
        }
    }

    // JSON 의 GroupDataList(JSON) -> ArrayList 로 저장
    // 가입이 불필요한 그룹에(본인이 개설한 그룹, 또는 이미 가입한 그룹)
    // 데이터가 추가되는 것을 막기 위해서 위 조건의 그룹들을 목록에 나타내지 않도록 한다
    public ArrayList<JSONObject> groupDataToList(Boolean joinedOrSearch) throws JSONException {

        ArrayList<JSONObject> groupList = new ArrayList<>();
        SharedPreferences getGroupList = getSharedPreferences("saveGroupData", MODE_PRIVATE);
        String groupListString = getGroupList.getString("myGroupListJson", null);

        if (groupListString != null) {
            JSONArray groupListJson = new JSONArray(groupListString);
            int groupCount = groupListJson.length();

            for (int i = 0; i < groupCount; i++) {
                JSONObject groupJson = (JSONObject) groupListJson.get(i);
                String masterUserId = groupJson.getString("masterUserId");

                if (!masterUserId.equals(userId)) {
                    // 본인이 개설한 그룹 제외
                    JSONArray groupMemberList = groupJson.optJSONArray("groupMemberList");

                    if (joinedOrSearch) {
                        // 가입한 그룹데이터를 보여줘야할때
                        // 가입한 그룹만 찾기
                        if (groupMemberList != null) {
                            int memberListSize = groupMemberList.length();

                            for (int j = 0; j < memberListSize; j++) {
                                String joinedUserId = groupMemberList.get(j).toString();

                                if (joinedUserId.equals(userId)) {
                                    groupList.add(groupJson);
                                }
                            }
                        }
                    } else {
                        // 그룹 탐색을 선택했을때
                        // 가입된 그룹 제외
                        if (groupMemberList != null) {
                            int memberListSize = groupMemberList.length();
                            List<String> memberList = new ArrayList<>();

                            for (int j = 0; j < memberListSize; j++) {
                                String joinedUserId = groupMemberList.get(j).toString();

                                memberList.add(joinedUserId);
                            }

                            if (!memberList.contains(userId)) {
                                groupList.add(groupJson);
                            }
                        } else {
                            groupList.add(groupJson);
                        }
                    }
                }
            }
        }

        return groupList;
    }
}