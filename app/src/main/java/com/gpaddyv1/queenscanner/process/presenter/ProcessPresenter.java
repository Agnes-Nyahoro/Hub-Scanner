package com.gpaddyv1.queenscanner.process.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.gpaddy.hungdh.util.Const;
import com.joshuabutton.queenscanner.process.model.FilterModel;
import com.joshuabutton.queenscanner.process.view.IProcessView;

import java.io.File;
import java.util.List;

/**
 * Created by Phí Văn Tuấn on 24/11/2018.
 */

public class ProcessPresenter implements com.joshuabutton.queenscanner.process.presenter.IProcessPresenter {
    private Context context;
    private IProcessView iProcessView;

    public ProcessPresenter(Context context, IProcessView iProcessView) {
        this.context = context;
        this.iProcessView = iProcessView;
    }

    @Override
    public void onItemClick(FilterModel adjuster) {
        iProcessView.onItemClick(adjuster);
    }

    @Override
    public List<FilterModel> getListModel() {
        return new FilterModel().getFilterModels();
    }

    @Override
    public String getFolderPath(String folderPath) {
        if (TextUtils.isEmpty(folderPath)) {
            String folder = "New Document";
            String folder2 = folder;
            int position = 0;
            File f = new File(Const.FOLDER_DOC);
            if (!f.exists()) {
                f.mkdirs();
            }
            try {
                for (File file : f.listFiles()) {
                    if (file.getName().equals(folder)) {
                        position = position + 1;
                        folder = folder2 + "(" + position + ")";
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            File file = new File(Const.FOLDER_DOC + folder + "/");
            if (!file.exists()) {
                file.mkdirs();
            }
            return file.getAbsolutePath();
        } else {
            return folderPath;
        }

    }
}
