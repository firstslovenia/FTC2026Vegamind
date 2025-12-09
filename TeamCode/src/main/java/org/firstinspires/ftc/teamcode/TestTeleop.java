package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;


import org.firstinspires.ftc.teamcode.magazine.Magazine;
import org.firstinspires.ftc.teamcode.shooter.Shooter;
import org.firstinspires.ftc.teamcode.manager.ShooterManager;

@TeleOp(name = "TestTeleop", group = "FTC 26")
public class TestTeleop extends OpMode {
    Magazine magazine;
    Shooter shooter;
    ShooterManager shooterManager;
    @Override
    public void init() {
        ColorSensor[] colorSensors = {hardwareMap.get(ColorSensor.class, "color1"),
                hardwareMap.get(ColorSensor.class, "color2"),
                hardwareMap.get(ColorSensor.class, "color3")};
        shooter = new Shooter(hardwareMap.get(DcMotor.class, "shooter1"), hardwareMap.get(DcMotor.class, "shooter2"));
        magazine = new Magazine(hardwareMap.get(DcMotor.class, "magazine"), hardwareMap.get(Servo.class, "gateServo"), hardwareMap.get(AnalogInput.class, "potentiometer"),
                colorSensors);

        shooterManager = new ShooterManager(magazine, shooter, 23);

        shooterManager.start();
        while(!shooterManager.shoot(3)) {
            shooterManager.update(telemetry);
            magazine.update(telemetry);
            telemetry.update();
        }
        telemetry.addLine("READY");

        while(true) {
            shooterManager.update(telemetry);
            magazine.update(telemetry);
            telemetry.update();
        }
    }

    @Override
    public void loop() {
        shooterManager.update();
        magazine.update(telemetry);
        telemetry.addData("state ", shooterManager.isActive());
        telemetry.update();
    }
}
