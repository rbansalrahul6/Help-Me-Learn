package com.example.rbansal.helpmelearn.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.rbansal.helpmelearn.R;
import com.example.rbansal.helpmelearn.firebaseUI.FirebaseListAdapter;
import com.example.rbansal.helpmelearn.models.Category;
import com.example.rbansal.helpmelearn.models.Topic;
import com.example.rbansal.helpmelearn.models.User;
import com.example.rbansal.helpmelearn.rest.RecyclerViewItemClickListener;
import com.example.rbansal.helpmelearn.rest.TopicsAdapter;
import com.example.rbansal.helpmelearn.ui.login.LoginActivity;
import com.example.rbansal.helpmelearn.utils.Constants;
import com.example.rbansal.helpmelearn.utils.Utils;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private Context mContext;
    private FirebaseDatabase database;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText topicName;
    private EditText topicDescription;
    private Spinner categoryList;
    private ProgressDialog mProgressDialog;
    private FirebaseListAdapter<Category> mAdapter;
    private TopicsAdapter topicsAdapter;
    private View positiveAction;
    private boolean exists;
    private String categoryChosed;
    private ArrayList<String> topicsDesc = new ArrayList<>();
    private ArrayList<Topic> topicsList = new ArrayList<>();
    private ArrayAdapter<String> listAdapter;
    private Category mCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_main);
        database = FirebaseDatabase.getInstance();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //setting up category list adapter
        DatabaseReference testRef = database.getReference(Constants.FIREBASE_LOCATION_CATEGORIES);
        mAdapter = new FirebaseListAdapter<Category>(this,Category.class,R.layout.support_simple_spinner_dropdown_item,testRef) {
            @Override
            protected void populateView(View view,Category catg,int position) {
                ((TextView)view.findViewById(android.R.id.text1)).setText(catg.getName());
            }

        };
        //
        final Spinner testList = (Spinner) findViewById(R.id.category_list_1);
        RecyclerView topicsRV = (RecyclerView) findViewById(R.id.topics_recyler_view);
        topicsAdapter = new TopicsAdapter(topicsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        topicsRV.setLayoutManager(mLayoutManager);
        topicsRV.addOnItemTouchListener(new RecyclerViewItemClickListener(this,
                new RecyclerViewItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        Topic topic = topicsList.get(position);
                        Intent intent = new Intent(getApplicationContext(),ResourcesActivity.class)
                                .putExtra("topicObj",topic);
                        startActivity(intent);
                    }
                }));
        topicsRV.setAdapter(topicsAdapter);
        testList.setAdapter(mAdapter);
        testList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Category c = (Category) testList.getSelectedItem();
                topicsList.clear();
                String[] topicRefs = c.getTopics().values().toArray(new String[0]);
                for(String ref : topicRefs) {
                    DatabaseReference topicref = database.getReference(Constants.FIREBASE_LOCATION_TOPICS).child(ref);
                    topicref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Topic topic = dataSnapshot.getValue(Topic.class);
                            topicsList.add(topic);
                            topicsAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //setting up progress dialog
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setTitle("Loading");
        mProgressDialog.setMessage("Checking");
        mProgressDialog.setCancelable(false);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                        .title("name")
                        .customView(R.layout.dialog_customview,true)
                        .positiveText("ADD")
                        .negativeText("CANCEL")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                mProgressDialog.show();
                                String topic = Utils.formatData(topicName.getText().toString());
                                String description = topicDescription.getText().toString();
                                checkIfAlreadyExists(categoryChosed,topic,description);
                               // Log.v("category",categoryChosed);
                            }
                        }).build();
                positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
                categoryList = (Spinner) dialog.getCustomView().findViewById(R.id.category_list);
                //categoryList.setPrompt("Choose");
                categoryList.setAdapter(mAdapter);
                categoryList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mCategory = (Category) categoryList.getSelectedItem();
                        categoryChosed = mCategory.getName();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                topicName = (EditText) dialog.getCustomView().findViewById(R.id.topic_name);
                topicDescription = (EditText) dialog.getCustomView().findViewById(R.id.topic_desc);
                topicName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        positiveAction.setEnabled(s.toString().trim().length() > 0);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                dialog.show();
                positiveAction.setEnabled(false); // disabled by default
                        
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //reading email from shared preferences
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String uid = sp.getString(Constants.KEY_USER_ID,null);
        final TextView textView = (TextView) findViewById(R.id.firebase_read);
        //testing
        DatabaseReference myRef = database.getReference(Constants.FIREBASE_LOCATION_USERS);
        final DatabaseReference userLocation = myRef.child(uid);
        userLocation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(user!=null) {
                    String userName = user.getName();
                    textView.setText(userName);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(LOG_TAG,
                        "Reading error" +
                                databaseError.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id == R.id.logout) {
            logout();
            return true;
        }
        if(id == R.id.test) {
            Intent intent = new Intent(this,AddResourceActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void takeUserToLoginScreenOnUnAuth() {
        /* Move user to LoginActivity, and remove the backstack */
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    protected void logout() {
        //clear shared preferences also
        FirebaseAuth.getInstance().signOut();
        takeUserToLoginScreenOnUnAuth();
    }
    private void checkIfAlreadyExists(final String category,final String topic,final String desc) {
        //boolean exists;
        DatabaseReference catgRef = database.getReference(Constants.FIREBASE_LOCATION_CATEGORIES);
        DatabaseReference myCatg = catgRef.child(category);
        DatabaseReference topicRef = myCatg.child(Constants.FIREBASE_LOCATION_TOPICS).child(topic);
        topicRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    //topic already exists
                    mProgressDialog.dismiss();
                    Utils.showToast(mContext,"Already Exists");
                   // Log.v(LOG_TAG,dataSnapshot.getValue().toString());
                }
                else {
                    //add the new topic
                    addTopic(category,topic,desc);
                    mProgressDialog.dismiss();
                    Utils.showToast(mContext,"Saved");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //Log.v(LOG_TAG,Boolean.toString(exists));
        //return exists;
    }
    private void addTopic(String category,String topicname,String description) {
        DatabaseReference topicsRef = database.getReference(Constants.FIREBASE_LOCATION_TOPICS);
        DatabaseReference newTopic = topicsRef.push();
        final String topicId = newTopic.getKey();
        Topic topic = new Topic(category,topicname,description);
        newTopic.setValue(topic);
        //adding topic to categories
        DatabaseReference catgRef = database.getReference(Constants.FIREBASE_LOCATION_CATEGORIES)
                .child(category).child(Constants.FIREBASE_LOCATION_TOPICS);
        catgRef.child(topicname).setValue(topicId);
    }
    //uselessssss
}
