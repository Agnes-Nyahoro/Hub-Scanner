package com.joshuabutton.queenscanner.process.presenter;

import com.joshuabutton.queenscanner.process.model.FilterModel;

import java.util.List;

/**
 * Created by Phí Văn Tuấn on 24/11/2018.
 */

public interface IProcessPresenter {
    void onItemClick(FilterModel adjuster);

    List<FilterModel> getListModel();

    String getFolderPath(String folderPath);

}
