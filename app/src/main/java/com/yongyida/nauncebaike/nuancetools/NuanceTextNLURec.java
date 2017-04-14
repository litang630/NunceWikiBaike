package com.yongyida.nauncebaike.nuancetools;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.nuance.speechkit.Interpretation;
import com.nuance.speechkit.Language;
import com.nuance.speechkit.Session;
import com.nuance.speechkit.Transaction;
import com.nuance.speechkit.TransactionException;
import com.yongyida.nauncebaike.baiketools.JsonParseTool;

public class NuanceTextNLURec {
	private Session speechSession;
    private State state = State.IDLE;
    public static final int SHOW_RESPONSE = 0;
    
    private Context mcontext;
	private INluTextRecCallBack mCallBack;
	
	private String recContent;
    
    public NuanceTextNLURec(INluTextRecCallBack callBack, String recContent) {
		this.mCallBack = callBack;
		this.recContent = recContent;
	}
    
    public void init(Context context) {
		this.mcontext = context;

		speechSession = Session.Factory.session(mcontext, Configuration.SERVER_URI, Configuration.APP_KEY);
        //setState(State.IDLE);
		state = State.IDLE;
		if(mCallBack != null){
			mCallBack.setState(State.IDLE);
		}

        switch (state) {
        case IDLE:
            recognize();
            break;
        case PROCESSING:
            break;
        }
	}
    
    private void recognize() {
		// TODO Auto-generated method stub
    	if (recContent.length() > 0) {
            //Setup our Reco transaction options.
            Transaction.Options options = new Transaction.Options();
            options.setLanguage(new Language(Configuration.LANGUAGE_CODE));

            //Add properties to appServerData for use with custom service. Leave empty for use with NLU.
            JSONObject appServerData = new JSONObject();
            try {
                appServerData.put("message", recContent);

                speechSession.transactionWithService(Configuration.CONTEXT_TAG, appServerData, options, recoListener);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            //setState(State.PROCESSING);
            state = State.PROCESSING;
    		if(mCallBack != null){
    			mCallBack.setState(State.PROCESSING);
    		}
        }
        else {
            Log.i("tangli","\n" +"nothing input");
        }
	}
    
    private Transaction.Listener recoListener = new Transaction.Listener() {
        @Override
        public void onServiceResponse(Transaction transaction, JSONObject response) {
            try {
                // 2 spaces for tabulations.
            	Log.i("tangli","\nonServiceResponse: " + response.toString(2));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onInterpretation(Transaction transaction, Interpretation interpretation) {
        	JsonParseTool jsonParse = new JsonParseTool(interpretation);
			String[] content = jsonParse.jsonParse();
			if (content != null && mCallBack != null) {
				mCallBack.getNuanceResult(content);
			}
        }

		@Override
        public void onSuccess(Transaction transaction, String s) {
			Log.i("tangli","\nonSuccess");

            //Notification of a successful transaction. Nothing to do here.
            //setState(State.IDLE);
			state = State.IDLE;
			if(mCallBack != null){
				mCallBack.setState(State.IDLE);
			}
        }

        @Override
        public void onError(Transaction transaction, String s, TransactionException e) {
        	Log.i("tangli","\nonError: " + e.getMessage() + s==null?"":(". " + s));

            //Something went wrong. Check Configuration.java to ensure that your settings are correct.
            //The user could also be offline, so be sure to handle this case appropriately.
            //setState(State.IDLE);
        	state = State.IDLE;
    		if(mCallBack != null){
    			mCallBack.setState(State.IDLE);
    		}
        }
    };

	public enum State {
        IDLE,
        PROCESSING
    }
    
    public interface INluTextRecCallBack {
		public void setState(State state);
		public void getNuanceResult(String result[]);
	}

}
