package summer15.manparvesh.sahayakmissedcallregistration;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;

import java.io.IOException;
import java.io.OutputStream;

public class UploadNumber extends BaseDemoActivity {

            private static final String TAG = "EditContentsActivity";
            public static String callerPhoneNumber;

            @Override
            public void onConnected(Bundle connectionHint) {
                super.onConnected(connectionHint);

                Bundle extras = getIntent().getExtras();
                callerPhoneNumber=extras.getString("callerPhoneNumber");



                final ResultCallback<DriveApi.DriveIdResult> idCallback = new ResultCallback<DriveApi.DriveIdResult>() {
                    @Override
                    public void onResult(DriveApi.DriveIdResult result) {
                        if (!result.getStatus().isSuccess()) {
                            showMessage("Cannot find DriveId. Are you authorized to view this file?");
                            return;
                        }
                        DriveFile file = Drive.DriveApi.getFile(getGoogleApiClient(), result.getDriveId());
                        new EditContentsAsyncTask(UploadNumber.this).execute(file);
                    }
                };
                SharedPreferences settings = getSharedPreferences("prefName", 0);
                String res = settings.getString("res", "");

                Drive.DriveApi.fetchDriveId(getGoogleApiClient(), res)
                        .setResultCallback(idCallback);
            }

            public class EditContentsAsyncTask extends ApiClientAsyncTask<DriveFile, Void, Boolean> {

                public EditContentsAsyncTask(Context context) {
                    super(context);
                }

                @Override
                protected Boolean doInBackgroundConnected(DriveFile... args) {
                    DriveFile file = args[0];
                    try {
                        DriveApi.DriveContentsResult driveContentsResult = file.open(
                                getGoogleApiClient(), DriveFile.MODE_WRITE_ONLY, null).await();
                        if (!driveContentsResult.getStatus().isSuccess()) {
                            return false;
                        }
                        DriveContents driveContents = driveContentsResult.getDriveContents();
                        OutputStream outputStream = driveContents.getOutputStream();
                        outputStream.write((callerPhoneNumber+"\n").getBytes());
                        com.google.android.gms.common.api.Status status =
                                driveContents.commit(getGoogleApiClient(), null).await();
                        return status.getStatus().isSuccess();
                    } catch (IOException e) {
                        Log.e(TAG, "IOException while appending to the output stream", e);
                    }
                    return false;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    if (!result) {
                        showMessage("Error while editing contents");
                        return;
                    }
                    showMessage("Successfully edited contents");
                }
            }
        }