package org.firstinspires.ftc.teamcode.vision;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.color.BallColor;
import org.firstinspires.ftc.teamcode.manager.FieldBall;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class MagazineCamPipeline extends OpenCvPipeline {
    OpenCvCamera cam;

    Mat hsvFrame = new Mat();
    Mat filteredFrame = new Mat();

    Mat hierarchy = new Mat(); // TOOD can this be null?

    int streamWidth, streamHeight;

    BallColor currentColor;
    boolean requestColor;


    public MagazineCamPipeline(WebcamName webcam, int streamWidth, int streamHeight) {
        this.streamWidth = streamWidth;
        this.streamHeight = streamHeight;

        initCam(webcam);
    }


    void initCam(WebcamName webcam) {
        MagazineCamPipeline pipeline = this;

        cam = OpenCvCameraFactory.getInstance()
                .createWebcam(webcam);

        cam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                cam.setPipeline(pipeline);
                cam.showFpsMeterOnViewport(true);
                cam.startStreaming(streamWidth, streamHeight, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {

            }
        });
    }

    boolean inColorRange(Scalar px, Scalar lowerB, Scalar upperB) {
        for(int i = 0; i < 3; i++) {
            if(px.val[i] < lowerB.val[i] || px.val[i] > upperB.val[i]) return false;
        }
        return true;
    }

    public synchronized BallColor getCurrentColor() {
        return currentColor;
    }

    @Override
    public Mat processFrame(Mat input) {
        Imgproc.cvtColor(input, hsvFrame, Imgproc.COLOR_RGB2HSV);
        Scalar px = new Scalar(hsvFrame.get(streamWidth/2, streamHeight/2));

        //g
        Scalar lowerG = new Scalar(30, 100, 30);
        Scalar upperG = new Scalar(100, 255, 255);

        //p
        Scalar lowerP = new Scalar(112.5, 30, 10);
        Scalar upperP = new Scalar(150, 255, 255);


                if(inColorRange(px, lowerG, upperG)) {
                        currentColor = BallColor.GREEN;
                        Core.inRange(hsvFrame, lowerG, upperG, filteredFrame);
                    }
                else if(inColorRange(px, lowerP, upperP)) {
                        currentColor = BallColor.PURPLE;
                        Core.inRange(hsvFrame, lowerP, upperP, filteredFrame);
                    }
                else {
                        currentColor = BallColor.NONE;
                    }
        /*

                        List<MatOfPoint> contours = new ArrayList<>();
                Imgproc.findContours(filteredFrame, contours, hierarchy,
                                Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

                    Imgproc.drawContours(input, contours, -1, new Scalar(255, 0, 0));
                    for(MatOfPoint cont : contours) {
                            if(Imgproc.contourArea(cont) > ((double) (640 * 480) / 2)) {
                                    requestColor = false;
                                    return input;
                                }
            }


                    +        currentColor = BallColor.NONE;
                   requestColor = false;

*/
        return input;
    }
}

//opencvcamerafactory*/
//.opencameradevice*/
//.startstreaming*/
//https://github.com/firstslovenia/ftc2025_auto/blob/master/TeamCode/src/main/java/opencv.java*/
//For HSV, hue range is [0,179], saturation range is [0,255], and value range is [0,255]. Different software use different scales. So if you are comparing ...