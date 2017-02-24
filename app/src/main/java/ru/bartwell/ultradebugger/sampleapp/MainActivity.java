package ru.bartwell.ultradebugger.sampleapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import ru.bartwell.ultradebugger.UltraDebugger;
import ru.bartwell.ultradebugger.module.logger.Logger;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Fill information about IP and port
        String ip = UltraDebugger.getIp();
        String address;
        if (TextUtils.isEmpty(ip)) {
            address = getString(R.string.unknown_ip_format, UltraDebugger.getPort());
        } else {
            address = getString(R.string.ip_format, ip, UltraDebugger.getPort());
        }
        ((TextView) findViewById(R.id.url)).setText(address);

        // Create and fill DB to show how to works SQLite module
        new DbHelper(this).getReadableDatabase();

        // Fill shared preferences for appropriate module
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString("string_value", "String value");
        editor.putInt("int_value", 123);
        editor.putBoolean("boolean_value", true);
        editor.commit();
    }

    // Method only for Reflection module
    public void showToast(String text) {
        Log.d(TAG, "showToast(): " + text);
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.saveValue(this, "RandomValue", new Random().nextInt(100));
        Logger.addLog(this, "onResume");
    }

    @Override
    protected void onPause() {
        Logger.addLog(this, "onPause");
        super.onPause();
    }
}
