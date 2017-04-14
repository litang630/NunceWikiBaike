package com.yongyida.nuancebaikedemo;

import android.content.Context;
import android.util.Log;

import com.nuance.speechkit.Audio;
import com.nuance.speechkit.AudioPlayer;
import com.nuance.speechkit.Language;
import com.nuance.speechkit.Session;
import com.nuance.speechkit.Transaction;
import com.nuance.speechkit.TransactionException;
import com.yongyida.nauncebaike.nuancetools.Configuration;

public class TTSAudioPlay implements AudioPlayer.Listener {
	private Session speechSession;
	private Transaction ttsTransaction;
	private String speakContent;
	
	public TTSAudioPlay(String args) {
		this.speakContent = args;
	}

	public void initTTS(Context context) {
		speechSession = Session.Factory.session(context, Configuration.SERVER_URI,
				Configuration.APP_KEY);
		speechSession.getAudioPlayer().setListener(this);
		
		//If we are not loading TTS from the server, then we should do so.
        if(ttsTransaction == null) {
            synthesize();
        }
        //Otherwise lets attempt to cancel that transaction
        else {
            stop();
        }
	}

	private void synthesize() {
		// TODO Auto-generated method stub
		//Setup our TTS transaction options.
        Transaction.Options options = new Transaction.Options();
        options.setLanguage(new Language(Configuration.LANGUAGE_CODE));
        //optionally change the Voice of the speaker, but will use the default if omitted.
        //options.setVoice(new Voice(Voice.SAMANTHA));
        
       
        Log.i("tangli","&&&&&&&&&&&speakContent = "+speakContent);
        //Start a TTS transaction
        ttsTransaction = speechSession.speakString(speakContent, options, new Transaction.Listener() {
            @Override
            public void onAudio(Transaction transaction, Audio audio) {
            	Log.i("tangli","\nonAudio");

                //The TTS audio has returned from the server, and has begun auto-playing.
                ttsTransaction = null;
            }

            @Override
            public void onSuccess(Transaction transaction, String s) {
            	Log.i("tangli","\nonSuccess");

                //Notification of a successful transaction. Nothing to do here.
            }

            @Override
            public void onError(Transaction transaction, String s, TransactionException e) {
            	Log.i("tangli","\nonError: " + e.getMessage() + ". " + s);

                //Something went wrong. Check Configuration.java to ensure that your settings are correct.
                //The user could also be offline, so be sure to handle this case appropriately.

                ttsTransaction = null;
            }
        });
	}
	
	private void stop() {
		// TODO Auto-generated method stub
		ttsTransaction.cancel();
	}

	@Override
	public void onBeginPlaying(AudioPlayer arg0, Audio arg1) {
		// TODO Auto-generated method stub
		Log.i("tangli","\nonBeginPlaying");
		//The TTS Audio will begin playing.
	}

	@Override
	public void onFinishedPlaying(AudioPlayer arg0, Audio arg1) {
		// TODO Auto-generated method stub
		Log.i("tangli","\nonFinishedPlaying");
		//The TTS Audio has finished playing
	}

}
