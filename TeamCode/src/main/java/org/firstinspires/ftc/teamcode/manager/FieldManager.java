package org.firstinspires.ftc.teamcode.manager;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.color.BallColor;
import org.firstinspires.ftc.teamcode.process.Process;
import org.firstinspires.ftc.teamcode.vision.BallPipeline;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import lombok.var;

public class FieldManager extends Process {
    List<FieldBall> balls = new ArrayList<>();

    final int FIELD_SIZE = 366; //cm
    final double BALL_SIZE = 12.5;
    final int GRID_SIZE = (int) Math.ceil(FIELD_SIZE / BALL_SIZE);

    List<List<List<FieldBall>>> fieldGrid;
    List<FieldBall> fieldBalls;

    double camOffsetX, camOffsetY, horFov, verFov;
    double streamWidth, streamHeight;

    BallPipeline pipeline;

    double focalLengthPx;
    Telemetry telemetry;

    double pitch, camPlaneX, camPlaneY;

    public FieldManager(HardwareMap hardwareMap, WebcamName webcamName, double streamWidth, double streamHeight,
                        double camOffsetX, double camOffsetY, double fov, long updateInterval, Telemetry telemetry) {
        super(updateInterval);
        this.camOffsetX = camOffsetX;
        this.camOffsetY = camOffsetY;
        this.streamWidth = streamWidth;
        this.streamHeight = streamHeight;
        this.horFov = fov;
        this.verFov = (streamHeight / streamWidth) * fov;
        this.focalLengthPx = (3.67 / 4.8) * streamWidth; // (focalLengthMm / sensorWidthMm) * streamWidth
        this.telemetry = telemetry;

        fieldBalls = new ArrayList<>();

        pipeline = new BallPipeline(webcamName, (int) streamWidth, (int) streamHeight,
                hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName()));
    }

    void initField() {
        fieldGrid = new ArrayList<>(GRID_SIZE);
        for(var list : fieldGrid) {
            list = new ArrayList<>(GRID_SIZE);
            for(var balls : list) {
                balls = new ArrayList<>();
            }
        }
    }

    public synchronized void updateCamInfo(double posX, double posY, double pitch) {
        camPlaneX = posX;
        camPlaneY = posY;
        this.pitch = pitch;
    }

    @Override
    protected synchronized void update() {
        List<FieldBall> balls = pipeline.getBallContours();
        BallColor currentColor = pipeline.getLastUpdateColor(); // technically a race condition possible but the lion isnt worried
        clearFieldArea(currentColor);

        for(FieldBall ball : balls) {
            computePositions(ball);
        }
    }

    void clearFieldArea(BallColor color) {
        FieldBall[] boundingBalls = { //we get a rect to clear the are in
                new FieldBall(0, streamHeight),
                new FieldBall(streamWidth, 0),
        };

        computePositions(boundingBalls[0]);
        computePositions(boundingBalls[1]);

        int startX =
                (int)Math.floor( Math.min(boundingBalls[0].realX, boundingBalls[1].realX) / BALL_SIZE);
        int startY =
                (int)Math.floor( Math.min(boundingBalls[0].realY, boundingBalls[1].realY) / BALL_SIZE);

        int endX =
                (int)Math.floor( Math.max(boundingBalls[0].realX, boundingBalls[1].realX) / BALL_SIZE);
        int endY =
                (int)Math.floor( Math.max(boundingBalls[0].realY, boundingBalls[1].realY) / BALL_SIZE);

        for(int y = startY; y < endY; y++) {
            for(int x = startX; x < endX; x++) {
                fieldGrid.get(y).get(x).removeIf(ball -> ball.color == color);
            }
        }
    }

    void computePositions(FieldBall fieldBall) {
        // Normalize pixel
        double Xn = (fieldBall.getX() - streamWidth / 2) / focalLengthPx;
        double Yn = (fieldBall.getY() - streamHeight / 2) / focalLengthPx;

        // Apply pitch rotation
        double rx = Xn;
        double ry = Yn * Math.cos(pitch) - Math.sin(pitch); // In radians
        double rz = Yn * Math.sin(pitch) + Math.cos(pitch); // In radians

        // Solve for intersection with ground
        double t = -(this.camOffsetY / rz); // camOffsetY is camera height

        // Final coordinates
        fieldBall.realX = camPlaneX + t * rx;
        fieldBall.realY = camPlaneY + t * ry;

        fieldGrid
                .get((int)Math.floor(fieldBall.realY / BALL_SIZE))
                .get((int)Math.floor(fieldBall.realX / BALL_SIZE))
                .add(fieldBall);

        if(telemetry != null) {
            // Temporary, write out positions:
            this.telemetry.addData("Ball X:", fieldBall.realX);
            this.telemetry.addData("Ball Y:", fieldBall.realY);
            this.telemetry.update();
        }
    }

    //finds a good enough route based on the requirements
    public List<FieldBall> getOptimalRoute(int purpleNeeded, int greenNeeded) {
        //THE LION DOES NOT CONCERN HIMSELF O SQUARED TIME COMPLEXITY

        //tTreeMap<D>
        return null;
    }
}
