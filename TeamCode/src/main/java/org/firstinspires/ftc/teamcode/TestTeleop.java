package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;


import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.magazine.Magazine;

@TeleOp(name = "MagTest", group = "FTC 26")
public class TestTeleop extends OpMode {
    ColorSensor color;
    DcMotor motor;
    DcMotor mag;
    DistanceSensor distance;
    Magazine magazine;
    @Override
    public void init() {
        color = hardwareMap.get(ColorSensor.class, "colorAlt");
        motor = hardwareMap.get(DcMotor.class, "intake");
        distance = hardwareMap.get(DistanceSensor.class, "distance");

        magazine = new Magazine(hardwareMap.get(DcMotor.class, "magazine"), hardwareMap.get(Servo.class, "helpServo"), hardwareMap.get(TouchSensor.class, "intakeSensor"),
                hardwareMap.get(TouchSensor.class, "outtakeSensor"), hardwareMap.get(ColorSensor.class, "colorAlt"), hardwareMap.get(DistanceSensor.class, "distance"));
    }

    @Override
    public void loop() {
        if(gamepad1.a)
            magazine.setIntake();

        motor.setPower(gamepad1.left_trigger * 0.3);
        magazine.update(telemetry);

        telemetry.addData("intake", hardwareMap.get(TouchSensor.class, "intakeSensor").isPressed());

        telemetry.update();

    }
}
