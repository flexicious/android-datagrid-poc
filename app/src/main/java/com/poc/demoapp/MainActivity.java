package com.poc.demoapp;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.flexicious.controls.core.Function;
import com.flexicious.nestedtreedatagrid.FlexDataGrid;
import com.flexicious.nestedtreedatagrid.FlexDataGridColumn;
import com.flexicious.nestedtreedatagrid.FlexDataGridColumnGroup;
import com.flexicious.nestedtreedatagrid.interfaces.IFlexDataGridCell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.ResUtils;
import util.GridUtils;

public class MainActivity extends AppCompatActivity {
    public FlexDataGrid grid;
    EditText searchBox;
    Spinner depositCBox, departmentCbox, areaCBox, familyCBox;
    RadioGroup promoGroup;
    RadioButton promo, noPromo;
    Button btnFilter, btnConfirm, btnClear;
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

        btnClear = findViewById(R.id.btnClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetFilters(true);
            }
        });

        editTexts[0] = findViewById(R.id.txtField1);
        editTexts[1] = findViewById(R.id.txtField2);
        editTexts[2] = findViewById(R.id.txtField3);

        resetFilters(false);

        grid = (FlexDataGrid) findViewById(R.id.dataGrid);
        grid.delegate = this;
        grid.buildFromXml(ResUtils.getStringFromResource(this, R.raw.grid_article));
        grid.getColumnLevel().setFilterFunction(new Function(this, "externalFilterFunction"));
        AddIconMerceTypeOrderColumn(new FlexDataGridColumn(), new ColumnInfo("Art", true), "icon", false, "");
        AddNumberColumn(grid, new FlexDataGridColumn(), new ColumnInfo("ID", true), "id", true, "fixed", null);
        GridUtils.setStyle(grid);
        grid.setPageSize(5);
        grid.setFilterRowHeight(120);
        grid.setDataProviderJson(ResUtils.getStringFromResource(this, R.raw.article_data));
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                MainActivity.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        grid.watchForDimensionChanges = false;
//                    }
//                });
//            }
//        }, 3000);
        grid.validateNow();
        grid.reDraw();
        updateAllComboBoxItems();
    }

    public void populateEditableItems() {
        for (int i = 0; i < editTexts.length; i++) {
            editTexts[i].setText(grid.getColumnByDataField("edit" + (i + 1)).itemToLabelCommon(grid.getSelectedItem()));
        }
        enableEditMode = true;
    }

    public void updateEditableColumns() {
        if (enableEditMode && grid.getSelectedItem() instanceof HashMap) {
            enableEditMode = !enableEditMode;
            HashMap<String, Object> item = (HashMap<String, Object>) grid.getSelectedItem();

            for (int i = 0; i < editTexts.length; i++) {
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
        if (depositCBox.getSelectedItemPosition() > 0 && !GridUtils.contains(item, "deposit", depositCBox.getSelectedItem()))
            return false;
        if (departmentCbox.getSelectedItemPosition() > 0 && !GridUtils.contains(item, "dept", departmentCbox.getSelectedItem()))
            return false;
        if (areaCBox.getSelectedItemPosition() > 0 && !GridUtils.contains(item, "area", areaCBox.getSelectedItem()))
            return false;
        if (familyCBox.getSelectedItemPosition() > 0 && !GridUtils.contains(item, "family", familyCBox.getSelectedItem()))
            return false;
        if (searchBox.getText().length() > 0 && !GridUtils.contains(item, "article", searchBox.getText()))
            return false;
        if (null != promo && promo.isChecked() && !GridUtils.contains(item, "promo", "yes") ||
                null != noPromo && noPromo.isChecked() && !GridUtils.contains(item, "promo", "no"))
            return false;
        return true;
    }

    public void resetFilters(boolean rebuildBody) {
        depositCBox.setSelection(0);
        departmentCbox.setSelection(0);
        areaCBox.setSelection(0);
        familyCBox.setSelection(0);
        searchBox.setText(null);
        promoGroup.clearCheck();
        if(rebuildBody) grid.rebuildBody(true);
    }

    public void AddNumberColumn(FlexDataGrid grid,
                                FlexDataGridColumn dgCol,
                                ColumnInfo ci,
                                String valueField,
                                boolean canEdit,
                                String fitAs,
                                Function sortCompareFunction) {
        if (ci == null) {
            Log.d("Utils", "null item :" + valueField);
            return;
        }

        dgCol.textAlign = "right";
        dgCol.sortNumeric = true;
        dgCol.setDataField(valueField);
        dgCol.setHeaderText(ci.getGridColumnName());
        dgCol.setVisible(ci.isGridVisible());
        dgCol.setEditable(canEdit);
        dgCol.setColumnWidthMode(fitAs);
        dgCol.setSortCompareFunction(sortCompareFunction);
        dgCol.setWidth(100);

        List<FlexDataGridColumn> dgCols = new ArrayList<>();

        for (FlexDataGridColumn col : grid.getColumns()) {
            dgCols.add(col);
        }

        dgCols.add(dgCol);
        grid.setColumns(dgCols);
    }

    public static class ColumnInfo {
        String name;
        boolean visible;

        public ColumnInfo(String name, boolean visible) {
            this.name = name;
            this.visible = visible;
        }

        public String getGridColumnName() {
            return name;
        }

        public boolean isGridVisible() {
            return visible;
        }
    }

    public int getSortedNumbers(Object o1, Object o2) {
        if (null == o1 && null == o2)
            return 0;
        if (null == o1)
            return -1;
        if (null == o2)
            return 1;

        Map<String, Object> mO1 = (Map<String, Object>) o1;
        Map<String, Object> mO2 = (Map<String, Object>) o2;

        int d = Integer.parseInt(String.valueOf(mO1.get("id"))) - Integer.parseInt(String.valueOf(mO2.get("id")));
        return d != 0 ? d < 0 ? -1 : 1 : 0;

    }

    public void AddIconMerceTypeOrderColumn(
            FlexDataGridColumn dgCol,
            ColumnInfo ci,
            String valueField,
            boolean canEdit,
            String fitAs)
    {

        dgCol.hideText=true;
        dgCol.setDataField(valueField);
        dgCol.setHeaderText(ci.getGridColumnName());
        dgCol.setVisible(ci.isGridVisible());
        dgCol.setEditable(canEdit);
        dgCol.setColumnWidthMode(FlexDataGridColumn.COLUMN_WIDTH_MODE_FIXED);
        dgCol.setWidth(64);
//        dgCol.iconRight=12;
//        dgCol.iconLeft=12;
        dgCol.setEnableIcon(true);

        dgCol.iconFunction= new Function(this,"IconFunctions_dynamicIconMerceFunction");

        List<FlexDataGridColumn> dgCols = new ArrayList<>();

        for (FlexDataGridColumn col : grid.getColumns()) {
            dgCols.add(col);
        }

        dgCols.add(dgCol);
        grid.setColumns(dgCols);
    }

    public Object IconFunctions_dynamicIconMerceFunction(IFlexDataGridCell cell, String state){

        if(cell.getRowInfo().getIsHeaderRow()) return null;

        String label = cell.getColumn().itemToLabelCommon(cell.getRowInfo().getData());

        switch(label)
        {
            case "+":
                return getImageResourceItem();
            case "%":
                return getImageResourceTwoWeeks();
            case "#":
                return getImageResourceThreeWeeks();

        }
        return getImageResourceEmpty();
    }

    public Integer getImageResourceNew(){
        return R.drawable.newart_32;
    }
    public Integer getImageResourceTwoWeeks(){
        return R.drawable.twoweek_32;
    }

    public Integer getImageResourceThreeWeeks(){
        return R.drawable.threeweek_32;
    }

    public Integer getImageResourceItem(){
        return R.drawable.cart_32;
    }

    public Integer getImageResourceEmpty(){
        return R.drawable.empty_32;
    }
}
