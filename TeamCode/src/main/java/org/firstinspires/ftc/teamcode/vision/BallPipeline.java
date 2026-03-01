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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class BallPipeline extends OpenCvPipeline {
    OpenCvCamera cam;

    Mat hsvFrame = new Mat();
    Mat filteredFrame = new Mat();

    Mat hierarchy = new Mat(); // TOOD can this be null?

    final List<MatOfPoint> contours = new ArrayList<>();
    boolean updatedContours = false;

    BallColor currentColor = BallColor.GREEN;

    int streamWidth, streamHeight;

    public BallPipeline(WebcamName webcam, int streamWidth, int streamHeight, int viewID) {
        this.streamWidth = streamWidth;
        this.streamHeight = streamHeight;

        initCam(webcam, viewID);
    }


    void initCam(WebcamName webcam, int viewID) {
        BallPipeline pipeline = this;

        cam = OpenCvCameraFactory.getInstance()
                .createWebcam(webcam, viewID);

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

    void smooth(Mat input) {
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(10, 10));
        Imgproc.morphologyEx(input, input, Imgproc.MORPH_OPEN, kernel);
        Imgproc.morphologyEx(input, input, Imgproc.MORPH_CLOSE, kernel);
    }

    void greenFilter(Mat input, Mat output) {
        Scalar lowerB = new Scalar(30, 100, 30);
        Scalar upperB = new Scalar(100, 255, 255);
        Core.inRange(input, lowerB, upperB, output);
    }

    void purpleFilter(Mat input, Mat output) {
        Scalar lowerB = new Scalar(112.5, 30, 10);
        Scalar upperB = new Scalar(150, 255, 255);
        Core.inRange(input, lowerB, upperB, output);
    }

    void filterContours(List<MatOfPoint> contours) {
        for(int i = 0; i < contours.size(); i++) {
            double area = Imgproc.contourArea(contours.get(i));
            if(area > 500) { // no balls :(
                continue;
            }

            contours.remove(i);
            i--;
        }
    }

    List<FieldBall> contour2Ball() {
        List<FieldBall> balls = new ArrayList<>();
        synchronized (contours) {
            for(MatOfPoint contour : contours) {
                Rect rect = Imgproc.boundingRect(contour);
                balls.add(
                        new FieldBall(rect.x, rect.y, getLastUpdateColor())
                );
            }
        }

        return balls;
    }

    public List<FieldBall> getBallContours() {
        if(!updatedContours) return null;
        updatedContours = false; // make sure we dont waste resources by going over the same contours twice

        return contour2Ball();
    }

    public BallColor getLastUpdateColor() {
        if(currentColor == BallColor.GREEN) return BallColor.PURPLE;
        else return BallColor.GREEN; // a bit counterintuitive but it switches on update
    }

    @Override
    public Mat processFrame(Mat input) {
        updatedContours = true;

        Imgproc.cvtColor(input, hsvFrame, Imgproc.COLOR_RGB2HSV);

        if(currentColor == BallColor.GREEN) {
            currentColor = BallColor.PURPLE;
            greenFilter(hsvFrame, filteredFrame);
        }
        else {
            currentColor = BallColor.GREEN;
            purpleFilter(hsvFrame, filteredFrame);
        }

        smooth(filteredFrame);

        Point offset = new Point(0, 0);
        synchronized(contours) {
            contours.clear();
            Imgproc.findContours(filteredFrame, contours, hierarchy,
                    Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE, offset);

            filterContours(contours);
        }


        for(MatOfPoint contour : contours) {
            Imgproc.rectangle(input, Imgproc.boundingRect(contour), new Scalar(255, 0, 0), -1);
        }

        //return input;
        return input;
    }
}

//opencvcamerafactory*/
//.opencameradevice*/
//.startstreaming*/
//https://github.com/firstslovenia/ftc2025_auto/blob/master/TeamCode/src/main/java/opencv.java*/
//For HSV, hue range is [0,179], saturation range is [0,255], and value range is [0,255]. Different software use different scales. So if you are comparing ...