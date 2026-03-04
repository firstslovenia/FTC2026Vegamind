package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;


import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.magazine.Magazine;
import org.firstinspires.ftc.teamcode.manager.IntakeManager;
import org.firstinspires.ftc.teamcode.manager.ShooterManager;
import org.firstinspires.ftc.teamcode.shooter.BallIO;

@TeleOp(name = "MagTest", group = "FTC 26")
public class TestTeleop extends OpMode {
    ColorSensor color;
    DcMotor motor;
    DcMotor mag;
    DistanceSensor distance;
    Magazine magazine;
    AnalogInput pot;
    ShooterManager shooterManager;
    IntakeManager intakeManager;
    BallIO shooter;
    BallIO intake;
    @Override
    public void init() {
//        color = hardwareMap.get(ColorSensor.class, "colorAlt");
//        motor = hardwareMap.get(DcMotor.class, "intake");
//        distance = hardwareMap.get(DistanceSensor.class, "distance");
//
        magazine = new Magazine(hardwareMap.get(DcMotor.class, "magazine"), hardwareMap.get(Servo.class, "helpServo"), null,
                null,  hardwareMap.get(WebcamName.class, "magCam"), hardwareMap.get(RevBlinkinLedDriver.class, "light"), 50);
        magazine.start();

        shooter = new BallIO(hardwareMap.get(DcMotor.class, "shooter"), DcMotorSimple.Direction.FORWARD, 1.0);
        intake = new BallIO(hardwareMap.get(DcMotor.class, "intake"), DcMotorSimple.Direction.FORWARD, 0.4);
        shooterManager = new ShooterManager(magazine, shooter, 23, 100);
        intakeManager = new IntakeManager(magazine, intake, 100);


        shooterManager.start();
        intakeManager.start();
    }

    @Override
    public void start() {
        magazine.start();
    }

    @Override
    public void loop() {
//        if(gamepad1.a)
//            magazine.setIntake();
//
//        motor.setPower(gamepad1.left_trigger * 0.3);
//        magazine.update(telemetry);

        if(gamepad1.triangle) {
            magazine.rotateToBall(0);
        }
        else if(gamepad1.circle) {
            magazine.rotateToBall(3);
        }
        else if(gamepad1.square) {
            magazine.rotateToBall(1);
        }
        else if(gamepad1.cross) {
            magazine.rotateToBall(4);
        }

        if(gamepad1.dpad_up) {
            shooterManager.startShooting();
        }
        if(gamepad1.dpad_down)
            intakeManager.startIntaking();

        telemetry.addData("s1", magazine.getBallAtSlot(0));
        telemetry.addData("s2", magazine.getBallAtSlot(1));
        telemetry.addData("s3", magazine.getBallAtSlot(2));

        telemetry.update();

    }
}
