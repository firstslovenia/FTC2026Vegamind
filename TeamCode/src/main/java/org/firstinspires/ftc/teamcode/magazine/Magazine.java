package org.firstinspires.ftc.teamcode.magazine;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.pid.MiniPID;
import org.firstinspires.ftc.teamcode.process.Process;

import lombok.Getter;

public class Magazine extends Process {
    DcMotor magazineMotor;

    Servo helpServo;
    ElapsedTime servoCycleTimer;
    double servoCycleTime = 300; //adjust, just to be safe for now

    TouchSensor intakeSensor;
    TouchSensor outtakeSensor;

    int currIndex = -1;
    int outtakePosOffset = -30;

    boolean isOuttakeTarget = false;


    double slotPeriod = 260; // set
    double margin = 50; // set
    double adjustPower = 0.2; //set
    int dir = 0;

    double lastEncoderPos = 0;

    public enum State {
        IDLE,
        ROTATE,
        DEPOSIT,
    };

    public enum Color {
        NONE,
        PURPLE,
        GREEN
    }

    Color[] slotColors = {Color.GREEN, Color.GREEN, Color.PURPLE};

    double[] motorPositions = {0.0, 2730, 5460, 4096, -1365, 1365};

    @Getter
    State magState = State.IDLE;

    MiniPID pid;

    public Magazine(DcMotor magazineMotor, Servo helpServo, TouchSensor intakeSensor, TouchSensor outtakeSensor,
                     long updateInterval) {
        super(updateInterval);
        this.magazineMotor = magazineMotor;
        this.helpServo = helpServo;
        this.intakeSensor = intakeSensor;
        this.outtakeSensor = outtakeSensor;

        magazineMotor.setPower(0.0);
        magazineMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        slotColors = new Color[]{Color.PURPLE, Color.GREEN, Color.GREEN};

        servoCycleTimer = new ElapsedTime();

        pid = new MiniPID(0.0003, 0.00002, 0.00125);
        magState = State.IDLE;
        currIndex = 0;
        helpServo.setPosition(1.0);
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

        magazineMotor.setTargetPosition(p);// good enough?
        if(magazineMotor.getMode() != DcMotor.RunMode.RUN_TO_POSITION)
            magazineMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

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
        /*if(telemetry != null) {
            telemetry.addData("curr mag pos:", magazineMotor.getCurrentPosition());
            telemetry.addData("target mag pos:", motorPositions[currIndex]);
            telemetry.addData("target outtake:", isOuttakeTarget);
            telemetry.addData("curr state", state);
            telemetry.addData("currIndex", currIndex);
            telemetry.addData("mag slot 1:", slotColors[0]);
            telemetry.addData("mag slot 2:", slotColors[1]);
            telemetry.addData("mag slot 3:", slotColors[2]);
        }*/
        double pos = -magazineMotor.getCurrentPosition();
        if(currIndex != -1) {
            double p = pid.getOutput(pos, motorPositions[currIndex]);
            magazineMotor.setPower(p);
        }

        switch(magState) {
            case IDLE:
                break;
            case ROTATE:
                if(approxEq(pos, motorPositions[currIndex], 70) && approxEq(lastEncoderPos, pos, 15)) {
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
                helpServo.setPosition(0.4);
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
        updateColorData();

        lastEncoderPos = pos;
    }

    void updateColorData() {
    }

    int findSlotWithColor(Color color) {
        for(int i = 0; i < slotColors.length; i++) {
            if(slotColors[i] == color) return i;
        }

        return -1;
    }

    public boolean setIntake() {
        int index = findSlotWithColor(Color.NONE);

        if(index == -1) return false;

        return rotateToBall(index);
    }

    public boolean setOuttake(Color color) {
        int index = findSlotWithColor(color);

        if(index == -1) return false;

        return rotateToBall(index + 3);
    }

    public void depositBall() {
        if(magState != State.IDLE) throw new RuntimeException();

        //TESTING slotColors[currIndex] = Color.NONE;
        magState = State.DEPOSIT;
    }

    public Color getBallAtSlot(int index) {
        return slotColors[index];
    }
}
