package com.example.knowyourgovernment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class AboutActivity extends AppCompatActivity {

    private static final String TAG = "AboutActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setTitle("Know Your Government");

        TextView apiInfoTV = (TextView) findViewById(R.id.aboutTitle3);
        apiInfoTV.setClickable(true);
        apiInfoTV.setMovementMethod(LinkMovementMethod.getInstance());
        String link = "<a href='https://developers.google.com/civic-information/'> Google Civic Information API </a>";
        apiInfoTV.setText(Html.fromHtml(link));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent clickedNoteIntent = new Intent();
                setResult(RESULT_OK, clickedNoteIntent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        Log.d(TAG, "onBackPressed: ");
        Intent clickedNoteIntent = new Intent();
        setResult(RESULT_OK, clickedNoteIntent);
        finish();
    }
}
