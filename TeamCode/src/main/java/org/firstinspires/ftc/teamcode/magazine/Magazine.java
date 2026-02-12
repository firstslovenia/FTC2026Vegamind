package org.firstinspires.ftc.teamcode.magazine;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.pid.MiniPID;

public class Magazine {
    DcMotor magazineMotor;

    Servo helpServo;
    ElapsedTime servoCycleTimer;
    double servoCycleTime = 1.5f; //adjust, just to be safe for now

    TouchSensor intakeSensor;
    TouchSensor outtakeSensor;

    ColorSensor colorSensor;
    DistanceSensor distanceSensor;

    int currIndex = 0;
    int outtakePosOffset = -30;

    boolean isOuttakeTarget = false;


    double slotPeriod = 260; // set
    double margin = 50; // set
    double adjustPower = 0.2; //set
    int dir = 0;

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

    Color[] slotColors = {Color.NONE, Color.NONE, Color.NONE};

    double[] motorPositions = {0.0, -2650, 2548, -3946, 1240, -1230};

    State state = State.IDLE;

    MiniPID pid;

    public Magazine(DcMotor magazineMotor, Servo helpServo, TouchSensor intakeSensor, TouchSensor outtakeSensor,
                    ColorSensor colorSensor, DistanceSensor distanceSensor) {
        this.magazineMotor = magazineMotor;
        this.helpServo = helpServo;
        this.intakeSensor = intakeSensor;
        this.outtakeSensor = outtakeSensor;
        this.colorSensor = colorSensor;
        this.distanceSensor = distanceSensor;

        magazineMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        slotColors = new Color[]{Color.NONE, Color.NONE, Color.NONE};

        servoCycleTimer = new ElapsedTime();

        pid = new MiniPID(0.00025, 0.00008, 0.00015);
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

    public boolean rotateToBall(int index) {
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

        state = State.ROTATE;

        return true;
    }

    public void update(Telemetry telemetry) {
        if(telemetry != null) {
            telemetry.addData("curr mag pos:", magazineMotor.getCurrentPosition());
            telemetry.addData("target mag pos:", motorPositions[currIndex]);
            telemetry.addData("target outtake:", isOuttakeTarget);
            telemetry.addData("curr state", state);
            telemetry.addData("currIndex", currIndex);
            telemetry.addData("mag slot 1:", slotColors[0]);
            telemetry.addData("mag slot 2:", slotColors[1]);
            telemetry.addData("mag slot 3:", slotColors[2]);
        }

        switch(state) {
            case IDLE:
                break;
            case ROTATE:
                double p = pid.getOutput(magazineMotor.getCurrentPosition(), motorPositions[currIndex]);
                magazineMotor.setPower(p);
                telemetry.addData("power", p);
                //state = State.DEPOSIT;
                break;
            case DEPOSIT:
                if(servoCycleTimer.milliseconds() < servoCycleTime) break;

                if(helpServo.getPosition() == 1) {
                    helpServo.setPosition(0);
                    servoCycleTimer.reset();
                    break;
                } else {
                    state = State.IDLE;
                }
        }
        updateColorData();

    }

    void updateColorData() {
        if(isOuttakeTarget || distanceSensor.getDistance(DistanceUnit.MM) > 30) return; // clearly just black

        if(colorSensor.green() > colorSensor.blue()) // stupid but works "well enough' i think probably
            slotColors[currIndex] = Color.GREEN;
        else
            slotColors[currIndex] = Color.PURPLE;
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
        if(state != State.IDLE) throw new RuntimeException();

        slotColors[currIndex] = Color.NONE;
        state = State.DEPOSIT;
        helpServo.setPosition(1.0);
        servoCycleTimer.reset();
    }

    public State getState() {
        return state;
    }

    public Color getBallAtSlot(int index) {
        return slotColors[index];
    }
}
