package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.drivetrain.Drivetrain;
import org.firstinspires.ftc.teamcode.drivetrain.Motors;
import org.firstinspires.ftc.teamcode.drivetrain.TankDrive;
import org.firstinspires.ftc.teamcode.input.InputMap;
import org.firstinspires.ftc.teamcode.input.PrimaryMap;
import org.firstinspires.ftc.teamcode.intake.Intake;
import org.firstinspires.ftc.teamcode.magazine.Magazine;
import org.firstinspires.ftc.teamcode.shooter.Shooter;

@TeleOp(name="Main teleop", group="FTC 26")
public class MainTeleop extends OpMode{

    Drivetrain drivetrain;
    Shooter shooter;
    Magazine magazine;
    Intake intake;
    PrimaryMap inputMap;

    @Override
    public void init() {
        Motors motors = new Motors(hardwareMap, "rearLeft", "rearRight", "frontLeft", "frontRight");

        PrimaryMap inputMap = new PrimaryMap(gamepad1);

        drivetrain = new TankDrive(hardwareMap, motors);
    }

    @Override
    public void loop() {
        drivetrain.run(inputMap);
    }
}

