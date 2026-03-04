package org.firstinspires.ftc.teamcode.manager;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.color.BallColor;
import org.firstinspires.ftc.teamcode.magazine.Magazine;
import org.firstinspires.ftc.teamcode.process.Process;
import org.firstinspires.ftc.teamcode.shooter.BallIO;

import java.util.concurrent.TimeUnit;

public class ShooterManager extends Process {

    enum State {
        INACTIVE,
        BALL_SELECT,
        WINDUP,
        SHOOT,
        WINDDOWN,
    }
    int gPos;
    int currSlot = 0;

    Magazine magazine;
    BallIO shooter;

    boolean isWindingUp = false;
    int shotsLeft = 0;


    State currState = State.INACTIVE;

    ElapsedTime shotTimer = new ElapsedTime();
    ElapsedTime windupTimer = new ElapsedTime();

    final int shootTime = 1500;
    final int windupTime = 1000;

    public ShooterManager(Magazine magazine, BallIO shooter, int tagID, long updateInterval) {
        super(updateInterval);
        gPos = tagID - 21;
        //https://ftc-resources.firstinspires.org/ftc/game/manual-10 - page 8

        this.magazine = magazine;
        this.shooter = shooter;
        currState = State.INACTIVE;
    }

    public int getWindupTime() {
        return windupTime;
    }

    public boolean isActive() {
        return currState != State.INACTIVE;
    }

    public boolean startShooting() {
        if(currState != State.INACTIVE) return false;

        currState = State.BALL_SELECT;
        shotsLeft=3;
        return true;
    }

    public void stopShooting() {
        currState = State.WINDDOWN;
    }

    public boolean shoot(int shotCount) {
        if(currState.ordinal() != State.SHOOT.ordinal() - 1) return false;

        currState = State.SHOOT;
        shotsLeft = shotCount;
        return true;
    }

    void ballSelectState() {
        if(magazine.getMagState() != Magazine.State.IDLE) return; // wait


        BallColor color = currSlot % 3 == gPos ? BallColor.GREEN : BallColor.PURPLE;
        if(magazine.setOuttake(color)) {
            currState = State.WINDUP;
            return; //great we can continue the pattern
        }

        boolean ret;
        if(color == BallColor.GREEN)
            ret = magazine.setOuttake(BallColor.PURPLE);
        else
            ret = magazine.setOuttake(BallColor.GREEN);

        currState = State.WINDUP;

        if(!ret) stopShooting(); // no more balls :(*/
    }

    void windupState() {
        if (shotsLeft > 0) currState = State.SHOOT;
        else currState = State.WINDDOWN;
        shooter.windup();
        windupTimer.startTime();
    }

    void shootState() {
        if(magazine.getMagState() != Magazine.State.IDLE || windupTimer.time(TimeUnit.MILLISECONDS) < windupTime) return;
        magazine.depositBall();

        currSlot = ++currSlot % 3;
        shotsLeft--;
        if(shotsLeft > 0) {
            currState = State.BALL_SELECT;
            return;
        }

        currState = State.WINDDOWN;
        shotTimer.startTime();
    }

    void windDownState() {
        if(magazine.getMagState() != Magazine.State.IDLE) return;

        shooter.winddown();

        currState = State.INACTIVE;
    }

    @Override
    public synchronized void update() {
        /*if(telemetry != null) {
            telemetry.addData("shootmanager ballindex: ", currSlot);
            telemetry.addData("shootmanager shots: ", shotsLeft);
            telemetry.addData("shootmanager state: ", currState);
            //telemetry.addData("shootmanager shotsleft: ", currSt);
        }*/

        switch(currState) {
            case BALL_SELECT:
                ballSelectState();
                break;
            case WINDUP:
                windupState();
                break;
            case SHOOT:
                shootState();
                break;
            case WINDDOWN:
                windDownState();
                break;
            case INACTIVE:
                break;
            default:
                throw new IllegalStateException();
        }
    }

    public void incCurrSlot() {
        currSlot = (++currSlot) % 3;
    }
}
