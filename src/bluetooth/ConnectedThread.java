package bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class ConnectedThread extends Thread {
	private final BluetoothSocket socket;
	private final InputStream inStream;
	private final OutputStream outStream;

	// MainActivity mainActivity;

	public ConnectedThread(BluetoothSocket socket) {
		this.socket = socket;

		// this.mainActivity = (MainActivity) mainActivity;

		InputStream tmpIn = null;
		OutputStream tmpOut = null;

		try {
			tmpIn = socket.getInputStream();
			tmpOut = socket.getOutputStream();
		} catch (IOException e) {
			Log.e("BluetoothConnector", "Failed to get streams.");
		}

		inStream = tmpIn;
		outStream = tmpOut;
	}

	public void run() {
		ObjectInputStream is = null;

		try {
			is = new ObjectInputStream(this.inStream);
		} catch (IOException e1) {
			Log.e("BluetoothConnector", "Failed to handle inpute stream.");
		}

		// TODO: change true condition to while connection is up
		while (true) {
			final DataStruct data;
			DataStruct temp = DataStruct.getDefault();

			try {
				temp = (DataStruct) is.readObject();
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
				Log.e("BluetoothConnector", "Failed to read from stream");
			}

			data = temp;

			// if new data is received
			if (!data.equals(DataStruct.getDefault())) {
				// mainActivity.runOnUiThread(new Runnable() {
				// public void run() {
				// TextView tv = (TextView)
				// mainActivity.findViewById(R.id.textView1);
				// tv.setText(tv.getText() + "\n" +
				// Calendar.getInstance().getTime() + " " + data.toString());
				// }
				// });
			}
		}
	}

	public void write(byte[] bytes) {
		try {
			outStream.write(bytes);
		} catch (IOException e) {
			Log.e("BluetoothConnector", "Failed to write to stream.");
		}
	}

	public void cancel() {
		try {
			socket.close();
		} catch (IOException e) {
			Log.e("BluetoothConnector", "Failed to close socket.");
		}
	}
}
