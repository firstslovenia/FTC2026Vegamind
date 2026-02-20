package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad1;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad2;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.auto.Constants;
import org.firstinspires.ftc.teamcode.drive.Drive;
import org.firstinspires.ftc.teamcode.input.PrimaryMap;
import org.firstinspires.ftc.teamcode.input.SecondaryMap;
import org.firstinspires.ftc.teamcode.magazine.Magazine;
import org.firstinspires.ftc.teamcode.manager.ShooterManager;
import org.firstinspires.ftc.teamcode.pathing.PedroPathEx;
import org.firstinspires.ftc.teamcode.shooter.BallIO;

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
    PedroPathEx pedroPathEx;


    double targetHeading = 0;

    double basketX, basketY;

    Pose prevPose;
    @Override
    public void init() {

        primaryMap = new PrimaryMap(gamepad1);
        secondaryMap = new SecondaryMap(gamepad2);
        // magazine = new Magazine(hardwareMap.get(DcMotor.class, "magazine"), hardwareMap.get(Servo.class, "helpServo"), hardwareMap.get(TouchSensor.class, "intakeSensor"),
        //       hardwareMap.get(TouchSensor.class, "outtakeSensor"), hardwareMap.get(ColorSensor.class, "colorAlt"), hardwareMap.get(DistanceSensor.class, "distance"));
        shooter = new BallIO(hardwareMap.get(DcMotor.class, "shooter"));
        intake = new BallIO(hardwareMap.get(DcMotor.class, "intake"));
        shooterManager = new ShooterManager(magazine, shooter, 23);
        follower = Constants.createFollower(hardwareMap);
        drive = new Drive(follower, new Pose());
        pedroPathEx = new PedroPathEx(follower);
        follower.setPose(new Pose(56, 8));
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
        //intake.windup();
        follower.followPath(pedroPathEx.Path2);
        waitF();
        // Rotate magazine here
        follower.followPath(pedroPathEx.Path3);
        waitF();
        // Rotate magazine here
        follower.followPath(pedroPathEx.Path4);
        waitF();
        // Rotate magazine here; move uptop
        //intake.winddown();

        // Move to shoot
        follower.followPath(pedroPathEx.Path5);
        waitF();
        follower.followPath(pedroPathEx.Path6);
        waitF();
        follower.followPath(pedroPathEx.Path7);
        waitF();
        // Shooting sequence here idk just rotate the mag and do the thing
        //shooterManager.start();
        //while(shooterManager.isActive());
        // shooty shooty

        follower.followPath(pedroPathEx.Path8);
        waitF();
        //intake.windup();
        follower.followPath(pedroPathEx.Path9);
        waitF();
        //rotate mag
        follower.followPath(pedroPathEx.Path10);
        waitF();
        //rotate mag
        follower.followPath(pedroPathEx.Path11);
        waitF();
        //intake.winddown();

        // Move to shoot
        follower.followPath(pedroPathEx.Path12);
        waitF();
        follower.followPath(pedroPathEx.Path13);
        waitF();

        //shooterManager.start();
        //while(shooterManager.isActive());
        // shooty shooty

        follower.followPath(pedroPathEx.Path14);
        waitF();
        //intake.windup();
        follower.followPath(pedroPathEx.Path15);
        waitF();
        //rotate mag
        follower.followPath(pedroPathEx.Path16);
        waitF();
        //rotate mag
        follower.followPath(pedroPathEx.Path17);
        waitF();
        //intake.winddown();

        // move to shoot
        follower.followPath(pedroPathEx.Path18);
        waitF();
        follower.followPath(pedroPathEx.Path19);
        waitF();

        //shooterManager.start();
        //while(shooterManager.isActive());
        // shooty shooty
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
