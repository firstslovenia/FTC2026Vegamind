package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;


import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.auto.Constants;
import org.firstinspires.ftc.teamcode.drive.Drive;
import org.firstinspires.ftc.teamcode.input.PrimaryMap;
import org.firstinspires.ftc.teamcode.input.SecondaryMap;
import org.firstinspires.ftc.teamcode.magazine.Magazine;
import org.firstinspires.ftc.teamcode.manager.Alliance;

@TeleOp(name = "ManualTeleOp", group = "FTC 26")
public class ManualTeleop extends OpMode {

    DcMotor magazine;
    Servo helpServo;
    DcMotor intake;
    DcMotor shooter;
    Drive drive;

    PrimaryMap primaryMap;
    SecondaryMap secondaryMap;

    @Override
    public void init() {
        drive = new Drive(Constants.createFollower(hardwareMap), new Pose());
        magazine = hardwareMap.get(DcMotor.class, "magazine");
        intake = hardwareMap.get(DcMotor.class, "intake");
        shooter = hardwareMap.get(DcMotor.class, "shooter");
        helpServo = hardwareMap.get(Servo.class, "helpServo");

        primaryMap = new PrimaryMap(gamepad1);
        secondaryMap = new SecondaryMap(gamepad2);

        magazine.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        magazine.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        intake.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        shooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    @Override
    public void loop() {
        magazine.setPower(gamepad2.left_stick_x * 0.25);
        intake.setPower(gamepad2.left_trigger * 0.2);
        shooter.setPower(-gamepad2.right_trigger);

        if(gamepad2.dpad_up) {
            helpServo.setPosition(0.0);
        } else {
            helpServo.setPosition(1.0);
        }

        //if(gamepad1.triangle)
        //    drive.reset();

        if(gamepad1.dpad_up)
            drive.drive(-primaryMap.driveY() * 0.2, -primaryMap.driveX() * 0.2, gamepad1.left_trigger - gamepad1.right_trigger);
        else
            drive.drive(-primaryMap.driveY(), -primaryMap.driveX(), gamepad1.left_trigger - gamepad1.right_trigger);
    }
}
