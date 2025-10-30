package org.firstinspires.ftc.teamcode.drivetrain;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.teamcode.input.InputMap;

public class RobotCentricDrivetrain extends Drivetrain {
    public RobotCentricDrivetrain( HardwareMap hardwareMap, IMU imu, Motors motors) {
        super(hardwareMap, imu, motors);
    }

    public void run(InputMap inputMap) {
        double y = inputMap.driveY();
        double x = inputMap.driveX() * 1.1;
        double rx = inputMap.rotateX();

    /* if (x == 0 && y == 0 && rx == 0) {
            y = secondaryInputMap.getDriveY();
            x = secondaryInputMap.getDriveX() * 1.1;
        }*/

        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
        double frontLeftPower = (y + x + rx) / denominator;
        double backLeftPower = (y - x + rx) / denominator;
        double frontRightPower = (y - x - rx) / denominator;
        double backRightPower = (y + x - rx) / denominator;

        motors.frontLeft.setPower(frontLeftPower);
        motors.rearLeft.setPower(backLeftPower);
        motors.frontRight.setPower(frontRightPower);
        motors.rearRight.setPower(backRightPower);
    }
}
