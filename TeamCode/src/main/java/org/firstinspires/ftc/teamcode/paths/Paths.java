package org.firstinspires.ftc.teamcode.paths;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public class Paths {

    public PathChain startPath;
    public PathChain refillPath;
    public PathChain shootPath;

    public Paths(Follower follower) {
        startPath = follower
                .pathBuilder()
                .addPath(
                        new BezierLine(new Pose(40.000, 135.500), new Pose(80.000, 100.000))
                )
                .setLinearHeadingInterpolation(Math.toRadians(270), Math.toRadians(45))
                .build();

        refillPath = follower
                .pathBuilder()
                .addPath(
                        new BezierLine(new Pose(80.000, 100.000), new Pose(10.000, 10.000))
                )
                .setTangentHeadingInterpolation()
                .setReversed()
                .build();

        shootPath = follower
                .pathBuilder()
                .addPath(
                        new BezierLine(new Pose(10.000, 10.000), new Pose(80.000, 100.000))
                )
                .setTangentHeadingInterpolation()
                .build();
    }
}
