package cmov.feup.eshop;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import cmov.feup.eshop.model.FinishedOrder;
import cmov.feup.eshop.model.Order;

/**
 * Created by Duje on 30.10.2017..
 */

public class PreviousOrdersListViewAdapter extends ArrayAdapter<FinishedOrder>{

    private ArrayList<FinishedOrder> dataSet;
    final String CURRENCY = "€";
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView orderID;
        TextView orderDate;
        TextView orderTotalPrice;
    }

    public PreviousOrdersListViewAdapter(ArrayList<FinishedOrder> data, Context context) {
        super(context, R.layout.layout_listview_item2, data);
        this.dataSet = data;
        this.mContext=context;

    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        FinishedOrder dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.layout_listview_item2, parent, false);
            viewHolder.orderID = (TextView) convertView.findViewById(R.id.item_number);
            viewHolder.orderDate = (TextView) convertView.findViewById(R.id.item_nameTxt);
            viewHolder.orderTotalPrice = (TextView) convertView.findViewById(R.id.priceTxt);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.orderID.setText(String.valueOf(dataModel.getId()));
        viewHolder.orderDate.setText(dataModel.getDateFormatted());
        viewHolder.orderTotalPrice.setText(dataModel.getTotalPrice() + CURRENCY);
        return convertView;
    }
}