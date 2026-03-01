package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.teamcode.auto.Constants;
import org.firstinspires.ftc.teamcode.drive.Drive;
import org.firstinspires.ftc.teamcode.input.PrimaryMap;
import org.firstinspires.ftc.teamcode.input.SecondaryMap;
import org.firstinspires.ftc.teamcode.magazine.Magazine;
import org.firstinspires.ftc.teamcode.manager.ShooterManager;
import org.firstinspires.ftc.teamcode.pathing.PedroPathRed;
import org.firstinspires.ftc.teamcode.shooter.BallIO;

@Autonomous(name="Red Autonomous", group="FTC 26")
public class RedAutonomous extends OpMode {

    BallIO shooter;
    BallIO intake;
    Magazine magazine;

    PrimaryMap primaryMap;
    SecondaryMap secondaryMap;
    Drive drive;
    Follower follower;
    ShooterManager shooterManager;
    PedroPathRed pedroPathEx;


    double targetHeading = 0;

    double basketX, basketY;

    Pose prevPose;
    @Override
    public void init() {

        primaryMap = new PrimaryMap(gamepad1);
        secondaryMap = new SecondaryMap(gamepad2);
         magazine = new Magazine(hardwareMap.get(DcMotor.class, "magazine"), hardwareMap.get(Servo.class, "helpServo"), hardwareMap.get(TouchSensor.class, "intakeSensor"),
               hardwareMap.get(TouchSensor.class, "outtakeSensor"), hardwareMap.get(ColorSensor.class, "colorAlt"), hardwareMap.get(DistanceSensor.class, "distance"));
        shooter = new BallIO(hardwareMap.get(DcMotor.class, "shooter"));
        intake = new BallIO(hardwareMap.get(DcMotor.class, "intake"));
        shooterManager = new ShooterManager(magazine, shooter, 23);
        follower = Constants.createFollower(hardwareMap);
        drive = new Drive(follower, new Pose());
        pedroPathEx = new PedroPathRed(follower);
        follower.setPose(new Pose(110, 135));
    }

    void waitF() {
        while (follower.isBusy()) {
            follower.update();
        }
    }

    @Override
    public void start() {
        // Sequence; TODO: Make it more readable
        follower.followPath(pedroPathEx.Path1);
        waitF();
        // shoot
        shooterManager.start();
        while(shooterManager.isActive()) {
            shooterManager.update(telemetry);
            magazine.update(telemetry);
            telemetry.update();
        }        follower.followPath(pedroPathEx.Path2);
        waitF();
        intake.windup();
        follower.followPath(pedroPathEx.Path3);
        waitF();
        // eat the ball
        follower.followPath(pedroPathEx.Path4);
        waitF();
        // eat the ball
        follower.followPath(pedroPathEx.Path5);
        waitF();
        intake.winddown();
        follower.followPath(pedroPathEx.Path6);
        waitF();
        // shoot
        shooterManager.start();
        while(shooterManager.isActive()) {
            shooterManager.update(telemetry);
            magazine.update(telemetry);
            telemetry.update();
        }
        follower.followPath(pedroPathEx.Path7);
        waitF();
        intake.windup();
        follower.followPath(pedroPathEx.Path8);
        waitF();
        // eat the ball
        follower.followPath(pedroPathEx.Path9);
        waitF();
        // eat the ball
        follower.followPath(pedroPathEx.Path10);
        waitF();
        intake.winddown();
        follower.followPath(pedroPathEx.Path11);
        waitF();
        // shoot
        shooterManager.start();
        while(shooterManager.isActive()) {
            shooterManager.update(telemetry);
            magazine.update(telemetry);
            telemetry.update();
        }
        follower.followPath(pedroPathEx.Path12);
        waitF();
        intake.windup();
        follower.followPath(pedroPathEx.Path13);
        waitF();
        // eat the ball
        follower.followPath(pedroPathEx.Path14);
        waitF();
        // eat the ball
        follower.followPath(pedroPathEx.Path15);
        waitF();
        intake.winddown();
        follower.followPath(pedroPathEx.Path16);
        waitF();
        // shoot
        shooterManager.start();
        while(shooterManager.isActive()) {
            shooterManager.update(telemetry);
            magazine.update(telemetry);
            telemetry.update();
        }        // Go to center
        follower.followPath(pedroPathEx.Path17);
        waitF();
    }

    @Override
    public void loop() {

        /*if(secondaryMap.startShooting())
            shooterManager.start();
        if(secondaryMap.stopShooting())
            shooterManager.stop();
        if(secondaryMap.incBall())
            shooterManager.incCurrSlot();
        if(secondaryMap.toggleIntake())
            intake.windup();
        else
            intake.winddown();

        if(secondaryMap.setupMag())
            magazine.setIntake();


        shooterManager.update(telemetry);
        magazine.update(telemetry);

        drive.drive(primaryMap.driveX(), primaryMap.driveY(), primaryMap.rotateX());*/
    }
}
