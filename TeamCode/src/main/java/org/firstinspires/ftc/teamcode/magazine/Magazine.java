package org.firstinspires.ftc.teamcode.magazine;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.color.BallColor;
import org.firstinspires.ftc.teamcode.color.HSV;
import org.firstinspires.ftc.teamcode.input.PrimaryMap;

public class Magazine {
    DcMotor magazineMotor;
    AnalogInput potentiometer;

    Servo gateServo;

    int currIndex = -1;

    int gateClosePos= 0;
    int gateOpenPos = 1;

    final int[] magazineBallPositions = {0, 1, 2}; //TODO replace
    ColorSensor[] sensors = new ColorSensor[3];

    final double middlePotentiometerVoltage = 2.5;

    public Magazine(DcMotor magazineMotor, Servo gateServo, AnalogInput potentiometer, ColorSensor[] sensors) {
        this.magazineMotor = magazineMotor;
        this.gateServo = gateServo;
        this.sensors = sensors;
        this.potentiometer = potentiometer;

        magazineMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        magazineMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    double round(double x, int decimals) {
        double factor = Math.pow(10.0, decimals);

        return Math.floor(x * factor) / factor;
    }

    public boolean init() { // cause robot can't move during init phase
        if(round(potentiometer.getVoltage(), 1) == round(middlePotentiometerVoltage, 1)) {
            //once we get to a known position we just reset so the motor knows where it is relative to the magazine,
            //we dont use the potentiometer everywhere because the fucking voltage curve isn't linear
            //(I <3 REV)
            magazineMotor.setPower(0.0);
            magazineMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            magazineMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            return true;
        }

        double dir = (potentiometer.getVoltage() > middlePotentiometerVoltage) ? 1 : -1;
        magazineMotor.setPower(dir);

        return false;
    }

    public void rotateToBall(int index) {
        magazineMotor.setTargetPosition(magazineBallPositions[index]);
        currIndex = -1;
    }

    /// returns true if magazine is at the correct position
    /// in which case the gate will open, otherwise it wont
    public boolean openGate() {
        if (!atTargetBall()) {
            return false;
        }

        gateServo.setPosition(gateOpenPos);

        return true;
    }

    public void closeGate() {
        gateServo.setPosition(gateClosePos);
    }


    public boolean atTargetBall() {
        if(currIndex == -1) return false;

        return gateServo.getPosition() == magazineBallPositions[currIndex]; // TODO add a margin for error?
    }

    public BallColor getBallAtSlot(int index) {
        int r = sensors[index].red();
        int g = sensors[index].green();
        int b = sensors[index].blue();

        HSV color = new HSV(r, g, b);

        if(color.v() < 0.3) return BallColor.NONE;

        if (color.h() < 180) return BallColor.GREEN; //good enough-ish
        else return BallColor.PURPLE;
    }
}
