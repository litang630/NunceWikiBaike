package com.yongyida.nauncebaike.baiketools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.format.Time;
import android.util.Log;

import com.google.gson.Gson;
import com.nuance.speechkit.Interpretation;
import com.yongyida.nauncebaike.nuancetools.NuanceBean;

public class JsonParseTool {
	private Interpretation mInterpretion;
	private String actionIntent;
	private String baikeLiteral;

	public JsonParseTool(Interpretation interpertion) {
		this.mInterpretion = interpertion;
	}

	public String[] jsonParse() {
		String content[] = new String[2];
		try {
			// logs.append("\nonInterpretation: " +
			// interpretation.getResult().toString(2));
			Gson gson = new Gson();
			NuanceBean nuance = gson.fromJson(mInterpretion.getResult()
					.toString(), NuanceBean.class);

			JSONObject json = new JSONObject(mInterpretion.getResult()
					.toString());
			JSONArray array = json.getJSONArray("interpretations");
			json = array.getJSONObject(0);
			Log.i("tangli", "\ninterpretations: " + json.toString(2));

			content[0] = nuance.interpretations.get(0).action.intent.value;
			content[1] = nuance.interpretations.get(0).concepts.baike_content
					.get(0).literal;
			Time time = new Time("GMT+8");
			time.setToNow();
			Log.i("tangli", "\nonjsonparse   " + content[0] + "    "
					+ time.minute + ":" + time.second);
			Log.i("tangli", "\nonjsonparse   " + content[1] + "    "
					+ time.minute + ":" + time.second);
		} catch (JSONException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
			return null;
		}
		return content;
	}

	public String getActionIntent() {
		return actionIntent;
	}

	public String getBaikeLiteral() {
		return baikeLiteral;
	}

}
