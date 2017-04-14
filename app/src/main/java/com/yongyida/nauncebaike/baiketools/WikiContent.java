package com.yongyida.nauncebaike.baiketools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;

import com.yongyida.nauncebaike.nuancetools.NuanceTTS;
import com.yongyida.nuancebaikedemo.TTSAudioPlay;

public class WikiContent {
	private Context mContext;
	private String mWikiTitle;
	private String linkText;
	
	public static final int SHOW_RESPONSE = 0;
	private Document doc;
	
	public WikiContent(String wikiTitle, Context context){
		this.mWikiTitle = wikiTitle;
		this.mContext = context;
	}

		// 新建Handler的对象，在这里接收Message，然后更新TextView控件的内容
		private Handler handler1 = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case SHOW_RESPONSE:
					String response = (String) msg.obj;
					Element link = doc.select("p").first();
					linkText = link.text();

					Time time = new Time("GMT+8");
					time.setToNow();
					Log.i("tangli", "\nwikiMessage" + linkText + "    "
							+ time.minute + ":" + time.second);

					if (linkText != null) {
						NuanceTTS playResult = new NuanceTTS(linkText);
						playResult.initTTS(mContext);
					}

					break;

				default:
					break;
				}
			}

		};

		public void sendRequestWithHttpURLConnection() {
			// TODO Auto-generated method stub
			Thread getThread = new Thread() {
				public void run() {
					String result = null;
					HttpURLConnection urlConnection = null;
					try {
						// String website =
						// "https://en.wikipedia.org/wiki/"+baikeLiteral;
						// URL url = new
						// URL("https://en.wikipedia.org/wiki/natural_language_understanding");
						URL url = new URL("https://en.wikipedia.org/wiki/"
								+ mWikiTitle);
						urlConnection = (HttpURLConnection) url.openConnection();
						// 设置请求方法，默认是GET
						urlConnection.setRequestMethod("GET");
						// 设置字符集
						urlConnection.setRequestProperty("Charset", "UTF-8");
						if (urlConnection.getResponseCode() == 200) {
							Log.i("tangli",
									"urlConnection.getResponseCode() == 200");
							InputStream is = urlConnection.getInputStream();
							Log.i("tangli", "InputStream is =" + is);
							BufferedReader reader = new BufferedReader(
									new InputStreamReader(is));
							StringBuilder sb = new StringBuilder();
							String line = null;
							try {
								while ((line = reader.readLine()) != null) {
									sb.append(line + "/n");
								}
							} catch (IOException e) {
								e.printStackTrace();
							} finally {
								try {
									is.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							result = sb.toString();

						}

						doc = Jsoup.parse(result);

						// 在子线程中将Message对象发出去
						Message message = new Message();
						message.what = SHOW_RESPONSE;
						message.obj = result.toString();
						handler1.sendMessage(message);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						urlConnection.disconnect();
					}
				}
			};
			getThread.start();
		}

}
