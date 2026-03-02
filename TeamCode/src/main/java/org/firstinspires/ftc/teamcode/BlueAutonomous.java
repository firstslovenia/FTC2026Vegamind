package org.firstinspires.ftc.teamcode;

import android.graphics.Path;
import android.graphics.Point;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.apache.commons.math3.Field;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.auto.Constants;
import org.firstinspires.ftc.teamcode.color.BallColor;
import org.firstinspires.ftc.teamcode.drive.Drive;
import org.firstinspires.ftc.teamcode.input.PrimaryMap;
import org.firstinspires.ftc.teamcode.input.SecondaryMap;
import org.firstinspires.ftc.teamcode.magazine.Magazine;
import org.firstinspires.ftc.teamcode.manager.FieldBall;
import org.firstinspires.ftc.teamcode.manager.FieldManager;
import org.firstinspires.ftc.teamcode.manager.FieldStruct;
import org.firstinspires.ftc.teamcode.manager.ShooterManager;
import org.firstinspires.ftc.teamcode.pathing.PedroPathBlue;
import org.firstinspires.ftc.teamcode.shooter.BallIO;
import org.firstinspires.ftc.teamcode.util.MapPoint;

import java.util.ArrayList;

@Autonomous(name="Blue Autonomous", group="FTC 26")
public class BlueAutonomous extends OpMode {

    BallIO shooter;
    BallIO intake;
    Magazine magazine;

    PrimaryMap primaryMap;
    SecondaryMap secondaryMap;
    Drive drive;
    Follower follower;
    ShooterManager shooterManager;
    PedroPathBlue pedroPathBlue;
    FieldManager fieldManager;


    double targetHeading = 0;

    double basketX, basketY;

    Pose prevPose;
    @Override
    public void init() {

        primaryMap = new PrimaryMap(gamepad1);
        secondaryMap = new SecondaryMap(gamepad2);
        magazine = new Magazine(hardwareMap.get(DcMotor.class, "magazine"), hardwareMap.get(Servo.class, "helpServo"), hardwareMap.get(TouchSensor.class, "intakeSensor"),
                hardwareMap.get(TouchSensor.class, "outtakeSensor"), 50);
        shooter = new BallIO(hardwareMap.get(DcMotor.class, "shooter"));
        intake = new BallIO(hardwareMap.get(DcMotor.class, "intake"));
        shooterManager = new ShooterManager(magazine, shooter, 23, 100);
        follower = Constants.createFollower(hardwareMap);
        drive = new Drive(follower, new Pose());
        pedroPathBlue = new PedroPathBlue(follower);

        follower.setPose(new Pose(34.25, 135.75));

        fieldManager = new FieldManager(hardwareMap, hardwareMap.get(WebcamName.class, "webcam"),
                1280, 720, 0, 70, .35, 200, telemetry);
        fieldManager.start();
        magazine.start();
        shooterManager.start();
    }

    void waitF() {
        while (follower.isBusy()) {
            follower.update();
        }
    }

    FieldBall closestToOrigin(FieldStruct struct) {
        if (struct == null || struct.getBalls() == null) { return null; }

        FieldBall closest = struct.getBalls().get(0);
        double minDistanceSquared = Math.pow(closest.getRealX(), 2) + Math.pow(closest.getRealY(), 2);

        for (int i = 1; i < struct.getBalls().toArray().length; i++) {
            double distanceSquared = Math.pow(struct.getBalls().get(i).getRealX(), 2) + Math.pow(struct.getBalls().get(i).getRealY(), 2);
            if (distanceSquared < minDistanceSquared) {
                minDistanceSquared = distanceSquared;
                closest = struct.getBalls().get(i);
            }
        }

        return closest;
    }

    MapPoint mapToField(FieldBall target) {
        double toX = follower.getPose().getX() + target.getRealX();
        double toY = follower.getPose().getY() + target.getRealY();

        return new MapPoint(toX, toY, follower.getHeading(), follower.getHeading() + 90); // Heading is in radians
    }

    void pickupArtifacts(PathChain path) {
        intake.windup();
        magazine.rotateToBall(1);
        follower.followPath(path);
        waitF();

        intake.winddown();
    }

    @Override
    public void start() {
        // TODO: Go to Shooting path first for preload; I'm skipping since I just want to scout balls right now
        follower.followPath(pedroPathBlue.PathScout1);
        waitF();

        ArrayList<FieldBall> a = new ArrayList<>();
        a.add(new FieldBall(0, 0, 30, 20));
        FieldStruct struct = new FieldStruct(a, BallColor.GREEN);//fieldManager.computePositions(3.141593/4, 0, 0); // Some random value for the pitch
        MapPoint closest = mapToField(closestToOrigin(struct)); // Unless something goes really wrong, this should always be on our side
        PathChain newPath = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(follower.getPose().getX(), follower.getPose().getY()),
                                new Pose(closest.getX(), closest.getY()) // TODO: This will b-line for the ball currently; move only one coordinate at a time to get more perpendicular movement
                        )
                )
                .setLinearHeadingInterpolation(closest.getFromHeading(), closest.getToHeading())
                .build();

        pickupArtifacts(newPath);
    }

    @Override
    public void loop() {
    }
}
