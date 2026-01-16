package org.firstinspires.ftc.teamcode.intake;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class Intake {
    private DcMotor intakeMotor;
    float power = 1.0f;

    public Intake(DcMotor intakeMotor) {
        this.intakeMotor = intakeMotor;

        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        intakeMotor.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    public void fuckywuckypowerupy() {
        intakeMotor.setPower(power);
        if (power == 1.0f) {
            power = 0.0f;
        } else {
            power = 1.0f;
        }
    }
}

