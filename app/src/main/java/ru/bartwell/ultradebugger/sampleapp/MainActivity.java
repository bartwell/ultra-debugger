package ru.bartwell.ultradebugger.sampleapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import ru.bartwell.ultradebugger.UltraDebugger;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((TextView) findViewById(R.id.url)).setText(getString(R.string.ip_format, UltraDebugger.getPort()));
        new DbHelper(this).getReadableDatabase();
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString("string_value", "String value");
        editor.putInt("int_value", 123);
        editor.putBoolean("boolean_value", true);
        editor.commit();
    }

    public void showToast(String text) {
        Log.d(TAG, "showToast(): " + text);
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
