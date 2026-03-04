package org.firstinspires.ftc.teamcode;

//exp  0 magazin

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.teamcode.auto.Constants;
import org.firstinspires.ftc.teamcode.drive.Drive;
import org.firstinspires.ftc.teamcode.input.PrimaryMap;
import org.firstinspires.ftc.teamcode.input.SecondaryMap;
import org.firstinspires.ftc.teamcode.intake.Intake;
import org.firstinspires.ftc.teamcode.magazine.Magazine;
import org.firstinspires.ftc.teamcode.manager.Alliance;
import org.firstinspires.ftc.teamcode.manager.ShooterManager;
import org.firstinspires.ftc.teamcode.shooter.BallIO;

public class MainTeleop extends OpMode {

    BallIO shooter;
    BallIO intake;
    Magazine magazine;

    PrimaryMap primaryMap;
    SecondaryMap secondaryMap;
    Drive drive;
    Follower follower;
    ShooterManager shooterManager;


    double targetHeading = 0;

    double basketX, basketY;


    Alliance alliance;

    Pose getBasketPos() {
        Pose bPos = new Pose(15, 130);
        Pose rPos = new Pose(130, 130);

        return alliance == Alliance.BLUE ? bPos : rPos;
    }

    protected void turnTowardBasket(Alliance alliance) {
        Pose tPos = getBasketPos();

        double heading = Math.atan2(tPos.getY() - follower.getPose().getY(), tPos.getX() - follower.getPose().getX());
        follower.turnTo(heading);
    }

    protected double getBasketDistance() {
        Pose tPos = getBasketPos();

        return Math.sqrt((
                Math.pow(follower.getPose().getX() - tPos.getX(), 2) +
                Math.pow(follower.getPose().getY() - tPos.getY(), 2)
        ));
    }

    Pose prevPose;
    @Override
    public void init() {

        primaryMap = new PrimaryMap(gamepad1);
        secondaryMap = new SecondaryMap(gamepad2);
       // magazine = new Magazine(hardwareMap.get(DcMotor.class, "magazine"), hardwareMap.get(Servo.class, "helpServo"), hardwareMap.get(TouchSensor.class, "intakeSensor"),
         //       hardwareMap.get(TouchSensor.class, "outtakeSensor"), hardwareMap.get(ColorSensor.class, "colorAlt"), hardwareMap.get(DistanceSensor.class, "distance"));
        shooter = new BallIO(hardwareMap.get(DcMotor.class, "shooter"), DcMotorSimple.Direction.REVERSE, 1.0);
        intake = new BallIO(hardwareMap.get(DcMotor.class, "intake"), DcMotorSimple.Direction.FORWARD, 0.4);
        shooterManager = new ShooterManager(magazine, shooter, hardwareMap.get(Servo.class, "shooterServo"), 23, 100);
        follower = Constants.createFollower(hardwareMap);
        drive = new Drive(follower, new Pose());

        /*
        follower.followPath(path);
        while(follower.isBusy()) {
            follower.update();
        }
        */
    }

    @Override
    public void loop() {

        if(secondaryMap.startShooting())
            shooterManager.startShooting(getBasketDistance());
        if(secondaryMap.stopShooting())
            shooterManager.stopShooting();
        if(secondaryMap.incBall())
            shooterManager.incCurrSlot();
        if(secondaryMap.toggleIntake())
            intake.windup();
        else
            intake.winddown();

        if(secondaryMap.setupMag())
            magazine.setIntake();


        drive.drive(primaryMap.driveX(), primaryMap.driveY(), primaryMap.rotateX());
    }
}

