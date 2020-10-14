package project.jaehyeok.ggym;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class CurrentCoronaAdapter extends BaseAdapter {
    List<JSONObject> data;

    public CurrentCoronaAdapter(List<JSONObject> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        view = inflater.inflate(R.layout.item_current_corona, viewGroup, false);

        TextView location = view.findViewById(R.id.location);
        TextView confirmedCaseCount = view.findViewById(R.id.confirmedCaseCount);

        JSONObject locationCoronaJson = data.get(i);
        try {
            // 지역, 지역별 확진자 수 텍스트뷰에 초기화
            String locationName = locationCoronaJson.keys().next();
            String coronaCount = locationCoronaJson.getString(locationName);

            location.setText(locationName);
            confirmedCaseCount.setText(coronaCount);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }
}
