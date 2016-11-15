package com.example.rbansal.helpmelearn.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.rbansal.helpmelearn.R;
import com.example.rbansal.helpmelearn.models.Resource;
import com.example.rbansal.helpmelearn.utils.Constants;
import com.example.rbansal.helpmelearn.utils.Utils;
import com.firebase.client.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nononsenseapps.filepicker.FilePickerActivity;

public class AddResourceActivity extends AppCompatActivity {
    private Context mContext;
    private ViewStub stub = null;
    private boolean isStubVisible = false;
    private Spinner types;
    private Button addBtn,cancelBtn,uploadBtn;
    private EditText getResDesc, getResLink, getResName;
    private TextView resFile;
    private String resDesc,link,fileName,resName;
    private static String LOG_TAG = AddResourceActivity.class.getSimpleName();
    private static final String[] resTypes = new String[]{"Url","Book","Notes","Video","Other"};
    private static final int FILE_CODE = 111;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_resource);
        mContext = this;
        Firebase.setAndroidContext(this);
        database = FirebaseDatabase.getInstance();
        //initialize screen
        types = (Spinner) findViewById(R.id.res_type);
        getResName = (EditText) findViewById(R.id.give_res_name);
        getResDesc = (EditText) findViewById(R.id.give_res_desc);
        getResLink = (EditText) findViewById(R.id.give_res_link);
        addBtn = (Button) findViewById(R.id.add_btn);
        cancelBtn = (Button) findViewById(R.id.cancel_btn);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,R.layout.support_simple_spinner_dropdown_item,resTypes);
        types.setAdapter(adapter);
        types.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String typeChosed = types.getSelectedItem().toString();
                if(!(typeChosed.equals("Url"))) {
                    showUploadBox();
                }
                else {
                    hideUploadBox();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
               // hideUploadBox();
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resName = getResName.getText().toString();
                resDesc = getResDesc.getText().toString();
                if(!anActionTaken()) {
                    Utils.showToast(mContext,"No Action Taken");
                }
                else {
                    //add to database
                    DatabaseReference ref = database.getReference(Constants.FIREBASE_LOCATION_TOPICS).child("test").child("resources");
                    DatabaseReference newRes = ref.push();
                    String resId = newRes.getKey();
                    Resource res = new Resource(resName,resDesc);
                    newRes.setValue(res);
                }
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == FILE_CODE && resultCode == Activity.RESULT_OK) {
            Uri fileUri = data.getData();
            resFile.setText(fileUri.getLastPathSegment());
            getResLink.setFocusable(false);
            getResLink.setEnabled(false);
        }
    }
    private void showUploadBox() {
        isStubVisible = true;
        if(stub == null) {
            stub = (ViewStub) findViewById(R.id.upload_box);
            View inflated = stub.inflate();
            uploadBtn = (Button) inflated.findViewById(R.id.upload_btn);
            resFile = (TextView) inflated.findViewById(R.id.file_name);
            uploadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // This always works
                    Intent i = new Intent(mContext, FilePickerActivity.class);
                    // This works if you defined the intent filter
                    // Intent i = new Intent(Intent.ACTION_GET_CONTENT);

                    // Set these depending on your use case. These are the defaults.
                    i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                    i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
                    i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);

                    // Configure initial directory by specifying a String.
                    // You could specify a String like "/storage/emulated/0/", but that can
                    // dangerous. Always use Android's API calls to get paths to the SD-card or
                    // internal memory.
                    i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

                    startActivityForResult(i,FILE_CODE);
                }
            });
        }
        else {
            stub.setVisibility(View.VISIBLE);
        }
    }
    private void hideUploadBox() {
        isStubVisible = false;
        if(stub!=null) {
            stub.setVisibility(View.GONE);
        }
    }
    private boolean anActionTaken() {
        boolean bool_link,bool_file = false;
        link = getResLink.getText().toString();
        bool_link = link.trim().length() > 0;
        if(stub!=null && resFile.isShown()) {
            fileName = resFile.getText().toString();
            bool_file = !TextUtils.isEmpty(fileName);
        }
        Log.v(LOG_TAG,Boolean.toString(bool_file));
        return (bool_link || bool_file);
    }
}
