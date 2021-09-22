package com.gpaddy.hungdh.listdoc;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.joshuabutton.queenscanner.document.DocumentModel;
import com.todobom.queenscanner.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Phí Văn Tuấn on 5/12/2018.
 */

public class DocsAdapter extends RecyclerView.Adapter<DocsAdapter.ViewHolder> {
    private Context context;
    private DocsContract.IDocsPresenter presenter;
    private List<DocumentModel> lstDocs;

    public DocsAdapter(Context context, DocsContract.IDocsPresenter presenter) {
        this.context = context;
        this.presenter = presenter;
        lstDocs = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_folder, parent, false));
    }

    public void loadData(List<DocumentModel> lstDocs) {
        if (lstDocs != null) {
            this.lstDocs.clear();
            this.lstDocs.addAll(lstDocs);
            notifyDataSetChanged();
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final File file = new File(lstDocs.get(position).getPath());

        holder.tvName.setText(file.getName());
        holder.tvDate.setText(presenter.bindLastModify(file.lastModified()));
        Glide.with(context).load(new File(presenter.getImagePath(file))).diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).into(holder.imgThumb);
        holder.tvPage.setText(presenter.getNumberOfImage(file) + "");
        holder.itemView.setLongClickable(true);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onItemClick(file);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                presenter.onItemLongClick(file);
                return true;
            }
        });
    }


    @Override
    public int getItemCount() {
        return lstDocs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imgThumb)
        ImageView imgThumb;
        @BindView(R.id.tvPage)
        TextView tvPage;
        @BindView(R.id.tvDate)
        TextView tvDate;
        @BindView(R.id.tvName)
        TextView tvName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
