package com.yongyida.nuancebaikedemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.gson.Gson;
import com.nuance.speechkit.Interpretation;
import com.nuance.speechkit.Language;
import com.nuance.speechkit.Session;
import com.nuance.speechkit.Transaction;
import com.nuance.speechkit.TransactionException;
import com.yongyida.nauncebaike.baiketools.WikiContent;
import com.yongyida.nauncebaike.nuancetools.Configuration;
import com.yongyida.nauncebaike.nuancetools.NuanceBean;
import com.yongyida.nauncebaike.nuancetools.NuanceTextNLURec;
import com.yongyida.nauncebaike.nuancetools.NuanceTextNLURec.INluTextRecCallBack;
import com.yongyida.nauncebaike.nuancetools.NuanceTextNLURec.State;
import com.yongyida.nuancebaikedemo.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

public class TextNLUActivity extends Activity {
	EditText textInput;
	Button buttonRec;
	WebView webWikiCon;
	
	private String baikeLiteral;
    private String actionIntent;
    
    private INluTextRecCallBack textRecCallBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_text_nlu);
		
		textInput = (EditText) findViewById(R.id.edittext_recognize_content);
		buttonRec = (Button) findViewById(R.id.button_recognize_1);
		webWikiCon = (WebView) findViewById(R.id.webView_wiki_1);
		
		buttonRec.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				NuanceTextNLURec myNuanceTextNLURec = new NuanceTextNLURec(textRecCallBack, textInput.getText().toString());
				myNuanceTextNLURec.init(getBaseContext());
			}
		});
		
		textRecCallBack = new INluTextRecCallBack() {
			
			@Override
			public void setState(State newState) {
				// TODO Auto-generated method stub
				//state = newState;
				switch (newState) {
			      case IDLE:
			          buttonRec.setText(getResources().getString(R.string.recognize));
			          break;
			      case PROCESSING:
			      	buttonRec.setText(getResources().getString(R.string.processing));
			          break;
				}
			}
			
			@Override
			public void getNuanceResult(String[] result) {
				// TODO Auto-generated method stub
				//getNuanceResult(result);
				actionIntent = result[0];
				baikeLiteral = result[1];
				if(actionIntent.equals("BaiKe")){
					Log.i("tangli","****************************");
					
					webWikiCon.loadUrl("https://en.wikipedia.org/wiki/"+baikeLiteral);			
					webWikiCon.setWebViewClient(new WebViewClient(){
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
