package konpro.nkescec.dz1;

import java.util.HashMap;
import java.util.Map;

public class LocationServer {

    private Map<Integer, Location> map;

    private Location meetingLocation;

    public LocationServer() {
        map = new HashMap<>();
    }

    public boolean changeLocation(int agentNumber, Location newLocation) {
        if (newLocation.getX() > Constants.MAX_X || newLocation.getX() < 0) {
            return false;
        }

        if (newLocation.getY() > Constants.MAX_Y || newLocation.getY() < 0) {
            return false;
        }

        final Location location = new Location();
        location.setX(newLocation.getX());
        location.setY(newLocation.getY());
        map.put(agentNumber, location);

        System.out.printf("Agent %s changed his location to %s%n", agentNumber, newLocation);

        return true;
    }

    public void setMeetingLocation(Location location) {
        this.meetingLocation = location;
    }

    public boolean isAtMeetingSpot(int agentNumber) {
        return map.getOrDefault(agentNumber, new Location()).equals(meetingLocation);
    }

}
