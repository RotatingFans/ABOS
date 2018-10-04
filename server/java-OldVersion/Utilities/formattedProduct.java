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

import java.math.BigDecimal;

public class formattedProduct {
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
