package org.firstinspires.ftc.teamcode.manager;

import org.firstinspires.ftc.teamcode.color.BallColor;

import lombok.Getter;

public class FieldBall {

    public FieldBall(double x, double y) {
        this.x = x;
        this.y = y;
        this.color = BallColor.NONE;
    }

    public FieldBall(double x, double y, BallColor color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    @Getter
    double x, y;

    @Getter
    BallColor color;
}
