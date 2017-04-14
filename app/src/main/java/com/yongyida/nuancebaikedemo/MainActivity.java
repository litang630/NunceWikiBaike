package com.yongyida.nuancebaikedemo;

import com.yongyida.nuancebaikedemo.NLUActivity;
import com.yongyida.nuancebaikedemo.R;
import com.yongyida.nuancebaikedemo.TextNLUActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class MainActivity extends Activity {
	Button activity_asrnlu;
	Button activity_textnlu;
	Intent intent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        activity_asrnlu = (Button) findViewById(R.id.asr_nlu);
        activity_textnlu = (Button) findViewById(R.id.text_nlu);
        
        activity_asrnlu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 intent = new Intent(MainActivity.this, NLUActivity.class);
				 if(intent != null) {
					 startActivity(intent);
					 }
				 }
			}); 
        
        activity_textnlu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				intent = new Intent(MainActivity.this, TextNLUActivity.class);
				if(intent != null) {
					startActivity(intent);
					}
				}
			}); 
        
        
    }
}
