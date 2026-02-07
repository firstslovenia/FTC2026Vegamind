package org.firstinspires.ftc.teamcode.manager;

import android.util.Pair;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.vision.BallPipeline;

import java.util.ArrayList;
import java.util.List;

import lombok.var;

public class FieldManager {
    List<FieldBall> balls = new ArrayList<>();

    final int FIELD_SIZE = 366; //cm

    final int GRID_SIZE = 32;
    final double GRID_LENGTH = (double) FIELD_SIZE / GRID_SIZE;

    List<List<List<FieldBall>>> ballFieldPos;

    double camOffsetX, camOffsetY, horFov, verFov;
    double streamWidth, streamHeight;

    BallPipeline pipeline;

    public FieldManager(HardwareMap hardwareMap, WebcamName webcamName, double streamWidth, double streamHeight, double camOffsetX, double camOffsetY, double fov) {
        this.camOffsetX = camOffsetX;
        this.camOffsetY = camOffsetY;
        this.streamWidth = streamWidth;
        this.streamHeight = streamHeight;
        this.horFov = fov;
        this.verFov = (streamHeight / streamWidth) * fov;

        ballFieldPos = new ArrayList<>(GRID_SIZE);
        for(var list : ballFieldPos) {
            list = new ArrayList<>(GRID_SIZE);
            for(var balls : list) {
                balls = new ArrayList<>();
            }
        }

        pipeline = new BallPipeline(webcamName, (int)streamWidth, (int)streamHeight,
                hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName()));
    }

    void update(double x, double y, double pitch) {
        List<FieldBall> balls = pipeline.getBallContours();
        if(balls == null) return;

        clearSeenArea(x, y, pitch); // TODO DONT REPEAT ALL OF THE MATH 1000X

        for(FieldBall ball : balls) {
           FieldBall fieldBall = new FieldBall
                    (x + computeBallPosX(ball, pitch), y + computeBallPosY(ball, pitch), ball.getColor());

            ballFieldPos.get(
                    (int)Math.floor(fieldBall.getX() / (GRID_LENGTH))
            ).get(
                    (int)Math.floor(fieldBall.getY() / (GRID_LENGTH))
            ).add(fieldBall);
        }
    }

    // before we insert new balls over the visible field area we have to clear what was there before so we
    // don't have duplicate / old data
    void clearSeenArea(double x, double y, double pitch) {
        double trapezoidOffsetX = Math.sin(pitch) * camOffsetY;

        double baseBottomLength = camOffsetY * 2 * Math.tan(horFov / 2);
        double bottomLength = baseBottomLength + camOffsetY / Math.sin(Math.PI/2 - horFov) * 2;

        double sideLength = camOffsetY * Math.tan(verFov);

        double topLength = bottomLength + 2 * sideLength * Math.sin(3.14159/2 - horFov / 2);
        double height = sideLength * Math.cos(90 - horFov / 2);

        x += trapezoidOffsetX;

        for(double xIdx = x; xIdx < x + height; xIdx += GRID_LENGTH) {
            for(double yIdx = y - topLength / 2; yIdx < y + sideLength / 2.0; yIdx += GRID_LENGTH) {
                ballFieldPos.get(
                        (int)Math.floor(x / (GRID_LENGTH))
                ).get(
                        (int)Math.floor(y / (GRID_LENGTH))
                ).clear(); // clear all the balls in that square
            } // also we kinda just shape the trapezoid into a square for this cause holy fuck I am not
            //overcomplicating this any further
        }
    }

    double computeBallPosX(FieldBall ball, double pitch) {
        double x1 = camOffsetX; // left point of triangle

        double length = Math.tan(pitch + horFov) * camOffsetY; // length of the bottom side of the view

        double x2 = x1 + length; // right point of triangle

        //also x and y are flipped because the camera stream is flipped
        double relativeBallPosX = Math.cos(ball.y / streamHeight/*normalize*/ * 3.14159 / 2 /*so we get normalized output*/);//normalized ball position relative to triangle, this calc is to account for distortion near the dges
        //also it is of note cos is used here because the distortion isn't linear

        double scaledBallPosX = relativeBallPosX * length + x1;

        return scaledBallPosX;
    }

    //retardation, im not taking pitch distortion into account when that's literally the only thing I should be doing
    double computeBallPosY(FieldBall ball, double pitch) {
        double trapezoidOffsetX = Math.sin(pitch) * camOffsetY;

        double baseBottomLength = camOffsetY * 2 * Math.tan(horFov / 2); // get the bottom line length of the trapezoid
        //first we halve the horFov to get the right triangle then we double it again to get the entire length

        double innerAngle = Math.PI / 2 - horFov / 2;

        double bottomLength = baseBottomLength + trapezoidOffsetX / Math.sin(innerAngle) * 2;
        // we calculate this in the same fashion we calculate the top length, we do this to account for pitch

        double sideLength = camOffsetY * Math.tan(verFov);// the hypotenuse of the triangle with points
        //A: the far point of the bottom line
        //B: far point of the bottom line + camOffsetY
        //C: far point on the top line

        double topLength = bottomLength + 2 * sideLength * Math.sin(innerAngle);
        double height = sideLength * Math.cos(Math.PI / 2 - horFov / 2); // using the side length we can make 2 right triangles at the
        //corners of the trapezoid, from that figure out their length on the part where they cover the top length of the trapezoid.
        //so that * 2 + bottomLineLength = topLineLength
        //we will use the height for interpolation

        //note for now ballPosY does not take the pitch into account as of now!!
        //this means we don't take into account some of the distortion, hopefully trivial

        double ballPosY = trapezoidOffsetX + (ball.x / streamWidth) * topLength;

        return ballPosY;
    }


}
