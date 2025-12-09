package org.firstinspires.ftc.teamcode.input;

import com.qualcomm.robotcore.hardware.Gamepad;

public class PrimaryMap extends InputMap{

    boolean previousShooterInput = false;
    boolean currentShooterState = false;

    public PrimaryMap(Gamepad gamepad) {
       super(gamepad);
    }

    @Override
    public double driveX() {
        return gamepad.right_stick_x;
    }

    @Override
    public double driveY() {
        return gamepad.right_stick_y;
    }

    @Override
    public double rotateX() {
        return gamepad.left_stick_x;
    }

    public int getBallIndex() {
        int selectCount = 0;
        int currSelection = -1;

        if(gamepad.dpad_left) {
            currSelection = 0;
            selectCount++;
        }
        if(gamepad.dpad_up) {
            currSelection = 1;
            selectCount++;
        }
        if(gamepad.dpad_right){
            currSelection = 2;
            selectCount++;
        }

        if(selectCount != 1) return -1; //very intentional
        // if there are multiple simultaneous presses we dont want
        // ambiguity so we dont do jack until the user finally decides
        // what to press

        return currSelection;
    }

    public boolean runShooter() {
        if(gamepad.cross == previousShooterInput) return currentShooterState;

        previousShooterInput = !previousShooterInput;

        if(previousShooterInput) currentShooterState = !currentShooterState;

        return currentShooterState;
    }

    public boolean turnTowardsBasket() {
        return gamepad.circle;
    }

    public boolean openGate() {
        return gamepad.triangle;
    }
}
