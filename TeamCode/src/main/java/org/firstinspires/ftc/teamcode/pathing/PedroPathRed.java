package org.firstinspires.ftc.teamcode.pathing;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

    public class PedroPathRed {
        public PathChain Path1;
        public PathChain Path2;
        public PathChain Path3;
        public PathChain Path4;
        public PathChain Path5;
        public PathChain Path6;
        public PathChain Path7;
        public PathChain Path8;
        public PathChain Path9;
        public PathChain Path10;
        public PathChain Path11;
        public PathChain Path12;
        public PathChain Path13;
        public PathChain Path14;
        public PathChain Path15;
        public PathChain Path16;
        public PathChain Path17;

        public PedroPathRed(Follower follower) {
            Path1 = follower.pathBuilder()
                    .addPath(
                            new BezierLine(
                                    new Pose(109.938, 135.186),
                                    new Pose(88.296, 103.089)
                            )
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(225))
                    .build();

            Path2 = follower.pathBuilder()
                    .addPath(
                            new BezierLine(
                                    new Pose(88.296, 103.089),
                                    new Pose(88.477, 83.533)
                            )
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))
                    .build();

            Path3 = follower.pathBuilder()
                    .addPath(
                            new BezierLine(
                                    new Pose(88.477, 83.533),
                                    new Pose(108.880, 83.675)
                            )
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))
                    .build();

            Path4 = follower.pathBuilder()
                    .addPath(
                            new BezierLine(
                                    new Pose(108.880, 83.675),
                                    new Pose(114.556, 83.691)
                            )
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))
                    .build();

            Path5 = follower.pathBuilder()
                    .addPath(
                            new BezierLine(
                                    new Pose(114.556, 83.691),
                                    new Pose(119.614, 83.690)
                            )
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))
                    .build();

            Path6 = follower.pathBuilder()
                    .addPath(
                            new BezierLine(
                                    new Pose(119.614, 83.690),
                                    new Pose(88.480, 103.022)
                            )
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(225))
                    .build();

            Path7 = follower.pathBuilder()
                    .addPath(
                            new BezierLine(
                                    new Pose(88.480, 103.022),
                                    new Pose(88.951, 59.650)
                            )
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))
                    .build();

            Path8 = follower.pathBuilder()
                    .addPath(
                            new BezierLine(
                                    new Pose(88.951, 59.650),
                                    new Pose(109.421, 59.679)
                            )
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))
                    .build();

            Path9 = follower.pathBuilder()
                    .addPath(
                            new BezierLine(
                                    new Pose(109.421, 59.679),
                                    new Pose(114.383, 59.557)
                            )
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))
                    .build();

            Path10 = follower.pathBuilder()
                    .addPath(
                            new BezierLine(
                                    new Pose(114.383, 59.557),
                                    new Pose(119.634, 59.598)
                            )
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))
                    .build();

            Path11 = follower.pathBuilder()
                    .addPath(
                            new BezierLine(
                                    new Pose(119.634, 59.598),
                                    new Pose(88.074, 103.015)
                            )
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(225))
                    .build();

            Path12 = follower.pathBuilder()
                    .addPath(
                            new BezierLine(
                                    new Pose(88.074, 103.015),
                                    new Pose(88.635, 35.607)
                            )
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))
                    .build();

            Path13 = follower.pathBuilder()
                    .addPath(
                            new BezierLine(
                                    new Pose(88.635, 35.607),
                                    new Pose(108.999, 35.602)
                            )
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))
                    .build();

            Path14 = follower.pathBuilder()
                    .addPath(
                            new BezierLine(
                                    new Pose(108.999, 35.602),
                                    new Pose(114.108, 35.617)
                            )
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))
                    .build();

            Path15 = follower.pathBuilder()
                    .addPath(
                            new BezierLine(
                                    new Pose(114.108, 35.617),
                                    new Pose(119.847, 35.688)
                            )
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))
                    .build();

            Path16 = follower.pathBuilder()
                    .addPath(
                            new BezierLine(
                                    new Pose(119.847, 35.688),
                                    new Pose(87.943, 103.036)
                            )
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(225))
                    .build();

            Path17 = follower.pathBuilder()
                    .addPath(
                            new BezierLine(
                                    new Pose(87.943, 103.036),
                                    new Pose(88.541, 59.380)
                            )
                    )
                    .setTangentHeadingInterpolation()
                    .build();
        }
    }
