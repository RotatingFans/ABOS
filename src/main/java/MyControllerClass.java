import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.List;

public class MyControllerClass {
    // the FXML annotation tells the loader to inject this variable before invoking initialize.
    @FXML
    private TreeView<String> selectNav;
    @FXML
    private Pane tab1;

    // the initialize method is automatically invoked by the FXMLLoader - it's magic
    public void initialize() {
        loadTreeItems("initial 1", "initial 2", "initial 3");
        selectNav.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // load new pane
            Pane newPane = null;
            try {
                newPane = FXMLLoader.load(getClass().getResource("UI/Main.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            // get children of parent of secPane (the VBox)
            List<Node> parentChildren = ((Pane) tab1.getParent()).getChildren();

            // replace the child that contained the old secPane
            parentChildren.set(parentChildren.indexOf(tab1), newPane);

            // store the new pane in the secPane field to allow replacing it the same way later
            tab1 = newPane;
        });
    }
    // loads some strings into the tree in the application UI.

    private void updateSelectedItem(Object newValue) {
        System.out.println(newValue);
    }

    public void loadTreeItems(String... rootItems) {
        TreeItem<String> root = new TreeItem<String>("Root Node");
        root.setExpanded(true);
        for (String itemString : rootItems) {
            root.getChildren().add(new TreeItem<String>(itemString));
        }

        selectNav.setRoot(root);
    }
}
