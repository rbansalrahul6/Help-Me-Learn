package com.example.rbansal.helpmelearn.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.rbansal.helpmelearn.R;
import com.example.rbansal.helpmelearn.firebaseUI.FirebaseRecyclerAdapter;
import com.example.rbansal.helpmelearn.models.Resource;
import com.example.rbansal.helpmelearn.models.Topic;
import com.example.rbansal.helpmelearn.rest.ResourceHolder;
import com.example.rbansal.helpmelearn.utils.Constants;
import com.example.rbansal.helpmelearn.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nononsenseapps.filepicker.FilePickerActivity;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ResourcesActivityFragment extends Fragment {
    private static final String LOG_TAG = ResourcesActivity.class.getSimpleName();
    private Context mContext;
    public static final int FILE_CODE = 111;
    private Uri fileUri = null;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private ProgressDialog uploadDialog;
    private View positiveAction;
    private FirebaseRecyclerAdapter mAdapter;
    private FirebaseDatabase database;
    private DatabaseReference mRef;
    private DatabaseReference votedRef;
    private String uid;
    private String ifVoted;

    public ResourcesActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_resources, container, false);
        Intent intent = getActivity().getIntent();
        Bundle bundle = intent.getExtras();
        Topic topic = bundle.getParcelable("topicObj");
        database = FirebaseDatabase.getInstance();
        mContext = getActivity();
        //getting current user
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        uid = sp.getString(Constants.KEY_USER_ID,null);
        mRef = database.getReference(Constants.FIREBASE_LOCATION_TOPICS).child("test").child("resources");
        //votedRef = database.getReference("voted");
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://help-me-learn.appspot.com");
        //recycler view
        RecyclerView resRecycler = (RecyclerView) rootView.findViewById(R.id.res_recycler_view);
        resRecycler.setHasFixedSize(true);
        resRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new FirebaseRecyclerAdapter<Resource,ResourceHolder>(Resource.class,R.layout.resource_card,ResourceHolder.class,mRef) {
            @Override
            protected void populateViewHolder(final ResourceHolder resourceHolder, final Resource res, final int position) {
                resourceHolder.setName(res.getName());
                resourceHolder.setDesc(res.getDescription());
                resourceHolder.setVoteCount(res.getVotes());
                resourceHolder.setButton(res.getVoted().get(uid),mContext);
                //click event for button
                Button voteBtn = (Button)resourceHolder.mView.findViewById(R.id.vote_btn);
                voteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int cur = res.getVotes();
                        Boolean ifVoted = res.getVoted().get(uid);
                        if(ifVoted) {
                            mAdapter.getRef(position).child("voted").child(uid).setValue(false);
                            mAdapter.getRef(position).child("votes").setValue(cur-1);
                        }
                        else {
                            mAdapter.getRef(position).child("voted").child(uid).setValue(true);
                            mAdapter.getRef(position).child("votes").setValue(cur+1);
                        }
                        //mAdapter.getRef(position).child("votes").setValue(cur+1); //improve
                        Utils.showToast(mContext,"Button clicked");
                    }
                });
            }
        };
        resRecycler.setAdapter(mAdapter);
        TextView topicName = (TextView) rootView.findViewById(R.id.top_name);
        topicName.setText(topic.getTopicName());
        TextView topicDesc = (TextView) rootView.findViewById(R.id.description);
        topicDesc.setText(topic.getDescription());
        Button addBtn = (Button) rootView.findViewById(R.id.add_resource);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
          /*      // This always works
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

                startActivityForResult(i,FILE_CODE); */
                Intent intent = new Intent(mContext,AddResourceActivity.class);
                startActivity(intent);
            }
        });
        //test code for view switcher
    /*    Button Prev = (Button) rootView.findViewById(R.id.prev);
        Button Next = (Button) rootView.findViewById(R.id.next);
        final ViewSwitcher switchView = (ViewSwitcher) rootView.findViewById(R.id.view_switch);
        Prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchView.showPrevious();
            }
        });
        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchView.showNext();
            }
        }); */
        //setting up dialog
        uploadDialog = new ProgressDialog(mContext);
        uploadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        uploadDialog.setTitle("upload");
        uploadDialog.setMessage("uploading");
        uploadDialog.setMax(100);
        uploadDialog.setIndeterminate(false);
        uploadDialog.setProgress(0);
        return rootView;
    }
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == FILE_CODE && resultCode == Activity.RESULT_OK) {
            if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
                // For JellyBean and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ClipData clip = data.getClipData();

                    if (clip != null) {
                        for (int i = 0; i < clip.getItemCount(); i++) {
                            Uri uri = clip.getItemAt(i).getUri();
                            // Do something with the URI
                        }
                    }
                    // For Ice Cream Sandwich
                } else {
                    ArrayList<String> paths = data.getStringArrayListExtra
                            (FilePickerActivity.EXTRA_PATHS);

                    if (paths != null) {
                        for (String path: paths) {
                            Uri uri = Uri.parse(path);
                            // Do something with the URI
                        }
                    }
                }

            } else {
                fileUri = data.getData();
                // Do something with the URI
            }
            Log.v(LOG_TAG,fileUri.toString());
            //uploading the file
            StorageReference fileRef = storageRef.child("Test/" + fileUri.getLastPathSegment());
            // Create the file metadata
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("txt")
                    .build();

// Upload file and metadata to the path 'images/mountains.jpg'
            final UploadTask uploadTask = fileRef.putFile(fileUri, metadata);
            uploadDialog.show();

// Listen for state changes, errors, and completion of the upload.
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    uploadDialog.setProgress((int) progress);
                    System.out.println("Upload is " + progress + "% done");
                }
            }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                    System.out.println("Upload is paused");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Log.v(LOG_TAG,"Upload error");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Handle successful uploads on complete
                    Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                    Log.v("download",downloadUrl.toString());
                    uploadDialog.dismiss();
                }
            });

        }
    }
}
