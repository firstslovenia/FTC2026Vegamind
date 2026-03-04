package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;


import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.magazine.Magazine;
import org.firstinspires.ftc.teamcode.manager.FieldManager;
import org.firstinspires.ftc.teamcode.vision.BallPipeline;

@TeleOp(name = "Vision", group = "FTC 26")
public class VisionTeleop extends OpMode {

    //BallPipeline pipeline;
    FieldManager fieldManager;
    Servo camSwivel;

    AnalogInput potentiometer;

    @Override
    public void init() {
        /*pipeline = new BallPipeline(hardwareMap.get(WebcamName.class, "webcam"), 1280, 720,
                hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName()));*/
        fieldManager = new FieldManager(hardwareMap, hardwareMap.get(WebcamName.class, "webcam"),
                1280, 720, 0, 0, .35, 200, telemetry);
        camSwivel =  hardwareMap.get(Servo.class, "camSwivel");
        camSwivel.setPosition(0.0);
        fieldManager.start();

        potentiometer = hardwareMap.get(AnalogInput.class, "potentiometer");

    }

    @Override
    public void loop() {
//        double pitch = 3.14159 / 2 - camSwivel.getPosition() * 3.14159;
        double pitch = Math.abs(potentiometer.getVoltage() / potentiometer.getMaxVoltage() - 0.5) * 3.14 / 2;

        fieldManager.updateCamInfo(0, 39 - Math.sin(pitch) * 5, pitch);
        camSwivel.setPosition(camSwivel.getPosition() + gamepad1.left_stick_y * 0.002);
        //telemetry.addData("deg", Math.toDegrees(3.14159 / 2 - camSwivel.getPosition() * 3.14159));
        telemetry.update();
    }
}
