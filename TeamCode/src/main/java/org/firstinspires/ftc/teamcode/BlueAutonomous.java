package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.auto.Constants;
import org.firstinspires.ftc.teamcode.drive.Drive;
import org.firstinspires.ftc.teamcode.input.PrimaryMap;
import org.firstinspires.ftc.teamcode.input.SecondaryMap;
import org.firstinspires.ftc.teamcode.magazine.Magazine;
import org.firstinspires.ftc.teamcode.manager.FieldBall;
import org.firstinspires.ftc.teamcode.manager.FieldManager;
import org.firstinspires.ftc.teamcode.manager.IntakeManager;
import org.firstinspires.ftc.teamcode.manager.ShooterManager;
import org.firstinspires.ftc.teamcode.pathing.PedroPathBlue;
import org.firstinspires.ftc.teamcode.shooter.BallIO;
import org.firstinspires.ftc.teamcode.util.MapPoint;
import org.firstinspires.ftc.teamcode.vision.AprilTagDetector;

import java.io.RandomAccessFile;
import java.util.List;

@Autonomous(name="Blue Autonomous", group="FTC 26")
public class BlueAutonomous extends LinearOpMode {

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
    IntakeManager intakeManager;
    Servo camSwivel;


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
            shooter = new BallIO(hardwareMap.get(DcMotor.class, "shooter")  , DcMotorSimple.Direction.FORWARD, 1.0);
            shooterManager = new ShooterManager(magazine, shooter, hardwareMap.get(Servo.class, "shooterServo"), 23, 100);
            intakeManager = new IntakeManager(magazine, intake, hardwareMap.get(Servo.class, "intakeGate"), 50);
            follower = Constants.createFollower(hardwareMap);
            drive = new Drive(follower, new Pose());
            pedroPathBlue = new PedroPathBlue(follower);
            //fieldManager = new FieldManager(hardwareMap, hardwareMap.get(WebcamName.class, "webcam"),
            //        1280, 720, 0, 0, .35, 200, telemetry);
            camSwivel = hardwareMap.get(Servo.class, "camSwivel");

            magazine.setTelemetry(telemetry);
            //fieldManager.start();
            shooterManager.start();
            magazine.start();
            magazine.start();
            follower.setPose(new Pose(35, 135));
            follower.update();
            intakeManager.start();

        new Thread(() -> {
            try (RandomAccessFile raf = new RandomAccessFile("/storage/self/primary/data.bin", "rw")) {
                while (true) {
                    // wipe file
                    raf.setLength(0);
                    // reset pointer to start
                    raf.seek(0);
                    // write new content
                    raf.writeDouble(follower.getPose().getX());
                    raf.writeDouble(follower.getPose().getY());
                    raf.writeDouble(follower.getHeading());
                    raf.writeInt(shooterManager.getTagID());

                    // ensure it's written
                    raf.getFD().sync();

                    Thread.sleep(100);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> { //TODO find optimal camera angle
            AprilTagDetector detector = new AprilTagDetector(hardwareMap.get(WebcamName.class, "webcam"));
            detector.start();

            while (OpModeState.isRunning) {
                int id = detector.readObelisk();
                if (id != -1) {
                    shooterManager.setTagID(id);
                    telemetry.addLine("Read april tag!");
                    telemetry.update();
                }
            }

            detector.stop();
            //fieldManager = new FieldManager(hardwareMap, hardwareMap.get(WebcamName.class, "webcam"),
            //        1280, 720, 0, 0, .35, 200, telemetry);
        }).start();

            waitForStart();
           // magazine.resetSlot(0);
           // magazine.resetSlot(1);
           // magazine.resetSlot(2);

           // pickupBalls();

            //fieldManager.updateCamInfo(25, 35, 3.14159 / 2 - camSwivel.getPosition() * 3.14159);

            shootSeq();


            PathChain pathChain = follower.pathBuilder()
                    .addPath(
                            new BezierLine(
                                    new Pose(follower.getPose().getX(), follower.getPose().getY()),
                                    //new Pose(closest.getY(), follower.getPose().getY())
                                    new Pose(50, 120)
                            )
                    )
                    .setLinearHeadingInterpolation(follower.getHeading(), follower.getHeading()) // Rotate towards balls
                    .build();

            follower.followPath(pathChain);
            waitF();
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
                new Pose(pedroPathBlue.shootX, pedroPathBlue.shootY)
        )).setLinearHeadingInterpolation(follower.getHeading(), pedroPathBlue.shootDeg).build();
        follower.followPath(shootPath);
        waitF();
        shooterManager.startShooting();
        while (shooterManager.isActive()); // This might spike CPU usage :(

        // 2
        scout1 = follower.pathBuilder().addPath(new BezierLine(
                new Pose(follower.getPose().getX(), follower.getPose().getY()),
                new Pose(pedroPathBlue.scout2X, pedroPathBlue.scout2Y)
        )).setLinearHeadingInterpolation(follower.getHeading(), pedroPathBlue.scout2Deg).build();
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
                new Pose(pedroPathBlue.shootX, pedroPathBlue.shootY)
        )).setLinearHeadingInterpolation(follower.getHeading(), pedroPathBlue.shootDeg).build();
        follower.followPath(shootPath);
        waitF();
        shooterManager.startShooting();
        while (shooterManager.isActive()); // This might spike CPU usage :(

        // 3
        scout1 = follower.pathBuilder().addPath(new BezierLine(
                new Pose(follower.getPose().getX(), follower.getPose().getY()),
                new Pose(pedroPathBlue.scout3X, pedroPathBlue.scout3Y)
        )).setLinearHeadingInterpolation(follower.getHeading(), pedroPathBlue.scout3Deg).build();
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
                new Pose(pedroPathBlue.shootX, pedroPathBlue.shootY)
        )).setLinearHeadingInterpolation(follower.getHeading(), pedroPathBlue.shootDeg).build();
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

    void aitF() throws InterruptedException {
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
        )) * 2.54;
    }

    void shootSeq() throws InterruptedException {
        Pose tPos = new Pose(130, 130); // TODO CHANGE FOR BLUE
        PathChain shootPath = follower.pathBuilder().addPath(new BezierLine(
                new Pose(follower.getPose().getX(), follower.getPose().getY()),
                new Pose(pedroPathBlue.shootX, pedroPathBlue.shootY)
        )).setLinearHeadingInterpolation(follower.getHeading(), Math.toRadians(135)).build(); // Start heading is 0deg
        follower.followPath(shootPath);
        waitF();
        shooterManager.startShooting(getBasketDistance());
        while (shooterManager.isActive() && !isStopRequested()) {
            Thread.sleep(10);
        }
    }
    PathChain getPickupPath() {
        return follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(follower.getPose().getX(), follower.getPose().getY()),
                                //new Pose(closest.getY(), follower.getPose().getY())
                                new Pose(follower.getPose().getX() + 5, follower.getPose().getY())
                        )
                )
                .setLinearHeadingInterpolation(follower.getHeading(), follower.getHeading()) // Rotate towards balls
                .build();
    }

    void pickupBalls() throws InterruptedException {

        follower.setMaxPower(0.2);

        intakeManager.intake(1);
        follower.followPath(getPickupPath());
        waitF();
        System.out.println("AUTO 1");
        while (intakeManager.isActive()) ;
        intakeManager.intake(1);
        follower.followPath(getPickupPath());
        waitF();
        System.out.println("AUTO 2");
        while (intakeManager.isActive()) ;
        intakeManager.intake(1);
        follower.followPath(getPickupPath());
        waitF();
        while (intakeManager.isActive());
        System.out.println("AUTO 3");
        intakeManager.intake(1);
        follower.followPath(getPickupPath());
        waitF();
        while (intakeManager.isActive());
        System.out.println("AUTO 4");

        follower.setMaxPower(1.0);
    }

    void waitF() throws InterruptedException {
        while (follower.isBusy() && !isStopRequested()) {
            follower.update();
            Thread.sleep(5);
        }
    }
}