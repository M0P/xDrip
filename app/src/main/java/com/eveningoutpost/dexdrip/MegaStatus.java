package com.eveningoutpost.dexdrip;

import static com.eveningoutpost.dexdrip.Home.get_master;
import static com.eveningoutpost.dexdrip.xdrip.gs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.eveningoutpost.dexdrip.models.JoH;
import com.eveningoutpost.dexdrip.utilitymodels.StatusItem;
import com.eveningoutpost.dexdrip.utils.ActivityWithMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MegaStatus extends ActivityWithMenu {

    private ListView listView;
    private final List<StatusItem> list = new ArrayList<>();
    private MegaStatusAdapter adapter;
    private Timer timer;

    @Override
    public String getMenuName() {
        return gs(R.string.system_status);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mega_status);
//        listView = (ListView) findViewById(R.id.mega_status_list_view);
//        adapter = new MegaStatusAdapter(this, list);
//        listView.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                updateList();
//            }
//        }, 10, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }


//    private void updateList() {
//        final List<StatusItem> newList = Home.getMegaStatus(get_master());
//        JoH.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                list.clear();
//                list.addAll(newList);
//                adapter.notifyDataSetChanged();
//            }
//        });
//    }


    private static class MegaStatusAdapter extends BaseAdapter {

        private final Context context;
        private final List<StatusItem> list;

        MegaStatusAdapter(Context context, List<StatusItem> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
//            if (convertView == null) {
////                convertView = LayoutInflater.from(context).inflate(R.layout.mega_status_item, parent, false);
//            }
//
//            StatusItem item = list.get(position);
//
////            TextView title = (TextView) convertView.findViewById(R.id.mega_status_item_title);
////            TextView value = (TextView) convertView.findViewById(R.id.mega_status_item_value);
//
//            title.setText(item.name);
//            value.setText(item.value);
//
//            if (item.highlight != null) {
//                value.setTextColor(item.highlight.color());
//            } else {
//                value.setTextColor(context.getResources().getColor(R.color.primary_text_default_material_dark));
//            }
//
//            if (item.onLongClick != null) {
//                convertView.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View v) {
//                        item.onLongClick.run();
//                        return true;
//                    }
//                });
//            } else {
//                convertView.setOnLongClickListener(null);
//            }

            return convertView;
        }
    }
}
