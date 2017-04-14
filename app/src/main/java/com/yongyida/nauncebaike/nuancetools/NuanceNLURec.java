package com.yongyida.nauncebaike.nuancetools;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import com.nuance.speechkit.Audio;
import com.nuance.speechkit.DetectionType;
import com.nuance.speechkit.Interpretation;
import com.nuance.speechkit.Language;
import com.nuance.speechkit.Recognition;
import com.nuance.speechkit.Session;
import com.nuance.speechkit.Transaction;
import com.nuance.speechkit.TransactionException;
import com.yongyida.nauncebaike.baiketools.JsonParseTool;
import com.yongyida.nuancebaikedemo.R;

public class NuanceNLURec {
	private Audio startEarcon;
	private Audio stopEarcon;
	private Audio errorEarcon;
	private Session speechSession;
	private Transaction recoTransaction;
	private State state = State.IDLE;
	private Context mcontext;
	private INluRecCallBack mCallBack;

	public NuanceNLURec(INluRecCallBack callBack) {
		this.mCallBack = callBack;
	}

	public void init(Context context) {
		this.mcontext = context;

		// Create a session
		speechSession = Session.Factory.session(context,
				Configuration.SERVER_URI, Configuration.APP_KEY);
		loadEarcons();

		switch (state) {
		case IDLE:
			recognize();
			break;
		case LISTENING:
			stopRecording();
			break;
		case PROCESSING:
			cancel();
			break;
		}
	}

	/**
	 * Stop recording the user
	 */
	private void stopRecording() {
		recoTransaction.stopRecording();
	}

	/**
	 * Cancel the Reco transaction. This will only cancel if we have not
	 * received a response from the server yet.
	 */
	private void cancel() {
		recoTransaction.cancel();
	}

	protected void recognize() {
		// TODO Auto-generated method stub
		// Setup our Reco transaction options.
		Transaction.Options options = new Transaction.Options();
		options.setDetection(DetectionType.Short);
		options.setLanguage(new Language(Configuration.LANGUAGE_CODE));
		options.setEarcons(startEarcon, stopEarcon, errorEarcon, null);

		// Add properties to appServerData for use with custom service. Leave
		// empty for use with NLU.
		JSONObject appServerData = new JSONObject();
		// Start listening
		recoTransaction = speechSession
				.recognizeWithService(Configuration.CONTEXT_TAG, appServerData,
						options, recoListener);
	}

	private Transaction.Listener recoListener = new Transaction.Listener() {
		@Override
		public void onStartedRecording(Transaction transaction) {
			Log.i("tangli", "\nonStartedRecording");

			// We have started recording the users voice.
			// We should update our state and start polling their volume.
			// setState(State.LISTENING);
			state = State.LISTENING;
			if (mCallBack != null) {
				mCallBack.setState(State.LISTENING);
			}
			startAudioLevelPoll();
		}

		@Override
		public void onFinishedRecording(Transaction transaction) {
			Log.i("tangli", "\nonFinishedRecording");

			// We have finished recording the users voice.
			// We should update our state and stop polling their volume.
			// setState(State.PROCESSING);
			state = State.PROCESSING;
			if (mCallBack != null) {
				mCallBack.setState(State.PROCESSING);
			}
			stopAudioLevelPoll();
		}

		@Override
		public void onServiceResponse(Transaction transaction,
				org.json.JSONObject response) {
			try {
				// 2 spaces for tabulations.
				Log.i("tangli", "\nonServiceResponse: " + response.toString(2));
			} catch (JSONException e) {
				e.printStackTrace();
			}

			// We have received a service response. In this case it is our NLU
			// result.
			// Note: this will only happen if you are doing NLU (or using a
			// service)
			// setState(State.IDLE);
			state = State.IDLE;
			if (mCallBack != null) {
				mCallBack.setState(State.IDLE);
			}
		}

		@Override
		public void onRecognition(Transaction transaction,
				Recognition recognition) {
			Log.i("tangli", "\nonRecognition: " + recognition.getText());

			// We have received a transcription of the users voice from the
			// server.
			// setState(State.IDLE);
			state = State.IDLE;
			if (mCallBack != null) {
				mCallBack.setState(State.IDLE);
			}
		}

		@Override
		public void onInterpretation(Transaction transaction,
				Interpretation interpretation) {
			JsonParseTool jsonParse = new JsonParseTool(interpretation);
			String[] content = jsonParse.jsonParse();
			if (content != null && mCallBack != null) {
				mCallBack.getNuanceResult(content);
			}

		}

		@Override
		public void onSuccess(Transaction transaction, String s) {
			Log.i("tangli", "\nonSuccess");

			// Notification of a successful transaction. Nothing to do here.
		}

		@Override
		public void onError(Transaction transaction, String s,
				TransactionException e) {
			Log.i("tangli", "\nonError: " + e.getMessage() + ". " + s);

			// Something went wrong. Check Configuration.java to ensure that
			// your settings are correct.
			// The user could also be offline, so be sure to handle this case
			// appropriately.
			// We will simply reset to the idle state.
			// setState(State.IDLE);
			state = State.IDLE;
			if (mCallBack != null) {
				mCallBack.setState(State.IDLE);
			}
		}
	};

	/* Audio Level Polling */

	private Handler handler = new Handler();

	/**
	 * Every 50 milliseconds we should update the volume meter in our UI.
	 */
	private Runnable audioPoller = new Runnable() {
		@Override
		public void run() {
			float level = recoTransaction.getAudioLevel();
			// volumeBar.setProgress((int)level);
			if (mCallBack != null) {
				mCallBack.setVolumeBar((int) level);
			}
			handler.postDelayed(audioPoller, 50);
		}
	};

	/**
	 * Start polling the users audio level.
	 */
	private void startAudioLevelPoll() {
		audioPoller.run();
	}

	/**
	 * Stop polling the users audio level.
	 */
	private void stopAudioLevelPoll() {
		handler.removeCallbacks(audioPoller);
		if (mCallBack != null) {
			mCallBack.setVolumeBar(0);
		}
		// volumeBar.setProgress(0);
	}

	/* Earcons */

	private void loadEarcons() {
		// Load all of the earcons from disk
		startEarcon = new Audio(mcontext, R.raw.sk_start,
				Configuration.PCM_FORMAT);
		stopEarcon = new Audio(mcontext, R.raw.sk_stop,
				Configuration.PCM_FORMAT);
		errorEarcon = new Audio(mcontext, R.raw.sk_error,
				Configuration.PCM_FORMAT);
	}

	public enum State {
		IDLE, LISTENING, PROCESSING
	}

	public interface INluRecCallBack {
		public void setState(State state);

		public void getNuanceResult(String result[]);

		public void setVolumeBar(int process);
	}

}
