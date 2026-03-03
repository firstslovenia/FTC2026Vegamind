package org.firstinspires.ftc.teamcode.shooter;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class BallIO {
    private DcMotor shooter;
    double maxF = 0.0;

    public BallIO(DcMotor shooter, DcMotorSimple.Direction dir, double maxF) {
        this.shooter = shooter;
        this.maxF = maxF;

        shooter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        shooter.setPower(0.0);
       shooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
       shooter.setDirection(dir);
    }

    public void windup() {
        shooter.setPower(maxF);
    }

    public boolean isWound() {
        return shooter.getPower() != 0.0;
    }

    public void winddown() {
        shooter.setPower(0.0f);
    }
}

