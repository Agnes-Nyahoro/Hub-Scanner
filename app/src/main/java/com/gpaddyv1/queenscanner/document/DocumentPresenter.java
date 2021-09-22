package com.joshuabutton.queenscanner.document;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import androidx.core.content.FileProvider;
import android.text.TextUtils;
import android.widget.Toast;

import com.gpaddy.hungdh.util.Const;
import com.gpaddy.hungdh.util.PathUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Phí Văn Tuấn on 30/11/2018.
 */

public class DocumentPresenter implements DocumentContract.IDocumentPresenter {
    private DocumentContract.DocumentView iView;
    private Context context;
    private DocumentModel model;
    private String pdfFile;

    public DocumentPresenter(DocumentContract.DocumentView iView, Context context) {
        this.iView = iView;
        this.context = context;
        model = new DocumentModel();
    }

    @Override
    public List<DocumentModel> getListDocument(String folder) {
        return model.getLstPathImage(folder);
    }

    @Override
    public void onItemClick(int document) {
        iView.onItemClick(document);
    }

    @Override
    public void openWith(String path) {
        File file = new File(path);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }

    @Override
    public void reName(String newName) {
        if (TextUtils.isEmpty(newName)) {
            Toast.makeText(context, "File name must not be blank", Toast.LENGTH_SHORT).show();
        } else if (isExistName(newName)) {
//            Toast.makeText(context, "File exits", Toast.LENGTH_SHORT).show();
        } else {
            iView.setNameFile(newName);
            File oldFile = new File(iView.getDefaultName());
            File file = new File(Const.FOLDER_DOC + newName);
            try {
                PathUtil.copyDirectory(oldFile, file);
                File pdf = new File(file, oldFile.getName() + ".pdf");
                pdf.renameTo(new File(file, file.getName() + ".pdf"));
                iView.setFolderName(file.getAbsolutePath());
                PathUtil.deleteDirectory(oldFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void shareFile(String myFilePath) {
        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        File fileWithinMyDir = new File(myFilePath);

        if (fileWithinMyDir.exists()) {
            intentShareFile.setType("application/pdf");
            Uri uri;
            if (Build.VERSION.SDK_INT < 24)
                uri = Uri.fromFile(fileWithinMyDir);
            else {
                uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", fileWithinMyDir);
            }
            intentShareFile.putExtra(Intent.EXTRA_STREAM, uri);

            intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                    "Sharing File...");
            intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");

            context.startActivity(Intent.createChooser(intentShareFile, "Share File"));
        }
    }

    private boolean isExistName(String name) {

        try {
            File file = new File(Const.FOLDER_DOC);
            for (File f : file.listFiles()) {
                if (f.getName().equals(name)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }


}
