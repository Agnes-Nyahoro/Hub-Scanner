/*
 * Copyright (c) 2016. Ted Park. All Rights Reserved
 */

package com.gun0912.tedpicker;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;

import androidx.fragment.app.Fragment;
import androidx.core.content.res.ResourcesCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.gun0912.tedpicker.view.CustomSquareFrameLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Gil on 04/03/2014.
 */
public class MyGalleryFragment extends Fragment {

    public static ImageGalleryAdapter mGalleryAdapter;
    public static ImagePickerActivity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.picker_fragment_gallery, container, false);
        GridView galleryGridView = (GridView) rootView.findViewById(R.id.gallery_grid);
        mActivity = ((ImagePickerActivity) getActivity());


        List<Uri> images = getImagesFromGallary(getActivity());
        mGalleryAdapter = new ImageGalleryAdapter(getActivity(), images);

        galleryGridView.setAdapter(mGalleryAdapter);
        galleryGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                Uri mUri = mGalleryAdapter.getItem(i);


                if (!mActivity.containsImage(mUri)) {
                    mActivity.addImage(mUri);
                } else {
                    mActivity.removeImage(mUri);

                }

                mGalleryAdapter.notifyDataSetChanged();
            }
        });

        return rootView;
    }


    public void refreshGallery(Context context) {

        List<Uri> images = getImagesFromGallary(context);

        if (mGalleryAdapter == null) {

            mGalleryAdapter = new ImageGalleryAdapter(context, images);
        } else {

            mGalleryAdapter.clear();
            mGalleryAdapter.addAll(images);
            mGalleryAdapter.notifyDataSetChanged();
        }
    }

    public List<Uri> getImagesFromGallary(Context context) {
        SharedPreferences mSharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String folderName = mSharedPref.getString("storage_folder", "CamScanner");
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString()
                + "/" + folderName);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        List<Uri> images = new ArrayList<Uri>();

        File[] listFiles = folder.listFiles();
        for (File aFile : listFiles) {
            if (aFile.getName().toLowerCase().endsWith("jpg") || aFile.getName().toLowerCase().endsWith("jpeg")) {
                Uri uri = Uri.parse(aFile.getPath());
                images.add(uri);
            }
        }

        return images;
    }

    class ViewHolder {
        CustomSquareFrameLayout root;

        ImageView mThumbnail;

        // This is like storing too much data in memory.
        // find a better way to handle this
        Uri uri;

        public ViewHolder(View view) {
            root = (CustomSquareFrameLayout) view.findViewById(R.id.root);
            mThumbnail = (ImageView) view.findViewById(R.id.thumbnail_image);
        }

    }

    public class ImageGalleryAdapter extends ArrayAdapter<Uri> {

        Context context;


        public ImageGalleryAdapter(Context context, List<Uri> images) {
            super(context, 0, images);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.picker_grid_item_gallery_thumbnail, null);
                holder = new ViewHolder(convertView);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Uri mUri = getItem(position);
            boolean isSelected = mActivity.containsImage(mUri);

            if (holder.root instanceof FrameLayout) {
                ((FrameLayout) holder.root).setForeground(isSelected ? ResourcesCompat.getDrawable(getResources(), R.drawable.gallery_photo_selected, null) : null);
            }

            if (holder.uri == null || !holder.uri.equals(mUri)) {
                Glide.with(context)
                        .load(mUri.toString())
                        .thumbnail(0.1f)
                        //.fit()
                        .dontAnimate()
                        //   .override(holder.mThumbnail.getWidth(), holder.mThumbnail.getWidth())
                        //  .override(holder.root.getWidth(), holder.root.getWidth())
                        .centerCrop()
                        .placeholder(R.drawable.place_holder_gallery)
                        .error(R.drawable.no_image)

                        .into(holder.mThumbnail);
                holder.uri = mUri;
            }
            return convertView;
        }
    }
}