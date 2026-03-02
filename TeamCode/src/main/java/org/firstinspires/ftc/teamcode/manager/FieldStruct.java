package org.firstinspires.ftc.teamcode.manager;

import org.firstinspires.ftc.teamcode.color.BallColor;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class FieldStruct {
    @Getter @Setter
    List<FieldBall> balls;

    @Getter @Setter
    BallColor color;

    public FieldStruct(List<FieldBall> balls, BallColor color) {
        this.balls = balls;
        this.color = color;
    }
}
