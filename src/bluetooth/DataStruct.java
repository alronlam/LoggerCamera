package bluetooth;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class DataStruct implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	double deg;
	double length;
	int steps;
	
	public DataStruct(double deg, double length, int steps){
		this.deg = deg;
		this.length = length;
		this.steps = steps;
	}

	public static DataStruct getDefault(){
		return new DataStruct(0,0,0);
	}
	
	public boolean equals(DataStruct ds){
		return deg == ds.deg && length == ds.length && steps == ds.steps;
	}
	
	public String toString(){
		return deg + " " + length + " " + steps;
	}
	
	public byte[] getByteArray() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(out);
		os.writeObject(this);
		return out.toByteArray();
	}
}