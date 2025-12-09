package org.firstinspires.ftc.teamcode.shooter;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class Shooter {
    private DcMotor motor1;
    private DcMotor motor2;

    public Shooter(DcMotor motor1, DcMotor motor2) {
       this.motor1 = motor1;
       this.motor2 = motor2;

       motor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
       motor2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
       motor1.setDirection(DcMotorSimple.Direction.FORWARD);
       motor2.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    public void windup() {
        motor1.setPower(1.0f);
        motor2.setPower(-1.0f);
    }

    public boolean isWound() {
        return motor1.getPower() != 0.0;
    }

    public void winddown() {
        motor1.setPower(0.0f);
        motor2.setPower(0.0f);
    }
}

