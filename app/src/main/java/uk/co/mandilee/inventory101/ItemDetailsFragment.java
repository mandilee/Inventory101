package uk.co.mandilee.inventory101;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by manda on 2017-05-25.
 */

public class ItemDetailsFragment extends Fragment {
    public static final String KEY_IMAGE_PATH = "IMAGE_PATH";
    public static final int DEFAULT_IMAGE_RESOURCE = R.drawable.no_image;
    public static final String PICTURE_DIRECTORY = Environment.getExternalStorageDirectory()
            + File.separator + "DCIM" + File.separator + "ItemImage" + File.separator;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 273;
    private static final String ARG_SECTION_NUMBER = "SECTION_NUMBER";
    private static final String ARG_PRODUCT_ID = "PRODUCT_ID";
    private static final String KEY_IMAGE_URI = "IMAGE_URI";
    public static int ACTION_REQUEST_IMAGE = 1000;
    private boolean InEditMode;
    private Item mItem;
    private View mRootView;
    private DbHelper db;

    //Image properties
    private String mCurrentImagePath = null;
    private Uri mCapturedImageURI = null;
    private ImageButton mProfileImageButton;

    private EditText mNameEditText,
            mStockEditText,
            mPriceEditText,
            mDescriptionEditText;


    public ItemDetailsFragment() {
        // Required empty public constructor
    }

    public static ItemDetailsFragment newInstance(int sectionNumber, long productId) {
        ItemDetailsFragment fragment = new ItemDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        if (productId > 0) {
            args.putLong(ARG_PRODUCT_ID, productId);
        }
        fragment.setArguments(args);
        return fragment;
    }

    private void GetPassedInItem() {
        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_PRODUCT_ID)) {
            long itemId = args.getLong(ARG_PRODUCT_ID, 0);
            if (itemId > 0) {
                mItem = db.getItemById(itemId);
                InEditMode = true;
            }
        } else {
            mItem = new Item();
            InEditMode = false;
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DbHelper(getActivity());

        setHasOptionsMenu(true);

        // Ensure there is a saved instance state.
        if (savedInstanceState != null) {

            // Get the saved Image uri string.
            String ImageUriString = savedInstanceState.getString(KEY_IMAGE_URI);

            // Restore the Image uri from the Image uri string.
            if (ImageUriString != null) {
                mCapturedImageURI = Uri.parse(ImageUriString);
            }
            mCurrentImagePath = savedInstanceState.getString(KEY_IMAGE_URI);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mCapturedImageURI != null) {
            outState.putString(KEY_IMAGE_URI, mCapturedImageURI.toString());
        }
        outState.putString(KEY_IMAGE_PATH, mCurrentImagePath);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.item_detail, container, false);
        InitializeViews();
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        GetPassedInItem();
        if (InEditMode) {
            PopulateFields();
        }
    }

    private void PopulateFields() {
        MainActivity myActivity = (MainActivity) getActivity();
        mNameEditText.setText(mItem.getName());
        mStockEditText.setText(mItem.getStock());
        mPriceEditText.setText(String.valueOf(mItem.getPrice()));
        mDescriptionEditText.setText(mItem.getDescription());


        // Update profile's Image
        if (mCurrentImagePath != null && !mCurrentImagePath.isEmpty()) {
            mProfileImageButton.setImageDrawable(new BitmapDrawable(getResources(),
                    FileUtils.getResizedBitmap(mCurrentImagePath, 512, 512)));
        } else {
            mProfileImageButton.setImageDrawable(mItem.getImage(getActivity()));
        }

    }

    private void InitializeViews() {
        mNameEditText = (EditText) mRootView.findViewById(R.id.et_product_name);
        mStockEditText = (EditText) mRootView.findViewById(R.id.et_product_stock);
        mStockEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        mPriceEditText = (EditText) mRootView.findViewById(R.id.et_product_price);
        mPriceEditText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mDescriptionEditText = (EditText) mRootView.findViewById(R.id.et_product_description);

        mProfileImageButton = (ImageButton) mRootView.findViewById(R.id.product_image_button);
        mProfileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
    }

    /*
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }
    //*/

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.item_details_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                break;
            case R.id.action_save_item:
                SaveItem();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void SaveItem() {
        String nameText = mNameEditText.getText().toString(),
                stockText = mStockEditText.getText().toString(),
                priceText = mPriceEditText.getText().toString(),
                descriptionText = mDescriptionEditText.getText().toString();

        if (nameText.equals("") || stockText.equals("") || priceText.equals("") || descriptionText.equals("")) {
            Toast.makeText(getActivity(), "All Fields Are Required", Toast.LENGTH_SHORT).show();

        } else {


            mItem.setName(nameText);
            mItem.setStock(Integer.valueOf(stockText));
            mItem.setPrice(Float.valueOf(priceText));
            mItem.setDescription(descriptionText);

            //Check to see if there is valid image path temporarily in memory
            //Then save that image path to the database and that becomes the profile
            //Image for this user.
            if (mCurrentImagePath != null && !mCurrentImagePath.isEmpty()) {
                mItem.setImagePath(mCurrentImagePath);
            }

            long result = db.addItem(mItem);
            if (result == -1) {
                Toast.makeText(getActivity(), "Unable to add item: " + mItem.getName(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Item Added: " + mItem.getName(), Toast.LENGTH_SHORT).show();
            }
            getActivity().onBackPressed();
        }
    }

    private void chooseImage() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read the contacts
            }

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
            // app-defined int constant that should be quite unique

            return;
        }

        //We need the item's name to to save the image file
        if (mNameEditText.getText() != null && !mNameEditText.getText().toString().isEmpty()) {
            // Determine Uri of camera image to save.
            final File rootDir = new File(PICTURE_DIRECTORY);

            //noinspection ResultOfMethodCallIgnored
            rootDir.mkdirs();

            // Create the temporary file and get it's URI.

            //Get the item name
            String itemName = mNameEditText.getText().toString();

            //Remove all white space in the item name
            itemName.replaceAll("\\s+", "");

            //Use the item name to create the file name of the image that will be captured
            File file = new File(rootDir, FileUtils.generateImageName(itemName));
            mCapturedImageURI = Uri.fromFile(file);

            // Initialize a list to hold any camera application intents.
            final List<Intent> cameraIntents = new ArrayList<>();

            // Get the default camera capture intent.
            final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // Get the package manager.
            final PackageManager packageManager = getActivity().getPackageManager();

            // Ensure the package manager exists.
            if (packageManager != null) {

                // Get all available image capture app activities.
                final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);

                // Create camera intents for all image capture app activities.
                for (ResolveInfo res : listCam) {

                    // Ensure the activity info exists.
                    if (res.activityInfo != null) {

                        // Get the activity's package name.
                        final String packageName = res.activityInfo.packageName;

                        // Create a new camera intent based on android's default capture intent.
                        final Intent intent = new Intent(captureIntent);

                        // Set the intent data for the current image capture app.
                        intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                        intent.setPackage(packageName);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);

                        // Add the intent to available camera intents.
                        cameraIntents.add(intent);
                    }
                }
            }

            // Create an intent to get pictures from the filesystem.
            final Intent galleryIntent = new Intent();
            galleryIntent.setType("image/*");
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

            // Chooser of filesystem options.
            final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

            // Add the camera options.
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                    cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

            // Start activity to choose or take a picture.
            startActivityForResult(chooserIntent, ACTION_REQUEST_IMAGE);
        } else {
            mNameEditText.setError("Please enter item name");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            // Get the resultant image's URI.
            final Uri selectedImageUri = (data == null) ? mCapturedImageURI : data.getData();

            // Ensure the image exists.
            if (selectedImageUri != null) {

                // Add image to gallery if this is an image captured with the camera
                //Otherwise no need to re-add to the gallery if the image already exists
                if (requestCode == ACTION_REQUEST_IMAGE) {
                    final Intent mediaScanIntent =
                            new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    mediaScanIntent.setData(selectedImageUri);
                    getActivity().sendBroadcast(mediaScanIntent);
                }

                mCurrentImagePath = FileUtils.getPath(getActivity(), selectedImageUri);

                // Update client's picture
                if (mCurrentImagePath != null && !mCurrentImagePath.isEmpty()) {
                    mProfileImageButton.setImageDrawable(new BitmapDrawable(getResources(),
                            FileUtils.getResizedBitmap(mCurrentImagePath, 512, 512)));
                }
            }
        }

    }
}
