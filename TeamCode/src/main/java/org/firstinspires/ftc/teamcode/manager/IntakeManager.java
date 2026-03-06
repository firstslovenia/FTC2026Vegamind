package org.firstinspires.ftc.teamcode.manager;

import com.qualcomm.robotcore.hardware.Servo;

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
    Servo intakeGate;

    int intakesLeft = 0;
    boolean manualStop;

    State currState = State.INACTIVE;


    public IntakeManager(Magazine magazine, BallIO intake, Servo intakeGate, long updateInterval) {
        super(updateInterval);
        //https://ftc-resources.firstinspires.org/ftc/game/manual-10 - page 8

        this.magazine = magazine;
        this.intake = intake;
        this.intakeGate = intakeGate;
        currState = State.INACTIVE;
        manualStop = false;

        intakeGate.setPosition(0.0);
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
        intakeGate.setPosition(0.0);
    }

    public boolean intake(int shotCount) {
        return intake(shotCount, false);
    }

    public boolean intake(int shotCount, boolean manualStop) {

        if(isActive()) return false;

        this.manualStop = manualStop;

        currState = State.BALL_SELECT;
        intakesLeft = shotCount;
        return true;
    }

    void ballSelectState() {
        intakeGate.setPosition(0.5);
        intake.windup();
        if(magazine.getMagState() != Magazine.State.IDLE) return; // wait

        if (!magazine.setIntake()) {
            stopIntaking();
            return;
        }

        magazine.resetSlot(magazine.getCurrIndex()-3);
        currState = State.INTAKE;

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
        else if(!manualStop) currState = State.WINDDOWN;
    }

    void windDownState() {
        if(magazine.getMagState() != Magazine.State.IDLE) return;

        intake.winddown();
        intakeGate.setPosition(0.0);

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
