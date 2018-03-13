package renderers;

import android.content.Context;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import com.flexicious.nestedtreedatagrid.FlexDataGrid;
import com.flexicious.utils.UIUtils;
import com.poc.demoapp.MainActivity;
import com.poc.demoapp.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.ResUtils;

/**
 * Created by SoftSuave-PC on 1/9/2018.
 */

public class IconRenderer extends GridLayout {
    private MainActivity ctx;

    private Object data;

    private PopupMenu popup;
    private ImageButton btnEdit, btnInfo;

    public IconRenderer(final Context context) {
        super(context);

        if(context instanceof MainActivity) {
            ctx = ((MainActivity) context);
        }

        setRowCount(1);
        setColumnCount(2);
        btnEdit = new ImageButton(context);
        btnEdit.setBackground(ResUtils.getDrawable(this.getContext(), R.drawable.edit));
        btnEdit.setMaxWidth(44);
        btnEdit.setMaxHeight(32);
        addView(btnEdit);
        btnEdit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                makeItemSelected();
                ctx.populateEditableItems();
            }
        });
        btnInfo = new ImageButton(context);
        btnInfo.setBackground(ResUtils.getDrawable(this.getContext(), R.drawable.info));
        btnInfo.setMaxWidth(34);
        btnInfo.setMaxHeight(32);
        addView(btnInfo);
        btnInfo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(makeItemSelected()) {
                    updatePopupMenu();
                }
                if( null != popup)
                    popup.show();
            }
        });
    }

    public Object getData() {
        return this.data;
    }

    public void setData(Object data) {
        this.data = data;
        updatePopupMenu();
    }

    private void updatePopupMenu() {
        if(null != data && data instanceof HashMap) {
            if(null != ctx.grid && ctx.grid.getColumnLevel().isItemSelected(data, false)) {
                popup = new PopupMenu(this.getContext(), btnInfo);
                HashMap<String, Object> map = (HashMap<String, Object>) data;
                for (Map.Entry entry : map.entrySet()) {
                    popup.getMenu().add(UIUtils.toString(entry.getKey()) + " :: " + UIUtils.toString(entry.getValue()));
                }
            } else {
                popup = null;
            }
        } else {
            popup = null;
        }
    }

    private boolean makeItemSelected() {
        if (null != ctx.grid && data != ctx.grid.getSelectedItem()) {
            ctx.grid.setSelectedObjects(Arrays.asList(new Object[]{data}), true);
            return true;
        }
        return false;
    }
}
