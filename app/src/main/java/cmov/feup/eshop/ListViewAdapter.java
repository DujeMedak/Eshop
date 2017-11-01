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

    private ArrayList<Order> dataSet;
    final String CURRENCY = "€";
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtNubmer;
        TextView txtName;
        TextView txtPrice;
    }

    public ListViewAdapter(ArrayList<Order> data, Context context) {
        super(context, R.layout.layout_listview_item, data);
        this.dataSet = data;
        this.mContext=context;

    }


    @Override
    public void onClick(View v) {
        /*
        int position=(Integer) v.getTag();
        Object object= getItem(position);
        DataModel dataModel=(DataModel)object;

        switch (v.getId())
        {
            case R.id.item_info:
                Snackbar.make(v, "Release date " +dataModel.getFeature(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
                break;
        }
        */
    }

    private int lastPosition = -1;

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
            viewHolder.txtNubmer = (TextView) convertView.findViewById(R.id.item_number);
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

        viewHolder.txtNubmer.setText(String.valueOf(dataModel.getQuantity()) + " x ");
        viewHolder.txtName.setText(dataModel.getProduct().getName());
        viewHolder.txtPrice.setText(String.format("%.2f", dataModel.getQuantity() * dataModel.getProduct().getPrice()) + CURRENCY);
        //viewHolder.info.setOnClickListener(this);
        //viewHolder.info.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }
}