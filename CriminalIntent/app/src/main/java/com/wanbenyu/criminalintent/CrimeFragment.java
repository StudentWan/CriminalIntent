package com.wanbenyu.criminalintent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;


import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by 本钰 on 2016/8/5.
 */
public class CrimeFragment extends Fragment {
    private static final String TAG = "CrimeFragment";
    public static final String EXTRA_CRIME_ID =
            "com.wanbenyu.android.criminalintent.crime_id";
    private static final String DIALOG_DATE = "date";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_PHOTO = 1;
    private static final int REQUEST_CONTACT = 2;
    private static final String DIALOG_IMAGE = "image";

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private ImageButton mPhotoButton;
    private Button mSuspectButton;
    private Button mReportButton;
    private ImageView mPhotoView;

    public static CrimeFragment newInstance(UUID crimeId){
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CRIME_ID,crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        UUID crimeId = (UUID)getArguments().getSerializable(EXTRA_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
    }

    @TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_crime,parent,false);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (NavUtils.getParentActivityName(getActivity()) != null) {
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        mTitleField = (EditText)v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = (Button)v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                FragmentManager fm = getActivity()
                        .getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this,REQUEST_DATE);
                dialog.show(fm,DIALOG_DATE);
            }
        });

        mSolvedCheckBox = (CheckBox)v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Set the crime's solved property
                mCrime.setSolved(isChecked);
            }
        });

        mPhotoButton = (ImageButton)v.findViewById(R.id.crime_imageButton);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CrimeCameraActivity.class);

                startActivityForResult(i, REQUEST_PHOTO);
            }
        });

        mPhotoView = (ImageView)v.findViewById(R.id.crime_imageView);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Photo p = mCrime.getPhoto();
                if (p == null) return;

                FragmentManager fm = getActivity()
                        .getSupportFragmentManager();
                String path = getActivity()
                        .getFileStreamPath(p.getFilename()).getAbsolutePath();
                ImageFragment.newInstance(path,p.getOrientation())
                        .show(fm, DIALOG_IMAGE);
            }
        });
        registerForContextMenu(mPhotoView);

        // If camera is not available, disable camera functionality
        PackageManager pm = getActivity().getPackageManager();
        boolean hasACamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) ||
                pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT) ||
                Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD ||
                Camera.getNumberOfCameras() > 0;
        if(!hasACamera) {
            mPhotoButton.setEnabled(false);
        }

        mSuspectButton = (Button)v.findViewById(R.id.crime_suspectButton);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK,
                        ContactsContract.Contacts.CONTENT_URI);
                PackageManager pm = getActivity().getPackageManager();
                List<ResolveInfo> activities = pm.queryIntentActivities(i,0);
                boolean isIntentSafe = activities.size() > 0;
                if(isIntentSafe) {
                    i = Intent.createChooser(i, getString(R.string.send_report));
                    startActivityForResult(i, REQUEST_CONTACT);
                }
            }
        });

        if(mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        mReportButton = (Button)v.findViewById(R.id.crime_reportButton);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.crime_report_subject));
                PackageManager pm = getActivity().getPackageManager();
                List<ResolveInfo> activities = pm.queryIntentActivities(i,0);
                boolean isIntentSafe = activities.size() > 0;
                if(isIntentSafe) {
                    i = Intent.createChooser(i, getString(R.string.send_report));
                    startActivity(i);
                }
            }
        });

        return v;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.photo_item_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(mCrime.getPhoto() != null) {
            File file = getActivity().getFileStreamPath(mCrime.getPhoto().getFilename());
            file.delete();
            mCrime.deletePhoto();
            showPhoto();
        }
        return super.onContextItemSelected(item);
    }

    private void showPhoto() {
        //(Re)set the image button's image based on our photo
        Photo p = mCrime.getPhoto();
        BitmapDrawable b = null;
        if(p != null) {
            String path = getActivity()
                    .getFileStreamPath(p.getFilename()).getAbsolutePath();
            b = PictureUtils.getScaledDrawable(getActivity(), path, p.getOrientation());
        }
        mPhotoView.setImageDrawable(b);
    }

    public void updateDate(){
        mDateButton.setText(mCrime.getDate().toString());
    }

    private String getCrimeReport() {
        String solvedString = null;
        if(mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_sovled);
        } else {
            solvedString = getString(R.string.crime_report_unsovled);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if(suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report,
                mCrime.getTitle(), dateString, solvedString, suspect);

        return report;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK) return;
        if(requestCode ==REQUEST_DATE){
            Date date = (Date)data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();
        } else if (requestCode == REQUEST_PHOTO) {
            // Create a new Photo object and attach it to the crime
            String filename = data.
                    getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
            int orientation = data.
                    getIntExtra(CrimeCameraFragment.EXTRA_PHOTO_ORIENTAION,0);
            if (filename != null) {
                if(mCrime.getPhoto() != null) {
                File file = getActivity().getFileStreamPath(mCrime.getPhoto().getFilename());
                file.delete();
                }
                Photo p = new Photo(filename,orientation);
                mCrime.setPhoto(p);
                showPhoto();
            }
        } else if (requestCode == REQUEST_CONTACT) {
            Uri contactUri = data.getData();

            //Specify which fields you want your query to return
            //values for.
            String[] queryFields = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            //Perform your query - the contactUri is like a "where"
            //clause here
            Cursor c = getActivity().getContentResolver()
                    .query(contactUri, queryFields, null, null, null);
            //Double-check that you actually got results
            if(c.getCount() == 0) {
                c.close();
                return;
            }

            //Pull out the first column of the first row of data -
            //that is your suspect's name.
            c.moveToFirst();
            String suspect = c.getString(0);
            mCrime.setSuspect(suspect);
            mSuspectButton.setText(suspect);
            c.close();
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                if(NavUtils.getParentActivityName(getActivity()) != null){
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;
            case R.id.menu_item_delete_crime:
                CrimeLab crimeLab = CrimeLab.get(getActivity());
                crimeLab.deleteCrime(mCrime);
                if(NavUtils.getParentActivityName(getActivity()) != null){
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.crime_list_item_context, menu);
    }

    @Override
    public void onStart() {
        super.onStart();
        showPhoto();
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).saveCrimes();
    }

    @Override
    public void onStop() {
        super.onStop();
        PictureUtils.cleanImageView(mPhotoView);
    }
}
