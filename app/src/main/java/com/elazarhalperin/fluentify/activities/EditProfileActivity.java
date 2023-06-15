package com.elazarhalperin.fluentify.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.elazarhalperin.fluentify.Models.TeacherModel;
import com.elazarhalperin.fluentify.Models.UserModel;
import com.elazarhalperin.fluentify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class EditProfileActivity extends AppCompatActivity {
    private static final int GO_TO_GALLERY_CODE = 121;

    FloatingActionButton fab_goBack;
    Button btn_save;

    EditText et_name, et_email, et_lessonPrice, et_info;

    ImageView iv_profileImage;

    LinearLayout ll_teacherContainer;

    FirebaseUser firebaseUser;
    StorageReference storageReference;

    Drawable updatedProfileImage;
    Uri uriUpdatedProfileImage;

    UserModel userModel;
    TeacherModel teacherModel;

    private ActivityResultLauncher<Intent> galleryLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        fab_goBack = findViewById(R.id.fab_goBack);

        btn_save = findViewById(R.id.btn_save);

        et_name = findViewById(R.id.et_name);
        et_email = findViewById(R.id.et_email);
        et_lessonPrice = findViewById(R.id.et_lessonPrice);
        et_info = findViewById(R.id.et_info);

        iv_profileImage = findViewById(R.id.iv_profileImage);

        ll_teacherContainer = findViewById(R.id.ll_teacherPart);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("profile_pictures").child("profile_image" + firebaseUser.getUid() + ".jpg");

        teacherModel = (TeacherModel) getIntent().getSerializableExtra("teacherModel");
        userModel = (UserModel) getIntent().getSerializableExtra("userModel");

        if (teacherModel == null) {
            ll_teacherContainer.setVisibility(View.GONE);
        } else {
            ll_teacherContainer.setVisibility(View.VISIBLE);
        }

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Toast.makeText(getApplicationContext(), "you are in on result", Toast.LENGTH_SHORT).show();
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Toast.makeText(getApplicationContext(), "is result ok", Toast.LENGTH_SHORT).show();
                            Intent data = result.getData();
                            // this checks weather the image is from the camera and not taken from the gallery.
                            if (data != null) {
                                Toast.makeText(getApplicationContext(), "you in the if data != null", Toast.LENGTH_SHORT).show();

                                Bundle extras = data.getExtras();
                                if (extras != null && extras.containsKey("data")) {
                                    Toast.makeText(getApplicationContext(), "you took a picture", Toast.LENGTH_SHORT).show();
                                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                                    // Process the captured image
                                    iv_profileImage.setImageBitmap(imageBitmap);
                                    updatedProfileImage = iv_profileImage.getDrawable();
                                    // setting it into file so we can turn it into uri and add it into firebase storage.
                                    File tempFile = new File(getApplicationContext().getCacheDir(), "image.png");
                                    try {
                                        FileOutputStream outputStream = new FileOutputStream(tempFile);
                                        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                                        outputStream.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    // Obtain the URI of the temporary file
                                    Uri uri = Uri.fromFile(tempFile);
                                    uriUpdatedProfileImage = uri;
                                    return;


                                }

                                // if we got here that means that the image was taken from gallery.
                                Uri selectedImageUri = data.getData();
                                iv_profileImage.setImageURI(selectedImageUri);
                                updatedProfileImage = iv_profileImage.getDrawable();
                                uriUpdatedProfileImage = selectedImageUri;


                            }
                        }
                    }
                });

        putFields();
        putImageProfile();
        setListeners();
    }

    /**
     * The function put texts into the EditTexts
     * in case it's a teacher it will add an eddition to that.
     */
    private void putFields() {
        et_name.setText(userModel.getName());
        et_email.setText(userModel.getEmail());

        if (teacherModel == null) return;

        et_info.setText(teacherModel.getInfo());
        et_lessonPrice.setText(teacherModel.getLessonPrice() + "");
    }



    /**
     *  Downloading the image from firebase
     *  and displaying it in the image view
     */
    private void putImageProfile() {
        long MEGABYTE = 1024 * 1024;
        storageReference.getBytes(MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                iv_profileImage.setImageBitmap(bitmap);
            }
        });
    }

    private void setListeners() {
        fab_goBack.setOnClickListener(v -> {
            finish();
        });

        btn_save.setOnClickListener(v -> {
            saveUserDetails();
        });

        iv_profileImage.setOnClickListener(v -> {
            openTakeImageIntent();
        });
    }

    /**
     * the function will open an intent with 2 choices
     * first is to take the image from the gallery
     * second is to take picture from the camera.
     */
    private void openTakeImageIntent() {

        Intent intentImage = new Intent(Intent.ACTION_GET_CONTENT);
        intentImage.setType("image/*");
        intentImage.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 1024 * 1024); // Limit image resolution to 1MB

        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCamera.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 1024 * 1024); // Limit image size

        // intent that you can choose between camera and gallery/
        Intent chooserIntent = Intent.createChooser(intentImage, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intentCamera});

        galleryLauncher.launch((chooserIntent));
    }

    /**
     * Saves the user details to Firebase Firestore.
     * If the user is a teacher, it also updates the teacher details.
     */
    private void saveUserDetails() {
        String name = et_name.getText().toString().trim();

        btn_save.setEnabled(false);

        // Validate user fields
        if (!isUserFieldsValid(name)) {
            return;
        }

        // Check if the name is the same as the current user's name and there is no teacherModel
        if (name.equals(userModel.getName()) && teacherModel == null) {
            return;
        }

        // Update the user details in Firebase Firestore
        updateUserInFirebase(name);

        // Check if the user is a teacher
        if (teacherModel == null) {
            return;
        }

        // Disable the save button
        btn_save.setEnabled(false);

        // Get the info and lesson price from the EditText fields
        String info = et_info.getText().toString().trim();
        String lessonPrice = et_lessonPrice.getText().toString().trim();

        // Validate the info field
        if (info.isEmpty()) {
            et_info.setError("Field is empty");
            et_info.requestFocus();
            return;
        }

        // Validate the lesson price field
        if (lessonPrice.isEmpty()) {
            et_lessonPrice.setError("Field is empty");
            et_info.requestFocus();
            return;
        }

        // Validate that the lesson price is numeric
        try {
            double d = Double.parseDouble(lessonPrice);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Only numeric values are allowed.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the teacher document reference
        DocumentReference teacherRef = FirebaseFirestore.getInstance().collection("teachers").document(firebaseUser.getUid());

        // Check if the info and lesson price are the same as the current teacher's info and lesson price
        if (lessonPrice.equals(teacherModel.getLessonPrice()) && info.equals(teacherModel.getInfo())) {
            return;
        }

        // Update the teacher model with the new info and lesson price
        teacherModel.setInfo(info);
        teacherModel.setLessonPrice(Double.parseDouble(lessonPrice));

        // Update the teacher document in Firebase Firestore
        teacherRef.update(teacherModel.getMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Updated successfully", Toast.LENGTH_SHORT).show();
                    btn_save.setEnabled(true);
                    finish();
                } else {
                    btn_save.setEnabled(false);
                }
            }
        });
    }

    /**
     * Updates the user details in Firebase Firestore.
     * If the user is a teacher, it updates the teacher details; otherwise, it updates the student details.
     *
     * @param name The updated name of the user.
     */
    private void updateUserInFirebase(String name) {
        userModel.setName(name);

        // Determine the collection based on whether the user is a teacher or a student
        String collection = teacherModel == null ? "students" : "teachers";
        DocumentReference studentRef = FirebaseFirestore.getInstance().collection(collection).document(firebaseUser.getUid());

        // Update the user document in Firebase Firestore
        studentRef.update(userModel.getMap())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (teacherModel == null) {
                            // Display a success message and disable the save button
                            Toast.makeText(getApplicationContext(), "Updated successfully", Toast.LENGTH_SHORT).show();
                            btn_save.setEnabled(false);
                            finish();
                        }
                    }
                });

        // Check if the profile image URI has been updated
        if (uriUpdatedProfileImage != null) {
            updateProfileImageInFirebase();
        }
    }

    /**
     * Updates the profile image in Firebase Storage.
     * The profile image file is uploaded to the storage reference.
     */
    private void updateProfileImageInFirebase() {
        storageReference.putFile(uriUpdatedProfileImage)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Fields were successfully updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Couldn't update profile image...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Validates the user fields.
     * Checks if the name field is empty.
     *
     * @param name The name to validate.
     * @return {@code true} if the fields are valid, {@code false} otherwise.
     */
    private boolean isUserFieldsValid(String name) {
        if (name.isEmpty()) {
            et_name.setError("Please fill the empty field!");
            et_name.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * Handles the result of the activity launched for result.
     * It is called when returning from the gallery after selecting an image.
     *
     * @param requestCode The request code of the original request.
     * @param resultCode  The result code returned by the activity.
     * @param data        The intent data containing the result.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GO_TO_GALLERY_CODE && resultCode == RESULT_OK && data != null) {
            // update the image view.
            iv_profileImage.setImageURI(data.getData());
            updatedProfileImage = iv_profileImage.getDrawable();
            // save the uri of the image to upload it to firebase.
            uriUpdatedProfileImage = data.getData();
        }
    }
}