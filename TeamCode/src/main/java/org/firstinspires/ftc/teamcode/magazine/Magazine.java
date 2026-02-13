package org.firstinspires.ftc.teamcode.magazine;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.color.BallColor;

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
        BROAD_ROTATE,
        ADJUST_ROTATE,
        DEPOSIT,
    };

    public enum Color {
        NONE,
        PURPLE,
        GREEN
    }

    Color[] slotColors = {Color.NONE, Color.NONE, Color.NONE};

    State state = State.IDLE;

    public Magazine(DcMotor magazineMotor, Servo helpServo, TouchSensor intakeSensor, TouchSensor outtakeSensor,
                    ColorSensor colorSensor, DistanceSensor distanceSensor) {
        this.magazineMotor = magazineMotor;
        this.helpServo = helpServo;
        this.intakeSensor = intakeSensor;
        this.outtakeSensor = outtakeSensor;
        this.colorSensor = colorSensor;
        this.distanceSensor = distanceSensor;

        magazineMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        magazineMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        slotColors = new Color[]{Color.NONE, Color.NONE, Color.NONE};

        servoCycleTimer = new ElapsedTime();
    }

    double round(double x, int decimals) {
        double factor = Math.pow(10.0, decimals);

        return Math.floor(x * factor) / factor;
    }

    boolean approxEq(double a, double b, double tolerance) {
        return Math.abs(a-b) <= tolerance;
    }

    void calibrate() {
        int x = 0; // Sensor len
        int s = 0; // Slack
        magazineMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        //magazineMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Clockwise
        if (intakeSensor.isPressed()) {
            // If initially pressed, rotate until not pressed, then do 1st iteration.
            int b = 0;
            while (!intakeSensor.isPressed()) {
                magazineMotor.setTargetPosition(b);
                b++;
            }
        }
        // 1st iter - Disregarded
        int i1 = 0;
        while (!intakeSensor.isPressed()) {
            magazineMotor.setTargetPosition(i1);
            i1++;
        }
        // 2nd iter - counted
        i1 = 0; // Clockwise step count for 1 rotation
        while (!intakeSensor.isPressed()) {
            magazineMotor.setTargetPosition(i1);
            i1++;
        }

        magazineMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); // Reset

        // Counter-Clockwise
        if (intakeSensor.isPressed()) {
            // If initially pressed, rotate until not pressed, then do 1st iteration.
            int b = 0;
            while (intakeSensor.isPressed()) {
                magazineMotor.setTargetPosition(b);
                b--;
            }
        }
        // 1st iter - Disregarded
        int i2 = 0;
        while (!intakeSensor.isPressed()) {
            magazineMotor.setTargetPosition(i1);
            i2++;
        }
        // 2nd iter - counted
        i2 = 0; // Counter-Clockwise step count for 1 rotation
        while (!intakeSensor.isPressed()) {
            magazineMotor.setTargetPosition(i1);
            i2++;
        }

        // TODO: Make the above code not so retarded

        s = Math.abs(i1 - i2);
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

    boolean rotateToBall(int index) {
        if(state != State.IDLE) return false;

        if(helpServo.getPosition() == 1) {
            magazineMotor.setPower(0.0f);
            throw new RuntimeException(); // THIS SHOULD BE HANDLED OUTSIDE
            //important failsafe
        }

        if(index >= 6 || index < 0) throw new ArrayIndexOutOfBoundsException();

        isOuttakeTarget = index >= 3;
        index %= 3;

        if(currIndex == index) return true;

        dir = (Math.abs(currIndex - index) == 1 ? 1 : -1) * (currIndex - index < 0 ? -1 : 1);
        //double targetPos = magazineMotor.getCurrentPosition() + dir * (slotPeriod - margin); // if this shit works first try i'll start believing in god
        double targetPos = (index - 1) * slotPeriod;
        dir = targetPos > 0 ? 1 : -1;
        if(isOuttakeTarget)
            targetPos += outtakePosOffset;

        currIndex = index;

        magazineMotor.setTargetPosition((int)targetPos);
        magazineMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        magazineMotor.setPower(adjustPower);

        state = State.BROAD_ROTATE;

        return true;
    }

    public void update(Telemetry telemetry) {
        if(telemetry != null) {
            telemetry.addData("curr mag pos:", magazineMotor.getCurrentPosition());
            telemetry.addData("target mag pos:", magazineMotor.getTargetPosition());
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
            case BROAD_ROTATE:
                if(!approxEq(magazineMotor.getCurrentPosition(), magazineMotor.getTargetPosition(), 10))
                    break;
                state = State.ADJUST_ROTATE;
                break;
            case ADJUST_ROTATE:
                TouchSensor sensor = isOuttakeTarget ? outtakeSensor : intakeSensor;

                magazineMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                magazineMotor.setPower(adjustPower * dir);

                if(!sensor.isPressed()) break;

                magazineMotor.setPower(0.0f);

                state = State.IDLE;
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
