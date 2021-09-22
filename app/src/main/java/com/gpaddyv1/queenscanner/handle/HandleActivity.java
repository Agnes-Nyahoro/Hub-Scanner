package com.joshuabutton.queenscanner.handle;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.github.chrisbanes.photoview.PhotoView;
import com.gpaddy.hungdh.base.BaseActivity;
import com.gpaddy.hungdh.util.Const;
import com.joshuabutton.queenscanner.document.DocumentModel;
import com.joshuabutton.queenscanner.process.view.CombineBitMapActivity;
import com.joshuabutton.queenscanner.sign.view.SignActivity;
import com.todobom.queenscanner.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.joshuabutton.queenscanner.process.view.CombineBitMapActivity.REQUEST_MERGE;
import static com.joshuabutton.queenscanner.sign.view.SignActivity.REQUEST_SIGN;

public class HandleActivity extends BaseActivity implements HandleContract.IHandleView {
    @BindView(R.id.viewPager)
    HackyViewPager viewPager;
    @BindView(R.id.layout_bot)
    LinearLayout layoutBot;
    @BindView(R.id.layout_top)
    RelativeLayout layoutTop;
    private ViewPagerAdapter adapter;
    private HandleContract.IHandlePresenter presenter;
    private String folder;
    private Bitmap bitmap;
    private String imagePath;
    private List<DocumentModel> lstDocument;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_handle;
    }

    @Override
    protected void initData() {
        folder = getIntent().getStringExtra("folder");
        adapter.loadData(new DocumentModel().getLstPathImage(folder));
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        getSupportActionBar().hide();
        presenter = new HandlePresenter(this, this);
        adapter = new ViewPagerAdapter(HandleActivity.this, presenter);
        viewPager.setAdapter(adapter);
    }

    @OnClick(R.id.imgBack)
    public void onBack() {
        onBackPressed();
    }

    @Override
    public void sign() {
        SignActivity.startSign(HandleActivity.this);
    }

    @OnClick(R.id.imgRotate)
    public void onRotate() {
        View view = viewPager.findViewWithTag(viewPager.getCurrentItem());
        PhotoView photoView = (PhotoView) view.findViewById(R.id.imageView);
        bitmap = presenter.getBitMapRotate(photoView);
        if (bitmap != null) {
            photoView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void updateView() {
        adapter = new ViewPagerAdapter(HandleActivity.this, presenter);
        viewPager.setAdapter(adapter);
        adapter.loadData(new DocumentModel().getLstPathImage(folder));
    }

    @OnClick(R.id.imgSign)
    public void onSign() {
        presenter.sign();
        imagePath = adapter.getListDoc().get(viewPager.getCurrentItem()).getPath();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //sau khi sign xong se combine 2 bitmap
        if (requestCode == REQUEST_SIGN && resultCode == RESULT_OK) {
            CombineBitMapActivity.onStartCombine(HandleActivity.this, imagePath);
        }
        //sau khi updateView 2 bitmap source va bitmap sign update lai image
        else if (requestCode == REQUEST_MERGE && resultCode == RESULT_OK) {
            presenter.merge();
        }
    }

    /**
     *
     * @param context
     * @param position vi tri item click
     * @param parentFolder parent path cua image click
     */
    public static void startHandle(Context context, int position, String parentFolder) {
        Intent intent = new Intent(context, HandleActivity.class);
        intent.putExtra("position", position);
        intent.putExtra("folder", parentFolder);
        context.startActivity(intent);
    }

    @Override
    public void createAnimationXia() {

        Animation translateAnimation = new TranslateAnimation(0, 0.0f, 0, 0.0f, 0, 0.0f, 0, (float) Const.dip2px(this, 56.0f));
        translateAnimation.setDuration(500);
        translateAnimation.setFillAfter(true);
        layoutBot.startAnimation(translateAnimation);


        Animation translateAnimation2 = new TranslateAnimation(0, 0.0f, 0, 0.0f, 0, 0.0f, 0, (float) (-Const.dip2px(this, 56.0f)));
        translateAnimation2.setDuration(500);
        translateAnimation2.setFillAfter(true);
        layoutTop.startAnimation(translateAnimation2);

    }

    @Override
    public String getFolderPath() {
        return this.folder;
    }

    @Override
    public void createAnimationTop() {

        Animation translateAnimation = new TranslateAnimation(0, 0.0f, 0, 0.0f, 0, (float) Const.dip2px(this, 56.0f), 0, 0.0f);
        translateAnimation.setDuration(500);
        translateAnimation.setFillAfter(true);
        layoutBot.startAnimation(translateAnimation);


        Animation translateAnimation2 = new TranslateAnimation(0, 0.0f, 0, 0.0f, 0, (float) (-Const.dip2px(this, 56.0f)), 0, 0.0f);
        translateAnimation2.setDuration(500);
        translateAnimation2.setFillAfter(true);
        layoutTop.startAnimation(translateAnimation2);

    }



   @OnClick(R.id.vDelete)
    public void onDelete(){
       imagePath=adapter.getListDoc().get(viewPager.getCurrentItem()).getPath();
       AlertDialog.Builder builder = new AlertDialog.Builder(this);
       builder.setTitle("Delete Page");
       builder.setMessage("Do you want to delete this page?");
       builder.setCancelable(false);
       builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialogInterface, int i) {
                 presenter.delete(imagePath);
           }
       });
       builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialogInterface, int i) {
               dialogInterface.dismiss();
           }
       });
       AlertDialog alertDialog = builder.create();
       alertDialog.show();
   }
}
