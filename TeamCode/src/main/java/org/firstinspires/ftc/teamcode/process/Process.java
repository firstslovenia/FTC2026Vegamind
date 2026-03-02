package org.firstinspires.ftc.teamcode.process;

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
        while(shouldRun) {
            long waitTime = updateInterval - (System.currentTimeMillis() - lastUpdate);
            if(waitTime > 0) {
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            update();
            lastUpdate = System.currentTimeMillis();
        }
    }

    protected abstract void update();
}
