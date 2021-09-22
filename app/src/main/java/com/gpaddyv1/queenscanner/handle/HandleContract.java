package com.joshuabutton.queenscanner.handle;

import android.graphics.Bitmap;

import com.github.chrisbanes.photoview.PhotoView;

/**
 * Created by Phí Văn Tuấn on 30/11/2018.
 */

public interface HandleContract {
    interface IHandleView{
        void sign();

        void updateView();

        void createAnimationTop();

        void createAnimationXia();

        String getFolderPath();
    }
    interface IHandlePresenter{
        void sign();

        Bitmap getBitMapRotate(PhotoView photoView);

        void merge();

        void createAnimationTop();

        void createAnimationXia();

        void share(String imagePath);

        void delete(String imagePath);



    }
}
