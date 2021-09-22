//package com.gpaddy.hungdh.camscanner;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.net.Uri;
//import android.os.Handler;
//import android.preference.PreferenceManager;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.owncloud.android.lib.common.OwnCloudClient;
//import com.owncloud.android.lib.common.OwnCloudClientFactory;
//import com.owncloud.android.lib.common.OwnCloudCredentialsFactory;
//import com.owncloud.android.lib.common.network.OnDatatransferProgressListener;
//import com.owncloud.android.lib.common.operations.OnRemoteOperationListener;
//import com.owncloud.android.lib.common.operations.RemoteOperation;
//import com.owncloud.android.lib.common.operations.RemoteOperationResult;
//import com.owncloud.android.lib.resources.files.UploadFileRemoteOperation;
//import com.todobom.opennotescanner.R;
//
//import java.io.File;
//
///**
// * Created by HUNGDH on 4/22/2017.
// */
//
//public class AutoNextCloud implements OnDatatransferProgressListener, OnRemoteOperationListener {
//
//    private Context context;
//
//    private OwnCloudClient mClient;
//    private Handler mHandler = new Handler();
//
//    private final String KEY_SWITCH_NETXT_CLOUD_IMAGE = "prefSwitchOnNextCloudImage";
//    private final String KEY_SWITCH_NETXT_CLOUD_PDF = "prefSwitchOnNextCloudPdf";
//
//    private boolean onSendImage = true;
//    private boolean onSendPdf = true;
//
//    private final String KEY_BASE_URL = "prefServer";
//    private final String KEY_USERNAME = "prefUsername";
//    private final String KEY_PASSWORD = "prefPassword";
//
//    private String serverBaseUrl = "";
//    private String username = "";
//    private String password = "";
//
//    public AutoNextCloud(Context context) {
//        this.context = context;
//
//        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
//        onSendImage = sharedPrefs.getBoolean(KEY_SWITCH_NETXT_CLOUD_IMAGE, true);
//        onSendPdf = sharedPrefs.getBoolean(KEY_SWITCH_NETXT_CLOUD_PDF, true);
//
//        serverBaseUrl = sharedPrefs.getString(KEY_BASE_URL, context.getString(R.string.server_default));
//        username = sharedPrefs.getString(KEY_USERNAME, context.getString(R.string.username_default));
//        password = sharedPrefs.getString(KEY_PASSWORD, context.getString(R.string.password_default));
//
//        initLogin();
//    }
//
//    private void initLogin() {
//        mHandler = new Handler();
//
//        // Parse URI to the base URL of the Nextcloud server
//        Uri serverUri = Uri.parse(serverBaseUrl);
//
//        // Create client object to perform remote operations
//        mClient = OwnCloudClientFactory.createOwnCloudClient(
//                serverUri,
//                context,
//                // Activity or Service context
//                true);
//
//        // Set basic credentials
//        mClient.setCredentials(
//                OwnCloudCredentialsFactory.newBasicCredentials(username, password)
//        );
//    }
//
//    public void uploadFileImage(String path) {
//        if (onSendImage && !serverBaseUrl.equals("") && !username.equals("") && !password.equals("")) {
//            Toast.makeText(context, context.getString(R.string.toast_sending), Toast.LENGTH_SHORT).show();
//
//            File fileToUpload = new File(path);
//            if (fileToUpload.exists()) {
//                String remotePath = "/Documentos escaneados/" + fileToUpload.getName();
//                String mimeType = "image/png";
//                startUpload(fileToUpload, remotePath, mimeType);
//            }
//        }
//    }
//
//    public void uploadFilePdf(String path) {
//        if (onSendPdf && !serverBaseUrl.equals("") && !username.equals("") && !password.equals("")) {
//            Toast.makeText(context, context.getString(R.string.toast_sending), Toast.LENGTH_SHORT).show();
//
//            File fileToUpload = new File(path);
//            if (fileToUpload.exists()) {
//                String remotePath = "/Documentos escaneados/" + fileToUpload.getName();
//                String mimeType = "application/pdf";
//                startUpload(fileToUpload, remotePath, mimeType);
//            }
//        }
//    }
//
//    private void startUpload(File fileToUpload, String remotePath, String mimeType) {
//        UploadFileRemoteOperation uploadOperation = new UploadFileRemoteOperation(fileToUpload.getAbsolutePath(), remotePath, mimeType,"");
//        uploadOperation.addDatatransferProgressListener(this);
//        uploadOperation.execute(mClient, this, mHandler);
//    }
//
//    @Override
//    public void onRemoteOperationFinish(RemoteOperation operation, RemoteOperationResult result) {
//        if (operation instanceof UploadFileRemoteOperation) {
//            if (result.isSuccess()) {
//                // do your stuff here
//                Toast.makeText(context, context.getString(R.string.toast_sent), Toast.LENGTH_SHORT).show();
//                Log.d("void", "Success");
//            }
//        }
//    }
//
//    @Override
//    public void onTransferProgress(long progressRate, long totalTransferredSoFar, long totalToTransfer, String fileName) {
//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                // do your UI updates about progress here
//                Log.d("void", "onProgress");
//            }
//        });
//    }
//}
