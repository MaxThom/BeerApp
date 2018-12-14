package view.beerapp.utility;

/**
 * Utility methods to calculate points on the map
 */
public class MapsCalculations {

    /**
     * This method uses the Harvesine formula to calculate great-circles distance between the two points
     * Reference:
     * https://stackoverflow.com/questions/27928/calculate-distance-between-two-latitude-longitude-points-haversine-formula/27943#2794
     * @param lon1 Longitude 1
     * @param lat1 Latitude 1
     * @param lon2 Longitude 2
     * @param lat2 Latitude 2
     * @return distance between the two points
     */
    public static double distanceBetweenTwoPoints(double lon1, double lat1, double lon2, double lat2){

        double R = 6371.0; // Radius of the earth in km
        double dLat = deg2rad(lat2-lat1);  // deg2rad below
        double dLon = deg2rad(lon2-lon1);
        double a =
                Math.sin(dLat/2) * Math.sin(dLat/2) +
                        Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                                Math.sin(dLon/2) * Math.sin(dLon/2)
                ;
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c; // Distance in km
        return d;

    }

    /**
     * Simple Method to convert degrees to rads
     * @param deg degree
     * @return radian
     */
    private static double deg2rad(double deg) {
        return deg * (Math.PI/180);
    }

}
