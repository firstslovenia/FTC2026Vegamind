package org.firstinspires.ftc.teamcode.pathing;

import com.pedropathing.follower.Follower;
import com.pedropathing.paths.PathChain;

public class PedroPathRed {
    public PathChain PathShoot;
    public PathChain PathScout1;
    public PathChain PathScout2;
    public PathChain PathScout3;

    public double shootX, scout1X, scout2X, scout3X;
    public double shootY, scout1Y, scout2Y, scout3Y;
    public double shootDeg, scout1Deg, scout2Deg, scout3Deg;

    public double hardcode1X, hardcode1Y, hardcode1Deg;
    public double hardcode2X, hardcode2Y, hardcode2Deg;
    public double hardcode3X, hardcode3Y, hardcode3Deg;

    public PedroPathRed(Follower follower) {
        shootX = 83.135; shootY = 102.425;
        scout1X = 100; scout1Y = 110;
        scout2X = 100; scout2Y = 90;
        scout3X = 100; scout3Y = 66;

        shootDeg = Math.toRadians(35);
        scout1Deg = Math.toRadians(270);
        scout2Deg = Math.toRadians(270);
        scout3Deg = Math.toRadians(270);

        hardcode1X = 100; hardcode1Y = 84; hardcode1Deg = Math.toRadians(0);
        hardcode2X = 100; hardcode2Y = 60; hardcode2Deg = Math.toRadians(0);
        hardcode3X = 100; hardcode2Y = 35; hardcode3Deg = Math.toRadians(0);

        /*PathShoot = follower.pathBuilder()
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
        PathScout2 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(57.500, 103.750),
                                new Pose(57.5, 80.0)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(145), Math.toRadians(90))
                .build();
        PathScout3 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(57.500, 110.250),
                                new Pose(57.500, 103.750)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(145), Math.toRadians(90))
                .build();*/
    }
}