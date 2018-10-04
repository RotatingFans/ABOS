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

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.math.BigDecimal;

public class formattedProductProps {

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

    public void setProductKey(int productKey) {
        this.productKey.set(productKey);
    }

    public String getProductID() {
        return productID.get();
    }

    public void setProductID(String productID) {
        this.productID.set(productID);
    }

    public String getProductName() {
        return productName.get();
    }

    public void setProductName(String productName) {
        this.productName.set(productName);
    }

    public String getProductSize() {
        return productSize.get();
    }

    public void setProductSize(String productSize) {
        this.productSize.set(productSize);
    }

    public BigDecimal getProductUnitPrice() {
        return productUnitPrice.get();
    }

    public void setProductUnitPrice(BigDecimal productUnitPrice) {
        this.productUnitPrice.set(productUnitPrice);
    }

    public String getProductCategory() {
        return productCategory.get();
    }

    public void setProductCategory(String productCategory) {
        this.productCategory.set(productCategory);
    }

    public int getOrderedQuantity() {
        return orderedQuantity.get();
    }

    public void setOrderedQuantity(int orderedQuantity) {
        this.orderedQuantity.set(orderedQuantity);
    }

    public String getProductUnitPriceString() {
        return productUnitPriceString.get();
    }

    public void setProductUnitPriceString(String productUnitPriceString) {
        this.productUnitPriceString.set(productUnitPriceString);
    }

    public String getOrderedQuantityString() {
        return orderedQuantityString.get();
    }

    public void setOrderedQuantityString(String orderedQuantityString) {
        this.orderedQuantityString.set(orderedQuantityString);
    }

    public BigDecimal getExtendedCost() {
        return extendedCost.get();
    }

    public void setExtendedCost(BigDecimal extendedCost) {
        this.extendedCost.set(extendedCost);
    }
}
