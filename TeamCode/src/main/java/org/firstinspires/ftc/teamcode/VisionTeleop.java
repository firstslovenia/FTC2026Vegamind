package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;


import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.magazine.Magazine;
import org.firstinspires.ftc.teamcode.vision.BallPipeline;

@TeleOp(name = "Vision", group = "FTC 26")
public class VisionTeleop extends OpMode {

    BallPipeline pipeline;

    @Override
    public void init() {
        pipeline = new BallPipeline(hardwareMap.get(WebcamName.class, "webcam"), 1280, 720,
                hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName()));
    }

    @Override
    public void loop() {

    }
}
