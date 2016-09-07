package com.blackbeard.landmarktest;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class ProductRecyclerAdapter
    extends RecyclerView.Adapter<ProductRecyclerAdapter.ItemHolder> {

  private Context mContext;
  private List productList;

  public ProductRecyclerAdapter(Context context, List<ProductItem> productItems) {
    super();
    this.mContext = context;
    this.productList = productItems;
  }

  @Override
  public ProductRecyclerAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater =
        (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View convertView = inflater.inflate(R.layout.product_list_item, parent, false);
    return new ItemHolder(convertView);
  }

  @Override public void onBindViewHolder(ProductRecyclerAdapter.ItemHolder holder, int position) {

    ProductItem productItem = (ProductItem) productList.get(holder.getAdapterPosition());

    //Load product image
    Picasso pInstance = Picasso.with(mContext);
    pInstance.load(productItem.imageUrl).error(R.mipmap.ic_launcher).into(holder.image);
    holder.leftBadge.setText(productItem.leftBadgeStr);
    holder.rightBadge.setText(productItem.rightBadgeStr);
    holder.title.setText(productItem.itemName);
    holder.oldPrice.setText(Html.fromHtml(productItem.priceOldStr));
    holder.oldPrice.setPaintFlags(holder.oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    holder.currencyAndPrice.setText(
        Html.fromHtml(productItem.priceCurrStr + " " + productItem.priceStr +" "));
    holder.type.setText(productItem.typrStr);

    holder.leftBadge.setVisibility(
        TextUtils.isEmpty(productItem.leftBadgeStr) ? View.INVISIBLE : View.VISIBLE);
    holder.rightBadge.setVisibility(
        TextUtils.isEmpty(productItem.rightBadgeStr) ? View.INVISIBLE : View.VISIBLE);
  }

  @Override public int getItemCount() {
    return productList.size();
  }

  protected static class ItemHolder extends RecyclerView.ViewHolder {
    public ImageView image;
    public TextView leftBadge;
    public TextView rightBadge;
    public TextView type;
    public TextView title;
    public TextView currencyAndPrice;
    public TextView oldPrice;

    public ItemHolder(View itemView) {
      super(itemView);
      image = (ImageView) itemView.findViewById(R.id.img);
      leftBadge = (TextView) itemView.findViewById(R.id.left_badge);
      rightBadge = (TextView) itemView.findViewById(R.id.right_badge);
      type = (TextView) itemView.findViewById(R.id.type);
      title = (TextView) itemView.findViewById(R.id.title);
      currencyAndPrice = (TextView) itemView.findViewById(R.id.curr_and_price);
      oldPrice = (TextView) itemView.findViewById(R.id.old_price);
    }
  }
}
