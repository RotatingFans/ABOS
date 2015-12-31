import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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


    CSV2XML(JDialog parent) {
        super(parent, true);

        initUI();
        setVisible(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    }

    public static void main(String[] args) {

    }

    public String getXML() {

        return xmlFile;
    }

    //SetBounds(X,Y,Width,Height)
    private void initUI() {
        setModal(true);

        setSize(450, 450);
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

        contentPanel.setMinimumSize(contentPanel.getPreferredSize());


        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);
        JButton okButton = new JButton("OK");

        buttonPane.add(okButton);
        getRootPane().setDefaultButton(okButton);
        JButton cancelButton = new JButton("Cancel");

        buttonPane.add(cancelButton);
        okButton.addActionListener(e -> {

            convert();
            setVisible(false);
            dispose();
        });
        okButton.setActionCommand("OK");

        cancelButton.addActionListener(e -> dispose());
        cancelButton.setActionCommand("Cancel");
        setSize((int) getContentPane().getPreferredSize().getWidth(), 150);

    }

    /**
     * Converts the CSV to XML
     */
    private void convert() {
        List<String> headers = new ArrayList<>(5);


        File file = new File(CsvLoc.getText());
        BufferedReader reader;

        try {

            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder domBuilder = domFactory.newDocumentBuilder();

            Document newDoc = domBuilder.newDocument();
            // Root element
            Element rootElement = newDoc.createElement("LawnGarden");
            newDoc.appendChild(rootElement);

            reader = new BufferedReader(new FileReader(file));
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

            ByteArrayOutputStream baos = null;
            OutputStreamWriter osw = null;

            try {
                baos = new ByteArrayOutputStream();
                osw = new OutputStreamWriter(baos);

                TransformerFactory tranFactory = TransformerFactory.newInstance();
                Transformer aTransformer = tranFactory.newTransformer();
                aTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
                aTransformer.setOutputProperty(OutputKeys.METHOD, "xml");
                aTransformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

                Source src = new DOMSource(newDoc);
                Result result = new StreamResult(osw);
                aTransformer.transform(src, result);

                osw.flush();
                System.out.println(new String(baos.toByteArray()));
                OutputStream outStream;

                try {
                    outStream = new FileOutputStream(XmlLoc.getText());// writing bytes in to byte output stream

                    baos.writeTo(outStream);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    xmlFile = XmlLoc.getText();
                }


            } catch (TransformerConfigurationException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (TransformerException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception exp) {
                exp.printStackTrace();
            } finally {
                try {
                    if (osw != null) {
                        osw.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
                try {
                    if (baos != null) {
                        baos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (DOMException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
