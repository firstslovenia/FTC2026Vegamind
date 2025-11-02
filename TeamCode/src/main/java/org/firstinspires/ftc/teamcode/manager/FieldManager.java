package org.firstinspires.ftc.teamcode.manager;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.color.BallColor;
import org.firstinspires.ftc.teamcode.vision.BallContour;
import org.firstinspires.ftc.teamcode.vision.BallPipeline;

import java.util.ArrayList;
import java.util.List;

public class FieldManager {
    List<FieldBall> balls = new ArrayList<>();

    final int FIELD_SIZE = 366; //cm

    int viewWidth, viewHeight, viewOffsetX, viewOffsetY;

    BallPipeline pipeline;

    public FieldManager(WebcamName webcamName, int viewWidth, int viewHeight, int viewOffsetX, int viewOffsetY, int streamWidth, int streamHeight) {
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        this.viewOffsetX = viewOffsetX;
        this.viewOffsetY = viewOffsetY;

        pipeline = new BallPipeline(webcamName, streamWidth, streamHeight);
    }

    void update(int robotX, int robotY) {
       List<BallContour> ballContours = pipeline.getBallContours();
        BallColor currColor = pipeline.getLastUpdateColor();

       if(ballContours == null || ballContours.isEmpty()) return;

       int viewCenterX = robotX + viewOffsetX; // relative to the field
       int viewCenterY = robotY + viewOffsetY;

        for(int i = 0; i < balls.size(); i++) { // remove all the balls we thought were in that area, as we dont know if they are anymore, we'll just replace them with what
            // we can see, and yes im just iterating through since the n is small
            FieldBall ball = balls.get(i);

            if(ball.x < viewCenterX - viewWidth / 2.0 || ball.x > viewCenterX + viewWidth / 2.0 || // should we account for radius, also what about balls only partially in view?
               ball.y < viewCenterY - viewHeight / 2.0 || ball.y > viewCenterY + viewHeight / 2.0) {
                continue; //ball outside view range, we wont be updating this part so we leave as is
            }

            balls.remove(i);
            i--;
        }

        for(BallContour ballContour : ballContours) {
            balls.add(
                    new FieldBall(ballContour.x * viewWidth + viewCenterX,
                                  ballContour.y * viewHeight + viewCenterY,
                                     currColor)
            );
        }
    }
}
