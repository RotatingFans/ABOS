class cPoint {
    private final String AddressM;
    private final Double LatM;
    private final Double LonM;


    cPoint(Double Lat, Double Lon, String Address) {
        AddressM = Address;
        LonM = Lon;
        LatM = Lat;

    }

    public Double getLat() {
        return LatM;
    }

// --Commented out by Inspection START (12/31/15 1:42 PM):
//    public void setLat(Double Lat) {
//        LatM = Lat;
//    }
// --Commented out by Inspection STOP (12/31/15 1:42 PM)

    public Double getLon() {
        return LonM;
    }

// --Commented out by Inspection START (12/31/15 1:42 PM):
//    public void setLon(Double Lon) {
//        LonM = Lon;
//    }
// --Commented out by Inspection STOP (12/31/15 1:42 PM)

    public String getAddress() {
        return AddressM;
    }

// --Commented out by Inspection START (12/31/15 1:42 PM):
//    public void setAddress(String Address) {
//        AddressM = Address;
//    }
// --Commented out by Inspection STOP (12/31/15 1:42 PM)
}
