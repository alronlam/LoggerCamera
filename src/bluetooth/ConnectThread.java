package bluetooth;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

@SuppressLint("NewApi")
public class ConnectThread extends Thread {

	private final BluetoothSocket socket;

	private static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
	// private Activity mainActivity;
	public ConnectedThread thread;

	public ConnectThread(BluetoothDevice device) {
		// this.mainActivity = mainActivity;

		BluetoothSocket temp = null;

		try {
			temp = device.createRfcommSocketToServiceRecord(MY_UUID);
		} catch (IOException e) {
			Log.e("BluetoothConnector", "Failed to get socket.");
		}

		socket = temp;
	}

	// TODO: Change Class to ASyncThread
	public void run() {
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		bluetoothAdapter.cancelDiscovery();

		Log.d("Bluetooth Module", "Attempting to connect");

		try {
			socket.connect();
		} catch (IOException e) {
			// TODO: connectionFailed();

			Log.d("BluetoothConnector", "Failed to connect to server.");
			this.cancel();
		}

		// TODO: clear the connectThread held in mainActivity
		manageConnection(socket);
	}

	// TODO: Move this method to BluetoothService
	// Possible stack overflow due to switching between run() and
	// manageConnection()
	private void manageConnection(BluetoothSocket socket) {
		boolean flag = true;

		// while (true) {
		if (flag) {
			// try {
			thread = new ConnectedThread(socket);
			flag = false;
		}
		// handle if connection broken
		// }
	}

	public void cancel() {
		try {
			socket.close();
		} catch (IOException e) {
			Log.e("BluetoothConnector", "Failed to close socket.");
		}
	}

	// TODO: Move this code block
	public static byte[] serialize(Object obj) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(out);
		os.writeObject(obj);
		return out.toByteArray();
	}

	public static DataStruct deserialize(byte[] data) throws IOException, ClassNotFoundException {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		ObjectInputStream is = new ObjectInputStream(in);
		return (DataStruct) is.readObject();
	}
}