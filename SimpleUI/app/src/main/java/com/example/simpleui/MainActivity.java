package com.example.simpleui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_MENU_ACTIVITY = 1;

    private EditText inputText;
    private CheckBox hideCheckBox;
    private ListView historyListView;
    private Spinner storeInfoSpinner;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private String menuResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main);
        storeInfoSpinner = (Spinner) findViewById(R.id.storeInfoSpinner);
        sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        inputText = (EditText) findViewById(R.id.inputText);
//        inputText.setText("1234");
        inputText.setText(sharedPreferences.getString("inputText", ""));
        inputText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        submit(v);
                        return true;
                    }
                }
                return false;
            }
        });

        hideCheckBox = (CheckBox) findViewById(R.id.hideCheckBox);
        hideCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("hideCheckBox", isChecked);
                editor.commit();
            }
        });
        hideCheckBox.setChecked(sharedPreferences.getBoolean("hideCheckBox", false));

        historyListView = (ListView) findViewById(R.id.historyListView);
        setHistory();
        setStoreInfo();

    }

    private void setStoreInfo() {
        ParseQuery<ParseObject> query =
          new ParseQuery<>("StoreInfo");
                query.findInBackground(new FindCallback<ParseObject>() {
             @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        String[] stores = new String[objects.size()];
                               for (int i = 0; i < stores.length; i++) {
                                   ParseObject object = objects.get(i);
                                   stores[i] = object.getString("name") + "," +
                                          object.getString("address");
                               }
                               ArrayAdapter<String> storeAdapter =
                                      new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, stores);
                               storeInfoSpinner.setAdapter(storeAdapter);
                           }
                 });
    }

    private void setHistory() {

        ParseQuery<ParseObject> query = new ParseQuery<>("Order");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                List<Map<String, String>> data = new ArrayList<>();

                for (int i = 0; i < objects.size(); i++) {
                    ParseObject object = objects.get(i);
                    String note = object.getString("note");
                    JSONArray array = object.getJSONArray("menu");

                    Map<String, String> item = new HashMap<>();
                    item.put("note", note);
                    item.put("drinkNum", "15");
                    item.put("storeInfo", "NTU Store");

                    data.add(item);
                }

                String[] from = {"note", "drinkNum", "storeInfo"};
                int[] to = {R.id.note, R.id.drinkNum, R.id.storeInfo};

                SimpleAdapter adapter = new SimpleAdapter(MainActivity.this,
                        data, R.layout.listview_item, from, to);

                historyListView.setAdapter(adapter);
            }
        });
    }

    /*
    {
        "note": "this is a note",
        "menu": [...]
    }
    */
    public void submit(View view) {
        String text = inputText.getText().toString();
        editor.putString("inputText", text);
        editor.commit();

        try {
            JSONObject orderData = new JSONObject();
            if (menuResult == null)
                menuResult = "[]";
            JSONArray array = new JSONArray(menuResult);
            orderData.put("note", text);
            orderData.put("menu", array);
            Utils.writeFile(this, "history.txt", orderData.toString() + "\n");

            ParseObject orderObject = new ParseObject("Order");
            orderObject.put("note", text);
            orderObject.put("menu", array);
            orderObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(MainActivity.this,
                                "[SaveCallback] ok", Toast.LENGTH_SHORT).show();
                    } else {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this,
                                "[SaveCallback] fail", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (hideCheckBox.isChecked()) {
            text = "**********";
            inputText.setText("***********");
        }
        setHistory();
    }

    public void goToMenu(View view) {
        Intent intent = new Intent();
        intent.setClass(this, DrinkMenuActivity.class);
        startActivityForResult(intent, REQUEST_CODE_MENU_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == REQUEST_CODE_MENU_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                menuResult = data.getStringExtra("result");
            }
        }
    }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
           getMenuInflater().inflate(R.menu.menu_main, menu);
           return true;
       }
   @Override
   public boolean onOptionsItemSelected(MenuItem item) {

   int id = item.getItemId();
    return super.onOptionsItemSelected(item);
   }



}
