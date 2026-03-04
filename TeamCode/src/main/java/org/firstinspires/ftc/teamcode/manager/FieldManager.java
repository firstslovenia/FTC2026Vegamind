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

import lombok.Getter;
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
        this.focalLengthPx = 978;
        this.telemetry = telemetry;

        fieldBalls = new ArrayList<>();

        pipeline = new BallPipeline(webcamName, (int) streamWidth, (int) streamHeight,
                hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName()));
        initField();
    }

    public static void ensureSize(ArrayList<?> list, int size) {
        list.ensureCapacity(size);
        while (list.size() < size) {
            list.add(null);
        }
    }

    public synchronized List<List<List<FieldBall>>> getFieldGrid() { return fieldGrid; }
    public synchronized List<FieldBall> getFieldBalls() { return fieldBalls; }

    void initField() {
        fieldGrid = new ArrayList<>();
        ensureSize((ArrayList<?>) fieldGrid, GRID_SIZE);
        for(int i = 0; i < fieldGrid.size(); i++) {
             fieldGrid.set(i, new ArrayList<>());
            ensureSize((ArrayList<?>)fieldGrid.get(i), GRID_SIZE);
            for(int y = 0; y < fieldGrid.size(); y++) {
                fieldGrid.get(i).set(y, new ArrayList<>());
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
        //clearFieldArea(currentColor);
        telemetry.addData("size", balls.size());

        for(FieldBall ball : balls) {
            computePositions(ball);
        }
        fieldBalls.clear();
        fieldBalls = balls;
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
        double Xn = (fieldBall.getX() -  636) / focalLengthPx;
        double Yn = (fieldBall.getY() - 480) / focalLengthPx;

        // Apply pitch rotation
        double ry = Yn * Math.cos(pitch) - Math.sin(pitch); // In radians
        double rz = Yn * Math.sin(pitch) + Math.cos(pitch); // In radians

        // Solve for intersection with ground
        double t = -(this.camPlaneY / rz); // camOffsetY is camera height (any unit - will match output)

        // Final coordinates
        fieldBall.realX = t * Xn;
        fieldBall.realY = t * ry;

       if(fieldBall.realY / BALL_SIZE > 0 && fieldBall.realY / BALL_SIZE < GRID_SIZE &&
        fieldBall.realX / BALL_SIZE > 0 && fieldBall.realX / BALL_SIZE < GRID_SIZE)  {
            fieldGrid
                    .get((int)Math.floor(fieldBall.realY / BALL_SIZE))
                    .get((int)Math.floor(fieldBall.realX / BALL_SIZE))
                    .add(fieldBall);
        }

        if(telemetry != null) {
            // Temporary, write out positions:
            telemetry.addData("p X:", fieldBall.getX());
            telemetry.addData("p Y:", fieldBall.getY());
            telemetry.addData("Ball X:", fieldBall.realX);
            telemetry.addData("Ball Y:", fieldBall.realY);
            telemetry.addData("t", t);
            telemetry.addData("pitch", pitch);
        }
    }

    //finds a good enough route based on the requirements
    public List<FieldBall> getOptimalRoute(int purpleNeeded, int greenNeeded) {
        //THE LION DOES NOT CONCERN HIMSELF O SQUARED TIME COMPLEXITY

        //tTreeMap<D>
        return null;
    }
}
