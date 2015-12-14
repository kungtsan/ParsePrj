package com.example.simpleui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DrinkMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_menu);
    }

    public void add(View view) {
        Button button = (Button) view;
        int number = Integer.parseInt(button.getText().toString());
        number++;
        button.setText(String.valueOf(number));
    }

    public void done(View view) {
        JSONArray jsonData = getData();
        Intent data = new Intent();
        data.putExtra("result", jsonData.toString());
        setResult(RESULT_OK, data);
        finish();
    }

        /*

[
    {"name": "black tea", "l": 2, "m": 0},
    {"name": "milk tea", "l": 10, "m":3},
    {"name": "green tea", "l": 5, "m": 3}
]
    * */

    public JSONArray getData() {
        LinearLayout rootLinearLayout =
                (LinearLayout) findViewById(R.id.root);
        int count = rootLinearLayout.getChildCount();

        JSONArray array = new JSONArray();

        for (int i = 0 ; i < count - 1; i++) {
            LinearLayout ll = (LinearLayout)
                    rootLinearLayout.getChildAt(i);

            TextView drinkNameTextView = (TextView) ll.getChildAt(0);
            Button lButton = (Button) ll.getChildAt(1);
            Button mButton = (Button) ll.getChildAt(2);

            String drinkName = drinkNameTextView.getText().toString();
            int lNumber = Integer.parseInt(lButton.getText().toString());
            int mNumber = Integer.parseInt(mButton.getText().toString());

            try {
                JSONObject object = new JSONObject();
                object.put("name", drinkName);
                object.put("l", lNumber);
                object.put("m", mNumber);
                array.put(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return array;

    }
}
