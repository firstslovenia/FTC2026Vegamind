package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.auto.Constants;
import org.firstinspires.ftc.teamcode.magazine.Magazine;
import org.firstinspires.ftc.teamcode.manager.ShooterManager;
import org.firstinspires.ftc.teamcode.paths.PathState;
import org.firstinspires.ftc.teamcode.paths.Paths;
import org.firstinspires.ftc.teamcode.shooter.Shooter;
import org.firstinspires.ftc.teamcode.vision.AprilTagDetector;

abstract class Auto extends OpMode{
    Magazine magazine;
    Shooter shooter;
    ShooterManager shooterManager;

    AprilTagDetector detector;

    Follower follower;
    Paths paths;


    PathState pathState = PathState.START;

    ElapsedTime refillTimer = null;
    final int refillTime = 5000;

    abstract void createPaths();

    //EVERYTHING MUST BE, IS, AND WILL BE NONBLOCKING OR I WILL STRANGLE SOMEONE
    @Override
    public void init() {
        follower = Constants.createFollower(hardwareMap);
        createPaths();
        ColorSensor[] colorSensors = {hardwareMap.get(ColorSensor.class, "color1"),
                hardwareMap.get(ColorSensor.class, "color2"),
                hardwareMap.get(ColorSensor.class, "color3")};

        shooter = new Shooter(hardwareMap.get(DcMotor.class, "shooter1"), hardwareMap.get(DcMotor.class, "shooter2"));
        magazine = new Magazine(hardwareMap.get(DcMotor.class, "magazine"), hardwareMap.get(Servo.class, "gateServo"), hardwareMap.get(AnalogInput.class, "potentiometer"),
                colorSensors);
        detector = new AprilTagDetector(
                hardwareMap.get(WebcamName.class, "webcam")
        );
    }


    void spotAprilTag() {
        int id = detector.update();

        if(id < 21 || id > 23) return;

        shooterManager = new ShooterManager(magazine, shooter, id);
    }

    @Override
    public void loop() {

        if(shooterManager == null) {
            spotAprilTag();
            return;
        }
        else
            shooterManager.update();

        telemetry.addData("aaaa", pathState);
        telemetry.update();

        switch(pathState) {
            case START:
                follower.followPath(paths.startPath);
                pathState = PathState.SHOOT;
                shooterManager.start();
                break;

            case SHOOT:
                if(follower.isBusy()) break;
                if(!shooterManager.shoot(3)) break; // we failed so we won't change state, but simply try again next iter
                if(shooterManager.isActive()) break;

                pathState = PathState.REFILL_PATH;

                follower.followPath(paths.endPath);
                break;

            case REFILL_PATH:
                if(follower.isBusy()) break;
                if(refillTimer == null) {
                    refillTimer = new ElapsedTime();
                    refillTimer.startTime();
                }

                if(refillTimer.milliseconds() < refillTime) break;

                return; // NO REFILL :(
/*
                follower.followPath(paths.shootPath);
                pathState = PathState.SHOOT_PATH;
                shooterManager.start();

                refillTimer = null;

                break;
*/
            case SHOOT_PATH:
                pathState = PathState.SHOOT;
                break;
        }

        follower.update();
    }
}

