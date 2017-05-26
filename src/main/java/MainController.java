import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.List;

public class MainController {
    // the FXML annotation tells the loader to inject this variable before invoking initialize.
    @FXML
    private TreeView<String> selectNav;
    @FXML
    private Pane tabPane;
    @FXML
    private Tab tab1;
    // the initialize method is automatically invoked by the FXMLLoader - it's magic
    public void initialize() {
        //loadTreeItems("initial 1", "initial 2", "initial 3");
        fillTreeView();
        selectNav.setShowRoot(false);

        selectNav.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Pane newPane = null;

            // load new pane
            switch (newValue.getValue()) {
                case "Add Customer":
                    new AddCustomer(observable.getValue().getParent().getValue());
                    break;
                case "Reports":
                    new Reports();
                    break;
                case "View Map":
                    new Map();
                    break;
                default:

                    if (observable.getValue().getParent().getValue() == "Root Node") {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("UI/Year.fxml"));
                        try {
                            newPane = loader.load();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        YearController yearCont = loader.getController();
                        yearCont.initYear(newValue.getValue());
                        tab1.setText("Year View - " + newValue.getValue());

                    } else {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("UI/Customer.fxml"));
                        try {
                            newPane = loader.load();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        CustomerController customerCont = loader.getController();
                        customerCont.initCustomer(observable.getValue().getParent().getValue(), newValue.getValue());
                        tab1.setText("Customer View - " + newValue.getValue() + " - " + observable.getValue().getParent().getValue());
                    }
                    // get children of parent of secPane (the VBox)
                    List<Node> parentChildren = ((Pane) tabPane.getParent()).getChildren();

                    // replace the child that contained the old secPane
                    parentChildren.set(parentChildren.indexOf(tabPane), newPane);

                    // store the new pane in the secPane field to allow replacing it the same way later
                    tabPane = newPane;
                    break;
            }
        });
    }

    /**
     * Adds the year buttons to the main panel.
     */
    private void fillTreeView() {
        Iterable<String> ret = DbInt.getYears();
        TreeItem<String> root = new TreeItem<String>("Root Node");
        root.getChildren().add(new TreeItem<>("Reports"));
        root.getChildren().add(new TreeItem<>("View Map"));

        ///Select all years
        //Create a button for each year
/*        for (String aRet : ret) {
            JButton b = new JButton(aRet);
            b.addActionListener(e -> {
                //On button click open Year window
                new YearWindow(((AbstractButton) e.getSource()).getText());

            });
            panel_1.add(b);
        }*/
        for (String itemString : ret) {
            TreeItem<String> tIYear = new TreeItem<String>(itemString);
            Year year = new Year(itemString);
            Iterable<String> customers = year.getCustomerNames();
            for (String customer : customers) {
                tIYear.getChildren().add(new TreeItem<String>(customer));
            }
            tIYear.getChildren().add(new TreeItem<>("Add Customer"));
            root.getChildren().add(tIYear);
        }
        selectNav.setRoot(root);

    }
}
