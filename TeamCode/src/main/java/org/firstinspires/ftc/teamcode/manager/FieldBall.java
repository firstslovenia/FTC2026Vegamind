package org.firstinspires.ftc.teamcode.manager;

import org.firstinspires.ftc.teamcode.color.BallColor;

import lombok.Getter;
import lombok.Setter;

@Getter
public class FieldBall {

    public FieldBall(double x, double y) {
        this.x = x;
        this.y = y;
        this.color = BallColor.NONE;
    }

    public FieldBall(double x, double y, double realX, double realY) {
        this.x = x;
        this.y = y;
        this.realX = realX;
        this.realY = realY;
        this.color = BallColor.NONE;
    }

    public FieldBall(double x, double y, BallColor color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    double x, y;

    BallColor color;

    @Setter
    double realX, realY;

    @Setter
    double pxw;
}
