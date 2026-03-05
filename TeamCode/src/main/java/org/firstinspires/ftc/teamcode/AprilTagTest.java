package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.manager.FieldManager;
import org.firstinspires.ftc.teamcode.vision.AprilTagDetector;

@TeleOp(name = "TagTest", group = "FTC 26")
public class AprilTagTest extends OpMode {

    AprilTagDetector tagDetector;

    @Override
    public void init() {
        /*pipeline = new BallPipeline(hardwareMap.get(WebcamName.class, "webcam"), 1280, 720,
                hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName()));*/

        tagDetector = new AprilTagDetector(hardwareMap.get(WebcamName.class, "webcam"));
        tagDetector.start();
    }

    @Override
    public void loop() {

    }
}
