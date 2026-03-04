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

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.magazine.Magazine;
import org.firstinspires.ftc.teamcode.manager.IntakeManager;
import org.firstinspires.ftc.teamcode.manager.ShooterManager;
import org.firstinspires.ftc.teamcode.shooter.BallIO;

@TeleOp(name = "ShooterTest", group = "FTC 26")
public class ShooterTest extends OpMode {

    DcMotor shooter;
    Servo helpServo;

    @Override
    public void init() {
//        color = hardwareMap.get(ColorSensor.class, "colorAlt");
//        motor = hardwareMap.get(DcMotor.class, "intake");
//        distance = hardwareMap.get(DistanceSensor.class, "distance");
//
        shooter = hardwareMap.get(DcMotor.class, "shooter");
        shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        shooter.setDirection(DcMotorSimple.Direction.FORWARD);
        helpServo = hardwareMap.get(Servo.class, "helpServo");


    }
    //330 ... 1
    //240.. 0.95
    ///200 ... 0.83
    //170 ... 0.83
    //120 ... 0.83
    /// 90 ... 0.8
    /// 80.. 0.86
    //

    @Override
    public void start() {
    }

    @Override
    public void loop() {
//        if(gamepad1.a)
//            magazine.setIntake();
//
//        motor.setPower(gamepad1.left_trigger * 0.3);
//        magazine.update(telemetry);

        shooter.setPower(shooter.getPower() + gamepad1.left_stick_y * 0.01);
        telemetry.addData("power", shooter.getPower());
        telemetry.update();

        helpServo.setPosition(gamepad1.dpad_down ? 0.4 : 1.0);

    }
}
