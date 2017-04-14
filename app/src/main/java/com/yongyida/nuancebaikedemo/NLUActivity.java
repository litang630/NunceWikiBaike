package com.yongyida.nuancebaikedemo;

import com.yongyida.nauncebaike.baiketools.WikiContent;
import com.yongyida.nauncebaike.nuancetools.NuanceNLURec;
import com.yongyida.nauncebaike.nuancetools.NuanceNLURec.INluRecCallBack;
import com.yongyida.nauncebaike.nuancetools.NuanceNLURec.State;
import com.yongyida.nuancebaikedemo.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class NLUActivity extends Activity {
	private TextView textviewRecContent;
	private Button buttonRecognize;
	private WebView webviewWiki;
	private ProgressBar volumeBar;

	private String baikeLiteral;
	private String actionIntent;	
	
	private INluRecCallBack callBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nlu);

		textviewRecContent = (TextView) findViewById(R.id.textview_recognize_content);
		buttonRecognize = (Button) findViewById(R.id.button_recognize);
		webviewWiki = (WebView) findViewById(R.id.webView_wiki);
		volumeBar = (ProgressBar) findViewById(R.id.volume_bar);

		buttonRecognize.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				// TODO Auto-generated method stub
				NuanceNLURec myNuanceNLURec = new NuanceNLURec(callBack );
				myNuanceNLURec.init(getBaseContext());
			}
		});
		
		callBack = new INluRecCallBack() {
			
			@Override
			public void setVolumeBar(int process) {
				// TODO Auto-generated method stub
				volumeBar.setProgress(process);
			}
			
			@Override
			public void setState(State newState) {
				// TODO Auto-generated method stub
				switch (newState) {
				case IDLE:
					buttonRecognize.setText(getResources()
							.getString(R.string.recognize));
					break;
				case LISTENING:
					buttonRecognize.setText(getResources()
							.getString(R.string.listening));
					break;
				case PROCESSING:
					buttonRecognize.setText(getResources().getString(
							R.string.processing));
					break;
				}
			}
			
			@Override
			public void getNuanceResult(String[] result) {
				// TODO Auto-generated method stub
				actionIntent = result[0];
				baikeLiteral = result[1];
				textviewRecContent.setText(baikeLiteral);
				if(actionIntent.equals("BaiKe")){
					Log.i("tangli","****************************");
					
					webviewWiki.loadUrl("https://en.wikipedia.org/wiki/"+baikeLiteral);			
					webviewWiki.setWebViewClient(new WebViewClient(){
						@Override
						public boolean shouldOverrideUrlLoading(WebView view, String url) {
							// TODO Auto-generated method stub
							view.loadUrl(url);
							return true;
						}				
					});
					
					WikiContent mWikiContent = new WikiContent(baikeLiteral, getBaseContext());
					mWikiContent.sendRequestWithHttpURLConnection();
				}
			}
		};
	}
}
