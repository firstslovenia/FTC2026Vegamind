package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.magazine.Magazine;
import org.firstinspires.ftc.teamcode.manager.ShooterManager;
import org.firstinspires.ftc.teamcode.paths.PathState;
import org.firstinspires.ftc.teamcode.paths.Paths;
import org.firstinspires.ftc.teamcode.shooter.Shooter;

@TeleOp(name="PABLO", group="FTC 26")
public class MainAuto extends OpMode{

    Magazine magazine;
    Shooter shooter;
    ShooterManager shooterManager; //TODO initialize

    Follower follower;
    Paths paths = new Paths(follower);

    PathState pathState = PathState.START;

    ElapsedTime refillTimer = null;
    final int refillTime = 5000;

    //EVERYTHING MUST BE, IS, AND WILL BE NONBLOCKING OR I WILL STRANGLE SOMEONE
    @Override
    public void init() {
        while(!magazine.init());
    }

    @Override
    public void loop() {
        shooterManager.update();

        switch(pathState) {
            case START:
                follower.followPath(paths.startPath);
                pathState = PathState.SHOOT;
                shooterManager.start();
                break;

            case SHOOT:
                if(follower.isBusy()) break;
                if(!shooterManager.shoot(3)) break; // we failed so we won't change state, but simply try again next iter
                if(shooterManager.isActive()) break;

                pathState = PathState.REFILL_PATH;

                follower.followPath(paths.refillPath);
                break;

            case REFILL_PATH:
                if(follower.isBusy()) break;
                if(refillTimer == null) {
                    refillTimer = new ElapsedTime();
                    refillTimer.startTime();
                }

                if(refillTimer.milliseconds() < refillTime) break;

                follower.followPath(paths.shootPath);
                pathState = PathState.SHOOT_PATH;
                shooterManager.start();

                refillTimer = null;

                break;

            case SHOOT_PATH:
                pathState = PathState.SHOOT;
                break;
        }
    }
}
