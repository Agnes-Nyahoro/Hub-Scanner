package com.joshuabutton.queenscanner.document;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.todobom.queenscanner.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Phí Văn Tuấn on 30/11/2018.
 */

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.ViewHolder> {
    private Context context;
    private List<DocumentModel> lstDocument;
    private DocumentContract.IDocumentPresenter presenter;

    public DocumentAdapter(Context context, DocumentContract.IDocumentPresenter presenter) {
        this.context = context;
        this.presenter = presenter;
        lstDocument = new ArrayList<>();
    }

    public void loadData(List<DocumentModel> lstDocument) {
        if (lstDocument != null && lstDocument.size() > 0) {
            this.lstDocument.clear();
            this.lstDocument.addAll(lstDocument);
            notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_document, parent, false));
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final DocumentModel model = lstDocument.get(position);
        int page = position + 1;
        if (page < 10) {
            holder.tvPage.setText("0" + page);
        } else {
            holder.tvPage.setText(page + "");
        }

        Glide.with(context).load(new File(model.getPath())).diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).into(holder.imgThumb);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onItemClick(position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return lstDocument.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imgThumb)
        ImageView imgThumb;
        @BindView(R.id.tvPage)
        TextView tvPage;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
