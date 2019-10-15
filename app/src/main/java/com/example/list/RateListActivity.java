package com.example.list;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RateListActivity extends ListActivity implements Runnable, AdapterView.OnItemClickListener {

    Handler handler;
    private String TAG = "mylist2";
    private ArrayList<HashMap<String,String>> listItems;
    SimpleAdapter listItemsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*
        setContentView(R.layout.activity_main);

*/

        Thread t = new Thread(this);
        t.start();

        handler = new Handler(){
            public void handleMessage(Message msg){
                if(msg.what == 7){
                    /*List<String> list2 = (List<String>) msg.obj;
                    ListAdapter adapter = new ArrayAdapter<String>(
                            RateListActivity.this,
                            android.R.layout.simple_list_item_1,
                            list2);

                    setListAdapter(adapter);*/
                    listItems = (ArrayList<HashMap<String, String>>)msg.obj;
                    listItemsAdapter = new SimpleAdapter(RateListActivity.this,
                            listItems,
                            R.layout.list_item,
                            new String[] {"ItemTitle", "ItemDetail"},
                            new int[] {R.id.itemTitle,R.id.itemDetail});
                    setListAdapter(listItemsAdapter);
                }
                super.handleMessage(msg);
            }
        };

        getListView().setOnItemClickListener(this);



        /*List<String> list1 = new ArrayList<String>();
        for(int i=1;i<100;i++){
            list1.add("item" + i);
        }

        String[] list_data = {"one","two","three","four"};
        ListAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list_data);
        setListAdapter(adapter);*/
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Log.i(TAG, "onItemClick: parent=" + parent);
        Log.i(TAG, "onItemClick: view=" + view);
        Log.i(TAG, "onItemClick: position=" + position);
        Log.i(TAG, "onItemClick: id=" + id);
        Object itemAtPosition = getListView().getItemAtPosition(position);
        HashMap<String,String> map = (HashMap<String, String>)itemAtPosition;
        String titleStr = map.get("ItemTitle");
        String detailStr = map.get("ItemDetail");
        Log.i(TAG, "onItemClick: titleStr=" + titleStr);
        Log.i(TAG, "onItemClick: detailStr=" + detailStr);

        TextView title = (TextView)view.findViewById(R.id.itemTitle);
        TextView detail = (TextView)view.findViewById(R.id.itemDetail);
        String title2 = String.valueOf(title.getText());
        String detail2 = String.valueOf(detail.getText());
        Log.i(TAG, "onItemClick: title2=" + title2);
        Log.i(TAG, "onItemClick: detail2=" + detail2);

        //打开新的页面，传入参数
        Intent rateCalc = new Intent(this,RateCalcActivity.class);
        rateCalc.putExtra("title",titleStr);
        rateCalc.putExtra("rate",Float.parseFloat(detailStr));
        startActivity(rateCalc);


    }

    public void run(){
        Log.i(TAG, "run:");

        Message msg =handler.obtainMessage();
        msg.what = 7;

        try {

            String url = "http://www.usd-cny.com/bankofchina.htm";
            Document doc = Jsoup.connect(url).get();
            Log.i(TAG, "run:" + doc.title());
            Elements tables = doc.getElementsByTag("table");

            Element table6 = tables.get(0);

            Elements tds = table6.getElementsByTag("td");
            /*List<String> list = new ArrayList<String>();*/
            ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String, String>>();
            for (int i = 0; i < tds.size(); i += 6) {
                Element td1 = tds.get(i);
                Element td2 = tds.get(i + 5);

                String str1 = td1.text();
                String val = td2.text();
                Log.i(TAG, "run:" + str1 + "==>" + val);

                /*list.add(str1 + "==>" + val);*/
                HashMap<String,String> map = new HashMap<String, String>();
                map.put("ItemTitle",str1);
                map.put("ItemDetail",val);
                list.add(map);


            }

            /*msg.obj = list;*/
            msg.obj = list;
            handler.sendMessage(msg);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
