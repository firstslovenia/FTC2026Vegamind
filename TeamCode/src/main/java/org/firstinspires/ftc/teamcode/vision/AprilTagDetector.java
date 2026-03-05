package org.firstinspires.ftc.teamcode.vision;

import android.util.Size;

import com.pedropathing.follower.Follower;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.manager.Alliance;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

public class AprilTagDetector {
    AprilTagProcessor processor;
    VisionPortal visionPortal;

    public AprilTagDetector(WebcamName webcam) {
        createProcessor();
        createVisionPortal(webcam);
    }

    void createProcessor() {
        AprilTagProcessor.Builder builder = new AprilTagProcessor.Builder();
        builder.setDrawTagID(true);

        processor = builder.build();
    }

    void createVisionPortal(WebcamName webcam) {
        VisionPortal.Builder builder = new VisionPortal.Builder();

        builder.setCamera(webcam);
        builder.addProcessor(processor);
        builder.setCameraResolution(new Size(640, 480));
        builder.setStreamFormat(VisionPortal.StreamFormat.MJPEG); //YUV2?
        builder.setShowStatsOverlay(true);
        builder.enableLiveView(true);

        visionPortal = builder.build();
    }

    public void start() {
        visionPortal.setProcessorEnabled(processor, true);
    }

    public void stop() {
        visionPortal.setProcessorEnabled(processor, false);
    }

    //returns -1 until an april tag is detected,
    //at which point it returns that and stops
    //since we only need it to get the green artifact
    //pos
    public int readObelisk() {
        List<AprilTagDetection> detections = processor.getDetections();

        if(detections.isEmpty()) return -1;

        visionPortal.stopStreaming();
        visionPortal.stopLiveView();

        for(AprilTagDetection detection : detections) {
           if(detection.id > 20 && detection.id < 24) return detection.id;
        }

        return -1;
    }
}
