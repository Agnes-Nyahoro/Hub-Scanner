package com.gpaddy.hungdh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.joshuabutton.queenscanner.document.DocumentContract;
import com.joshuabutton.queenscanner.document.DocumentModel;
import com.todobom.queenscanner.R;

import org.askerov.dynamicgrid.BaseDynamicGridAdapter;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DynamicAdapter extends BaseDynamicGridAdapter {
    private Context context;
    private DocumentContract.IDocumentPresenter presenter;
    private OnLongClickItem onLongClickItem;
    private OnChangeDynamic onChangeDynamic;

    public DynamicAdapter(Context context, List<?> items, int columnCount) {
        super(context, items, columnCount);
        this.context = context;
    }

    public void setOnChangeDynamic(OnChangeDynamic onChangeDynamic) {
        this.onChangeDynamic = onChangeDynamic;
    }

    public void setOnLongClickItem(OnLongClickItem onLongClickItem) {
        this.onLongClickItem = onLongClickItem;
    }

    public void setPresenter(DocumentContract.IDocumentPresenter presenter) {
        this.presenter = presenter;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_document, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.build(position);

        return convertView;
    }

    public class ViewHolder {
        @BindView(R.id.imgThumb)
        ImageView imgThumb;
        @BindView(R.id.tvPage)
        TextView tvPage;

        private ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        void build(int position) {
            DocumentModel model = (DocumentModel) getItem(position);
            int page = position + 1;
            if (page < 10) {
                tvPage.setText("0" + page);
            } else {
                tvPage.setText(page + "");
            }
            Glide.with(context).load(new File(model.getPath())).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).into(imgThumb);
            imgThumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.onItemClick(position);
                }
            });
            imgThumb.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onLongClickItem.longClickItem(position);
                    return true;
                }
            });
            onChangeDynamic.onChangeItem();
        }
    }
}
