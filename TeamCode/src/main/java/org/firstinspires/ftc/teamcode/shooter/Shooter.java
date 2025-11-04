package org.firstinspires.ftc.teamcode.shooter;

import com.qualcomm.robotcore.hardware.DcMotor;

public class Shooter {
    private DcMotor motor;
    public Shooter(DcMotor motor) {
       this.motor = motor;

       motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    public void windup() {
        motor.setPower(1.0f);
    }

    public boolean isWound() {
        return motor.getPower() != 0.0;
    }

    public void winddown() {
        motor.setPower(0.0f);
    }
}

