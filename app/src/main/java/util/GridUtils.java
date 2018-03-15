package util;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.flexicious.controls.core.Function;
import com.flexicious.nestedtreedatagrid.FlexDataGrid;
import com.flexicious.nestedtreedatagrid.FlexDataGridColumn;
import com.flexicious.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SoftSuave-PC on 1/9/2018.
 */

public class GridUtils {

    public static void setStyle(FlexDataGrid grid) {
        grid.verticalGridLines = grid.horizontalGridLines = true;
        grid.verticalGridLineThickness = grid.horizontalGridLineThickness = 2;
        grid.horizontalGridLineColor = Color.parseColor("#cccccc");
        grid.verticalGridLineColor = Color.parseColor("#cccccc");
    }

    public static void updateSpinnerContent(Context context, Spinner comboBox, List<?> list) {
        ArrayAdapter<?> dataAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        comboBox.setAdapter(dataAdapter);
    }

    public static List<?> getDistinctValues(FlexDataGrid grid, String dataField) {
        List<String> list = new ArrayList<>();
        FlexDataGridColumn col = grid.getColumnByDataField(dataField);
        if(null == col) return list;
        List<?> items = col.getDistinctValues(grid.getDataProvider(), null, null, grid.getColumnLevel());
        list.add(col.getUniqueIdentifier());
        for(Object o : items) {
            list.add(String.valueOf(UIUtils.resolveExpression(o, "label")));
        }
        return list;
    }

    public static boolean contains(Object item, String dataField, Object searchItem) {
        String label = UIUtils.toString(UIUtils.resolveExpression(item, dataField));
        return label.indexOf(UIUtils.toString(searchItem)) != -1;
    }
}
