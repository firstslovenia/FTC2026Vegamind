package org.firstinspires.ftc.teamcode.manager;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import android.renderscript.Script;
import android.util.Pair;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.vision.BallPipeline;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import lombok.var;

public class FieldManager {
    List<FieldBall> balls = new ArrayList<>();

    final int FIELD_SIZE = 366; //cm

    final int GRID_SIZE = 32;
    final double GRID_LENGTH = (double) FIELD_SIZE / GRID_SIZE;

    //List<List<List<FieldBall>>> ballFieldPos;
    List<FieldBall> fieldBalls;

    double camOffsetX, camOffsetY, horFov, verFov;
    double streamWidth, streamHeight;

    BallPipeline pipeline;

    double focalLengthPx;
    Telemetry telemetry;

    public FieldManager(HardwareMap hardwareMap, WebcamName webcamName, double streamWidth, double streamHeight, double camOffsetX, double camOffsetY, double fov, Telemetry telemetry) {
        this.camOffsetX = camOffsetX;
        this.camOffsetY = camOffsetY;
        this.streamWidth = streamWidth;
        this.streamHeight = streamHeight;
        this.horFov = fov;
        this.verFov = (streamHeight / streamWidth) * fov;
        this.focalLengthPx = (3.67 / 4.8) * streamWidth; // (focalLengthMm / sensorWidthMm) * streamWidth
        this.telemetry = telemetry;

        /*ballFieldPos = new ArrayList<>(GRID_SIZE);
        for(var list : ballFieldPos) {
            list = new ArrayList<>(GRID_SIZE);
            for(var balls : list) {
                balls = new ArrayList<>();
            }
        }*/
        fieldBalls = new ArrayList<>();

        pipeline = new BallPipeline(webcamName, (int) streamWidth, (int) streamHeight,
                hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName()));
    }

    public void computePositions(double pitch, double camPlaneX, double camPlaneY) {
        List<FieldBall> balls = pipeline.getBallContours();
        if (balls == null || balls.isEmpty()) { return; }
        this.telemetry.addData("Ball Count", balls.toArray().length);

        for (FieldBall fieldBall : balls) {
            if (fieldBall == null) { continue; }

            // Normalize pixel
            double Xn = (fieldBall.getX() - this.streamWidth / 2) / this.focalLengthPx;
            double Yn = (fieldBall.getY() - this.streamHeight / 2) / this.focalLengthPx;

            // Apply pitch rotation
            double rx = Xn;
            double ry = Yn * Math.cos(pitch) - Math.sin(pitch); // In radians
            double rz = Yn * Math.sin(pitch) + Math.cos(pitch); // In radians

            // Solve for intersection with ground
            double t = -(this.camOffsetY / rz); // camOffsetY is camera height

            // Final coordinates
            fieldBall.realX = camPlaneX + t * rx;
            fieldBall.realY = camPlaneY + t * ry;

            // Temporary, write out positions:
            this.telemetry.addData("Ball X:", fieldBall.realX);
            this.telemetry.addData("Ball Y:", fieldBall.realY);
            this.telemetry.update();
        }
    }
}
