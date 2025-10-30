package org.firstinspires.ftc.teamcode.input;

import com.qualcomm.robotcore.hardware.Gamepad;

public abstract class InputMap {
    Gamepad gamepad;

    InputMap(Gamepad gamepad) {
        this.gamepad = gamepad;
    }

    abstract public double driveX();
    abstract public double driveY();
    abstract public double rotateX();
}
