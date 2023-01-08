package konpro.nkescec.dz1;

import java.util.concurrent.CountDownLatch;

public class MeetingScheduler implements Runnable {

    private final LocationServer locationServer;

    private final CountDownLatch countDownLatch;

    private Location meetingLocation;

    public MeetingScheduler(LocationServer locationServer, CountDownLatch countDownLatch, Location meetingLocation) {
        this.locationServer = locationServer;
        this.countDownLatch = countDownLatch;
        this.meetingLocation = meetingLocation;
    }

    @Override
    public void run() {
        boolean[] waitingAgent = new boolean[Constants.TOTAL_AGENTS];
        locationServer.setMeetingLocation(meetingLocation);

        System.out.printf("Manager set meeting location %s%n", meetingLocation);

        while (countDownLatch.getCount() != 0) {
            for (int i = 0; i < Constants.TOTAL_AGENTS; i++) {
                if (!waitingAgent[i] && locationServer.isAtMeetingSpot(i)) {
                    countDownLatch.countDown();
                    waitingAgent[i] = true;
                }
            }
        }

        System.out.println("All agents were in a meeting!");
    }

}
