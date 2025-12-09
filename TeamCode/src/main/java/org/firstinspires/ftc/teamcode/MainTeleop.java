package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.auto.Tuning.follower;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.auto.Constants;
import org.firstinspires.ftc.teamcode.drivetrain.Drivetrain;
import org.firstinspires.ftc.teamcode.drivetrain.FieldCentricDrivetrain;
import org.firstinspires.ftc.teamcode.drivetrain.Motors;
import org.firstinspires.ftc.teamcode.drivetrain.TankDrive;
import org.firstinspires.ftc.teamcode.input.PrimaryMap;
import org.firstinspires.ftc.teamcode.magazine.Magazine;
import org.firstinspires.ftc.teamcode.shooter.Shooter;

public class MainTeleop extends OpMode{

    Shooter shooter;
    Magazine magazine;
    PrimaryMap inputMap;

    Follower follower;

    double targetHeading = 0;

    double basketX, basketY;
    @Override
    public void init() {

        ColorSensor[] colorSensors = {hardwareMap.get(ColorSensor.class, "color1"),
                                      hardwareMap.get(ColorSensor.class, "color2"),
                                      hardwareMap.get(ColorSensor.class, "color3")};

        inputMap = new PrimaryMap(gamepad1);
        shooter = new Shooter(hardwareMap.get(DcMotor.class, "shooter1"), hardwareMap.get(DcMotor.class, "shooter2"));
        magazine = new Magazine(hardwareMap.get(DcMotor.class, "magazine"), hardwareMap.get(Servo.class, "gateServo"), hardwareMap.get(AnalogInput.class, "potentiometer"),
                colorSensors);
        follower = Constants.createFollower(hardwareMap);
        follower.useCentripetal = true;
        follower.setStartingPose(new Pose());
        follower.update();
    }

    @Override
    public void loop() {
        magazine.rotateToBall(inputMap.getBallIndex());

        if(inputMap.runShooter()) shooter.windup();
        else shooter.winddown();

        if(inputMap.openGate()) magazine.openGate();
        else magazine.closeGate();

        telemetry.addData("heading", follower.getHeading());
        magazine.update(telemetry);
        Path path = new Path(
                        new BezierLine(follower.getPose(), new Pose(follower.getPose().getX() + gamepad1.left_stick_x, follower.getPose().getY() + gamepad1.left_stick_y,
                                follower.getPose().getHeading() + gamepad1.right_stick_x)));
        follower.followPath(path);
        follower.update();
    }
}

