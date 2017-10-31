package cmov.feup.eshop;

import android.view.View;

/**
 * Created by Duje on 26.10.2017..
 */

public interface OrderdClickListener {
    void onEditOrderClicked(View v, int position);
    void onRemoveOrderClicked(View v, int position);
}
