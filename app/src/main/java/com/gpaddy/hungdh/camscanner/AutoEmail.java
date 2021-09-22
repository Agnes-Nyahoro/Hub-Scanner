package com.gpaddy.hungdh.camscanner;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.todobom.queenscanner.R;

/**
 * Created by HUNGDH on 4/17/2017.
 */

public class AutoEmail {

    public static final String KEY_SWITCH_EMAIL_IMAGE = "prefSwitchOnEmailImage";
    public static final String KEY_SWITCH_EMAIL_PDF = "prefSwitchOnEmailPdf";
    public static final String KEY_MAIL_TO = "prefMailTo";
    public static final String KEY_SUBJECT = "prefMailSubject";
    public static final String KEY_BODY = "prefMailBody";

    private boolean onSendImage = false;
    private boolean onSendPdf = false;
    private String mailTo = "";
    private String subject = "";
    private String body = "";

    private Context context;

    public AutoEmail(Context context) {
        this.context = context;
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        onSendImage = sharedPrefs.getBoolean(KEY_SWITCH_EMAIL_IMAGE, false);
        onSendPdf = sharedPrefs.getBoolean(KEY_SWITCH_EMAIL_PDF, false);
        mailTo = sharedPrefs.getString(KEY_MAIL_TO, "");
        subject = sharedPrefs.getString(KEY_SUBJECT, context.getString(R.string.email_subject));
        body = sharedPrefs.getString(KEY_BODY, context.getString(R.string.email_body));
    }

    public void sendEmailImage(String path) {
        if (onSendImage && !mailTo.equals("")) {
            Toast.makeText(context, context.getString(R.string.toast_sending), Toast.LENGTH_SHORT).show();

            BackgroundMail.newBuilder(context)
                    .withUsername("cargoscansfile@gmail.com")
                    .withPassword("1Cargo2@#")
                    .withMailto(mailTo)
                    .withType(BackgroundMail.TYPE_PLAIN)
                    .withSubject(subject)
                    .withBody(body)
                    .withAttachments(path)
                    .withProcessVisibility(false)
                    .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                        @Override
                        public void onSuccess() {
//                            Toast.makeText(context, context.getString(R.string.toast_sent), Toast.LENGTH_SHORT).show();
                            //do some magic
                        }
                    })
                    .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                        @Override
                        public void onFail() {
                            //do some magic
                        }
                    })
                    .send();
        }
    }

    public void sendEmailPdf(String path) {
        if (onSendPdf && !mailTo.equals("")) {
            Toast.makeText(context, context.getString(R.string.toast_sending), Toast.LENGTH_SHORT).show();

            BackgroundMail.newBuilder(context)
                    .withUsername("cargoscansfile@gmail.com")
                    .withPassword("1Cargo2@#")
                    .withMailto(mailTo)
                    .withType(BackgroundMail.TYPE_PLAIN)
                    .withSubject(subject)
                    .withBody(body)
                    .withAttachments(path)
                    .withProcessVisibility(false)
                    .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(context, context.getString(R.string.toast_sent), Toast.LENGTH_SHORT).show();
                            //do some magic
                        }
                    })
                    .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                        @Override
                        public void onFail() {
                            //do some magic
                        }
                    })
                    .send();
        }
    }
}
