<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:template match="/">
        <html>
            <body>
                <div style="position:relative; width:100%">
                    <img alt="" width="200px" style="position:relative">
                        <xsl:attribute name="src">
                            <xsl:value-of select="LawnGardenReports/info/logo"/>
                        </xsl:attribute>

                    </img>
                    <div style="position:relative; float:right">
                        <h2>
                            <xsl:value-of select="LawnGardenReports/info/name"/>
                        </h2>
                        <h2>
                            <xsl:value-of select="LawnGardenReports/info/streetAddress"/>
                        </h2>
                        <h2>
                            <xsl:value-of select="LawnGardenReports/info/City"/>
                        </h2>
                        <h2>
                            <xsl:value-of select="LawnGardenReports/info/rank"/>
                        </h2>
                    </div>
                </div>
                <div>
                    <h2 style="text-align:center; position:relative; top:20px; bottom:20px">
                        <xsl:value-of select="LawnGardenReports/info/reportTitle"/>
                    </h2>
                </div>
                <div>
                    <h2 style="text-align:left; position:relative; top:20px; bottom:20px">
                        <xsl:value-of select="LawnGardenReports/info/splitting"/>
                    </h2>
                </div>
                <xsl:for-each select="LawnGardenReports/customerYear">

                    <div>
                        <h2 style="text-align:center; position:relative; top:20px; bottom:20px">
                            <xsl:value-of select="title"/>
                        </h2>
                    </div>
                    <table id="Bordered" border="1" style="width:100%; position:relative; top:20px">
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
                        <tr id="Bordered">
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td id="Bordered">Total Cost:</td>
                            <td id="Bordered">
                                <xsl:value-of select="totalCost"/>
                            </td>
                        </tr>

                    </table>
                </xsl:for-each>
                <h2 style="text-align:right; position:relative; top:20px; bottom:20px">
                    TOTALS
                </h2>
                <style>
                    #Bordered {
                    border: 1px solid black;
                    border-collapse: collapse;
                    }
                </style>
                <table id="Bordered" border="0" style="position:relative; top:20px; float:right">


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
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>