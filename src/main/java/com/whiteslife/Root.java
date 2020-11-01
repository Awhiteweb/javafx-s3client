package com.whiteslife;

import com.whiteslife.aws.s3.Client;
import com.whiteslife.rx.observables.S3Observables;
import com.whiteslife.rx.observables.TestObs;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Root extends Application {

    public void start( Stage stage ) throws Exception {
        S3Observables s3Observables = new S3Observables( new Client() );

        GridPane pane = new GridPane();
        Label title = new Label("S3 bucket list");
        pane.add( title, 0, 0 );

        ListView<String> list = new ListView<String>();
        ObservableList observableList = FXCollections.observableList(new ArrayList<>());
        list.setItems( observableList );
        pane.add( list, 0, 1, 1, 2 );

        Button startBtn = new Button("Start task");
        startBtn.setOnAction( actionEvent -> {
            startBtn.setDisable( true );
            try {
                new TestObs().expandTest( observableList );
            }
            catch( Throwable throwable ) {
                throwable.printStackTrace();
            }
        } );
        Button stopBtn = new Button("Stop task");
        pane.add( startBtn, 1, 0 );
        pane.add( stopBtn, 1, 1 );

        Scene grid = new Scene( pane );
        stage.setScene( grid );
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }
}
