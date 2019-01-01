module.exports = '<html>\n' +
  '<head>\n' +
  '  <meta http-equiv="content-type" content="text/html; charset=UTF-8" />\n' +
  '  <title> {{info.reportTitle}}\n' +
  '  </title>\n' +
  '  <style type="text/css">\n' +
  '    * {\n' +
  '      margin-top: 0px;\n' +
  '      margin-bottom: 0px;\n' +
  '      font-family: \'Helvetica Neue\', \'Helvetica\', Helvetica, Arial, sans-serif;\n' +
  '    }\n' +
  '\n' +
  '    .LBordered {\n' +
  '      border-left: 1px solid black;\n' +
  '      border-collapse: collapse;\n' +
  '    }\n' +
  '\n' +
  '    .Bordered {\n' +
  '      border: 1px solid black;\n' +
  '      border-collapse: collapse;\n' +
  '    }\n' +
  '\n' +
  '    .UBordered {\n' +
  '      border: 0px solid black;\n' +
  '      border-collapse: collapse;\n' +
  '    }\n' +
  '\n' +
  '    .splitTitle {\n' +
  '\n' +
  '    }\n' +
  '\n' +
  '    h4 {\n' +
  '      margin: 1px;\n' +
  '      padding: 1px;\n' +
  '    }\n' +
  '\n' +
  '    table {\n' +
  '      width: 100%;\n' +
  '      margin-bottom: 0.4pt;\n' +
  '      margin-top: 0;\n' +
  '      margin-left: 0;\n' +
  '      margin-right: 0;\n' +
  '      text-indent: 0;\n' +
  '    }\n' +
  '\n' +
  '    tr {\n' +
  '      vertical-align: inherit;\n' +
  '      border: 0;\n' +
  '    }\n' +
  '\n' +
  '    table>tr {\n' +
  '      vertical-align: middle;\n' +
  '    }\n' +
  '\n' +
  '    td {\n' +
  '      background-color: #FFF;\n' +
  '      font-size: 10pt;\n' +
  '      padding: 1px;\n' +
  '      text-align: inherit;\n' +
  '      vertical-align: inherit;\n' +
  '    }\n' +
  '\n' +
  '    th {\n' +
  '      background-color: #FFF;\n' +
  '      font-size: 10pt;\n' +
  '      color: #000;\n' +
  '      display: table-cell;\n' +
  '      font-weight: bold;\n' +
  '      padding: 1px;\n' +
  '      vertical-align: inherit;\n' +
  '    }\n' +
  '  </style>\n' +
  '</head>\n' +
  '\n' +
  '<body>\n' +
  '\n' +
  '{{#each customerYear}}\n' +
  '<div style="page-break-after: always;">\n' +
  '  {{#if header}}\n' +
  '  <table border="0" style="position:relative; width:100%">\n' +
  '    <tr>\n' +
  '      <td>\n' +
  '        <img alt="logo" style="position:relative; width:200px; display:inline;" src="{{../info.logo}}" />\n' +
  '\n' +
  '\n' +
  '\n' +
  '      </td>\n' +
  '      <td style="text-align:right; vertical-align: top">\n' +
  '        <h4>\n' +
  '          {{../info.name}}\n' +
  '        </h4>\n' +
  '        <h4>\n' +
  '          {{../info.streetAddress}}\n' +
  '\n' +
  '        </h4>\n' +
  '        <h4>\n' +
  '          {{../info.city}}\n' +
  '\n' +
  '        </h4>\n' +
  '        <h4>\n' +
  '          {{../info.PhoneNumber}}\n' +
  '\n' +
  '        </h4>\n' +
  '        <h4>\n' +
  '          {{../info.rank}}\n' +
  '\n' +
  '        </h4>\n' +
  '\n' +
  '      </td>\n' +
  '    </tr>\n' +
  '  </table>\n' +
  '  <div>\n' +
  '    <h2 style="text-align:center; position:relative;">\n' +
  '      {{../info.reportTitle}}\n' +
  '\n' +
  '    </h2>\n' +
  '  </div>\n' +
  '  {{/if}}\n' +
  '  <div>\n' +
  '    <h2 class="SplitTitle" style="text-align:center; position:relative">{{../splitting}} {{title}}</h2>\n' +
  '\n' +
  '    {{#if custAddr}}\n' +
  '    <div style="">\n' +
  '      <h4>\n' +
  '        {{name}} </h4>\n' +
  '      <h4>\n' +
  '        {{streetAddress}}\n' +
  '      </h4>\n' +
  '      <h4>\n' +
  '        {{city}}\n' +
  '      </h4>\n' +
  '      <h4>\n' +
  '        {{PhoneNumber}}\n' +
  '      </h4>\n' +
  '    </div>\n' +
  '    {{/if}}\n' +
  '    <div>\n' +
  '      {{#each specialInfo}}\n' +
  '      <h2 class="specialInfo" style="text-align:center; position:relative">\n' +
  '        {{text}}\n' +
  '      </h2>\n' +
  '      {{/each}}\n' +
  '    </div>\n' +
  '\n' +
  '  </div>\n' +
  '  {{#if prodTable}}\n' +
  '\n' +
  '  <table cellspacing="5" cellpadding="5" class="Bordered" style="width:100%; position:relative; padding-top:20px;clear:both;">\n' +
  '    <tr bgcolor="#9acd32">\n' +
  '      {{#each ../column}}\n' +
  '      <th style="text-align:left; border-bottom:1px solid black;">\n' +
  '        {{name}}\n' +
  '      </th>\n' +
  '      {{/each}}\n' +
  '    </tr>\n' +
  '    {{#each Product}}\n' +
  '\n' +
  '    <tr class="Bordered" style="border-bottom:1px solid black;">\n' +
  '      <td width="5%" style="border-bottom:1px solid black;">\n' +
  '        {{ID}}\n' +
  '      </td>\n' +
  '      <td class="LBordered" style="border-bottom:1px solid black;" width="45%">\n' +
  '        {{Name}}\n' +
  '\n' +
  '      </td>\n' +
  '      <td class="LBordered" style="border-bottom:1px solid black;" width="10%">\n' +
  '        {{Size}}\n' +
  '\n' +
  '      </td>\n' +
  '      <td class="LBordered" style="border-bottom:1px solid black;" width="8%">\n' +
  '        {{UnitCost}}\n' +
  '\n' +
  '      </td>\n' +
  '      <td class="LBordered" style="border-bottom:1px solid black;" width="8%">\n' +
  '        {{Quantity}}\n' +
  '\n' +
  '      </td>\n' +
  '      <td class="LBordered" style="border-bottom:1px solid black;" width="8%">\n' +
  '        {{TotalCost}}\n' +
  '\n' +
  '      </td>\n' +
  '    </tr>\n' +
  '    {{/each}}\n' +
  '    <tr class="UBordered">\n' +
  '      <td class="UBordered"></td>\n' +
  '      <td class="UBordered"></td>\n' +
  '      <td class="UBordered"></td>\n' +
  '      <td class="UBordered"></td>\n' +
  '      <td class="LBordered">Sub Total:</td>\n' +
  '      <td class="LBordered">\n' +
  '        {{totalCost}}\n' +
  '      </td>\n' +
  '    </tr>\n' +
  '    {{#if includeDonation}}\n' +
  '    <tr class="UBordered">\n' +
  '      <td class="UBordered"></td>\n' +
  '      <td class="UBordered"></td>\n' +
  '      <td class="UBordered"></td>\n' +
  '      <td class="UBordered"></td>\n' +
  '      <td class="Bordered">Total Pledged Donation:</td>\n' +
  '      <td class="Bordered">\n' +
  '        {{Donation}}\n' +
  '      </td>\n' +
  '    </tr>\n' +
  '    <tr class="UBordered">\n' +
  '      <td class="UBordered"></td>\n' +
  '      <td class="UBordered"></td>\n' +
  '      <td class="UBordered"></td>\n' +
  '      <td class="UBordered"></td>\n' +
  '      <td style="border-left:1px solid black;">Grand Total:</td>\n' +
  '      <td style="border-left:1px solid black;">\n' +
  '        {{GrandTotal}}\n' +
  '\n' +
  '      </td>\n' +
  '    </tr>\n' +
  '    {{/if}}\n' +
  '\n' +
  '  </table>\n' +
  '  {{/if}}\n' +
  '  <div>\n' +
  '    {{#each DonationThanks}}\n' +
  '    <h2 class="DonateGrat" style="text-align:center; position:relative; display:block; padding-top:80px; padding-bottom:20px; width:100%;">\n' +
  '      {{text}}\n' +
  '    </h2>\n' +
  '    {{/each}}\n' +
  '  </div>\n' +
  '\n' +
  '</div>\n' +
  '{{/each}}\n' +
  '<h2 style="text-align:right; position:relative;">\n' +
  '  TOTALS\n' +
  '</h2>\n' +
  '<div style="position:relative;">\n' +
  '  <table class="Bordered" border="0" style="position:absolute; top:0px; right:0px;">\n' +
  '\n' +
  '\n' +
  '    <tr class="Bordered">\n' +
  '\n' +
  '      <td>Total Cost:</td>\n' +
  '      <td class="Bordered">  {{info.TotalCost}}\n' +
  '      </td>\n' +
  '    </tr>\n' +
  '    <tr class="Bordered">\n' +
  '\n' +
  '      <td>Total Quantity:</td>\n' +
  '      <td class="Bordered"> {{info.TotalQuantity}}\n' +
  '      </td>\n' +
  '    </tr>\n' +
  '\n' +
  '  </table>\n' +
  '</div>\n' +
  '\n' +
  '</body>\n' +
  '\n' +
  '</html>';
