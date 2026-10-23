package com.example.quanlysinhvien; // Giữ nguyên dòng package của máy bạn

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class AddEditActivity extends AppCompatActivity {
    private EditText edtName, edtMssv, edtEmail, edtAvatar;
    private ImageView imgPreview;
    private DatabaseHelper db;
    private Student editingStudent = null;

    // Trình chọn ảnh từ thiết bị
    private final ActivityResultLauncher<String[]> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri != null) {
                    try {
                        getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } catch (Exception ignored) {}
                    edtAvatar.setText(uri.toString());
                    imgPreview.setImageURI(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        db = new DatabaseHelper(this);
        TextView tvFormTitle = findViewById(R.id.tvFormTitle);
        imgPreview = findViewById(R.id.imgPreview);
        edtName = findViewById(R.id.edtName);
        edtMssv = findViewById(R.id.edtMssv);
        edtEmail = findViewById(R.id.edtEmail);
        edtAvatar = findViewById(R.id.edtAvatar);
        Button btnPickImage = findViewById(R.id.btnPickImage);
        Button btnSave = findViewById(R.id.btnSave);

        if (getIntent().hasExtra("student")) {
            editingStudent = (Student) getIntent().getSerializableExtra("student");
            if (editingStudent != null) {
                tvFormTitle.setText(R.string.title_edit);
                edtName.setText(editingStudent.name);
                edtMssv.setText(editingStudent.mssv);
                edtEmail.setText(editingStudent.email);
                edtAvatar.setText(editingStudent.avatar);
                Student.loadAvatar(imgPreview, editingStudent.avatar);
            }
        }

        btnPickImage.setOnClickListener(v -> pickImageLauncher.launch(new String[]{"image/*"}));

        btnSave.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String mssv = edtMssv.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String avatar = edtAvatar.getText().toString().trim();

            if (name.isEmpty() || mssv.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, R.string.err_empty, Toast.LENGTH_SHORT).show();
                return;
            }

            if (editingStudent == null) {
                // Thêm mới
                db.addStudent(new Student(0, name, mssv, email, avatar));
                finish();
            } else {
                // Sửa thông tin -> Bật hộp thoại xác nhận trước khi lưu
                new AlertDialog.Builder(this)
                        .setTitle(R.string.confirm_title)
                        .setMessage(R.string.confirm_edit_msg)
                        .setPositiveButton(R.string.btn_yes, (dialog, which) -> {
                            editingStudent.name = name;
                            editingStudent.mssv = mssv;
                            editingStudent.email = email;
                            editingStudent.avatar = avatar;
                            db.updateStudent(editingStudent);
                            finish();
                        })
                        .setNegativeButton(R.string.btn_no, null)
                        .show();
            }
        });
    }
}