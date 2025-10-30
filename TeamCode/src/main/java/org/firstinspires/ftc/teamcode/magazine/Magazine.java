package org.firstinspires.ftc.teamcode.magazine;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.input.PrimaryMap;

public class Magazine {
    Servo magazineServo;
    Servo gateServo;

    int currIndex = -1;

    int gateClosePos= 0;
    int gateOpenPos = 1;

    final int[] magazineBallPositions = {0, 1, 2}; //TODO replace

    public Magazine(Servo magazineServo, Servo gateServo) {
        this.magazineServo = magazineServo;
        this.gateServo = gateServo;
    }

    public void run(PrimaryMap map) {
        updateGate(); //TODO it being 1 iteration behind shouldnt cause problems right?

        int index = map.getBallIndex();
        if(index == -1 || currIndex == index) return;

        rotateToBall(index);
    }

    public void rotateToBall(int index) {
        magazineServo.setPosition(magazineBallPositions[index]);
        currIndex = -1;
    }

    void updateGate() {
        if (atTargetBall()) magazineServo.setPosition(gateOpenPos);
        else magazineServo.setPosition(gateClosePos);
    }


    public boolean atTargetBall() {
        if(currIndex == -1) return false;

        return magazineServo.getPosition() == magazineBallPositions[currIndex]; // TODO add a margin for error?
    }
}
