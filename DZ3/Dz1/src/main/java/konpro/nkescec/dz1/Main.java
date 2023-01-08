package konpro.nkescec.dz1;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(Constants.TOTAL_AGENTS);
        final LocationServer locationServer = new LocationServer();
        final MeetingScheduler meetingScheduler =
            new MeetingScheduler(locationServer, countDownLatch, new Location(3, 3));

        final ExecutorService pool = Executors.newFixedThreadPool(Constants.TOTAL_AGENTS + 1);
        for (int i = 0; i < Constants.TOTAL_AGENTS; i++) {
            pool.submit(new Agent(i, new Location(i, i), locationServer, countDownLatch));
        }

        pool.submit(meetingScheduler);
        pool.shutdown();
    }

}
