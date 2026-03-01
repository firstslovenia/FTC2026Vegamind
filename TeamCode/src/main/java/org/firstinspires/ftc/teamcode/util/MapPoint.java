package org.firstinspires.ftc.teamcode.util;

import lombok.Getter;
import lombok.Setter;

public class MapPoint {
    @Getter @Setter
    double x, y, fromHeading, toHeading;
    
    public MapPoint(double x, double y, double fromHeading, double toHeading) {
        this.x = x;
        this.y = y;
        this.fromHeading = fromHeading;
        this.toHeading = toHeading;
    }
}
