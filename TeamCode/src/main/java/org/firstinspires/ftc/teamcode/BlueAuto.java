package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.paths.PathsBlue;

@Autonomous(name="WATER PABLO", group="FTC 26")
public class BlueAuto extends Auto{
    @Override
    void createPaths() {
        paths = new PathsBlue(follower);
    }
}
