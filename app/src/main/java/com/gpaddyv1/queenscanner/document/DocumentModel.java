package com.joshuabutton.queenscanner.document;

import android.util.Log;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Phí Văn Tuấn on 30/11/2018.
 */

public class DocumentModel implements Serializable {
    private String path;

    public DocumentModel(String path) {
        this.path = path;
    }

    public DocumentModel() {

    }

    public String getPath() {
        return path;
    }

    public List<DocumentModel> getLstPathImage(String folder) {
        List<DocumentModel> lst = new ArrayList<>();
        File file = new File(folder);
        File[] files = file.listFiles();
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File object1, File object2) {
                return object1.getName().compareTo(object2.getName());
            }
        });
        if (file.exists()) {
            for (int i = 0; i < files.length; i++) {
                try {
                    if (files[i].getAbsolutePath().endsWith(".jpg")) {
                        lst.add(new DocumentModel(files[i].getAbsolutePath()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        Log.e("bvh", "ccccccc");
        return lst;
    }

    public List<DocumentModel> getLstDocs(String folder) {
        List<DocumentModel> lst = new ArrayList<>();
        File file = new File(folder);
        if (file.exists()) {
            for (File f : file.listFiles()) {
                try {
                    if (f.exists() && f.isDirectory()) {
                        lst.add(new DocumentModel(f.getAbsolutePath()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return lst;
    }
}
