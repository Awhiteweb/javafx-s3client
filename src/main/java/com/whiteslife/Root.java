package com.whiteslife;

import com.amazonaws.regions.Regions;
import com.whiteslife.aws.s3.ObservableS3Client;
import com.whiteslife.aws.s3.S3Repository;
import com.whiteslife.javafx.extensions.BaseGridPane;
import com.whiteslife.view.models.ListModel;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.LinkedList;

public class Root extends Application {
    private S3Repository s3Repository;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private String selectedBucket = null;

    public void start( Stage stage ) {
        this.s3Repository = new S3Repository( ObservableS3Client.instance( Regions.EU_WEST_1 ) );

        ////////////
        // Labels //
        ////////////
        Label title = new Label( "S3 bucket list" );
        Label errorMessage = new Label( "" );

        ///////////
        // Lists //
        ///////////
        ListView<String> bucketListView = new ListView<>();
        ListView<ListModel> keyListView = new ListView<>();

        /////////////
        // Buttons //
        /////////////
        Button getBucketsButton = new Button( "Get Buckets" );
        Button getKeysButton = new Button( "Get Keys" );
        Button listMultipartUploadsButton = new Button( "List multipart uploads" );

        /////////////
        // Content //
        /////////////
        getKeysButton.setDisable( true );
        listMultipartUploadsButton.setDisable( true );
        errorMessage.setBorder( new Border( new BorderStroke( Paint.valueOf( "red" ), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN ) ) );
        ObservableList<String> observableBucketList = FXCollections.observableList( new LinkedList<>() );
        bucketListView.setItems( observableBucketList );
        ObservableList<ListModel> observableKeyList = FXCollections.observableList( new ArrayList<>() );
        keyListView.setItems( observableKeyList );
        keyListView.setCellFactory( c -> new ListCell<ListModel>(){
            @Override
            protected void updateItem(ListModel item, boolean empty) {
                super.updateItem( item, empty );
                if(empty || item == null || item.getDisplayLabel() == null) {
                    setText( null );
                }
                else {
                    setText( item.getDisplayLabel() );
                }
            }
        } );

        /////////////
        // Actions //
        /////////////
        this.compositeDisposable.add( s3Repository.observeBucketStream()
                .doOnNext( s -> {
                    s.forEach( observableBucketList::add );
                    getBucketsButton.setDisable( false );
                    getKeysButton.setDisable( false );
                } )
                .doOnError( Throwable::printStackTrace )
                .subscribe() );
        this.compositeDisposable.add( s3Repository.observeKeyStream()
                .doOnNext( s -> s.forEach( observableKeyList::add ) )
                .doOnError( Throwable::printStackTrace )
                .doOnComplete( () -> getKeysButton.setDisable( false ) )
                .subscribe() );

        bucketListView.getSelectionModel().selectedItemProperty().addListener(
                ( observableValue, previous, current ) -> {
                    selectedBucket = current;
                    getKeysButton.setDisable( current == null );
                    listMultipartUploadsButton.setDisable( current == null );
                } );

        getBucketsButton.setOnAction( actionEvent -> {
            getBucketsButton.setDisable( true );
            try {
                observableBucketList.clear();
                s3Repository.retrieveBucketStream();
            }
            catch( Throwable throwable ) {
                throwable.printStackTrace();
                getBucketsButton.setDisable( false );
            }
        } );

        getKeysButton.setOnAction( actionEvent -> {
            if(selectedBucket != null) {
                getKeysButton.setDisable( true );
                try {
                    System.out.printf( "triggering retrieval of key list for bucket %s%n", selectedBucket );
                    observableKeyList.clear();
                    s3Repository.retrieveObjectKeys(selectedBucket);
                }
                catch( Throwable throwable ) {
                    throwable.printStackTrace();
                    getKeysButton.setDisable( false );
                }
            }
            else {
                errorMessage.setText( "No bucket has been selected, one bust be selected to continue" );
            }
        } );

        listMultipartUploadsButton.setOnAction( actionEvent -> {
            if(selectedBucket != null) {
                listMultipartUploadsButton.setDisable( true );
                try {
                    observableKeyList.clear();
                    s3Repository.retrieveMultipartUploadList( selectedBucket );
                }
                catch( Throwable throwable ) {
                    throwable.printStackTrace();
                    listMultipartUploadsButton.setDisable( false );
                }
            }
        } );

        ////////////
        // Layout //
        ////////////
        BaseGridPane pane = new BaseGridPane();
        // Row 0
        pane.add( title, 0, 0 );
        // Row 1
        pane.add( errorMessage, 0, 1 );
        // Row 2
        pane.add( bucketListView, 0, 2, 1, 2 );
        pane.add( keyListView, 2, 2, 1, 2 );
        pane.add( getBucketsButton, 1, 2 );
        pane.add( getKeysButton, 1, 3 );
        pane.add( listMultipartUploadsButton, 1, 4 );

        Scene grid = new Scene( pane );
        stage.setScene( grid );
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        if(this.s3Repository != null ) {
            this.s3Repository.disposeAll();
        }
        this.compositeDisposable.dispose();
        super.stop();
    }
}
