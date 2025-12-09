package org.firstinspires.ftc.teamcode.magazine;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.color.BallColor;
import org.firstinspires.ftc.teamcode.color.HSV;

public class Magazine {
    DcMotor magazineMotor;
    AnalogInput potentiometer;

    Servo gateServo;

    int currIndex = -1;

    int gateClosePos = 1;
    int gateOpenPos = 0;

    final int[] magazineBallPositions = {-145, 0, 155}; //TODO replace
    ColorSensor[] sensors;

    final double middlePotentiometerVoltage = 1.423;

    int targetPos = 0;

    ElapsedTime magOpenTimer;
    int magOpenTime = 1500;

    public Magazine(DcMotor magazineMotor, Servo gateServo, AnalogInput potentiometer, ColorSensor[] sensors) {
        this.magazineMotor = magazineMotor;
        this.gateServo = gateServo;
        this.sensors = sensors;
        this.potentiometer = potentiometer;

        magazineMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        magazineMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    double round(double x, int decimals) {
        double factor = Math.pow(10.0, decimals);

        return Math.floor(x * factor) / factor;
    }

    boolean approxEq(double a, double b, double tolerance) {
        return Math.abs(a-b) <= tolerance;
    }

    boolean goToPosPotentiometer(double v, double p) {
        if(approxEq(potentiometer.getVoltage(), v, 0.02)) {
            //once we get to a known position we just reset so the motor knows where it is relative to the magazine,
            //we dont use the potentiometer everywhere because the fucking voltage curve isn't linear
            //(I <3 REV)
            magazineMotor.setPower(0.0);

            return true;
        }

        if(magazineMotor.getMode() != DcMotor.RunMode.RUN_USING_ENCODER)
            magazineMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        double dir = (potentiometer.getVoltage() > v) ? -1 : 1;
        magazineMotor.setPower(dir * p);

        return false;
    }

    boolean goToPosEncoder(int p) {
        if (approxEq(magazineMotor.getCurrentPosition(), p, 2)) {
            magazineMotor.setPower(0.0f);
            return true;
        }

        magazineMotor.setTargetPosition(p);
        if(magazineMotor.getMode() != DcMotor.RunMode.RUN_TO_POSITION)
            magazineMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        magazineMotor.setPower(1.0f);

        return false;
    }

    public void rotateToBall(int index) {
        if(index > 2 || index < 0) return;

        targetPos = magazineBallPositions[index];
        currIndex = -1;
    }

    public void update(Telemetry telemetry) {
        telemetry.addData("target po: ", targetPos);
        telemetry.addData("potentionmeter: ", potentiometer.getVoltage());
        goToPosEncoder(targetPos);
    }

    /// returns true if magazine is at the correct position
    /// in which case the gate will open, otherwise it wont
    public boolean openGate() {
//        if (!atTargetBall()) {
  //          return false;
    //    }

        gateServo.setPosition(gateOpenPos);

        if(magOpenTimer == null) {
            magOpenTimer = new ElapsedTime();
        }

        return magOpenTimer.milliseconds() > magOpenTime;
    }

    public void closeGate() {
        gateServo.setPosition(gateClosePos);
        magOpenTimer = null;
    }


    public boolean atTargetBall() {
        if(currIndex == -1) return false;

        return gateServo.getPosition() == magazineBallPositions[currIndex]; // TODO add a margin for error?
    }

    public BallColor getBallAtSlot(int index) {
        /*int r = sensors[index].red();
        int g = sensors[index].green();
        int b = sensors[index].blue();

        HSV color = new HSV(r, g, b);

        if(color.v() < 0.3) return BallColor.NONE;

        if (color.h() < 180) return BallColor.GREEN; //good enough-ish
        else return BallColor.PURPLE;*/

        if(index == 0) return BallColor.GREEN;
        else return BallColor.PURPLE;
    }
}
