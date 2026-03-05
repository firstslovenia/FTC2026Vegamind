package org.firstinspires.ftc.teamcode.manager;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.color.BallColor;
import org.firstinspires.ftc.teamcode.magazine.Magazine;
import org.firstinspires.ftc.teamcode.process.Process;
import org.firstinspires.ftc.teamcode.shooter.BallIO;

import lombok.Getter;
import lombok.Setter;

public class ShooterManager extends Process {

    enum State {
        IDLE,
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
    double targetP = 1.0;

    @Setter
    int slotOffset;

    State currState = State.IDLE;

    ElapsedTime shotTimer = new ElapsedTime();
    ElapsedTime windupTimer = new ElapsedTime();

    final int shootTime = 1500;
    @Getter
    final int windupTime = 1500;

    Servo shooterServo;
    @Getter
    int tagID;

    public void setTagID(int id) {
        tagID = id;
        gPos = id - 21;
    }

    final double[][] powerDistance =
            {
                    {330, 1},
                    {240, 0.95},
                    {200, 0.83},
                    {170, 0.83},
                    {120, 0.83},
                    {90, 0.8},
                    {80, 0.86},
            };
    public ShooterManager(Magazine magazine, BallIO shooter, Servo shooterServo, int tagID, long updateInterval) {
        super(updateInterval);
        this.tagID = tagID;
        gPos = tagID - 21;
        //https://ftc-resources.firstinspires.org/ftc/game/manual-10 - page 8

        this.magazine = magazine;
        this.shooter = shooter;
        this.shooterServo = shooterServo;
        currState = State.IDLE;
        shooterServo.setPosition(0);
    }

    public boolean isActive() {
        return currState != State.IDLE;
    }

    void findOptimalDistance(double dist) {
        double bestDist = Math.abs(dist - powerDistance[0][0]);

        for(int i = 1; i < powerDistance.length; i++) {
            if(bestDist > Math.abs(dist - powerDistance[i][0])) {
                targetP = powerDistance[i][1];
                bestDist = Math.abs(dist - powerDistance[i][0]);
            }
        }
    }

    public boolean startShooting(double dist) {
        if(currState != State.IDLE) return false;

        findOptimalDistance(dist);

        currState = State.BALL_SELECT;
        shotsLeft=3;

        currSlot = slotOffset;
        slotOffset = 0;

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
        shooter.windup(targetP);
        shooterServo.setPosition(1);
        windupTimer = new ElapsedTime();
        windupTimer.reset();
    }

    void shootState() {
        if(magazine.getMagState() != Magazine.State.IDLE || windupTimer.milliseconds() < windupTime) return;
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

    void windDownState() throws InterruptedException {
        if(magazine.getMagState() != Magazine.State.IDLE) return;

        shooter.winddown();
        currState = State.IDLE;
        Thread.sleep(500); // just for safety
        shooterServo.setPosition(0);

    }

    @Override
    public synchronized void update() throws InterruptedException {
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
            case IDLE:
                break;
            default:
                throw new IllegalStateException();
        }
    }

    public void incCurrSlot() {
        currSlot = (++currSlot) % 3;
    }
}
