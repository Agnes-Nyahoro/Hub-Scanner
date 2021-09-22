package com.joshuabutton.queenscanner.document;

import java.util.List;

/**
 * Created by Phí Văn Tuấn on 30/11/2018.
 */

public interface DocumentContract {
    interface DocumentView{

        void onItemClick(int position);

        String getDefaultName();

        void setFolderName(String folderName);

        void setNameFile(String nameFile);
    }

    interface IDocumentPresenter {
        List<DocumentModel> getListDocument(String folder);

        void onItemClick(int position);

        void openWith(String path);

        void reName(String newName);

        void shareFile(String path);

    }


}
