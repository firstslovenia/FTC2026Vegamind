package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.drivetrain.Drivetrain;
import org.firstinspires.ftc.teamcode.drivetrain.FieldCentricDrivetrain;
import org.firstinspires.ftc.teamcode.drivetrain.Motors;
import org.firstinspires.ftc.teamcode.drivetrain.TankDrive;
import org.firstinspires.ftc.teamcode.input.PrimaryMap;
import org.firstinspires.ftc.teamcode.magazine.Magazine;
import org.firstinspires.ftc.teamcode.shooter.Shooter;

@TeleOp(name="Main teleop", group="FTC 26")
public class MainTeleop extends OpMode{

    Drivetrain drivetrain;
    Shooter shooter;
    Magazine magazine;
    PrimaryMap inputMap;

    @Override
    public void init() {
        Motors motors = new Motors(hardwareMap, "rearLeft", "rearRight", "frontLeft", "frontRight");

        ColorSensor[] colorSensors = {hardwareMap.get(ColorSensor.class, "color1"),
                                      hardwareMap.get(ColorSensor.class, "color2"),
                                      hardwareMap.get(ColorSensor.class, "color3")};

        inputMap = new PrimaryMap(gamepad1);
        drivetrain = new FieldCentricDrivetrain(hardwareMap, hardwareMap.get(IMU.class, "imu"), motors);
        shooter = new Shooter(hardwareMap.get(DcMotor.class, "shooter1"), hardwareMap.get(DcMotor.class, "shooter2"));
        magazine = new Magazine(hardwareMap.get(DcMotor.class, "magazineServo"), hardwareMap.get(Servo.class, "gateServo"), hardwareMap.get(AnalogInput.class, "potentiometer"),
                colorSensors);

        while(!magazine.init());
    }

    @Override
    public void loop() {drivetrain.run(inputMap);
        magazine.rotateToBall(inputMap.getBallIndex());

        if(inputMap.runShooter()) shooter.windup();
        else shooter.winddown();

        if(inputMap.openGate()) magazine.openGate();
        else magazine.closeGate();
    }
}

