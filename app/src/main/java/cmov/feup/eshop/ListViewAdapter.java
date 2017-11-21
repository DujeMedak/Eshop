package cmov.feup.eshop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import cmov.feup.eshop.model.Order;

/**
 * Created by Duje on 30.10.2017..
 */

public class ListViewAdapter extends ArrayAdapter<Order> implements View.OnClickListener{

    final String CURRENCY = "€";
    Context mContext;
    private ArrayList<Order> dataSet;
    private int lastPosition = -1;

    public ListViewAdapter(ArrayList<Order> data, Context context) {
        super(context, R.layout.layout_listview_item, data);
        this.dataSet = data;
        this.mContext=context;

    }


    @Override
    public void onClick(View v) {
        //TODO open new activity with details
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Order dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.layout_listview_item, parent, false);
            viewHolder.txtNumber = (TextView) convertView.findViewById(R.id.item_number);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.item_nameTxt);
            viewHolder.txtPrice = (TextView) convertView.findViewById(R.id.priceTxt);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtNumber.setText(String.valueOf(dataModel.getQuantity()) + " x ");
        viewHolder.txtName.setText(dataModel.getProduct().getName());
        viewHolder.txtPrice.setText(String.format("%.2f", dataModel.getQuantity() * dataModel.getProduct().getPrice()) + CURRENCY);

        return convertView;
    }

    // View lookup cache
    private static class ViewHolder {
        TextView txtNumber;
        TextView txtName;
        TextView txtPrice;
    }
}