package org.firstinspires.ftc.teamcode.process;

import org.firstinspires.ftc.teamcode.OpModeState;

public abstract class Process extends Thread {
    Thread thread;

    long lastUpdate;
    long updateInterval;

    boolean shouldRun = true;

    protected Process(long updateInterval) {
        lastUpdate = 0;
        this.updateInterval = updateInterval;
    }

    @Override
    public void run() {
        while(shouldRun && OpModeState.isRunning) {
            long waitTime = updateInterval - (System.currentTimeMillis() - lastUpdate);
            if(waitTime > 0) {
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            try {
                update();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            lastUpdate = System.currentTimeMillis();
        }
    }

    protected abstract void update() throws InterruptedException;
}
