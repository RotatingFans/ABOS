<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:template match="/">
        <html>
            <body>
                <div style="position:relative; width:100%">
                    <img alt="" style="position:relative">
                        <xsl:attribute name="src">
                            <xsl:value-of select="catalog/info/logo"/>
                        </xsl:attribute>

                    </img>
                    <div style="position:relative; float:right">
                        <h2>
                            <xsl:value-of select="catalog/info/name"/>
                        </h2>
                        <h2>
                            <xsl:value-of select="catalog/info/streetAddress"/>
                        </h2>
                        <h2>
                            <xsl:value-of select="catalog/info/City"/>
                        </h2>
                        <h2>
                            <xsl:value-of select="catalog/info/rank"/>
                        </h2>
                    </div>
                </div>
                <div>
                    <h2 style="text-align:center; position:relative; top:20px; bottom:20px">
                        <xsl:value-of select="catalog/info/reportTitle"/>
                    </h2>
                </div>
                <table border="1" style="width:100%; position:relative; top:20px">
                    <tr bgcolor="#9acd32">
                        <xsl:for-each select="catalog/columns">
                            <xsl:value-of select="column"/>
                        </xsl:for-each>
                    </tr>
                    <xsl:for-each select="catalog/cd">
                        <tr>
                            <td>
                                <xsl:value-of select="ID"/>
                            </td>
                            <td>
                                <xsl:value-of select="Name"/>
                            </td>
                            <td>
                                <xsl:value-of select="Size"/>
                            </td>
                            <td>
                                <xsl:value-of select="UnitCost"/>
                            </td>
                            <td>
                                <xsl:value-of select="Quantity"/>
                            </td>
                            <td>
                                <xsl:value-of select="TotalCost"/>
                            </td>
                        </tr>
                    </xsl:for-each>
                    <tr>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td>
                            <xsl:value-of select="catalog/info/TotalCost"/>
                        </td>
                    </tr>

                </table>


            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>