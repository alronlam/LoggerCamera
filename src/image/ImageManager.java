package image;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import android.app.Activity;
import android.util.Log;
import android.view.SurfaceView;
import dlsu.vins.R;

public class ImageManager implements CvCameraViewListener2 {
	private static final String TAG = "Feature Manager";

	private BaseLoaderCallback loaderCallback;
	private CameraBridgeViewBase cameraView;

	Mat currentFrame;
	
	private ImageIO imageIO;
	private ImageManagerListener listener;
	private boolean deleteImages;
	
	public ImageManager(Activity caller, ImageManagerListener listener, boolean deleteImages) {
		Log.i(TAG, "constructed");

		Log.i(TAG, "Trying to load OpenCV library");
		this.listener = listener;
		this.deleteImages = deleteImages;
		initLoader(caller);

		cameraView = (CameraBridgeViewBase) caller.findViewById(R.id.surface_view);
		cameraView.setMaxFrameSize(400, 1280); // sets to 320 x 240
		cameraView.setVisibility(SurfaceView.VISIBLE);

		cameraView.setCvCameraViewListener(this);
		
		if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, caller, loaderCallback)) {
			Log.e(TAG, "Cannot connect to OpenCV Manager");
		}
	}

	
	private void initLoader(Activity caller) {
		loaderCallback = new BaseLoaderCallback(caller) {
			@Override
			public void onManagerConnected(int status) {
				switch (status) {
				case LoaderCallbackInterface.SUCCESS: {
					Log.i(TAG, "OpenCV loaded successfully");
					cameraView.enableView();					
					cameraView.enableView();
					imageIO = new ImageIO();
					
					if (deleteImages)
						imageIO.deletePhotos();
					
					listener.initDone();
				}
					break;
				default: {
					super.onManagerConnected(status);
				}
					break;
				}
			}
		};
	}

	
	public void onCameraViewStarted(int width, int height) {}

	public void onCameraViewStopped() {}

	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		Log.d("Image Manager", "onCameraFrame");

		Mat mat, temp;

		mat = Mat.zeros(0, 0, CvType.CV_32F);
		temp = inputFrame.rgba().t();
		Core.flip(temp, mat, 1);
		
		mat = Mat.zeros(0, 0, CvType.CV_32F);
		temp = inputFrame.gray().t();
		Core.flip(temp, mat, 1);
		currentFrame = mat;

		
		if (currentFrame.get(0, 0)[0] == currentFrame.get(currentFrame.rows() - 1, 0)[0]) {
			currentFrame = null;
			Log.i("Image Capture", "Invalid Image");
		}

		return inputFrame.gray();
	}

	
	public void deleteImages() {
		imageIO.deletePhotos();
	}
		
	
	/**
	 * 
	 * @param filename  Requires a file extension.
	 */
	public void captureImage(String filename) {
		imageIO.save(filename, currentFrame);
	}
	
	/**
	 * Saves the current frame with using the next digit as the filename.
	 */
	public void captureImage() {
		if (currentFrame != null)
			imageIO.saveNext(currentFrame);
	}
	
}
