package org.firstinspires.ftc.teamcode.magazine;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.color.BallColor;
import org.firstinspires.ftc.teamcode.pid.MiniPID;
import org.firstinspires.ftc.teamcode.process.Process;
import org.firstinspires.ftc.teamcode.vision.MagazineCamPipeline;

import lombok.Getter;
import lombok.Setter;

public class Magazine extends Process {
    DcMotor magazineMotor;

    Servo helpServo;
    ElapsedTime servoCycleTimer;
    double servoCycleTime = 300; //adjust, just to be safe for now

    TouchSensor intakeSensor;
    TouchSensor outtakeSensor;

    @Getter
    int currIndex = -1;
    int outtakePosOffset = -30;
    @Getter
    double p;

    boolean isOuttakeTarget = false;


    double slotPeriod = 260; // set
    double margin = 50; // set
    double adjustPower = 0.2; //set
    int dir = 0;

    double lastEncoderPos = 0;

    @Getter @Setter
    boolean pidActive = true;

    public enum State {
        IDLE,
        ROTATE,
        DEPOSIT,
    };

    BallColor[] slotColors;

    double[] motorPositions = {0.0, 2730, 5460, 4096, -1365, 1365};

    @Getter
    State magState = State.IDLE;

    MiniPID pid;

    MagazineCamPipeline pipeline;

    @Setter
    Telemetry telemetry;//debugging TODO remove
    double lastUpdate;

    public Magazine(DcMotor magazineMotor, Servo helpServo, TouchSensor intakeSensor, TouchSensor outtakeSensor, WebcamName webcamName, RevBlinkinLedDriver light,
                     long updateInterval) {
        super(updateInterval);
        this.magazineMotor = magazineMotor;
        this.helpServo = helpServo;
        this.intakeSensor = intakeSensor;
        this.outtakeSensor = outtakeSensor;

        lastUpdate = 0;

            magazineMotor.setPower(0.0);
        magazineMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        light.setPattern(RevBlinkinLedDriver.BlinkinPattern.WHITE);

        slotColors = new BallColor[]{BallColor.GREEN, BallColor.PURPLE, BallColor.PURPLE};

        servoCycleTimer = new ElapsedTime();

        pid = new MiniPID(0.0013, 0.00012, 0.01);
        magState = State.IDLE;
        currIndex = 0;
        helpServo.setPosition(1.0);

        pipeline = new MagazineCamPipeline(webcamName, 640, 480);

    }

    double round(double x, int decimals) {
        double factor = Math.pow(10.0, decimals);

        return Math.floor(x * factor) / factor;
    }

    boolean approxEq(double a, double b, double tolerance) {
        return Math.abs(a-b) <= tolerance;
    }

    boolean goToPosEncoder(int p) {
        if (approxEq(magazineMotor.getCurrentPosition(), p, 2)) {
            magazineMotor.setPower(0.0f);
            return true;
        }

        if (pidActive)
            magazineMotor.setTargetPosition(p);// good enough?
        if(magazineMotor.getMode() != DcMotor.RunMode.RUN_TO_POSITION)
            magazineMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        if (pidActive)
            magazineMotor.setPower(1.0f);

        return false;
    }

    public synchronized boolean rotateToBall(int index) {
        //if(state != State.IDLE) return false;

      //  if(helpServo.getPosition() == 0) {
      //      magazineMotor.setPower(0.0f);
      //      throw new RuntimeException(); // THIS SHOULD BE HANDLED OUTSIDE
      //      //important failsafe
      //  }

        if(index >= 6 || index < 0) throw new ArrayIndexOutOfBoundsException();

        if(currIndex == index) return true;

        currIndex = index;

        //double targetPos = magazineMotor.getCurrentPosition() + dir * (slotPeriod - margin); // if this shit works first try i'll start believing in god
       // double targetPos = potentiometerPositions[currIndex];
       // dir = potentiometer.getVoltage() > targetPos ? 1 : -1;


        magazineMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        magazineMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
       // magazineMotor.setPower(1.0f * dir);

        magState = State.ROTATE;
        pid.reset();

        return true;
    }

    @Override
    public synchronized void update() {
        double pos = -magazineMotor.getCurrentPosition();
        if(magState != State.IDLE) {
            p = pid.getOutput(pos, motorPositions[currIndex]);
            magazineMotor.setPower(p);
        }

        switch(magState) {
            case IDLE:
                    magazineMotor.setPower(0.0);
            case ROTATE:
                if(approxEq(pos, motorPositions[currIndex], 30) && approxEq(lastEncoderPos, pos, 15)) {
                    //magState = State.DEPOSIT;
                    magState = State.IDLE;
                }

                break;
            case DEPOSIT:
                if(currIndex >= 3) {
                    magState = State.IDLE;
                    break;
                }
                    magazineMotor.setPower(0.0f);
                    helpServo.setPosition(0.0);
                try {
                    Thread.sleep((long)servoCycleTime); // abs waiting time where NOTHING SHOULD HAPPEN
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                    helpServo.setPosition(1.0);
                try {
                    Thread.sleep((long)servoCycleTime); // abs waiting time where NOTHING SHOULD HAPPEN
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                magState = State.IDLE;
        }

        lastEncoderPos = pos;

        if(currIndex > 2 && slotColors[currIndex-3] == BallColor.NONE)
            updateColorData();
    }

    public synchronized void updateColorData() {
        if(currIndex < 3) throw new RuntimeException("Tried updating color data while at invalid position " + Integer.toString(currIndex));
        if(!approxEq(-magazineMotor.getCurrentPosition(), motorPositions[currIndex], 500)) return;
        slotColors[currIndex-3] = pipeline.getCurrentColor();
    }

    synchronized public void resetSlot(int i) {
        slotColors[i] = BallColor.NONE;
    }

    public State getS() {
        return magState;
    }

    synchronized  int findSlotWithColor(BallColor color) {
        for(int i = 0; i < slotColors.length; i++) {
            if(slotColors[i] == color) return i;
        }

        return -1;
    }

    synchronized public boolean setIntake() {
        int index = findSlotWithColor(BallColor.NONE);

        if(index == -1) return false;

        return rotateToBall(index + 3);
    }

    public boolean setOuttake(BallColor color) {
        int index = findSlotWithColor(color);

        if(index == -1) return false;

        return rotateToBall(index);
    }

    public void depositBall() {
        if(magState != State.IDLE) throw new RuntimeException();

        slotColors[currIndex] = BallColor.NONE;
        magState = State.DEPOSIT;
    }

    public BallColor getBallAtSlot(int index) {
        return slotColors[index];
    }
}
