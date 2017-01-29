class cPoint {

    private String AddressM;
    private String CityM;
    private String StateM;
    private Double LatM;
    private Double LonM;


    public cPoint(Double Lat, Double Lon, String Address, String City, String State) {
        AddressM = Address;
        LonM = Lon;
        LatM = Lat;
        CityM = City;
        StateM = State;

    }

    public Double getLat() {
        return LatM;
    }

// --Commented out by Inspection START (1/2/2016 12:01 PM):
//    public void setLat(Double Lat) {
//        LatM = Lat;
//    }
// --Commented out by Inspection STOP (1/2/2016 12:01 PM)

    public Double getLon() {
        return LonM;
    }

// --Commented out by Inspection START (1/2/2016 12:01 PM):
//    public void setLon(Double Lon) {
//        LonM = Lon;
//    }
// --Commented out by Inspection STOP (1/2/2016 12:01 PM)

    public String getAddress() {
        return AddressM;
    }

    public String getCity() {
        return CityM;
    }

    public String getState() {
        return StateM;
    }

// --Commented out by Inspection START (1/2/2016 12:01 PM):
//    public void setAddress(String Address) {
//        AddressM = Address;
//    }
// --Commented out by Inspection STOP (1/2/2016 12:01 PM)
}
