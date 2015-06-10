package com.apps.ruijorge.accelateamproject;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements SensorEventListener{

    TextView textView;
    List<Float> steps;
    float root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView)findViewById(R.id.textView);
        init();

        Button resetButton = (Button)findViewById(R.id.button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init();
            }
        });


        SensorManager sensorManager = (SensorManager)this.getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void init() {
        root = Float.parseFloat(PreferenceManager.getDefaultSharedPreferences(this).getString("root", "2.0"));
        ((TextView)findViewById(R.id.textView2)).setText("Acceleration square root threshold - "+root);
        textView.setText("");
        steps = new ArrayList<>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        float x, y, z, accelationSquareRoot;
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            x = values[0];
            y = values[1];
            z = values[2];
            float sum = x + y + z;
            accelationSquareRoot = (x * x + y * y + z * z) / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
            Log.i("sensor", accelationSquareRoot+"");
            if (accelationSquareRoot > root) {
                steps.add(accelationSquareRoot);
                updateTextView();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void updateTextView() {
        String stepsStr = "";
        for (float step: steps) {
            stepsStr += (steps.indexOf(step)+1) + ". " + Float.toString(step) + "\n";
        }
        textView.setText(stepsStr);
    }
}
