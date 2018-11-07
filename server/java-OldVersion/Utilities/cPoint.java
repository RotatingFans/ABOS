/*******************************************************************************
 * ABOS
 * Copyright (C) 2018 Patrick Magauran
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package Utilities;

public class cPoint {

    private final String AddressM;
    private final String CityM;
    private final String StateM;
    private final Double LatM;
    private final Double LonM;

    /**
     * @param Lat     Latitude
     * @param Lon     Longitude
     * @param Address Street Address
     * @param City    City
     * @param State   State
     */
    public cPoint(Double Lat, Double Lon, String Address, String City, String State) {
        AddressM = Address;
        LonM = Lon;
        LatM = Lat;
        CityM = City;
        StateM = State;

    }

    /**
     * @return The Latitude
     */
    public Double getLat() {
        return LatM;
    }

// --Commented out by Inspection START (1/2/2016 12:01 PM):
//    public void setLat(Double Lat) {
//        LatM = Lat;
//    }
// --Commented out by Inspection STOP (1/2/2016 12:01 PM)

    /**
     * @return The Longitude
     */
    public Double getLon() {
        return LonM;
    }

// --Commented out by Inspection START (1/2/2016 12:01 PM):
//    public void setLon(Double Lon) {
//        LonM = Lon;
//    }
// --Commented out by Inspection STOP (1/2/2016 12:01 PM)

    /**
     * @return The street Address
     */
    public String getAddress() {
        return AddressM;
    }

    /**
     * @return The city
     */
    public String getCity() {
        return CityM;
    }

    /**
     * @return The state
     */
    public String getState() {
        return StateM;
    }

// --Commented out by Inspection START (1/2/2016 12:01 PM):
//    public void setAddress(String Address) {
//        AddressM = Address;
//    }
// --Commented out by Inspection STOP (1/2/2016 12:01 PM)
}
