package org.firstinspires.ftc.teamcode;


import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.manager.Alliance;

@TeleOp(name="Blue teleop", group="FTC 26")
public class BlueTeleop extends MainTeleop {
    Alliance alliance = Alliance.BLUE;

    @Override
    protected void goToHomeBase() {
        PathChain homePath = follower.pathBuilder().addPath(
                new BezierLine(
                        new Pose(follower.getPose().getX(), follower.getPose().getY()),
                        new Pose(105.1098, 33.3040)
                )
        ).setLinearHeadingInterpolation(follower.getHeading(), 180).build();
    }
}
