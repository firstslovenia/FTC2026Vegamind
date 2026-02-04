package org.firstinspires.ftc.teamcode.input;

import com.qualcomm.robotcore.hardware.Gamepad;

public class SecondaryMap extends InputMap{

    boolean previousIncBallInput = false;
    boolean previousIntakeInput = false;
    boolean intakeState = false;

    boolean previousSetupMagInput = false;

    public SecondaryMap(Gamepad gamepad) {
       super(gamepad);
    }

    @Override
    public double driveX() {
        return gamepad.right_stick_x * 0.2;
    }

    @Override
    public double driveY() {
        return gamepad.right_stick_y * 0.2;
    }

    @Override
    public double rotateX() {
        return gamepad.left_stick_x * 0.2;
    }

    public boolean startShooting() {
       return gamepad.square;
    }

    public boolean stopShooting() {
        return gamepad.circle;
    }

    public boolean incBall() {
        if(previousIncBallInput && gamepad.dpad_up) return false;
        previousIncBallInput = gamepad.dpad_up;
        return gamepad.dpad_up;
    }

    public boolean toggleIntake() {
        intakeState = (!previousIntakeInput && gamepad.triangle) ? !intakeState : intakeState;
        previousIntakeInput = gamepad.triangle;

        return intakeState;
    }

    public boolean setupMag() {
        if(previousSetupMagInput && gamepad.x) return false;
        previousSetupMagInput = gamepad.x;
        return gamepad.x;
    }
}
