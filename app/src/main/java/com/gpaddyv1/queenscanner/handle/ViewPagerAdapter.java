package com.joshuabutton.queenscanner.handle;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.joshuabutton.queenscanner.document.DocumentModel;
import com.todobom.queenscanner.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Phí Văn Tuấn on 1/12/2018.
 */

public class ViewPagerAdapter extends PagerAdapter {
    private List<DocumentModel> lst;
    private Context context;
    private boolean isTab=false;
    private HandleContract.IHandlePresenter presenter;
    public ViewPagerAdapter(Context context,HandleContract.IHandlePresenter presenter) {
        this.context = context;
        this.presenter=presenter;
        lst = new ArrayList<>();
    }


    public void loadData(List<DocumentModel> lst) {
        if (lst != null && lst.size() > 0) {
            this.lst.clear();
            this.lst.addAll(lst);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public List<DocumentModel> getListDoc() {
        return lst;
    }

    public void rotate(){

    }
    @Override
    public int getCount() {
        return lst.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_simple_document_scan_result, null);
        PhotoView photoView = (PhotoView) view.findViewById(R.id.imageView);
        DocumentModel document=lst.get(position);
        Glide.with(context).load(new File(document.getPath())).diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).into(photoView);

        photoView.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(ImageView view, float x, float y) {
                if (isTab) {
                    presenter.createAnimationTop();
                    isTab = false;
                } else {
                    isTab = true;
                    presenter.createAnimationXia();
                }
            }
        });
        view.setTag(position);
        container.addView(view);
        return view;
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return object == view;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }


}
