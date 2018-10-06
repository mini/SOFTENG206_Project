package assignment4.ui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;

/**
 * Hides the header of provided tables
 */
public class HideHeader implements ChangeListener<Number> {
	private TableView<? extends Object> table;

	public HideHeader(TableView<? extends Object> table) {
		this.table = table;
	}

	@Override
	public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
		Pane header = (Pane) table.lookup("TableHeaderRow");
		if (header != null && header.isVisible()) {
			header.setMaxHeight(0);
			header.setMinHeight(0);
			header.setPrefHeight(0);
			header.setVisible(false);
			header.setManaged(false);
		}
	}
}
