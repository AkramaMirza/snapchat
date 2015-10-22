package com.akramamirza.photobabble;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.StateListAnimator;
import android.animation.TimeInterpolator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CameraFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CameraFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CameraFragment extends android.support.v4.app.Fragment {

    private FrameLayout cameraFrame;
    private CameraTextureView cameraTextureView;
    private int currentCameraId;
    private int numOfCameras;
    private int frontCameraId = Integer.MAX_VALUE;
    private int backCameraId = Integer.MAX_VALUE;
    private boolean isFacingBack = true;
    public final int MEDIA_TYPE_IMAGE = 1;
    public final int MEDIA_TYPE_VIDEO = 2;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CameraFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CameraFragment newInstance(String param1, String param2) {
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public CameraFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        numOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();

        for (int i = 0; i < numOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                backCameraId = i;
            } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                frontCameraId = i;
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_camera, container, false);

        cameraFrame = (FrameLayout) rootView.findViewById(R.id.cameraFrame);
        cameraTextureView = new CameraTextureView(MainApplication.getAppContext(), getCameraInstance(backCameraId));
        cameraFrame.addView(cameraTextureView);

        final Button switchCameraButton = (Button) rootView.findViewById(R.id.switchCameraButton);
        final Button captureButton = (Button) rootView.findViewById(R.id.captureButton);
        final Button crossButton = (Button) rootView.findViewById(R.id.crossButton);
        final Button sendButton = (Button) rootView.findViewById(R.id.sendButton);

        final Button button = (Button) rootView.findViewById(R.id.button);
        int buttonWidthHeight = getResources().getDisplayMetrics().heightPixels / 14;
        ViewGroup.LayoutParams buttonLayoutParams = button.getLayoutParams();
        buttonLayoutParams.height = buttonWidthHeight;
        buttonLayoutParams.width = buttonWidthHeight;
        button.setLayoutParams(buttonLayoutParams);

        // set the width and height of the button to the height of the screen divided by 14
        int switchCameraButtonWidthHeight = getResources().getDisplayMetrics().heightPixels / 14;
        ViewGroup.LayoutParams switchCameraButtonLayoutParams = switchCameraButton.getLayoutParams();
        switchCameraButtonLayoutParams.height = switchCameraButtonWidthHeight;
        switchCameraButtonLayoutParams.width = switchCameraButtonWidthHeight;
        switchCameraButton.setLayoutParams(switchCameraButtonLayoutParams);
        switchCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCamera();
            }
        });

        int captureButtonWidthHeight = getResources().getDisplayMetrics().heightPixels / 7;
        ViewGroup.LayoutParams captureButtonLayoutParams = captureButton.getLayoutParams();
        captureButtonLayoutParams.height = captureButtonWidthHeight;
        captureButtonLayoutParams.width = captureButtonWidthHeight;
        captureButton.setLayoutParams(captureButtonLayoutParams);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Runnable takePictureRunnable = new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bitmap = cameraTextureView.getBitmap();
                        cameraTextureView.getCamera().stopPreview();

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                        byte[] byteArray = stream.toByteArray();

                        //If the user wants to save the photo on their phone
                        /*File pictureFile = getOutputMediaFile();
                        pictureFile.createNewFile();
                        FileOutputStream fos = new FileOutputStream(pictureFile);
                        fos.write(byteArray);
                        fos.close();*/

                        // do this when the user presses the send button, otherwise keep bitmap offline until he sends
                        /*ParseFile imageFile = new ParseFile("snap.jpg", byteArray);
                        imageFile.saveInBackground();
                        ParseObject snap = new ParseObject("Snap");
                        snap.put("imageFile", imageFile);
                        snap.saveInBackground();*/
                    }
                };
                Thread takePictureThread = new Thread(takePictureRunnable);
                takePictureThread.start();

                captureButton.getStateListAnimator().jumpToCurrentState();
                doAnimation(R.animator.scale_in, crossButton, sendButton);
                getCircleAnimator(captureButton).start();
                getCircleAnimator(switchCameraButton).start();



                /*cameraTextureView.getCamera().takePicture(new Camera.ShutterCallback() {
                    @Override
                    public void onShutter() {
                        //cameraTextureView.getCamera().stopPreview();
                    }
                }, null, new Camera.PictureCallback() { //instead of take picture, take a screenshot of the cameraframe
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        try {
                            File pictureFile = getOutputMediaFile();
                            pictureFile.createNewFile();
                            FileOutputStream fos = new FileOutputStream(pictureFile);
                            fos.write(data);
                            fos.close();
                        } catch (FileNotFoundException e) {
                            Log.d("ERROR ", "File not found: " + e.getMessage());
                        } catch (IOException e) {
                            Log.d("ERROR ", "Error accessing file: " + e.getMessage());
                        }
                        camera.stopPreview();
                        captureButton.setVisibility(View.GONE);
                        switchCameraButton.setVisibility(View.GONE);
                        crossButton.setVisibility(View.VISIBLE);
                    }
                });*/
            }
        });

        int crossButtonWidthHeight = getResources().getDisplayMetrics().heightPixels / 20;
        ViewGroup.LayoutParams crossButtonLayoutParams = crossButton.getLayoutParams();
        crossButtonLayoutParams.height = crossButtonWidthHeight;
        crossButtonLayoutParams.width = crossButtonWidthHeight;
        crossButton.setLayoutParams(crossButtonLayoutParams);
        crossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        cameraTextureView.getCamera().startPreview();
                    }
                });
                thread.start();

                crossButton.getStateListAnimator().jumpToCurrentState();
                getCircleAnimator(crossButton).start();
                getCircleAnimator(sendButton).start();
                doAnimation(R.animator.scale_in, switchCameraButton, captureButton);



            }
        });

        int sendButtonWidthHeight = getResources().getDisplayMetrics().heightPixels / 12;
        ViewGroup.LayoutParams sendButtonLayoutParams = sendButton.getLayoutParams();
        sendButtonLayoutParams.height = sendButtonWidthHeight;
        sendButtonLayoutParams.width = sendButtonWidthHeight;
        sendButton.setLayoutParams(sendButtonLayoutParams);



        return rootView;
    }

    private void doAnimation(int animationId, View... myObjects) {

        ArrayList<AnimatorSet> animatorSets = new ArrayList<AnimatorSet>();

        for (View myObject : myObjects) {
            AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(MainApplication.getAppContext(), animationId);
            set.setTarget(myObject);
            animatorSets.add(set);
        }

        for (AnimatorSet animatorSet : animatorSets) {
            animatorSet.start();
        }
    }

    private Animator getCircleAnimator(final View myView) {
        // get the center for the clipping circle
        int cx = myView.getWidth() / 2;
        int cy = myView.getHeight() / 2;

        // get the initial radius for the clipping circle
        int initialRadius = myView.getWidth();

        // create the animation (the final radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius, 0);

        anim.setInterpolator(new DecelerateInterpolator(3));

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                myView.setVisibility(View.GONE);
            }
        });
        return anim;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (cameraTextureView.getCamera() == null) {
            cameraTextureView.setCamera(getCameraInstance(backCameraId));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        cameraTextureView.getCamera().release();
        cameraTextureView.setCamera(null);
    }

    public Camera getCameraInstance(int cameraId){
        Camera c = null;
        try {
            c = Camera.open(cameraId); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    void switchCamera() {
        if (numOfCameras < 1) {
            return;
        }
        cameraTextureView.getCamera().stopPreview();
        cameraTextureView.getCamera().release();

        if (isFacingBack) {
            cameraTextureView.setCamera(getCameraInstance(frontCameraId));
        } else {
            cameraTextureView.setCamera(getCameraInstance(backCameraId));
        }

        isFacingBack = !isFacingBack;

    }

    private File getOutputMediaFile() throws IOException {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        File mediaDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +
                File.separator + "PhotoBabble" + File.separator);
        mediaDirectory.mkdirs();

        File mediaFile = new File(mediaDirectory, "IMG_"+ timeStamp + ".jpg");
        Log.d("A", "DIRECTORY: " + mediaFile.getPath());



/*      implement this when adding video
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }
*/

        return mediaFile;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            // TODO: mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
