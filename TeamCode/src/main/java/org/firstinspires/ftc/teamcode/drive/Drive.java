package org.firstinspires.ftc.teamcode.drive;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.BezierPoint;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;

public class Drive {

    Pose holdPose = null;

    public Drive(Follower follower, Pose startPose) {
        this.follower = follower;
        follower.startTeleopDrive(true);
        follower.setStartingPose(startPose);
        follower.update();
    }

    public void drive(double driveX, double driveY, double driveRot) {
        follower.setTeleOpDrive(driveX, driveY, driveRot, false);

        follower.update();
    }

    public void reset() {
        follower.setPose( new Pose());
        follower.update();
    }

    Follower follower;
};