package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;


import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.magazine.Magazine;

@TeleOp(name = "MagResistance", group = "FTC 26")
public class MagResistance extends OpMode {
    DcMotorEx motorEx;
    @Override
    public void init() {
//        color = hardwareMap.get(ColorSensor.class, "colorAlt");
//        motor = hardwareMap.get(DcMotor.class, "intake");
//        distance = hardwareMap.get(DistanceSensor.class, "distance");
//
        motorEx = hardwareMap.get(DcMotorEx.class, "magazine");
        motorEx.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    @Override
    public void start() {
    }

    @Override
    public void loop() {
//        if(gamepad1.a)
//            magazine.setIntake();
//
//        motor.setPower(gamepad1.left_trigger * 0.3);
//        magazine.update(telemetry);

        if(gamepad1.dpad_up) motorEx.setPower(1.0);
        else motorEx.setPower(0.0);

        telemetry.addData("current", motorEx.getCurrent(CurrentUnit.MILLIAMPS));
        telemetry.update();

    }
}
