package converter.utils;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import app.exploitr.nsg.youp3.AudioConverter;
import app.exploitr.nsg.youp3.R;
import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
import cafe.adriel.androidaudioconverter.model.AudioFormat;

public class ConverterService extends Service {
	
	Notification notification;
	Notification.Builder builder;
	
	@TargetApi(Build.VERSION_CODES.O)
	private static NotificationChannel channel(Context ctx) {
		NotificationManager notificationManager =
				(NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		String channelId = "converter_check_channel_id";
		CharSequence channelName = "Conversion Notification";
		NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW);
		notificationChannel.enableVibration(true);
		notificationChannel.setVibrationPattern(new long[]{100});
		if (notificationManager != null) {
			notificationManager.createNotificationChannel(notificationChannel);
		}
		return notificationChannel;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		PendingIntent pendingIntent = PendingIntent.getActivity(this,
				1, new Intent(this, AudioConverter.class), 0);
		
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			builder = new Notification.Builder(this.getBaseContext(), channel(this).getId())
					.setContentTitle("Ongoing Audio Conversion")
					.setSmallIcon(R.drawable.ic_download_on)
					
					.setContentIntent(pendingIntent)
					.setOngoing(true);
			notification = builder.build();
			startForeground(18, notification);
		} else {
			builder = new Notification.Builder(this)
					.setContentTitle("Ongoing Audio Conversion")
					.setSmallIcon(R.drawable.ic_download_on)
					.setOngoing(true)
					.setContentIntent(pendingIntent);
			notification = builder.build();
			notification.flags = Notification.FLAG_ONGOING_EVENT;
			startForeground(18, notification);
		}
		
		String inPath = intent.getStringExtra(AudioConverter.INPUT_);
		String outPath = intent.getStringExtra(AudioConverter.OUTPUT_);
		int id = intent.getIntExtra(AudioConverter.ID_, R.id.mp3);
		
		AndroidAudioConverter.with(getApplicationContext())
				.setFile(new File(inPath))
				.setFormat(getFormat(id))
				.setCallback(new IConvertCallback() {
					@Override
					public void onSuccess(File convertedFile) {
						try {
							FileUtils.moveFileToDirectory(convertedFile, new File(outPath), true);
							Toast.makeText(ConverterService.this, "Conversion Finished", Toast.LENGTH_SHORT).show();
						} catch (IOException e) {
							e.printStackTrace();
							Toast.makeText(ConverterService.this, e.getMessage(), Toast.LENGTH_SHORT).show();
						}
						AudioConverter.isConversionRunning = false;
						stopForeground(true);
						stopSelf();
					}
					
					@Override
					public void onFailure(Exception error) {
						AudioConverter.isConversionRunning = false;
						Toast.makeText(ConverterService.this, "Conversion Failed", Toast.LENGTH_SHORT).show();
						stopForeground(true);
						stopSelf();
					}
				})
				.convert();
		
		startChecking();
		return START_NOT_STICKY;
	}
	
	private void startChecking() {
		Handler handler = new Handler();
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				if (!AudioConverter.isConversionRunning) {
					stopForeground(true);
					stopSelf();
					NotificationManagerCompat.from(getApplicationContext()).cancel(18);
				}
				handler.postDelayed(this, 250);
			}
		});
	}
	
	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private AudioFormat getFormat(int id) {
		switch (id) {
			case R.id.mp3:
				return AudioFormat.MP3;
			case R.id.m4a:
				return AudioFormat.M4A;
			case R.id.wma:
				return AudioFormat.WMA;
			case R.id.wav:
				return AudioFormat.WAV;
			case R.id.flac:
				return AudioFormat.FLAC;
			case R.id.aac:
				return AudioFormat.AAC;
			default:
				return AudioFormat.MP3;
		}
	}
}

