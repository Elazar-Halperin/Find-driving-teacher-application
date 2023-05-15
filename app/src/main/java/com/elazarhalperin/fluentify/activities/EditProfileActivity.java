package com.elazarhalperin.fluentify.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
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

        putFields();
        putImageProfile();
        setListeners();
    }

    private void putFields() {
        et_name.setText(userModel.getName());
        et_email.setText(userModel.getEmail());

        if (teacherModel == null) return;

        et_info.setText(teacherModel.getInfo());
        et_lessonPrice.setText(teacherModel.getLessonPrice() + "");
    }

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
            openGallery();
        });
    }

    private void openGallery() {
        Intent intentImage = new Intent();
        intentImage.setType("image/*");
        intentImage.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intentImage, GO_TO_GALLERY_CODE);
    }

    private void saveUserDetails() {
        String name = et_name.getText().toString().trim();

        btn_save.setEnabled(false);

        if (!isUserFieldsValid(name)) {
            return;
        }

        if(name.equals(userModel.getName()) && teacherModel == null) {
            return;
        }

        updateUserInFirebase(name);


        if (teacherModel == null) return;
        // that means that the user is actually a teacher so we will update the teacher.

        btn_save.setEnabled(false);

        String info = et_info.getText().toString().trim();
        String lessonPrice = et_lessonPrice.getText().toString().trim();

        if (info.isEmpty()) {
            et_info.setError("field is empty");
            et_info.requestFocus();
            return;
        }

        if(lessonPrice.isEmpty()) {
            et_lessonPrice.setError("field is empty");
            et_info.requestFocus();
            return;
        }

        try {
            double d = Double.parseDouble(lessonPrice);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "only numeric is possible.", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference teacherRef = FirebaseFirestore.getInstance().collection("teachers").document(firebaseUser.getUid());

        if(lessonPrice.equals(teacherModel.getLessonPrice()) && info.equals(teacherModel.getInfo()) ) return;

        teacherModel.setInfo(info);
        teacherModel.setLessonPrice(Double.parseDouble(lessonPrice));


        teacherRef.update(teacherModel.getMap()).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "updated successfully", Toast.LENGTH_SHORT).show();
                            btn_save.setEnabled(true);
                            finish();
                        } else {
                            btn_save.setEnabled(false);
                        }
                    }
                });
    }

    private void updateUserInFirebase(String name) {
        userModel.setName(name);

        String collection = teacherModel == null ? "students" : "teachers";
        DocumentReference studentRef = FirebaseFirestore.getInstance().collection(collection).document(firebaseUser.getUid());

        studentRef.update(userModel.getMap())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(teacherModel == null) {
                            Toast.makeText(getApplicationContext(), "updated successfully", Toast.LENGTH_SHORT).show();
                            btn_save.setEnabled(false);
                            finish();
                        }
                    }
                });

        if (uriUpdatedProfileImage != null) {
            updateProfileImageInFirebase();
        }


    }

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


    private boolean isUserFieldsValid(String name) {
        if (name.isEmpty()) {
            et_name.setError("Please fill the empty field!");
            et_name.requestFocus();
            return false;
        }
        return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GO_TO_GALLERY_CODE && resultCode == RESULT_OK && data != null) {
            iv_profileImage.setImageURI(data.getData());
            updatedProfileImage = iv_profileImage.getDrawable();
            uriUpdatedProfileImage = data.getData();
        }
    }
}