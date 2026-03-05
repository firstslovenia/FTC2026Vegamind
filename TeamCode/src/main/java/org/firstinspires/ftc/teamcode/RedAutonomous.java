package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.auto.Constants;
import org.firstinspires.ftc.teamcode.color.BallColor;
import org.firstinspires.ftc.teamcode.drive.Drive;
import org.firstinspires.ftc.teamcode.input.PrimaryMap;
import org.firstinspires.ftc.teamcode.input.SecondaryMap;
import org.firstinspires.ftc.teamcode.magazine.Magazine;
import org.firstinspires.ftc.teamcode.manager.FieldBall;
import org.firstinspires.ftc.teamcode.manager.FieldManager;
import org.firstinspires.ftc.teamcode.manager.IntakeManager;
import org.firstinspires.ftc.teamcode.manager.ShooterManager;
import org.firstinspires.ftc.teamcode.pathing.PedroPathRed;
import org.firstinspires.ftc.teamcode.shooter.BallIO;
import org.firstinspires.ftc.teamcode.util.MapPoint;

import java.util.List;

@Autonomous(name="Red Autonomous", group="FTC 26")
public class RedAutonomous extends LinearOpMode {

    BallIO shooter;
    BallIO intake;
    Magazine magazine;

    PrimaryMap primaryMap;
    SecondaryMap secondaryMap;
    Drive drive;
    Follower follower;
    ShooterManager shooterManager;
    PedroPathRed pedroPathRed;
    FieldManager fieldManager;
    IntakeManager intakeManager;Servo camSwivel;


    double targetHeading = 0;

    double basketX, basketY;

    Pose prevPose;

    @Override
    public void runOpMode() throws InterruptedException {
        try {
            OpModeState.isRunning = true;

            primaryMap = new PrimaryMap(gamepad1);
            secondaryMap = new SecondaryMap(gamepad2);
            magazine = new Magazine(hardwareMap.get(DcMotor.class, "magazine"), hardwareMap.get(Servo.class, "helpServo"), null,
                    null, hardwareMap.get(WebcamName.class, "magCam"), hardwareMap.get(RevBlinkinLedDriver.class, "light"), 50);
            intake = new BallIO(hardwareMap.get(DcMotor.class, "intake"), DcMotorSimple.Direction.FORWARD, 0.7);
            shooter = new BallIO(hardwareMap.get(DcMotor.class, "shooter"), DcMotorSimple.Direction.FORWARD, 1.0);
            shooterManager = new ShooterManager(magazine, shooter, hardwareMap.get(Servo.class, "shooterServo"), 23, 100);
            intakeManager = new IntakeManager(magazine, intake, hardwareMap.get(Servo.class, "intakeGate"), 50);
            follower = Constants.createFollower(hardwareMap);
            drive = new Drive(follower, new Pose());
            pedroPathRed = new PedroPathRed(follower);
            //fieldManager = new FieldManager(hardwareMap, hardwareMap.get(WebcamName.class, "webcam"),
            //        1280, 720, 0, 0, .35, 200, telemetry);
            camSwivel = hardwareMap.get(Servo.class, "camSwivel");

            magazine.setTelemetry(telemetry);
            //fieldManager.start();
            shooterManager.start();
            magazine.start();
            magazine.start();
            follower.setPose(new Pose(110, 135));
            follower.update();
            intakeManager.start();

            while (!isStarted()) ;
            camSwivel.setPosition(0.0);

            camSwivel.setPosition(0.4);
            //fieldManager.updateCamInfo(25, 35, 3.14159 / 2 - camSwivel.getPosition() * 3.14159);

            shootSeq();

            PathChain scout1 = follower.pathBuilder().addPath(new BezierLine(
                    new Pose(follower.getPose().getX(), follower.getPose().getY()),
                    new Pose(pedroPathRed.scout1X, pedroPathRed.scout1Y)
            )).setLinearHeadingInterpolation(follower.getHeading(), pedroPathRed.scout1Deg).build();
            follower.followPath(scout1);
            waitF();

            PathChain newPath = follower.pathBuilder()
                    .addPath(
                            new BezierLine(
                                    new Pose(follower.getPose().getX(), follower.getPose().getY()),
                                    //new Pose(closest.getY(), follower.getPose().getY())
                                    new Pose(pedroPathRed.hardcode1X, pedroPathRed.hardcode1Y)
                            )
                    )
                    .setLinearHeadingInterpolation(follower.getHeading(), pedroPathRed.hardcode1Deg) // Rotate towards balls
                    .build();
            follower.followPath(newPath);
            waitF();

            pickupBalls();
            shootSeq();

            newPath = follower.pathBuilder()
                    .addPath(
                            new BezierLine(
                                    new Pose(follower.getPose().getX(), follower.getPose().getY()),
                                    //new Pose(closest.getY(), follower.getPose().getY())
                                    new Pose(pedroPathRed.hardcode2X, pedroPathRed.hardcode2Y)
                            )
                    )
                    .setLinearHeadingInterpolation(follower.getHeading(), pedroPathRed.hardcode1Deg) // Rotate towards balls
                    .build();
            follower.followPath(newPath);
            waitF();

            pickupBalls();
            shootSeq();

            newPath = follower.pathBuilder()
                    .addPath(
                            new BezierLine(
                                    new Pose(follower.getPose().getX(), follower.getPose().getY()),
                                    //new Pose(closest.getY(), follower.getPose().getY())
                                    new Pose(pedroPathRed.hardcode3X, pedroPathRed.hardcode3Y)
                            )
                    )
                    .setLinearHeadingInterpolation(follower.getHeading(), pedroPathRed.hardcode1Deg) // Rotate towards balls
                    .build();
            follower.followPath(newPath);
            waitF();

            pickupBalls();
            shootSeq();
        /*


        //ArrayList<FieldBall> a = new ArrayList<>();
        //a.add(new FieldBall(0, 0, 30, 20));
        //FieldStruct struct = new FieldStruct(a, BallColor.GREEN);
        // Some random value for the pitch
        BallColor color;
        List<FieldBall> balls = fieldManager.getFieldBalls();
        do {
            balls = fieldManager.getFieldBalls();
        } while (balls.isEmpty() || balls.get(0).getColor() != BallColor.GREEN);
        closest = mapToField(closestToOrigin(balls)); // Unless something goes really wrong, this should always be on our side
        PathChain newPath = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(follower.getPose().getX(), follower.getPose().getY()),
                                //new Pose(closest.getY(), follower.getPose().getY())
                                new Pose(closest.getX(), follower.getPose().getY())
                        )
                )
                .setLinearHeadingInterpolation(follower.getHeading(), closest.getToHeading()) // Rotate towards balls
                .build();
        follower.followPath(newPath);
        waitF();
        intakeManager.startIntaking();
        newPath = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(follower.getPose().getX(), follower.getPose().getY()),
                                //new Pose(closest.getY(), follower.getPose().getY())
                                new Pose(follower.getPose().getX(), closest.getY() + 20)
                        )
                )
                .setLinearHeadingInterpolation(follower.getHeading(), closest.getToHeading()) // Rotate towards balls
                .build();
        follower.setMaxPower(0.3);
        follower.followPath(newPath);
        waitF();
        intakeManager.stopIntaking();

        follower.setMaxPower(1.0);
        shootPath = follower.pathBuilder().addPath(new BezierLine(
                new Pose(follower.getPose().getX(), follower.getPose().getY()),
                new Pose(pedroPathRed.shootX, pedroPathRed.shootY)
        )).setLinearHeadingInterpolation(follower.getHeading(), pedroPathRed.shootDeg).build();
        follower.followPath(shootPath);
        waitF();
        shooterManager.startShooting();
        while (shooterManager.isActive()); // This might spike CPU usage :(

        // 2
        scout1 = follower.pathBuilder().addPath(new BezierLine(
                new Pose(follower.getPose().getX(), follower.getPose().getY()),
                new Pose(pedroPathRed.scout2X, pedroPathRed.scout2Y)
        )).setLinearHeadingInterpolation(follower.getHeading(), pedroPathRed.scout2Deg).build();
        follower.followPath(scout1);
        waitF();

        balls = fieldManager.getFieldBalls();
        do {
            balls = fieldManager.getFieldBalls();
        } while (balls.isEmpty() || balls.get(0).getColor() != BallColor.GREEN);
        closest = mapToField(closestToOrigin(balls)); // Unless something goes really wrong, this should always be on our side
        newPath = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(follower.getPose().getX(), follower.getPose().getY()),
                                //new Pose(closest.getY(), follower.getPose().getY())
                                new Pose(closest.getX(), follower.getPose().getY())
                        )
                )
                .setLinearHeadingInterpolation(follower.getHeading(), closest.getToHeading()) // Rotate towards balls
                .build();
        follower.followPath(newPath);
        waitF();
        intakeManager.startIntaking();
        newPath = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(follower.getPose().getX(), follower.getPose().getY()),
                                //new Pose(closest.getY(), follower.getPose().getY())
                                new Pose(follower.getPose().getX(), closest.getY() + 20)
                        )
                )
                .setLinearHeadingInterpolation(follower.getHeading(), closest.getToHeading()) // Rotate towards balls
                .build();
        follower.setMaxPower(0.3);
        follower.followPath(newPath);
        waitF();
        intakeManager.stopIntaking();

        follower.setMaxPower(1.0);
        shootPath = follower.pathBuilder().addPath(new BezierLine(
                new Pose(follower.getPose().getX(), follower.getPose().getY()),
                new Pose(pedroPathRed.shootX, pedroPathRed.shootY)
        )).setLinearHeadingInterpolation(follower.getHeading(), pedroPathRed.shootDeg).build();
        follower.followPath(shootPath);
        waitF();
        shooterManager.startShooting();
        while (shooterManager.isActive()); // This might spike CPU usage :(

        // 3
        scout1 = follower.pathBuilder().addPath(new BezierLine(
                new Pose(follower.getPose().getX(), follower.getPose().getY()),
                new Pose(pedroPathRed.scout3X, pedroPathRed.scout3Y)
        )).setLinearHeadingInterpolation(follower.getHeading(), pedroPathRed.scout3Deg).build();
        follower.followPath(scout1);
        waitF();

        balls = fieldManager.getFieldBalls();
        do {
            balls = fieldManager.getFieldBalls();
        } while (balls.isEmpty() || balls.get(0).getColor() != BallColor.GREEN);
        closest = mapToField(closestToOrigin(balls)); // Unless something goes really wrong, this should always be on our side
        newPath = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(follower.getPose().getX(), follower.getPose().getY()),
                                //new Pose(closest.getY(), follower.getPose().getY())
                                new Pose(closest.getX(), follower.getPose().getY())
                        )
                )
                .setLinearHeadingInterpolation(follower.getHeading(), closest.getToHeading()) // Rotate towards balls
                .build();
        follower.followPath(newPath);
        waitF();
        intakeManager.startIntaking();
        newPath = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(follower.getPose().getX(), follower.getPose().getY()),
                                //new Pose(closest.getY(), follower.getPose().getY())
                                new Pose(follower.getPose().getX(), closest.getY() + 20)
                        )
                )
                .setLinearHeadingInterpolation(follower.getHeading(), closest.getToHeading()) // Rotate towards balls
                .build();
        follower.setMaxPower(0.3);
        follower.followPath(newPath);
        waitF();
        intakeManager.stopIntaking();

        follower.setMaxPower(1.0);
        shootPath = follower.pathBuilder().addPath(new BezierLine(
                new Pose(follower.getPose().getX(), follower.getPose().getY()),
                new Pose(pedroPathRed.shootX, pedroPathRed.shootY)
        )).setLinearHeadingInterpolation(follower.getHeading(), pedroPathRed.shootDeg).build();
        follower.followPath(shootPath);
        waitF();
        shooterManager.startShooting();
        while (shooterManager.isActive()); // This might spike CPU usage :(


         */

            OpModeState.isRunning = false;
        }
        catch (InterruptedException e) {
            OpModeState.isRunning = false;
            throw e;
        }
    }

    MapPoint closest;

    void waitF() throws InterruptedException {
        while (follower.isBusy() && !isStopRequested()) {
            follower.update();
            Thread.sleep(5);
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
        double toX = follower.getPose().getX() - target.getRealY() / 2.54;
        double toY = follower.getPose().getY() - target.getRealX() / 2.54;

        return new MapPoint(toX, toY, follower.getHeading(), follower.getHeading() - 3.14 / 2); // Heading is in radians
    }

    protected double getBasketDistance() {
        Pose tPos = new Pose(130, 130);

        return Math.sqrt((
                Math.pow(follower.getPose().getX() - tPos.getX(), 2) +
                        Math.pow(follower.getPose().getY() - tPos.getY(), 2)
        ));
    }

    void shootSeq() throws InterruptedException {
        PathChain shootPath = follower.pathBuilder().addPath(new BezierLine(
                new Pose(follower.getPose().getX(), follower.getPose().getY()),
                new Pose(pedroPathRed.shootX, pedroPathRed.shootY)
        )).setLinearHeadingInterpolation(0, pedroPathRed.shootDeg).build(); // Start heading is 0deg
        follower.followPath(shootPath);
        waitF();
        shooterManager.startShooting(getBasketDistance());
        while (shooterManager.isActive() && !isStopRequested()) {
            Thread.sleep(10);
        }
    }

    void pickupBalls() throws InterruptedException {
        intakeManager.startIntaking();
        PathChain newPath = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(follower.getPose().getX(), follower.getPose().getY()),
                                //new Pose(closest.getY(), follower.getPose().getY())
                                new Pose(follower.getPose().getX() + 30, follower.getPose().getY())
                        )
                )
                .setLinearHeadingInterpolation(follower.getHeading(), follower.getHeading()) // Rotate towards balls
                .build();
        follower.setMaxPower(0.2);
        follower.followPath(newPath);
        waitF();
        while(intakeManager.isActive()) {
            Thread.sleep(10);
        }

        follower.setMaxPower(1.0);
    }
}