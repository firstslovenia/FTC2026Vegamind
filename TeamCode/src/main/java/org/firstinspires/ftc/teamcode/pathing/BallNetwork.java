package org.firstinspires.ftc.teamcode.pathing;

import org.firstinspires.ftc.teamcode.manager.FieldBall;
import org.firstinspires.ftc.teamcode.pathing.element.Network;
import org.firstinspires.ftc.teamcode.pathing.element.Node;

import java.util.List;

public class BallNetwork extends Network {

    List<Node> nodes; //balls

    public void updateNodes(List<FieldBall> balls) {
        nodes.clear();

        for(FieldBall ball : balls) {
            nodes.add(
                    new Node()
            )
        }
    }

    @Override
    public Iterable<Node> getNodes() {
        return null;
    }
}
