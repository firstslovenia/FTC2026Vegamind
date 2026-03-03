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
import com.qualcomm.robotcore.hardware.DcMotorSimple;
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
import java.util.List;

@Autonomous(name="Blue Autonomous", group="FTC 26")
public class BlueAutonomous extends OpMode {

    ShooterManager shooterManager;
    BallIO shooter;
    BallIO intake;
    Magazine magazine;

    PrimaryMap primaryMap;
    SecondaryMap secondaryMap;
    Drive drive;
    Follower follower;
    PedroPathBlue pedroPathBlue;
    FieldManager fieldManager;
    Servo camSwivel;


    double targetHeading = 0;

    double basketX, basketY;

    Pose prevPose;
    @Override
    public void init() {

        primaryMap = new PrimaryMap(gamepad1);
        secondaryMap = new SecondaryMap(gamepad2);
        magazine = new Magazine(hardwareMap.get(DcMotor.class, "magazine"), hardwareMap.get(Servo.class, "helpServo"), null,
                null, 50);
        shooter = new BallIO(hardwareMap.get(DcMotor.class, "shooter"), DcMotorSimple.Direction.REVERSE, 1.0);
        intake = new BallIO(hardwareMap.get(DcMotor.class, "intake"), DcMotorSimple.Direction.FORWARD, 0.4);
        shooterManager = new ShooterManager(magazine, shooter, 23, 100);
        follower = Constants.createFollower(hardwareMap);
        drive = new Drive(follower, new Pose());
        pedroPathBlue = new PedroPathBlue(follower);
        camSwivel =  hardwareMap.get(Servo.class, "camSwivel");
        camSwivel.setPosition(0.0);
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

    FieldBall closestToOrigin(List<FieldBall> struct) {
        if (struct == null || struct.isEmpty()) { return null; }

        FieldBall closest = struct.get(0);

        double minDistanceSquared = Math.pow(closest.getRealX(), 2) + Math.pow(closest.getRealY(), 2);
        for (FieldBall ball : struct) {
            double distSqrd = Math.pow(ball.getRealX(), 2) + Math.pow(ball.getRealY(), 2);
            if (distSqrd < minDistanceSquared) {
                minDistanceSquared = distSqrd;
                closest = ball;
            }
        }

        return closest;
    }

    MapPoint mapToField(FieldBall target) {
        double toX = follower.getPose().getX() + Math.abs(target.getRealY());
        double toY = follower.getPose().getY() - Math.abs(target.getRealX());

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
        camSwivel.setPosition(0.4);
        fieldManager.updateCamInfo(0, 35, 3.14159 / 2 - camSwivel.getPosition() * 3.14159);

        // TODO: Go to Shooting path first for preload; I'm skipping since I just want to scout balls right now
        /*PathChain scout1 = follower.pathBuilder().addPath(new BezierLine(
                new Pose(follower.getPose().getX(), follower.getPose().getY()),
                new Pose(pedroPathBlue.scout1X, pedroPathBlue.scout1Y)
        )).setLinearHeadingInterpolation(follower.getHeading(), pedroPathBlue.scout1Deg).build();
        follower.followPath(scout1);
        waitF();*/

        //ArrayList<FieldBall> a = new ArrayList<>();
        //a.add(new FieldBall(0, 0, 30, 20));
        //FieldStruct struct = new FieldStruct(a, BallColor.GREEN);
        // Some random value for the pitch
        BallColor color;
        List<FieldBall> balls = fieldManager.getFieldBalls();
        do {
            balls = fieldManager.getFieldBalls();
        } while (balls.isEmpty() || balls.get(0).getColor() == BallColor.GREEN);
        MapPoint closest = mapToField(closestToOrigin(balls)); // Unless something goes really wrong, this should always be on our side
        PathChain newPath = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(follower.getPose().getX(), follower.getPose().getY()),
                                new Pose(follower.getPose().getX(), closest.getY())
                        )
                )
                .setLinearHeadingInterpolation(closest.getFromHeading(), closest.getToHeading() + Math.toRadians(90)) // Rotate towards balls
                .build();
        follower.followPath(newPath);
        waitF();

        /*pickupArtifacts(newPath);

        // Eat the balls
        // Move to scout 2
        PathChain scout2 = follower.pathBuilder().addPath(new BezierLine(
                new Pose(follower.getPose().getX(), follower.getPose().getY()),
                new Pose(pedroPathBlue.scout2X, pedroPathBlue.scout2Y)
        )).setLinearHeadingInterpolation(follower.getHeading(), pedroPathBlue.scout2Deg).build(); // Heading might be fucked
        follower.followPath(scout2);
        waitF();

        // TODO: Implement the scouting routine here as I am not just gonna copy the array list again :angry:
        // TODO: Pickup artifacts

        // Eat the balls (Pt. 2)
        PathChain scout3 = follower.pathBuilder().addPath(new BezierLine(
                        new Pose(follower.getPose().getX(), follower.getPose().getY()),
                        new Pose(pedroPathBlue.scout3X, pedroPathBlue.scout3Y)
                )).setLinearHeadingInterpolation(follower.getHeading(), pedroPathBlue.scout3Deg).build(); // Heading might be fucked
                follower.followPath(scout3);
                waitF();

        // TODO: Implement the scouting routine here as I am not just gonna copy the array list again :angry:
        // TODO: Pickup artifacts*/
    }

    @Override
    public void loop() {
    }
}
