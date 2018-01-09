package com.poc.demoapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.flexicious.controls.core.Function;
import com.flexicious.nestedtreedatagrid.FlexDataGrid;

import java.util.HashMap;

import util.ResUtils;
import util.GridUtils;

public class MainActivity extends AppCompatActivity {
    public FlexDataGrid grid;
    EditText searchBox;
    Spinner depositCBox, departmentCbox, areaCBox, familyCBox;
    RadioGroup promoGroup;
    RadioButton promo, noPromo;
    Button btnFilter, btnConfirm;
    EditText[] editTexts = new EditText[3];

    boolean enableEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        depositCBox = findViewById(R.id.despositDropDownMenu);
        departmentCbox = findViewById(R.id.departmentDropDownMenu);
        areaCBox = findViewById(R.id.areaDropDownMenu);
        familyCBox = findViewById(R.id.familyDropDownMenu);

        promoGroup = findViewById(R.id.promoGrp);
        promo = (RadioButton) promoGroup.getChildAt(0);
        noPromo = (RadioButton) promoGroup.getChildAt(1);
        searchBox = findViewById(R.id.searchBox);

        btnFilter = findViewById(R.id.btnApply);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                grid.rebuildBody(true);
            }
        });

        btnConfirm = findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateEditableColumns();
                grid.rebuildBody(true);
            }
        });

        editTexts[0] = findViewById(R.id.txtField1);
        editTexts[1] = findViewById(R.id.txtField2);
        editTexts[2] = findViewById(R.id.txtField3);

        grid = (FlexDataGrid) findViewById(R.id.dataGrid);
        grid.delegate = this;
        grid.buildFromXml(ResUtils.getStringFromResource(this, R.raw.grid_article));
        grid.getColumnLevel().setFilterFunction(new Function(this, "externalFilterFunction"));
        GridUtils.setStyle(grid);
        grid.setDataProviderJson(ResUtils.getStringFromResource(this, R.raw.article_data));
        updateAllComboBoxItems();
    }

    public void populateEditableItems() {
        for(int i=0;i<editTexts.length;i++) {
            editTexts[i].setText(grid.getColumnByDataField("edit" + (i + 1)).itemToLabelCommon(grid.getSelectedItem()));
        }
        enableEditMode = true;
    }

    public void updateEditableColumns() {
        if( enableEditMode && grid.getSelectedItem() instanceof HashMap ) {
            enableEditMode = !enableEditMode;
            HashMap<String, Object> item = (HashMap<String, Object>) grid.getSelectedItem();

            for(int i=0;i<editTexts.length;i++) {
                item.put("edit" + (i + 1), editTexts[i].getText());
                editTexts[i].setText(null);
            }
        }
    }

    private void updateAllComboBoxItems() {
        GridUtils.updateSpinnerContent(this, depositCBox, GridUtils.getDistinctValues(grid, "deposit"));
        GridUtils.updateSpinnerContent(this, departmentCbox, GridUtils.getDistinctValues(grid, "dept"));
        GridUtils.updateSpinnerContent(this, areaCBox, GridUtils.getDistinctValues(grid, "area"));
        GridUtils.updateSpinnerContent(this, familyCBox, GridUtils.getDistinctValues(grid, "family"));
    }

    public Boolean externalFilterFunction(Object item) {
        if(depositCBox.getSelectedItemPosition() > 0 && !GridUtils.contains(item, "deposit", depositCBox.getSelectedItem()))
            return false;
        if(departmentCbox.getSelectedItemPosition() > 0 && !GridUtils.contains(item, "dept", departmentCbox.getSelectedItem()))
            return false;
        if(areaCBox.getSelectedItemPosition() > 0 && !GridUtils.contains(item, "area", areaCBox.getSelectedItem()))
            return false;
        if(familyCBox.getSelectedItemPosition() > 0 && !GridUtils.contains(item, "family", familyCBox.getSelectedItem()))
            return false;
        if(searchBox.getText().length() > 0 && !GridUtils.contains(item, "article", searchBox.getText()))
            return false;
        if( null != promo && promo.isChecked() && !GridUtils.contains(item, "promo", "yes") ||
            null != noPromo && noPromo.isChecked() && !GridUtils.contains(item, "promo", "no"))
            return false;
        return true;
    }
}
