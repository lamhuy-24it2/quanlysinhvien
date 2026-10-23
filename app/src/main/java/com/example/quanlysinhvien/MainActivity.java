package com.example.quanlysinhvien; // Giữ nguyên dòng package của máy bạn

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private List<Student> studentList;
    private StudentAdapter adapter;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);
        tvEmpty = findViewById(R.id.tvEmpty);
        ListView lvStudents = findViewById(R.id.lvStudents);
        Button btnAddStudent = findViewById(R.id.btnAddStudent);

        studentList = db.getAllStudents();
        adapter = new StudentAdapter();
        lvStudents.setAdapter(adapter);

        // Nút mở Trang 3 để Thêm mới
        btnAddStudent.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
            startActivity(intent);
        });

        // Click vào item SV ở Trang 1 -> Mở Trang 2 (Hộp thoại chi tiết SV)
        lvStudents.setOnItemClickListener((parent, view, position, id) -> {
            showStudentDetailDialog(studentList.get(position));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        studentList = db.getAllStudents();
        tvEmpty.setVisibility(studentList.isEmpty() ? View.VISIBLE : View.GONE);
        adapter.notifyDataSetChanged();
    }

    // TRANG 2: Hộp thoại hiển thị chi tiết Sinh viên
    private void showStudentDetailDialog(Student s) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 32, 48, 16);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);

        ImageView img = new ImageView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(240, 240);
        params.bottomMargin = 24;
        img.setLayoutParams(params);
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Student.loadAvatar(img, s.avatar);
        layout.addView(img);

        TextView tvInfo = new TextView(this);
        String info = getString(R.string.label_name) + ": " + s.name + "\n\n" +
                getString(R.string.label_mssv) + ": " + s.mssv + "\n\n" +
                getString(R.string.label_email) + ": " + s.email;
        tvInfo.setText(info);
        tvInfo.setTextSize(16f);
        layout.addView(tvInfo);

        new AlertDialog.Builder(this)
                .setTitle(R.string.title_detail)
                .setView(layout)
                .setPositiveButton(R.string.btn_edit, (d, w) -> openEditScreen(s))
                .setNeutralButton(R.string.btn_delete, (d, w) -> confirmDeleteStudent(s))
                .setNegativeButton(R.string.btn_close, null)
                .show();
    }

    private void openEditScreen(Student s) {
        Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
        intent.putExtra("student", s);
        startActivity(intent);
    }

    // Hộp thoại xác nhận Xóa thông tin SV
    private void confirmDeleteStudent(Student s) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_title)
                .setMessage(R.string.confirm_delete_msg)
                .setPositiveButton(R.string.btn_yes, (dialog, which) -> {
                    db.deleteStudent(s.id);
                    refreshList();
                })
                .setNegativeButton(R.string.btn_no, null)
                .show();
    }

    private class StudentAdapter extends BaseAdapter {
        @Override
        public int getCount() { return studentList.size(); }
        @Override
        public Object getItem(int position) { return studentList.get(position); }
        @Override
        public long getItemId(int position) { return studentList.get(position).id; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(MainActivity.this)
                        .inflate(R.layout.item_student, parent, false);
            }
            Student s = studentList.get(position);
            ImageView imgAvatar = convertView.findViewById(R.id.imgAvatar);
            TextView tvName = convertView.findViewById(R.id.tvName);
            TextView tvMssv = convertView.findViewById(R.id.tvMssv);
            TextView tvEmail = convertView.findViewById(R.id.tvEmail);
            Button btnEdit = convertView.findViewById(R.id.btnItemEdit);
            Button btnDelete = convertView.findViewById(R.id.btnItemDelete);

            tvName.setText(s.name);
            tvMssv.setText(getString(R.string.label_mssv) + ": " + s.mssv);
            tvEmail.setText(s.email);
            Student.loadAvatar(imgAvatar, s.avatar);

            btnEdit.setOnClickListener(v -> openEditScreen(s));
            btnDelete.setOnClickListener(v -> confirmDeleteStudent(s));

            return convertView;
        }
    }
}