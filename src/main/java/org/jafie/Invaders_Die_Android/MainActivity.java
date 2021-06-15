package org.jafie.Invaders_Die_Android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	public static MediaPlayer player;

	private void showPopUp2() {

		AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
		helpBuilder.setTitle("Settings");
		helpBuilder.setMessage("Enable/Disable sound");
		helpBuilder.setPositiveButton("On",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						if (player.isPlaying()) {
						} else {
							BackgroundSound mBackgroundSound = new BackgroundSound();
							mBackgroundSound.execute();
						}
					}
				});

		helpBuilder.setNegativeButton("Off",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (player.isPlaying()) {
							player.stop();
						}

						// Do nothing
					}
				});

		// Remember, create doesn't show the dialog
		AlertDialog helpDialog = helpBuilder.create();
		helpDialog.show();

	}

	public class BackgroundSound extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			if (player.isPlaying()) {
				player.stop();
			}
			return null;
		}

	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		player.start();
		return 1;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button btnPlay = (Button) findViewById(R.id.btnPlay);
		Typeface fontPlay = Typeface.createFromAsset(getAssets(),
				"fonts/greenavocado.ttf");
		btnPlay.setTypeface(fontPlay);
		btnPlay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						Invader_Die_Main_Activity.class);
				//intent.putExtra("info", info);
				startActivity(intent);
			}
		});

		Button btnSettings = (Button) findViewById(R.id.btnSettings);
		Typeface fontSettings = Typeface.createFromAsset(getAssets(),
				"fonts/greenavocado.ttf");
		btnSettings.setTypeface(fontSettings);
		btnSettings.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showPopUp2();
				// Toast.makeText(null, SOUND, 5);
			}

		});

		player = MediaPlayer.create(this, R.raw.soundtrack_forest);
		player.setLooping(true); // Set looping
		player.setVolume(100, 100);

		Button btnAbout = (Button) findViewById(R.id.btnAbout);
		Typeface fontAbout = Typeface.createFromAsset(getAssets(),
				"fonts/greenavocado.ttf");
		btnAbout.setTypeface(fontAbout);
		btnAbout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast toast = Toast.makeText(MainActivity.this,
						"Invaders die - 2013", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.START | Gravity.BOTTOM, 0, 0);
				toast.show();
				// Toast.makeText(null, SOUND, 5);
			}

		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		player.start();

	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		player.stop();
		finish();
	}
}
