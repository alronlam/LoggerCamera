package dlsu.vins;

import image.ImageManager;
import image.ImageManagerListener;

import java.util.Set;
import java.util.Timer;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import bluetooth.ConnectThread;

public class DriverActivity extends Activity implements ImageManagerListener {

	private ImageManager imageManager;
	private boolean isFeaturesReady = false;

	private final boolean DELETE_IMAGES = true;
	private ConnectThread connectThread;
	private int imagesCaptured = 0;
	private boolean isStarted;

	private Timer recordTimer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fastlayout);
		imageManager = new ImageManager(this, this, DELETE_IMAGES);
		this.startTryingToConnectToServer();
	}

	/** Bluetooth Related **/
	private void startTryingToConnectToServer() {
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		if (mBluetoothAdapter != null) {

			/* Turn on Bluetooth if it is not turned on */
			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, Constants.REQUEST_ENABLE_BT);
			}

			/* Search for the target device. */
			Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

			BluetoothDevice pairDevice = null;
			// If there are paired devices
			if (pairedDevices.size() > 0) {
				// Loop through paired devices
				for (BluetoothDevice device : pairedDevices) {
					if (device.getName().equals(Constants.SERVER_DEVICE_NAME)) {
						pairDevice = device;
						break;
					}
				}
			}

			this.connectThread = new ConnectThread(pairDevice);
			this.connectThread.start();
			this.signalServerToStart();
		}
	}

	private void signalServerToStart() {
		while (this.connectThread.thread == null)
			;

		byte[] msg = { Constants.SIGNAL_SERVER_START_MSG };
		this.connectThread.thread.write(msg);
		isStarted = true;
	}

	private void signalServerToStop() {
		while (this.connectThread.thread == null)
			;

		byte[] msg = { Constants.SIGNAL_SERVER_STOP_MSG };
		this.connectThread.thread.write(msg);
		isStarted = false;
	}

	/** Actual Image Capturing **/
	private void captureOneImage() {
		if (!isFeaturesReady)
			return;

		imageManager.captureImage();
		imagesCaptured++;
	}

	/** Activity Life Cycle Methods **/
	@Override
	protected void onPause() {
		super.onPause();
		recordTimer.cancel();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void initDone() {
		isFeaturesReady = true;
	}
}
