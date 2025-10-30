package org.firstinspires.ftc.teamcode.drivetrain;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.teamcode.input.InputMap;

public class TankDrive extends Drivetrain {

    public TankDrive(HardwareMap hardwareMap, Motors motors) {
        super(hardwareMap, hardwareMap.get(IMU.class, "imu"), motors);
        motors.rearRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motors.rearLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motors.frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motors.frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    @Override
    public void run(InputMap inputMap) {
        motors.rearRight.setPower(inputMap.driveY());
        motors.frontRight.setPower(inputMap.driveY());

        motors.rearLeft.setPower(inputMap.driveX());
        motors.frontLeft.setPower(inputMap.driveX());
    }
}
