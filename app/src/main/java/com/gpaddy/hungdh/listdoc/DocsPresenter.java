package com.gpaddy.hungdh.listdoc;

import android.content.Context;
import android.text.TextUtils;

import com.itextpdf.text.pdf.PdfReader;
import com.joshuabutton.queenscanner.document.DocumentModel;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Phí Văn Tuấn on 5/12/2018.
 */

public class DocsPresenter implements DocsContract.IDocsPresenter {
    private Context context;
    private DocsContract.IDocsView iView;

    public DocsPresenter(Context context, DocsContract.IDocsView iView) {
        this.context = context;
        this.iView = iView;
    }

    @Override
    public void onItemClick(File file) {
        iView.onItemClick(file);
    }

    @Override
    public void onItemLongClick(File file) {
        iView.onItemLongClick(file);
    }

    @Override
    public String bindLastModify(long time) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        return dateFormat.format(new Date(time));
    }

    @Override
    public String getPagePdf(File folder) {
        if (folder.exists()) {
            for (File file : folder.listFiles()) {
                if (file.getAbsolutePath().endsWith(".pdf")) {
                    PdfReader reader = null;
                    try {
                        reader = new PdfReader(file.getAbsolutePath());

                        return reader.getNumberOfPages() + "";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return "0";
    }

    @Override
    public int getNumberOfImage(File folder) {
        int num = 0;
        if (folder.exists()) {
            for (File file : folder.listFiles()) {
                if (file.getAbsolutePath().endsWith(".jpg")) {
                    num++;
                }
            }
        }
        return num;
    }


    @Override
    public String getImagePath(File folder) {
        if (folder.exists()) {
            for (File file : folder.listFiles()) {
                if (file.getAbsolutePath().endsWith(".jpg")) {
                    return file.getAbsolutePath();
                }
            }
        }
        return "0";
    }

    @Override
    public List<DocumentModel> getListDocs(String folder) {
        List<DocumentModel> lst = new DocumentModel().getLstDocs(folder);
        List<DocumentModel> lstDocs = new ArrayList<>();
        for (int i = 0; i < lst.size(); i++) {
            if (!TextUtils.isEmpty(getPagePdf(new File(lst.get(i).getPath())))) {
                lstDocs.add(lst.get(i));
            }
        }
        return lstDocs;
    }
}
