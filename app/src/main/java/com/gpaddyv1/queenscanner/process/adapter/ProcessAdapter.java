package com.joshuabutton.queenscanner.process.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.joshuabutton.queenscanner.process.model.FilterModel;
import com.joshuabutton.queenscanner.process.presenter.IProcessPresenter;
import com.todobom.queenscanner.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Phí Văn Tuấn on 24/11/2018.
 */

public class ProcessAdapter extends RecyclerView.Adapter<ProcessAdapter.ViewHolder> {
    private Context context;
    private List<FilterModel> lstFilter;
    private List<Integer> lstPosition;
    private IProcessPresenter processPresenter;
    private static int enable = 1;
    private static int disable = 0;
    private int oldPosition = 0;

    public ProcessAdapter(Context context, IProcessPresenter processPresenter) {
        this.context = context;
        this.processPresenter = processPresenter;
        lstFilter = new ArrayList<>();
        lstPosition = new ArrayList<>();
    }

    public void loadData(List<FilterModel> lstFilter) {
        if (lstFilter != null && lstFilter.size() > 0) {
            this.lstFilter.clear();
            this.lstPosition.clear();
            this.lstFilter.addAll(lstFilter);
            for (int i = 0; i < lstFilter.size(); i++) {
                if (i == 0) {
                    lstPosition.add(enable);
                } else {
                    lstPosition.add(disable);
                }

            }
            notifyDataSetChanged();
        }
    }

    public List<FilterModel> getLstFilter() {
        return lstFilter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_color, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final FilterModel baseAdjuster = lstFilter.get(position);
        holder.tvTitle.setText(baseAdjuster.getTitle());
        if (lstPosition.get(position) == enable) {
            holder.tvTitle.setTextColor(context.getResources().getColor(R.color.colorAccent));
        } else {
            holder.tvTitle.setTextColor(context.getResources().getColor(R.color.white));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processPresenter.onItemClick(baseAdjuster);
                lstPosition.set(oldPosition, disable);

                lstPosition.set(position, enable);
                oldPosition = position;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return lstFilter.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvTitle)
        TextView tvTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
