/*
 * Copyright (c) Patrick Magauran 2017.
 *   Licensed under the AGPLv3. All conditions of said license apply.
 *       This file is part of ABOS.
 *
 *       ABOS is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Affero General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       ABOS is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Affero General Public License for more details.
 *
 *       You should have received a copy of the GNU Affero General Public License
 *       along with ABOS.  If not, see <http://www.gnu.org/licenses/>.
 */

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by patrick on 7/27/16.
 */
public class Product {
    public final int productKey;
    public final String productID;
    public final String productName;
    public final String productSize;
    public final BigDecimal productUnitPrice;
    public final String productCategory;

    public Product(int productKey, String productID, String productName, String productSize, BigDecimal productUnitPrice, String productCategory) {
        this.productKey = productKey;
        this.productID = productID;
        this.productName = productName;
        this.productSize = productSize;
        this.productUnitPrice = productUnitPrice;
        this.productCategory = productCategory;
    }

    @SuppressWarnings("unused")
    public static List<String> GetProductInfo(String info, String PID, String year) {
        List<String> ret = new ArrayList<>();

        try (Connection con = DbInt.getConnection(year);
             PreparedStatement prep = con.prepareStatement("SELECT * FROM products WHERE idproducts=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setString(1, PID);

            try (ResultSet rs = prep.executeQuery()) {

                while (rs.next()) {

                    ret.add(rs.getString(info));

                }
            }
            ////DbInt.pCon.close()

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        return ret;
    }

    public static class formattedProduct {
        public final int productKey;

        public final String productID;
        public final String productName;
        public final String productSize;
        public final BigDecimal productUnitPrice;
        public final String productCategory;
        public final int orderedQuantity;
        public final BigDecimal extendedCost;

        public formattedProduct(int productKey, String productID, String productName, String productSize, BigDecimal productUnitPrice, String productCategory, int orderedQuantity, BigDecimal extendedCost) {
            this.productKey = productKey;
            this.productID = productID;
            this.productName = productName;
            this.productSize = productSize;
            this.productUnitPrice = productUnitPrice;
            this.productCategory = productCategory;
            this.orderedQuantity = orderedQuantity;
            this.extendedCost = extendedCost;
        }
    }

    public static class formattedProductProps {

        public final SimpleIntegerProperty productKey = new SimpleIntegerProperty();

        public final SimpleObjectProperty<BigDecimal> productUnitPrice = new SimpleObjectProperty<>();

        public final SimpleStringProperty productUnitPriceString = new SimpleStringProperty();

        public final SimpleStringProperty productID = new SimpleStringProperty();

        public final SimpleStringProperty productName = new SimpleStringProperty();
        public final SimpleStringProperty productSize = new SimpleStringProperty();
        public final SimpleObjectProperty<BigDecimal> extendedCost = new SimpleObjectProperty();
        public final SimpleStringProperty productCategory = new SimpleStringProperty();
        public final SimpleStringProperty orderedQuantityString = new SimpleStringProperty();

        public final SimpleIntegerProperty orderedQuantity = new SimpleIntegerProperty();

        public formattedProductProps(int ProductKey, String productID, String productName, String productSize, BigDecimal productUnitPrice, String productCategory, int orderedQuantity, BigDecimal extendedCost) {
            this.productKey.set(ProductKey);
            this.productID.set(productID);
            this.productName.set(productName);
            this.productSize.set(productSize);
            this.productUnitPrice.set(productUnitPrice);
            this.productUnitPriceString.set(productUnitPrice.toPlainString());
            this.productCategory.set(productCategory);
            this.orderedQuantity.set(orderedQuantity);
            this.orderedQuantityString.set(String.valueOf(orderedQuantity));
            this.extendedCost.set(extendedCost);
        }

        public SimpleIntegerProperty productKeyProperty() {
            return productKey;
        }

        public int getProductKey() {
            return productKey.get();
        }
        public String getProductID() {
            return productID.get();
        }

        public String getProductName() {
            return productName.get();
        }

        public String getProductSize() {
            return productSize.get();
        }

        public BigDecimal getProductUnitPrice() {
            return productUnitPrice.get();
        }

        public String getProductCategory() {
            return productCategory.get();
        }

        public int getOrderedQuantity() {
            return orderedQuantity.get();
        }

        public String getProductUnitPriceString() {
            return productUnitPriceString.get();
        }


        public String getOrderedQuantityString() {return orderedQuantityString.get();}

        public BigDecimal getExtendedCost() {
            return extendedCost.get();
        }
    }
}
