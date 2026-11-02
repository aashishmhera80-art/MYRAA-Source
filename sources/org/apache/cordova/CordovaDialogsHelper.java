package org.apache.cordova;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.widget.EditText;

/* JADX INFO: loaded from: classes.dex */
public class CordovaDialogsHelper {
    private final Context context;
    private AlertDialog lastHandledDialog;

    public interface Result {
        void gotResult(boolean z, String str);
    }

    public CordovaDialogsHelper(Context context) {
        this.context = context;
    }

    public void showAlert(String message, final Result result) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(this.context);
        dlg.setMessage(message);
        dlg.setTitle("Alert");
        dlg.setCancelable(true);
        dlg.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: org.apache.cordova.CordovaDialogsHelper.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                result.gotResult(true, null);
            }
        });
        dlg.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.apache.cordova.CordovaDialogsHelper.2
            @Override // android.content.DialogInterface.OnCancelListener
            public void onCancel(DialogInterface dialog) {
                result.gotResult(false, null);
            }
        });
        dlg.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: org.apache.cordova.CordovaDialogsHelper.3
            @Override // android.content.DialogInterface.OnKeyListener
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode != 4) {
                    return true;
                }
                result.gotResult(true, null);
                return false;
            }
        });
        this.lastHandledDialog = dlg.show();
    }

    public void showConfirm(String message, final Result result) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(this.context);
        dlg.setMessage(message);
        dlg.setTitle("Confirm");
        dlg.setCancelable(true);
        dlg.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: org.apache.cordova.CordovaDialogsHelper.4
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                result.gotResult(true, null);
            }
        });
        dlg.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() { // from class: org.apache.cordova.CordovaDialogsHelper.5
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                result.gotResult(false, null);
            }
        });
        dlg.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.apache.cordova.CordovaDialogsHelper.6
            @Override // android.content.DialogInterface.OnCancelListener
            public void onCancel(DialogInterface dialog) {
                result.gotResult(false, null);
            }
        });
        dlg.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: org.apache.cordova.CordovaDialogsHelper.7
            @Override // android.content.DialogInterface.OnKeyListener
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == 4) {
                    result.gotResult(false, null);
                    return false;
                }
                return true;
            }
        });
        this.lastHandledDialog = dlg.show();
    }

    public void showPrompt(String message, String defaultValue, final Result result) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(this.context);
        dlg.setMessage(message);
        final EditText input = new EditText(this.context);
        if (defaultValue != null) {
            input.setText(defaultValue);
        }
        dlg.setView(input);
        dlg.setCancelable(false);
        dlg.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: org.apache.cordova.CordovaDialogsHelper.8
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                String userText = input.getText().toString();
                result.gotResult(true, userText);
            }
        });
        dlg.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() { // from class: org.apache.cordova.CordovaDialogsHelper.9
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                result.gotResult(false, null);
            }
        });
        this.lastHandledDialog = dlg.show();
    }

    public void destroyLastDialog() {
        if (this.lastHandledDialog != null) {
            this.lastHandledDialog.cancel();
        }
    }
}
