package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.paths.PathsRed;

@Autonomous(name="FIRE PABLO", group="FTC 26")
public class RedAuto extends Auto{
    @Override
    void createPaths() {
        paths = new PathsRed(follower);
    }
}
