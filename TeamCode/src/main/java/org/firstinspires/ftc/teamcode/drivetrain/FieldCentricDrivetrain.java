package org.firstinspires.ftc.teamcode.drivetrain;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.input.InputMap;

public class FieldCentricDrivetrain extends Drivetrain {
    public FieldCentricDrivetrain(HardwareMap hardwareMap, IMU imu, Motors motors) {
        super(hardwareMap, imu, motors);
    }

    @Override
    public void run(InputMap inputMap) {
        double robotDirection = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

        double x = inputMap.driveX() * Math.cos(-robotDirection) - inputMap.driveY() * Math.sin(-robotDirection);
        double y  = inputMap.driveX() * Math.sin(-robotDirection) + inputMap.driveY() * Math.cos(-robotDirection);
        double rot = inputMap.rotateX();

        //  TODO imu reset on button press

        Pose2d move = new Pose2d(
                -y,
                -x,
                rot
        );

        drive(move);
    }

    public void drive(Pose2d pose2d) {
        this.setDrivePower(pose2d);
    }
}
