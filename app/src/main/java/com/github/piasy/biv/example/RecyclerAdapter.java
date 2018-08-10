package com.github.piasy.biv.example;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.piasy.biv.indicator.progresspie.ProgressPieIndicator;
import com.github.piasy.biv.view.BigImageView;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

  private List<String> imageUrls;

  public RecyclerAdapter(List<String> imageUrls) {
    this.imageUrls = imageUrls;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_view, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    holder.bind(imageUrls.get(position));
  }

  @Override
  public int getItemCount() {
    return imageUrls.size();
  }

  @Override
  public void onViewRecycled(ViewHolder holder) {
    super.onViewRecycled(holder);
    holder.clear();
  }

  @Override
  public void onViewAttachedToWindow(ViewHolder holder) {
    super.onViewAttachedToWindow(holder);
    if (holder.hasNoImage()) {
      holder.rebind();
    }
  }

  class ViewHolder extends RecyclerView.ViewHolder {

    private BigImageView itemImage;
    private String imageUrl;

    ViewHolder(View itemView) {
      super(itemView);
      itemImage = itemView.findViewById(R.id.itemImage);
      itemImage.setProgressIndicator(new ProgressPieIndicator());
      itemImage.setTapToRetry(true);

    }

    void bind(String imageUrl) {
      this.imageUrl = imageUrl;
      itemImage.showImage(Uri.parse(imageUrl));
    }

    void rebind() {
      itemImage.showImage(Uri.parse(imageUrl));
    }

    void clear() {
      itemImage.getSSIV().recycle();
    }

    boolean hasNoImage() {
      return !itemImage.getSSIV().hasImage();
    }
  }
}
