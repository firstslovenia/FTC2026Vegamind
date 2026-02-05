package org.firstinspires.ftc.teamcode.manager;

import android.util.Pair;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.vision.BallPipeline;

import java.util.ArrayList;
import java.util.List;

public class FieldManager {
    List<FieldBall> balls = new ArrayList<>();

    final int FIELD_SIZE = 366; //cm

    List<Pair<Double, Double>> ballFieldPos;

    double camOffsetX, camOffsetY, horFov, verFov;
    double streamWidth, streamHeight;

    BallPipeline pipeline;

    public FieldManager(WebcamName webcamName, double streamWidth, double streamHeight, double camOffsetX, double camOffsetY, double fov) {
        this.camOffsetX = camOffsetX;
        this.camOffsetY = camOffsetY;
        this.streamWidth = streamWidth;
        this.streamHeight = streamHeight;
        this.horFov = fov;
        this.verFov = (streamHeight / streamWidth) * fov;

        //pipeline = new BallPipeline(webcamName, streamWidth, streamHeight);
    }

    double computeBallPosX(FieldBall ball, double pitch) {
        double x1 = camOffsetX; // left point of triangle

        double length = Math.tan(pitch + horFov) * camOffsetY; // length of the bottom side of the view

        double x2 = x1 + length; // right point of triangle

        //also x and y are flipped because the camera stream is flipped
        double relativeBallPosX = Math.cos(ball.y / streamHeight/*normalize*/ * Math.PI / 2 /*so we get normalized output*/);//normalized ball position relative to triangle, this calc is to account for distortion near the dges
        //also it is of note cos is used here because the distortion isn't linear

        double scaledBallPosX = relativeBallPosX * length + x1;

        return scaledBallPosX;
    }

    double computeBallPosY(FieldBall ball, double pitch) {
        double trapezoidOffsetX = Math.sin(pitch) * camOffsetY;

        double bottomLength = camOffsetY * 2 * Math.tan(horFov / 2); // get the bottom line length of the trapezoid
        //first we halve the horFov to get the right triangle then we double it again to get the entire length

        double sideLength = camOffsetY * Math.tan(verFov);// the hypotenuse of the triangle with points
        //A: the far point of the bottom line
        //B: far point of the bottom line + camOffsetY
        //C: far point on the top line

        double topLength = bottomLength + 2 * sideLength * Math.sin(Math.PI/2 - horFov / 2);
        double height = sideLength * Math.cos(Math.PI / 2 - horFov / 2); // using the side length we can make 2 right triangles at the
        //corners of the trapezoid, from that figure out their length on the part where they cover the top length of the trapezoid.
        //so that * 2 + bottomLineLength = topLineLength
        //we will use the height for interpolation

        //note for now ballPosY does not take the pitch into account as of now!!

        double ballPosY = trapezoidOffsetX + (ball.x / streamWidth) * topLength;

        return ballPosY;
    }


}
