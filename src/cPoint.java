public class cPoint {
    private String AddressM;
    private Double LatM;
    private Double LonM;


    public cPoint(Double Lat, Double Lon, String Address) {
        AddressM = Address;
        LonM = Lon;
        LatM = Lat;

    }

    public Double getLat() {
        return LatM;
    }

    public void setLat(Double Lat) {
        LatM = Lat;
    }

    public Double getLon() {
        return LonM;
    }

    public void setLon(Double Lon) {
        LonM = Lon;
    }

    public String getAddress() {
        return AddressM;
    }

    public void setAddress(String Address) {
        AddressM = Address;
    }
}
