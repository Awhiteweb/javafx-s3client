package com.whiteslife.javafx.extensions;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;

public class BaseGridPane extends GridPane {

    /**
     * Adds given element at the column and row positions
     * @param node Element to display in the grid
     * @param column column number for position
     * @param row row number for position
     */
    public void add( Node node, int column, int row ) {
        super.add( node, column, row );
    }

    /**
     * Adds given element at the column and row positions with spanning capabilities
     * @param node Element to display in the grid
     * @param column column number for position
     * @param row row number for position
     * @param colspan how many columns to span
     * @param rowspan how many rows to span
     */
    public void add( Node node, int column, int row, int colspan, int rowspan ) {
        super.add( node, column, row, colspan, rowspan );
    }
}
