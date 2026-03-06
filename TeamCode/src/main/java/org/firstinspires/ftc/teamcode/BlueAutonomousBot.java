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
import org.firstinspires.ftc.teamcode.manager.FieldManager;
import org.firstinspires.ftc.teamcode.manager.IntakeManager;
import org.firstinspires.ftc.teamcode.manager.ShooterManager;
import org.firstinspires.ftc.teamcode.pathing.PedroPathRed;
import org.firstinspires.ftc.teamcode.shooter.BallIO;
import org.firstinspires.ftc.teamcode.vision.AprilTagDetector;

import java.io.RandomAccessFile;

@Autonomous(name="BLUE Autonomous BOT", group="FTC 26")
public class BlueAutonomousBot extends LinearOpMode {

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
    IntakeManager intakeManager;
    Servo camSwivel;


    double targetHeading = 0;

    double basketX, basketY;

    Pose prevPose;
    @Override
    public void runOpMode() throws InterruptedException {
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
        follower.setPose(new Pose(80, 9, 3.14));
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

        new Thread(() -> {
            AprilTagDetector detector = new AprilTagDetector(hardwareMap.get(WebcamName.class, "webcam"));
            detector.start();

            while (true) {
                int id = detector.readObelisk();
                if (id != -1) {
                    shooterManager.setTagID(id);
                    telemetry.addLine("Read april tag!");
                    telemetry.update();
                    break;
                }
            }

            detector.stop();
            //fieldManager = new FieldManager(hardwareMap, hardwareMap.get(WebcamName.class, "webcam"),
            //        1280, 720, 0, 0, .35, 200, telemetry);
        }).start();

        waitForStart();

        PathChain pathChain = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                follower.getPose(),
                                new Pose(follower.getPose().getX(), follower.getPose().getY() + 40)
                        )
                )
                .setLinearHeadingInterpolation(follower.getHeading(), 3.14/2)
                .build();

        follower.followPath(pathChain);
        while(follower.isBusy() && !isStopRequested()) {
            follower.update();
        }

        OpModeState.isRunning = false;
    }
}