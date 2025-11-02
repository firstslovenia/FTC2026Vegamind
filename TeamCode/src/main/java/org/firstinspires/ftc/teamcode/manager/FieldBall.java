package org.firstinspires.ftc.teamcode.manager;

import org.firstinspires.ftc.teamcode.color.BallColor;

public class FieldBall {

    FieldBall(double x, double y, BallColor color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }
    double x, y;
    BallColor color;
}
