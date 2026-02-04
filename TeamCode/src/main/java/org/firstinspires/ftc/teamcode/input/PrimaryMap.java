package org.firstinspires.ftc.teamcode.input;

import com.qualcomm.robotcore.hardware.Gamepad;

public class PrimaryMap extends InputMap{

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
}
