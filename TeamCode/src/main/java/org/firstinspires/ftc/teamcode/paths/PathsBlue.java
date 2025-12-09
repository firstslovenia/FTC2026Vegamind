package org.firstinspires.ftc.teamcode.paths;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public class PathsBlue extends Paths {
        public PathsBlue(Follower follower) {
            startPath = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(40.000, 135.500), new Pose(50.000, 100.000))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(135))
                    .build();

            endPath = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(50.000, 100.000), new Pose(40.000, 10.000))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(135), Math.toRadians(90))
                    .setReversed()
                    .build();
        }

}
