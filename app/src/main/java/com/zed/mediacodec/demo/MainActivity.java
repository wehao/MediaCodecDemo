package com.zed.mediacodec.demo;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author zed
 */
public class MainActivity extends Activity {
	private final String TAG = MainActivity.class.getSimpleName();

	private MediaCodecSurfaceView mSurfaceView;
	private TextView              tv_text;
	private int mVideoWidth  = 0;
	private int mVideoHeight = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tv_text = (TextView) findViewById(R.id.tv_text);

		mSurfaceView = (MediaCodecSurfaceView) findViewById(R.id.surface);
		mSurfaceView.setOnSupportListener(new OnSupportListener() {
			@Override
			public void UnSupport() {
				Log.i(TAG, "this device not support this resolution!");
			}
		});

		mSurfaceView.setOnDecodeListener(new OnDecodeListener() {
			@Override
			public void decodeResult(int w, int h) {
				if (mVideoWidth != w || mVideoHeight != h) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							tv_text.setText(mVideoWidth + " x " + mVideoHeight);
						}
					});
				}
			}
		});

		new Thread(){
			@Override
			public void run() {
				super.run();
				startDecodeH264();
			}
		}.start();
	}


	/**
	 * 模拟数据发送
	 */
	private void startDecodeH264() {

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		InputStream inputStream = null;

		byte[] bytes = new byte[1024 * 8];
		int readSize = 0;
		int count = 0;

		while (count < 449) {
			try {
				inputStream = getAssets().open(count + ".h264");
				while ((readSize = inputStream.read(bytes)) != -1) {
					byteArrayOutputStream.write(bytes, 0, readSize);
				}
				byteArrayOutputStream.flush();
				mSurfaceView.onReceived(byteArrayOutputStream.toByteArray());

				byteArrayOutputStream.reset();
				inputStream.close();
				inputStream = null;
				count++;
				SystemClock.sleep(100);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (byteArrayOutputStream != null) {
					try {
						byteArrayOutputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}
}
