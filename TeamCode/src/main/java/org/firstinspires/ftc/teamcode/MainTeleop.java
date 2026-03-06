package org.firstinspires.ftc.teamcode;

//exp  0 magazin

import android.util.Log;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.auto.Constants;
import org.firstinspires.ftc.teamcode.drive.Drive;
import org.firstinspires.ftc.teamcode.input.PrimaryMap;
import org.firstinspires.ftc.teamcode.input.SecondaryMap;
import org.firstinspires.ftc.teamcode.intake.Intake;
import org.firstinspires.ftc.teamcode.magazine.Magazine;
import org.firstinspires.ftc.teamcode.manager.Alliance;
import org.firstinspires.ftc.teamcode.manager.IntakeManager;
import org.firstinspires.ftc.teamcode.manager.ShooterManager;
import org.firstinspires.ftc.teamcode.shooter.BallIO;

import java.io.RandomAccessFile;
import java.util.Random;

@TeleOp(name="Main TeleOp",group = "FTC 26")
public class MainTeleop extends OpMode {

    BallIO shooter;
    BallIO intake;
    Magazine magazine;

    PrimaryMap primaryMap;
    SecondaryMap secondaryMap;
    Drive drive;
    Follower follower;
    ShooterManager shooterManager;
    IntakeManager intakeManager;
    boolean isIntaking = false;
    boolean isShooting = false;

    double targetHeading = 0;

    double basketX, basketY;
    boolean isManual = false;
    DcMotor magMotor;
    Servo intakeServo, pushServo, blockServo;

    Alliance alliance;
    int tagID;

    Pose getBasketPos() {
        Pose bPos = new Pose(15, 130);
        Pose rPos = new Pose(130, 130);

        return alliance == Alliance.BLUE ? bPos : rPos;
    }

    protected void turnTowardBasket(Alliance alliance) {
        Pose tPos = getBasketPos();

        double heading = Math.atan2(tPos.getY() - follower.getPose().getY(), tPos.getX() - follower.getPose().getX());
        follower.turn(heading - follower.getHeading(), true);
        while(follower.isBusy() && Math.abs(follower.getHeading() - heading) > 0.2) {
            follower.update();
        }
        follower.startTeleOpDrive(true);
        follower.update();
    }

    protected double getBasketDistance() {
        Pose tPos = getBasketPos();

        return Math.sqrt((
                Math.pow(follower.getPose().getX() - tPos.getX(), 2) +
                Math.pow(follower.getPose().getY() - tPos.getY(), 2)
        )) * 2.54;
    }

    void pickupSequence() {
        final double pickupLength = 20;
        final double pickupSpeed = 0.2;

        PathChain pathChain = follower.pathBuilder()
                .addPath(
                        new Path(
                                new BezierLine(
                                        follower.getPose(),
                                        new Pose(
                                                follower.getPose().getX() + Math.cos(follower.getHeading()) * pickupLength, // the lion does not bother himself with normalization
                                                follower.getPose().getY() + Math.sin(follower.getHeading()) * pickupLength)
                                )
                        )
                ).build();

            intakeManager.intake(1, true);
            follower.setMaxPower(pickupSpeed);
            follower.followPath(pathChain);
            ElapsedTime timeout = new ElapsedTime();
            while(follower.isBusy() && timeout.milliseconds() < 3000) follower.update();
            intakeManager.stopIntaking();
            follower.setMaxPower(1.0);
            follower.startTeleOpDrive(true);
            follower.update();
    }

    protected void goToHomeBase() { }

    Pose prevPose;
    @Override
    public void init() {

        OpModeState.isRunning = true;
        primaryMap = new PrimaryMap(gamepad1);
        secondaryMap = new SecondaryMap(gamepad2);
        magazine = new Magazine(hardwareMap.get(DcMotor.class, "magazine"), hardwareMap.get(Servo.class, "helpServo"), null,
                null,  hardwareMap.get(WebcamName.class, "magCam"), hardwareMap.get(RevBlinkinLedDriver.class, "light"), 50);
        shooter = new BallIO(hardwareMap.get(DcMotor.class, "shooter"), DcMotorSimple.Direction.FORWARD, 1.0);
        intake = new BallIO(hardwareMap.get(DcMotor.class, "intake"), DcMotorSimple.Direction.FORWARD, 1.0);
        shooterManager = new ShooterManager(magazine, shooter, hardwareMap.get(Servo.class, "shooterServo"), 23, 100);
        follower = Constants.createFollower(hardwareMap);
        intakeManager = new IntakeManager(magazine, intake, hardwareMap.get(Servo.class, "intakeGate"), 50);

        drive = new Drive(follower, new Pose());

        magazine.start();
        magazine.start();
        shooterManager.start();
        intakeManager.start();

        try (RandomAccessFile raf = new RandomAccessFile("/storage/self/primary/data.bin", "rw")) {
            raf.seek(0);
            double x, y, h;
            x = raf.readDouble();
            y = raf.readDouble();
            h = raf.readDouble();
            follower.setPose(new Pose(x, y, h));
            tagID = raf.readInt();
            shooterManager.setTagID(tagID);

            telemetry.addData("X", x);
            telemetry.addData("Y", y);
            telemetry.addData("H", h);
            telemetry.addData("A", tagID);
            telemetry.update();

        } catch (Exception e) {
            System.out.println("FUCK");
            e.printStackTrace();
        }

        /*
        follower.followPath(path);
        while(follower.isBusy()) {
            follower.update();
        }
        */

    }

    @Override
    public void loop() {

        if (gamepad2.circle) {
            if (!isShooting)
                shooterManager.startShooting(getBasketDistance());
            else
                shooterManager.stopShooting();
            isShooting = !isShooting;
        }

        if (gamepad1.right_bumper) {
            turnTowardBasket(alliance);
        }

        if (gamepad1.dpad_down) {
            pickupSequence();
        }

        if(gamepad1.right_stick_button)
            drive.reset();

        if (gamepad1.dpad_up) {
            //turnTowardBasket(alliance);
            shooterManager.startShooting(getBasketDistance());
        }

        if (gamepad1.dpad_left) {
            //goToHomeBase();
        }

      //  if(gamepad2.dpad_up) {
      //      shooterManager.setSlotOffset(0);
      //  }
      //  else if(gamepad2.dpad_right) {
      //      shooterManager.setSlotOffset(1);
      //  }
      //  else if(gamepad2.dpad_down) {
      //      shooterManager.setSlotOffset(2);
      //  }

        /*if(secondaryMap.startShooting())
            shooterManager.startShooting();
        if(secondaryMap.stopShooting())
            shooterManager.stopShooting();
        if(secondaryMap.incBall())
            shooterManager.incCurrSlot();
        if(secondaryMap.toggleIntake())
            intake.windup();
        else
            intake.winddown();

        if(secondaryMap.setupMag())
            magazine.setIntake();*/


        drive.drive(primaryMap.driveY(), primaryMap.driveX(), primaryMap.rotateX());
    }
    @Override
    public void stop() {
        OpModeState.isRunning = false;
    }
}

