<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:template match="/">

        <head>
            <meta http-equiv="content-type" content="text/html; charset=UTF-8"></meta>
            <title>
                <xsl:value-of select="LawnGardenReports/info/reportTitle"/>
            </title>
            <style type="text/css">
                #Bordered {
                border: 1px solid black;
                border-collapse: collapse;
                }
                #UBordered {
                border: 0px solid black;
                border-collapse: collapse;
                }
                #SplitTitle {display:inline;}
                h4{
                margin:1px;
                padding:1px;
                }
            </style>
        </head>
        <html>
            <body>

                <xsl:for-each select="LawnGardenReports/customerYear">
                    <div style="page-break-after: always;">
                        <xsl:if test="header">
                            <div style="position:relative; width:100%">
                                <img alt="logo" style="position:relative; width:200px;">
                                    <xsl:attribute name="src">
                                        <xsl:value-of select="/LawnGardenReports/info/logo"/>
                                    </xsl:attribute>

                                </img>
                                <div style="position:relative; float:right">
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

                                </div>
                            </div>
                            <div>
                                <h2 style="text-align:center; position:relative; padding-top:20px; padding-bottom:20px">
                                    <xsl:value-of select="/LawnGardenReports/info/reportTitle"/>
                                </h2>
                            </div>
                        </xsl:if>
                        <div>
                            <h2 id="SplitTitle"
                                style="text-align:left; position:relative; padding-top:20px; padding-bottom:20px">
                                <xsl:value-of select="//splitting"/>
                            </h2>


                            <xsl:if test="custAddr">
                                <div style="float:left; padding-top:20px; padding-bottom: 20px;clear:both;">
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
                                    <h2 id="specialInfo"
                                        style="text-align:center; position:relative; padding-top:20px; padding-bottom:20px">
                                        <xsl:value-of select="text"/>
                                    </h2>
                                </xsl:for-each>
                            </div>
                            <h2 id="SplitTitle"
                                style="text-align:center; position:relative; padding-top:20px; padding-bottom:20px">
                                <xsl:value-of select="title"/>
                            </h2>
                        </div>

                        <table id="Bordered"
                               style="width:100%; position:relative; padding-top:20px;clear:both;">
                            <tr bgcolor="#9acd32">
                                <xsl:for-each select="//column">
                                    <th style="text-align:left">
                                        <xsl:value-of select="name"/>
                                    </th>
                                </xsl:for-each>
                            </tr>
                            <xsl:for-each select="Product">
                                <tr id="Bordered">
                                    <td id="Bordered">
                                        <xsl:value-of select="ID"/>
                                    </td>
                                    <td id="Bordered">
                                        <xsl:value-of select="Name"/>
                                    </td>
                                    <td id="Bordered">
                                        <xsl:value-of select="Size"/>
                                    </td>
                                    <td id="Bordered">
                                        <xsl:value-of select="UnitCost"/>
                                    </td>
                                    <td id="Bordered">
                                        <xsl:value-of select="Quantity"/>
                                    </td>
                                    <td id="Bordered">
                                        <xsl:value-of select="TotalCost"/>
                                    </td>
                                </tr>
                            </xsl:for-each>
                            <tr id="UBordered">
                                <td id="UBordered"></td>
                                <td id="UBordered"></td>
                                <td id="UBordered"></td>
                                <td id="UBordered"></td>
                                <td id="Bordered">Sub Total:</td>
                                <td id="Bordered">
                                    <xsl:value-of select="totalCost"/>
                                </td>
                            </tr>
                            <xsl:if test="includeDonation">
                                <tr id="UBordered">
                                    <td id="UBordered"></td>
                                    <td id="UBordered"></td>
                                    <td id="UBordered"></td>
                                    <td id="UBordered"></td>
                                    <td id="Bordered">Total Pledged Donation:</td>
                                    <td id="Bordered">
                                        <xsl:value-of select="Donation"/>
                                    </td>
                                </tr>
                                <tr id="UBordered">
                                    <td id="UBordered"></td>
                                    <td id="UBordered"></td>
                                    <td id="UBordered"></td>
                                    <td id="UBordered"></td>
                                    <td id="Bordered">Grand Total:</td>
                                    <td id="Bordered">
                                        <xsl:value-of select="GrandTotal"/>
                                    </td>
                                </tr>
                            </xsl:if>
                        </table>
                    </div>
                </xsl:for-each>
                <h2 style="text-align:right; position:relative; padding-top:20px; padding-bottom:20px">
                    TOTALS
                </h2>
                <div style="position:relative;">
                    <table id="Bordered" border="0" style="position:absolute; top:0px; right:0px;">


                        <tr id="Bordered">

                            <td>Total Cost:</td>
                            <td>
                                <xsl:value-of select="LawnGardenReports/info/TotalCost"/>
                            </td>
                        </tr>
                        <tr id="Bordered">

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