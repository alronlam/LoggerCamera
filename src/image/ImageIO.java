package image;

import java.io.File;

import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import android.os.Environment;
import android.util.Log;

public class ImageIO {
	private String TAG = "ImageIO";
	
	private final File PHOTO_DIRECTORY = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
	private final String FOLDER = "breadcrumb";
	private final String DEFAULT_FILENAME = "0";
	private final String EXTENSION = ".jpg";
	
	private final int QUALITY = 100;
	private final MatOfInt PARAMS;
	
	private File path;
	private int lastDigitSaved = -1;
	
	
	
	public ImageIO() {
		int paramInt[] = new int[] {Highgui.CV_IMWRITE_JPEG_QUALITY, QUALITY};
		PARAMS = new MatOfInt(paramInt);
		
		
		// Create directory
		String imageLocationStr = PHOTO_DIRECTORY.getAbsolutePath() + File.separator + FOLDER;
		File imageLocation = new File(imageLocationStr);
		imageLocation.mkdir();
		path = imageLocation;
		
		// Retrieve last digit for continuous saving
		String[] files = path.list();
		if (files.length != 0) {
			String lastfile = files[files.length - 1];
			String digitString = lastfile.split("\\.")[0];
			lastDigitSaved = Integer.parseInt(digitString);
		}
	}
	
	
	public void deletePhotos() {
		File[] photos = path.listFiles();
		for (int i = 0; i < photos.length; i++) {
			photos[i].delete();
		}
		lastDigitSaved = -1;
	}
	
	
	/**
	 * Saves the image using the default filename.
	 */
	public void save(Mat image) {
		save(DEFAULT_FILENAME + EXTENSION, image);
	}
	
	
	/**
	 * Saves the image using the next digit as the filename.
	 */
	public void saveNext(Mat image) {
		lastDigitSaved++;
		save(lastDigitSaved + EXTENSION, image);
	}

	
	/**
	 * @param filename  Requires a file extension.
	 * @param image
	 */
	public void saveGray(String filename, Mat image) {
		Mat grayImage = new Mat();
		Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
		save(filename, grayImage);
	}
	
	
	/**
	 * Saves the image in its original color.
	 * 
	 * @param filename  Requires a file extension.
	 * @param image
	 */
	public void save(String filename, Mat image) {
		File file = new File(path, filename);
		filename = file.toString();
	    boolean result = Highgui.imwrite(filename, image, PARAMS);

	    if (result)
	    	Log.d(TAG, "SUCCESS writing image to " + filename);
	    else
		    Log.d(TAG, "Fail writing to external storage: " + filename);
	}
	
	
	/**
	 * @return The default image in color.
	 */
	public Mat load() {
		return load(DEFAULT_FILENAME);
	}
	
	
	/**
	 * @param filename  Requires a file extension.
	 * @return The image in color.
	 */
	public Mat load(String filename) {
		File file = new File(path, filename);
		filename = file.toString();
		Mat image = Highgui.imread(filename);
		
	    if (!image.empty())
	    	Log.d(TAG, "SUCCESS loading image " + filename);
	    else
		    Log.d(TAG, "Fail loading image " + filename);
		
		return image;
	}
}
