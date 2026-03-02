package org.firstinspires.ftc.teamcode.pathing;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public class PedroPathBlue {
    public PathChain PathShoot;
    public PathChain PathScout1;

    public PedroPathBlue(Follower follower) {
        PathShoot = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(34.250, 135.750),
                                new Pose(57.500, 110.250)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(145))
                .build();
        PathScout1 = follower.pathBuilder()
          .addPath(
            new BezierLine(
              new Pose(57.500, 110.250),
            new Pose(57.500, 103.750)
            )
          )
          .setLinearHeadingInterpolation(Math.toRadians(145), Math.toRadians(90))
          .build();
    }
}