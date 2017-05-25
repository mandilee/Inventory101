package uk.co.mandilee.inventory101;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ItemAdapter extends ArrayAdapter<Item> {

    public ItemAdapter(Context context, List<Item> items) {
        super(context, 0, items);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_content, parent, false);

        }

        final Item currentItem = getItem(position);

        TextView productName = (TextView) listItemView.findViewById(R.id.et_product_name);
        productName.setText(currentItem.getName());

        TextView productStock = (TextView) listItemView.findViewById(R.id.et_product_stock);
        if (currentItem.getStock() > 0) {
            productStock.setText(getContext().getString(R.string.in_stock, currentItem.getStock()));
        } else {
            productStock.setText(getContext().getString(R.string.out_of_stock));
        }

        TextView productPrice = (TextView) listItemView.findViewById(R.id.et_product_price);
        productPrice.setText(getContext().getString(R.string.format_price, currentItem.getPrice()));

        ImageView imageView = (ImageView) listItemView.findViewById(R.id.product_image_button);
        if (currentItem.hasImage()) {
            imageView.setImageDrawable(currentItem.getThumbnail(getContext()));
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.INVISIBLE);
        }

        return listItemView;
    }

}

