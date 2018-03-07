<!--
  ~ Copyright (c) Patrick Magauran 2018.
  ~   Licensed under the AGPLv3. All conditions of said license apply.
  ~       This file is part of ABOS.
  ~
  ~       ABOS is free software: you can redistribute it and/or modify
  ~       it under the terms of the GNU Affero General Public License as published by
  ~       the Free Software Foundation, either version 3 of the License, or
  ~       (at your option) any later version.
  ~
  ~       ABOS is distributed in the hope that it will be useful,
  ~       but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~       GNU Affero General Public License for more details.
  ~
  ~       You should have received a copy of the GNU Affero General Public License
  ~       along with ABOS.  If not, see <http://www.gnu.org/licenses/>.
  -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:template match="/">

        <head>
            <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
            <title>
                <xsl:value-of select="LawnGardenReports/info/reportTitle"/>
            </title>
            <style type="text/css">
                * {
                margin-top: 0px;
                margin-bottom: 0px;
                }
                .LBordered {
                border-left: 1px solid black;
                border-collapse: collapse;
                }
                .Bordered {
                border: 1px solid black;
                border-collapse: collapse;
                }
                .UBordered {
                border: 0px solid black;
                border-collapse: collapse;
                }
                .splitTitle {display:inline;}
                h4{
                margin:1px;
                padding:1px;
                }
                table {
                width:100%;
                margin-bottom: 0.4pt;
                margin-top: 0;
                margin-left: 0;
                margin-right: 0;
                text-indent: 0;
                }
                tr {
                vertical-align: inherit;
                border:0;
                }
                table > tr {
                vertical-align: middle;
                }
                td {
                background-color:#FFF;
                font-size:10pt;
                padding: 1px;
                text-align: inherit;
                vertical-align: inherit;
                }
                th {
                background-color: #FFF;
                font-size:10pt;
                color:#000;
                display: table-cell;
                font-weight: bold;
                padding: 1px;
                vertical-align: inherit;
                }
            </style>
        </head>
        <html>
            <body>

                <xsl:for-each select="LawnGardenReports/customerYear">
                    <div style="page-break-after: always;">
                        <xsl:if test="header">
                            <table border="0" style="position:relative; width:100%">
                                <tr>
                                    <td>
                                        <img alt="logo" style="position:relative; width:200px; display:inline;">
                                    <xsl:attribute name="src">
                                        <xsl:value-of select="/LawnGardenReports/info/logo"/>
                                    </xsl:attribute>

                                </img>
                                    </td>
                                    <td style="text-align:right;">
                                    <h4>
                                        <xsl:value-of select="/LawnGardenReports/info/name"/>
                                    </h4>
                                    <h4>
                                        <xsl:value-of select="/LawnGardenReports/info/streetAddress"/>
                                    </h4>
                                    <h4>
                                        <xsl:value-of select="/LawnGardenReports/info/city"/>
                                    </h4>
                                    <h4>
                                        <xsl:value-of select="/LawnGardenReports/info/PhoneNumber"/>
                                    </h4>
                                    <h4>
                                        <xsl:value-of select="/LawnGardenReports/info/rank"/>
                                    </h4>

                                    </td>
                                </tr>
                            </table>
                            <div>
                                <h2 style="text-align:center; position:relative;">
                                    <xsl:value-of select="/LawnGardenReports/info/reportTitle"/>
                                </h2>
                            </div>
                        </xsl:if>
                        <div>
                            <h2 class="SplitTitle"
                                style="text-align:center; position:relative;">
                                <xsl:value-of select="//splitting"/>
                                <xsl:text>&#xA0;</xsl:text>
                                <xsl:value-of select="title"/>

                            </h2>


                            <xsl:if test="custAddr">
                                <div style="float:left;clear:both;">
                                    <h4>
                                        <xsl:value-of select="name"/>
                                    </h4>
                                    <h4>
                                        <xsl:value-of select="streetAddress"/>
                                    </h4>
                                    <h4>
                                        <xsl:value-of select="city"/>
                                    </h4>
                                    <h4>
                                        <xsl:value-of select="PhoneNumber"/>
                                    </h4>
                                </div>
                            </xsl:if>
                            <div>
                                <xsl:for-each select="specialInfo">
                                    <h2 class="specialInfo"
                                        style="text-align:center; position:relative">
                                        <xsl:value-of select="text"/>
                                    </h2>
                                </xsl:for-each>
                            </div>

                        </div>
                        <xsl:if test="prodTable">

                            <table cellspacing="5" cellpadding="5" class="Bordered"
                                   style="width:100%; position:relative; padding-top:20px;clear:both;">
                                <tr bgcolor="#9acd32">
                                    <xsl:for-each select="//column">
                                        <th style="text-align:left; border-bottom:1px solid black;">
                                            <xsl:value-of select="name"/>
                                        </th>
                                    </xsl:for-each>
                                </tr>
                                <xsl:for-each select="Product">
                                    <tr class="">
                                        <td style="border-bottom:1px solid black;" width="5%">
                                            <xsl:value-of select="ID"/>
                                        </td>
                                        <td class="LBordered" width="45%">
                                            <xsl:value-of select="Name"/>
                                        </td>
                                        <td class="LBordered" width="10%">
                                            <xsl:value-of select="Size"/>
                                        </td>
                                        <td class="LBordered" width="8%">
                                            <xsl:value-of select="UnitCost"/>
                                        </td>
                                        <td class="LBordered" width="8%">
                                            <xsl:value-of select="Quantity"/>
                                        </td>
                                        <td class="LBordered" width="8%">
                                            <xsl:value-of select="TotalCost"/>
                                        </td>
                                    </tr>
                                </xsl:for-each>
                                <tr class="UBordered">
                                    <td class="UBordered"></td>
                                    <td class="UBordered"></td>
                                    <td class="UBordered"></td>
                                    <td class="UBordered"></td>
                                    <td class="LBordered">Sub Total:</td>
                                    <td class="LBordered">
                                        <xsl:value-of select="totalCost"/>
                                    </td>
                                </tr>
                                <xsl:if test="includeDonation">
                                    <tr class="UBordered">
                                        <td class="UBordered"></td>
                                        <td class="UBordered"></td>
                                        <td class="UBordered"></td>
                                        <td class="UBordered"></td>
                                        <td class="LBordered">Total Pledged Donation:</td>
                                        <td class="LBordered">
                                            <xsl:value-of select="Donation"/>
                                        </td>
                                    </tr>
                                    <tr class="UBordered">
                                        <td class="UBordered"></td>
                                        <td class="UBordered"></td>
                                        <td class="UBordered"></td>
                                        <td class="UBordered"></td>
                                        <td style="border-left:1px solid black;">Grand Total:</td>
                                        <td style="border-left:1px solid black;">
                                            <xsl:value-of select="GrandTotal"/>
                                        </td>
                                    </tr>
                                </xsl:if>

                            </table>
                        </xsl:if>
                        <div>
                            <xsl:for-each select="DonationThanks">
                                <h2 class="DonateGrat"
                                    style="text-align:center; position:relative; display:block; padding-top:80px; padding-bottom:20px; width:100%;">
                                    <xsl:value-of select="text"/>
                                </h2>
                            </xsl:for-each>
                        </div>

                    </div>
                </xsl:for-each>
                <h2 style="text-align:right; position:relative;">
                    TOTALS
                </h2>
                <div style="position:relative;">
                    <table class="Bordered" border="0" style="position:absolute; top:0px; right:0px;">


                        <tr class="Bordered">

                            <td>Total Cost:</td>
                            <td>
                                <xsl:value-of select="LawnGardenReports/info/TotalCost"/>
                            </td>
                        </tr>
                        <tr class="Bordered">

                            <td>Total Quantity:</td>
                            <td>
                                <xsl:value-of select="LawnGardenReports/info/totalQuantity"/>
                            </td>
                        </tr>

                    </table>
                </div>

            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>