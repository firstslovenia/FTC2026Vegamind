package org.firstinspires.ftc.teamcode.shooter;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class BallIO {
    private DcMotor shooter;

    public BallIO(DcMotor shooter) {
        this.shooter = shooter;

        shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

       shooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
       shooter.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    public void windup() {
        shooter.setPower(0.3f);
    }

    public boolean isWound() {
        return shooter.getPower() != 0.0;
    }

    public void winddown() {
        shooter.setPower(0.0f);
    }
}

