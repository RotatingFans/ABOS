/*
 * Copyright (c) Patrick Magauran 2017.
 * Licensed under the AGPLv3. All conditions of said license apply.
 *     This file is part of LawnAndGarden.
 *
 *     LawnAndGarden is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     LawnAndGarden is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with LawnAndGarden.  If not, see <http://www.gnu.org/licenses/>.
 */

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by patrick on 9/15/15.
 */
class CSV2XML extends JDialog {
    private final JPanel contentPanel = new JPanel();
    private String xmlFile = null;
    private JTextField CsvLoc;
    private JTextField XmlLoc;


    public CSV2XML(JDialog parent) {
        super(parent, true);

        initUI();
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }

    public String getXML() {

        return xmlFile;
    }

    //SetBounds(X,Y,Width,Height)
    private void initUI() {
        setModal(true);

        //setSize(450, 450);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout());
        //North
        {
            JPanel north = new JPanel(new FlowLayout());
            {
                JLabel lblNewLabel = new JLabel("Location of CSV");
                //   lblNewLabel.setBounds(10, 25, 80, 14);
                north.add(lblNewLabel);
            }
            {
                CsvLoc = new JTextField();
                //Address.setBounds(90, 15, 340, 28);
                north.add(CsvLoc);
                CsvLoc.setColumns(30);

            }
            {
                JButton openFile = new JButton("...");
                north.add(openFile);
                openFile.addActionListener(e -> {
                    //Creates JFileChooser to select a CSV file
                    JFileChooser chooser = new JFileChooser();


                    FileNameExtensionFilter filter = new FileNameExtensionFilter("Comma Seperated Value File", "csv");
                    chooser.setFileFilter(filter);
                    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    int returnVal = chooser.showOpenDialog(this);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        CsvLoc.setText(chooser.getSelectedFile().getAbsolutePath());
                    }


                });
            }
            north.setMinimumSize(north.getPreferredSize());

            contentPanel.add(north, BorderLayout.NORTH);
        }

        //SOUTH
        {
            JPanel south = new JPanel(new FlowLayout());
            {
                JLabel lblNewLabel = new JLabel("Location of XML");
                //lblNewLabel.setBounds(10, 25, 80, 14);
                south.add(lblNewLabel);
            }
            {
                XmlLoc = new JTextField();
                //XmlLoc.setBounds(90, 15, 340, 28);
                south.add(XmlLoc);
                XmlLoc.setColumns(30);

            }
            {
                JButton openFile = new JButton("...");
                south.add(openFile);
                openFile.addActionListener(e -> {
                    //Creates a JFileChooser to select save location of XML file
                    JFileChooser chooser = new JFileChooser();
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("XML File", "xml");
                    chooser.setFileFilter(filter);

                    int returnVal = chooser.showSaveDialog(this);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        if (chooser.getSelectedFile().getName().endsWith(".xml")) {
                            XmlLoc.setText(chooser.getSelectedFile().getAbsolutePath());
                        } else {
                            XmlLoc.setText(chooser.getSelectedFile().getAbsolutePath() + ".xml");

                        }
                    }


                });
            }
            south.setMinimumSize(south.getPreferredSize());

            contentPanel.add(south, BorderLayout.SOUTH);

        }
        contentPanel.setMinimumSize(contentPanel.getPreferredSize());


        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            JButton okButton;
            {
                okButton = new JButton("OK");

                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            JButton cancelButton;
            {
                cancelButton = new JButton("Cancel");

                buttonPane.add(cancelButton);
            }
            okButton.addActionListener(e -> {

                convert();
                setVisible(false);
                dispose();
            });
            okButton.setActionCommand("OK");

            cancelButton.addActionListener(e -> dispose());
            cancelButton.setActionCommand("Cancel");
        }
        setSize(((int) getContentPane().getPreferredSize().getWidth()), 150);

    }

    /**
     * Converts the CSV to XML
     */
    private void convert() {
        List<String> headers = new ArrayList<>(5);


        File file = new File(CsvLoc.getText());

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder domBuilder = domFactory.newDocumentBuilder();

            Document newDoc = domBuilder.newDocument();
            // Root element
            Element rootElement = newDoc.createElement("LawnGarden");
            newDoc.appendChild(rootElement);

            int line = 0;

            String text;
            while ((text = reader.readLine()) != null) {

                StringTokenizer st = new StringTokenizer(text, ";", false);
                String[] rowValues = new String[st.countTokens()];
                int index = 0;
                while (st.hasMoreTokens()) {

                    String next = st.nextToken();
                    rowValues[index] = next;
                    index++;

                }

                //String[] rowValues = text.split(",");

                if (line == 0) { // Header row
                    Collections.addAll(headers, rowValues);
                } else { // Data row
                    Element rowElement = newDoc.createElement("Products");
                    rootElement.appendChild(rowElement);
                    Attr attr = newDoc.createAttribute("id");
                    attr.setValue(Integer.toString(line - 1));
                    rowElement.setAttributeNode(attr);
                    for (int col = 0; col < headers.size(); col++) {
                        String header = headers.get(col);
                        String value;

                        if (col < rowValues.length) {
                            value = rowValues[col].trim();
                        } else {
                            // ?? Default value
                            value = "";
                        }

                        Element curElement = newDoc.createElement(header);
                        curElement.appendChild(newDoc.createTextNode(value));
                        rowElement.appendChild(curElement);
                    }
                }
                line++;
            }

            OutputStreamWriter osw = null;

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                osw = new OutputStreamWriter(baos);

                TransformerFactory tranFactory = TransformerFactory.newInstance();
                Transformer aTransformer = tranFactory.newTransformer();
                aTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
                aTransformer.setOutputProperty(OutputKeys.METHOD, "xml");
                //aTransformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
                aTransformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

                Source src = new DOMSource(newDoc);
                Result result = new StreamResult(osw);
                aTransformer.transform(src, result);

                osw.flush();
                //System.out.println(new String(baos.toByteArray()));

                try (OutputStream outStream = new FileOutputStream(XmlLoc.getText())) {// writing bytes in to byte output stream

                    baos.writeTo(outStream);
                } catch (IOException e) {
                    LogToFile.log(e, Severity.SEVERE, "Error writing XML file. Please try again.");
                } finally {
                    xmlFile = XmlLoc.getText();
                }


            } catch (Exception exp) {
                LogToFile.log(exp, Severity.SEVERE, "Error writing XML file. Please try again.");
            } finally {
                try {
                    osw.close();
                } catch (IOException e) {
                    LogToFile.log(e, Severity.SEVERE, "Error closing file. Please try again.");
                }

            }
        } catch (Exception e) {
            LogToFile.log(e, Severity.SEVERE, "Error reading CSV file. Ensure the path exists, and the software has permission to read it.");
        }
    }
}
