package org.firstinspires.ftc.teamcode.input;

import com.qualcomm.robotcore.hardware.Gamepad;

public class PrimaryMap extends InputMap{

    public PrimaryMap(Gamepad gamepad) {
       super(gamepad);
    }

    @Override
    public double driveX() {
        return gamepad.left_stick_x;
    }

    @Override
    public double driveY() {
        return gamepad.left_stick_y;
    }

    @Override
    public double rotateX() {
        return gamepad.right_trigger - gamepad.left_trigger;
    }
}
