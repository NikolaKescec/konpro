package konpro.nkescec.dz1;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

public class Agent implements Runnable {

    private final LocationServer locationServer;

    private final CountDownLatch countDownLatch;

    private final boolean rowIteration;

    private final int directionX;

    private final int directionY;

    private int agentNumber;

    private Location initialLocation;

    public Agent(int agentNumber, Location initialLocation, LocationServer locationServer,
        CountDownLatch countDownLatch) {
        this.agentNumber = agentNumber;
        this.initialLocation = initialLocation;
        this.locationServer = locationServer;
        this.countDownLatch = countDownLatch;

        this.rowIteration = ThreadLocalRandom.current().nextBoolean();
        this.directionX = ThreadLocalRandom.current().nextBoolean() ? 1 : -1;
        this.directionY = ThreadLocalRandom.current().nextBoolean() ? 1 : -1;
    }

    @Override
    public void run() {
        Location location = initialLocation;

        while (true) {
            locationServer.changeLocation(agentNumber, location);

            try {
                final int timeToSleep =
                    Math.abs(ThreadLocalRandom.current().nextInt() % Constants.MAX_SLEEP_TIME);
                Thread.sleep(timeToSleep);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (locationServer.isAtMeetingSpot(this.agentNumber)) {
                try {
                    System.out.printf("Agent %d is waiting for a meeting%n", agentNumber);
                    countDownLatch.await();
                    break;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            int newX = location.getX();
            int newY = location.getY();
            if(rowIteration) {
                newY = getNext(location.getY(), this.directionY, Constants.MAX_Y);
                if(newY == 0) {
                    newX = getNext(location.getX(), this.directionX, Constants.MAX_X);
                }
            } else {
                newX = getNext(location.getX(), this.directionX, Constants.MAX_X);
                if(newX == 0) {
                    newY = getNext(location.getY(), this.directionY, Constants.MAX_Y);
                }
            }

            location.setX(newX);
            location.setY(newY);
        }

        System.out.printf("Agent %s done%n", agentNumber);
    }

    private int getNext(int base, int direction, int max) {
        int next = (base + direction) % max;
        if (next < 0) {
            next = max - 1;
        }

        return next;
    }

}
