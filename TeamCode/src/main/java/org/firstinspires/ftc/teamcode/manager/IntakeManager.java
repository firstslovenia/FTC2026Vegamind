package org.firstinspires.ftc.teamcode.manager;

import org.firstinspires.ftc.teamcode.color.BallColor;
import org.firstinspires.ftc.teamcode.magazine.Magazine;
import org.firstinspires.ftc.teamcode.process.Process;
import org.firstinspires.ftc.teamcode.shooter.BallIO;

public class IntakeManager extends Process {

    enum State {
        INACTIVE,
        BALL_SELECT,
        INTAKE,
        WINDDOWN,
    }
    int currSlot = 0;

    Magazine magazine;
    BallIO intake;

    int intakesLeft = 0;

    State currState = State.INACTIVE;


    public IntakeManager(Magazine magazine, BallIO shooter, long updateInterval) {
        super(updateInterval);
        //https://ftc-resources.firstinspires.org/ftc/game/manual-10 - page 8

        this.magazine = magazine;
        this.intake = shooter;
        currState = State.INACTIVE;
    }

    public boolean isActive() {
        return currState != State.INACTIVE;
    }

    public boolean startIntaking() {
        if(currState != State.INACTIVE) return false;

        currState = State.BALL_SELECT;
        intakesLeft = 3;
        return true;
    }

    public void stopIntaking() {
        currState = State.WINDDOWN;
    }

    public boolean shoot(int shotCount) {
        if(currState.ordinal() != State.INTAKE.ordinal() - 1) return false;

        currState = State.INTAKE;
        intakesLeft = shotCount;
        return true;
    }

    void ballSelectState() {
        if(magazine.getMagState() != Magazine.State.IDLE) return; // wait

        if (!magazine.setIntake()) {
            stopIntaking();
            return;
        }

        magazine.resetSlot(magazine.getCurrIndex()-3);
        currState = State.INTAKE;
        intake.windup();

        return;
       /*
        Magazine.Color color = currSlot % 3 == gPos ? Magazine.Color.GREEN : Magazine.Color.PURPLE;
        if(magazine.setOuttake(color)) {
            currState = State.WINDUP;
            return; //great we can continue the pattern
        }

        boolean ret;
        if(color == Magazine.Color.GREEN)
            ret = magazine.setOuttake(Magazine.Color.PURPLE);
        else
            ret = magazine.setOuttake(Magazine.Color.GREEN);

        //if(!ret) stop(); // no more balls :(*/
    }

    void intakeState() {
//        if(magazine.getBallAtSlot(magazine.getCurrIndex() - 3) == BallColor.NONE)
//            magazine.updateColorData();
        if(magazine.getMagState() != Magazine.State.IDLE) return;

        currSlot = ++currSlot % 3;
        intakesLeft--;
        if(intakesLeft > 0) currState = State.BALL_SELECT;
        else currState = State.WINDDOWN;
    }

    void windDownState() {
        if(magazine.getMagState() != Magazine.State.IDLE) return;

        intake.winddown();

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
            case INTAKE:
                intakeState();
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
