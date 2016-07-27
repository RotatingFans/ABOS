/**
 * Created by patrick on 7/26/16.
 */
public class Customer {
    public String[] getCustAddressFrmName(String name, String year) {

        String city = DbInt.getCustInf(year, name, "TOWN");
        String State = DbInt.getCustInf(year, name, "STATE");
        String zipCode = DbInt.getCustInf(year, name, "ZIPCODE");
        String strtAddress = DbInt.getCustInf(year, name, "ADDRESS");
        String[] address = new String[4];
        address[0] = city;
        address[1] = State;
        address[2] = zipCode;
        address[3] = strtAddress;
        return address;
    }

    public String getAddr(String name, String year) {
        return DbInt.getCustInf(year, name, "ADDRESS");
    }

    /**
     * Return Phone number of the customer whose name has been specified.
     *
     * @param name The name of the customer
     * @return The Phone number of the specified customer
     */
    public String getPhone(String name, String year) {
        return DbInt.getCustInf(year, name, "PHONE");
    }

    /**
     * Returns if the customer has paid.
     *
     * @param name The name of the customer
     * @return The Payment status of the specified customer
     */
    public String getPaid(String name, String year) {
        return DbInt.getCustInf(year, name, "PAID");
    }

    /**
     * Return Delivery status of the customer whose name has been specified.
     *
     * @param name The name of the customer
     * @return The Delivery status of the specified customer
     */
    public String getDelivered(String name, String year) {
        return DbInt.getCustInf(year, name, "DELIVERED");
    }

    /**
     * Return Email Address of the customer whose name has been specified.
     *
     * @param name The name of the customer
     * @return The Email Address of the specified customer
     */
    public String getEmail(String name, String year) {
        return DbInt.getCustInf(year, name, "Email");
    }

    /**
     * Return Order ID of the customer whose name has been specified.
     *
     * @param name The name of the customer
     * @return The Order ID of the specified customer
     */
    public String getOrderId(String name, String year) {
        return DbInt.getCustInf(year, name, "ORDERID");
    }

    /**
     * Return Donation amount of the customer whose name has been specified.
     *
     * @param name The name of the customer
     * @return The Donation Amount of the specified customer
     */
    public String getDontation(String name, String year) {
        return DbInt.getCustInf(year, name, "DONATION");
    }
}
