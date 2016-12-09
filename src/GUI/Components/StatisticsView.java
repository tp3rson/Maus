package GUI.Components;

import GUI.Controller;
import GUI.Styler;
import Server.Data.PseudoBase;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;


public class StatisticsView {
    public static Label getConnectionsLabel() {
        return connectionsLabel;
    }
    private static Label connectionsLabel = null;

    public HBox getStatisticsView() {
        Timeline fiveSecondTime = new Timeline(new KeyFrame(Duration.seconds(5), event -> Controller.updateStats()));
        fiveSecondTime.setCycleCount(Timeline.INDEFINITE);
        fiveSecondTime.play();
        HBox hBox = new HBox();
        HBox.setHgrow(hBox, Priority.ALWAYS);
        VBox vBox = new VBox();
        vBox.getStylesheets().add(Styler.globalCSS);
        VBox.setVgrow(vBox, Priority.ALWAYS);
        connectionsLabel = new Label("Connections: " + PseudoBase.getMausData().size());
        connectionsLabel = (Label) Styler.styleAdd(connectionsLabel, "label-bright");
        vBox.getChildren().add(connectionsLabel);
        hBox.getChildren().add(vBox);
        return hBox;
    }
}
